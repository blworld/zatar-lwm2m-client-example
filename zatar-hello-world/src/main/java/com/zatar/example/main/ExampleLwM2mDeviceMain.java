package com.zatar.example.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.LwM2mClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.node.Value.DataType;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.RegisterResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public class ExampleLwM2mDeviceMain {

	private static String zatarHostname;
	private static Integer zatarPort;

	private static String deviceManufacturer;
	private static String deviceModel;
	private static String deviceSerialNumber;
	private static String deviceToken;

	public static void main(final String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java -jar <jar> <properties-file>");
			System.exit(1);
		}

		try {
			final Properties props = new Properties();
			props.load(new FileInputStream(args[0]));
			zatarHostname = props.getProperty("zatar.hostname");
			zatarPort = Integer.parseInt(props.getProperty("zatar.port"));
			deviceManufacturer = props.getProperty("device.manufacturer");
			deviceModel = props.getProperty("device.model");
			deviceSerialNumber = props.getProperty("device.serial.number");
			deviceToken = props.getProperty("device.token");

			if (zatarHostname == null ||
					zatarPort == null ||
					deviceManufacturer == null ||
					deviceModel == null ||
					deviceSerialNumber == null ||
					deviceToken == null) {
				System.err.println("One or more of the required properties is missing. Aborting.");
				System.exit(1);
			}
		} catch (final IOException e) {
			System.err.println("Could not read file " + args[0] + ". Aborting.");
			System.exit(1);
		} catch (final NumberFormatException e) {
			System.err.println("Invalid port number in properties file. Aborting.");
			System.exit(1);
		}

		final Map<Integer, ObjectModel> objectModels = new HashMap<>();
		// load OMA objects
		loadModelObjects(objectModels, ObjectLoader.loadDefault());
		// load Zatar objects
		final InputStream input = ExampleLwM2mDeviceMain.class.getResourceAsStream("/custom_oma_object_specs/zatar-objects-spec.json");
		if (input == null) {
			System.err.println("Unable to load zatar object models");
		} else {
			loadModelObjects(objectModels, ObjectLoader.loadJsonStream(input));
		}

		final ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(objectModels));
		initializer.setClassForObject(3, Device.class);
		initializer.setClassForObject(23854, DevToken.class);

		final LwM2mClient client = new LeshanClientBuilder().
				setBindingMode(BindingMode.T).
				setServerAddress(new InetSocketAddress(zatarHostname, zatarPort)).
				setObjectsInitializer(initializer).
				build(3, 23854);

		client.start();
		final String endpoint = UUID.randomUUID().toString();
		final RegisterResponse response = client.send(new RegisterRequest(endpoint));
		final String registrationID = response.getRegistrationID();
		System.out.println("Registered with ID: " + registrationID);

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				if (registrationID != null) {
					System.out.println("\tDevice: Deregistering Client '" + registrationID + "'");
					client.send(new DeregisterRequest(registrationID));
					client.stop();
				}
			}

		});
	}

	private static final void loadModelObjects(final Map<Integer, ObjectModel> map, final Iterable<ObjectModel> models) {
		for (final ObjectModel model : models) {
			System.out.println("Loading object model: " + model);
			final ObjectModel old = map.put(model.id, model);
			if (old != null) {
				System.out.println("Model already exists for object " + model.id + ". Overriding it.");
			}
		}
	}

	public static class Device extends BaseInstanceEnabler {


		public Device() {
			System.out.println("Device instance created.");
		}


		@Override
		public ValueResponse read(final int resourceId) {
			System.out.println("Read request received on Device for resource ID " + resourceId);
			switch (resourceId) {
				case 0:
					return new ValueResponse(ResponseCode.CONTENT,
							new LwM2mResource(0, Value.newStringValue(deviceManufacturer)));
				case 1:
					return new ValueResponse(ResponseCode.CONTENT,
							new LwM2mResource(1, Value.newStringValue(deviceModel)));
				case 2:
					return new ValueResponse(ResponseCode.CONTENT,
							new LwM2mResource(2, Value.newStringValue(deviceSerialNumber)));
				default:
					return new ValueResponse(ResponseCode.NOT_FOUND);
			}
		}

	}

	public static class DevToken extends BaseInstanceEnabler {

		public DevToken() {
			System.out.println("DevToken instance created.");
		}

		@Override
		public ValueResponse read(final int resourceId) {
			System.out.println("Read request received on DevToken for resource ID " + resourceId);
			switch (resourceId) {
				case 0:
					return new ValueResponse(ResponseCode.CONTENT,
							new LwM2mResource(0, Value.newStringValue(deviceToken)));
				case 3:
					return new ValueResponse(ResponseCode.METHOD_NOT_ALLOWED);
				default:
					return new ValueResponse(ResponseCode.NOT_FOUND);
			}
		}

		@Override
		public LwM2mResponse write(final int resourceId, final LwM2mResource resource) {
			switch (resourceId) {
				case 0:
					return new LwM2mResponse(ResponseCode.METHOD_NOT_ALLOWED);
				case 3:
					if (resource.getValue().type == DataType.INTEGER) {
						@SuppressWarnings("unchecked")
						final Value<Integer> value = (Value<Integer>) resource.getValue();
						final int validated = value.value;
						if (validated == 1) {
							System.out.println("Device Token was accepted");
							return new LwM2mResponse(ResponseCode.CHANGED);
						}
						if (validated == 0) {
							System.out.println("Device Token was rejected");
							return new LwM2mResponse(ResponseCode.CHANGED);
						}
					}
					return new LwM2mResponse(ResponseCode.BAD_REQUEST);
				default:
					return new LwM2mResponse(ResponseCode.NOT_FOUND);
			}
		}

	}

}
