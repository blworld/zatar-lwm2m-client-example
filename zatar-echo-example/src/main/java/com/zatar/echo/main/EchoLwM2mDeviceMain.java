package com.zatar.echo.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.eclipse.leshan.client.LwM2mClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.californium.LeshanClientBuilder.TCPConfigBuilder;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.ResourceModel.Operations;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.RegisterResponse;

import com.zatar.echo.enablers.DeviceToken;
import com.zatar.echo.enablers.Echoer;

public class EchoLwM2mDeviceMain {

	private static String zatarHostname;
	private static Integer zatarPort;

	private static String deviceManufacturer;
	private static String deviceModel;
	private static String deviceSerialNumber;

	private static String tlsProtocol;
	private static boolean isTlsEnabled;


	public static void main(final String[] args) {
		initProperties(args);

		final Map<Integer, ResourceModel> deviceResources = new HashMap<Integer, ResourceModel>();
		deviceResources.put(0, new ResourceModel(0, deviceManufacturer, Operations.R, false, false, Type.STRING, "", "", ""));
		deviceResources.put(1, new ResourceModel(1, deviceModel, Operations.R, false, false, Type.STRING, "", "", ""));
		deviceResources.put(2, new ResourceModel(2, deviceSerialNumber, Operations.R, false, false, Type.STRING, "", "", ""));
		final ObjectModel deviceObjectModel = new ObjectModel(3, "Device", "", false, true, deviceResources);

		final Map<Integer, ResourceModel> devTokenResources = new HashMap<Integer, ResourceModel>();
		devTokenResources.put(0, new ResourceModel(0, DeviceToken.deviceToken, Operations.R, false, false, Type.STRING, "", "", ""));
		devTokenResources.put(1, new ResourceModel(1, "-1", Operations.W, false, false, Type.INTEGER, "", "", ""));
		final ObjectModel devTokenObjectModel = new ObjectModel(23854, "Zatar Device Token", "", false, true, devTokenResources);

		final Map<Integer, ResourceModel> echoerResources = new HashMap<Integer, ResourceModel>();
		echoerResources.put(0, new ResourceModel(0, "", Operations.E, false, false, Type.OPAQUE, "", "", ""));
		echoerResources.put(1, new ResourceModel(1, "", Operations.RW, false, false, Type.STRING, "", "", ""));
		echoerResources.put(2, new ResourceModel(2, "", Operations.RW, false, false, Type.INTEGER, "", "", ""));
		echoerResources.put(3, new ResourceModel(3, "", Operations.R, false, false, Type.TIME, "", "", ""));
		final ObjectModel echoerObjectModel = new ObjectModel(11111, "Echoer", "", false, true, echoerResources);

		final Map<Integer, ObjectModel> objectModels = new HashMap<>();
		objectModels.put(3, deviceObjectModel);
		objectModels.put(23854, devTokenObjectModel);
		objectModels.put(11111, echoerObjectModel);

		final Echoer echoer = new Echoer();
		final ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(objectModels));
		initializer.setClassForObject(23854, DeviceToken.class);
		initializer.setInstancesForObject(11111, echoer);

		final LeshanClientBuilder builder = new LeshanClientBuilder()
				.setServerAddress(new InetSocketAddress(zatarHostname, zatarPort)).setObjectsInitializer(initializer);
		final TCPConfigBuilder tcpBuilder = builder.addBindingModeTCPClient();
		if(isTlsEnabled) {
			SSLContext context = null;
			try {
				context = SSLContext.getInstance(tlsProtocol);
				context.init(null, null, null);
			} catch (final NoSuchAlgorithmException e) {
				System.out.println("There was problem initializing the TLS objects, please make sure that chosen protocol exists");
				e.printStackTrace();
				System.exit(-1);
			} catch (final KeyManagementException e) {
				System.out.println("There was problem initializing the TLS objects, please make sure that keystore exists");
				e.printStackTrace();
				System.exit(-1);
			}
			//configure TLS
			tcpBuilder.secure().setSSLContext(context).configure();
		}
		//configure TCP and build a LWM2M Client
		final LwM2mClient client = tcpBuilder.configure().build();
		client.start();

		final String endpoint = UUID.randomUUID().toString();
		final RegisterResponse response = client.send(new RegisterRequest(endpoint));
		final String registrationID = response.getRegistrationID();
		System.out.println("Registered with ID: " + registrationID);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (registrationID != null) {
					System.out.println("\nDeregistering Client '" + registrationID + "'");
					client.send(new DeregisterRequest(registrationID));
					client.stop();
				}
			}
		});

		final Scanner scanner = new Scanner(System.in);
		System.out.println("Input any text to reset the Echo count and notify Zatar.");
		while (scanner.hasNext()) {
			final String textInput = scanner.next();
			System.out.println("User input '" + textInput + "'.  Resetting Echo Count to 1, reset date to now and notifying Zatar.");
			echoer.resetEchoCount();
		}
		scanner.close();
	}

	private static void initProperties(final String[] args) {
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
			tlsProtocol = props.getProperty("tls.protocol");
			isTlsEnabled = props.containsKey("tls.enabled") ? Boolean.parseBoolean(props.getProperty("tls.enabled")) : true;

			DeviceToken.deviceToken = props.getProperty("device.token");

			if (zatarHostname == null ||
					zatarPort == null ||
					deviceManufacturer == null ||
					deviceModel == null ||
					deviceSerialNumber == null ||
					DeviceToken.deviceToken == null ||
					tlsProtocol == null) {
				System.err.println("One or more of the required properties is missing. Aborting.");
				System.exit(1);
			}
		} catch (final IOException e) {
			System.err.println("Could not read file " + args[0] + ". Aborting.");
			System.exit(1);
		} catch (final NumberFormatException e) {
			System.err.println("The port number must be a valid integer. Aborting.");
			System.exit(1);
		}
	}

}
