package com.zatar.example.main;

import java.io.FileInputStream;
import java.io.IOException;
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
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.ResourceModel.Operations;
import org.eclipse.leshan.core.model.ResourceModel.Type;
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
	private static int zatarPort;

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
		} catch (final IOException e) {
			System.err.println("Could not read file " + args[0] + ". Aborting.");
		}

		final Map<Integer, ObjectModel> objectModels = new HashMap<>();
		objectModels.put(3, createDeviceObjectModel());
		objectModels.put(23854, createDevTokenObjectModel());
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

	private static ObjectModel createDeviceObjectModel() {
		final Map<Integer, ResourceModel> resources = new HashMap<Integer, ResourceModel>();
		resources.put(0, new ResourceModel(0, "Manufacturer", Operations.R, false, false, Type.STRING, "", "", ""));
		resources.put(1, new ResourceModel(1, "Model", Operations.R, false, false, Type.STRING, "", "", ""));
		resources.put(2, new ResourceModel(2, "Serial Number", Operations.R, false, false, Type.STRING, "", "", ""));
		return new ObjectModel(3, "Device", "", false, true, resources);
	}

	private static ObjectModel createDevTokenObjectModel() {
		final Map<Integer, ResourceModel> resources = new HashMap<Integer, ResourceModel>();
		resources.put(0, new ResourceModel(0, "Token", Operations.R, false, false, Type.STRING, "", "", ""));
		resources.put(3, new ResourceModel(1, "Validation", Operations.W, false, false, Type.INTEGER, "", "", ""));
		return new ObjectModel(23854, "Zatar Device Token", "", false, true, resources);
	}


	public static class Device extends BaseInstanceEnabler {


		@Override
		public ValueResponse read(final int resourceId) {
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

		@Override
		public ValueResponse read(final int resourceId) {
			switch (resourceId) {
				case 0:
					return new ValueResponse(ResponseCode.CONTENT,
							new LwM2mResource(0, Value.newStringValue(deviceToken)));
				case 1:
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
