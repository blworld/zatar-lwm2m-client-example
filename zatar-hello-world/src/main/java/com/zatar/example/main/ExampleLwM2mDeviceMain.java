package com.zatar.example.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.eclipse.leshan.client.LwM2mClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.ResourceModel.Operations;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.RegisterResponse;

public class ExampleLwM2mDeviceMain {

	private static String zatarHostname;
	private static Integer zatarPort;

	private static String deviceModel;
	private static String deviceSerialNumber;
	private static String deviceToken;
	
	private static String tlsProtocol;

	public static void main(final String[] args) {
		initProperties(args);

		final Map<Integer, ResourceModel> deviceResources = new HashMap<Integer, ResourceModel>();
		deviceResources.put(1, new ResourceModel(1, deviceModel, Operations.R, false, false, Type.STRING, "", "", ""));
		deviceResources.put(2, new ResourceModel(2, deviceSerialNumber, Operations.R, false, false, Type.STRING, "", "", ""));
		final ObjectModel deviceObjectModel = new ObjectModel(3, "Device", "", false, true, deviceResources);

		final Map<Integer, ResourceModel> devTokenResources = new HashMap<Integer, ResourceModel>();
		devTokenResources.put(0, new ResourceModel(0, deviceToken, Operations.R, false, false, Type.STRING, "", "", ""));
		final ObjectModel devTokenObjectModel = new ObjectModel(23854, "Zatar Device Token", "", false, true, devTokenResources);

		final Map<Integer, ObjectModel> objectModels = new HashMap<>();
		objectModels.put(3, deviceObjectModel);
		objectModels.put(23854, devTokenObjectModel);
		final ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(objectModels));
		SSLContext context = null;
		try {
			context = SSLContext.getInstance(tlsProtocol);
			context.init(null, null, null);
		} catch (final NoSuchAlgorithmException e) {
			System.out.println("There was problem initilizaing the TLS objects, please make sure that chosen protocol exist");
			e.printStackTrace();
			System.exit(-1);
		} catch (final KeyManagementException e) {
			System.out.println("There was problem initilizaing the TLS objects, please make sure that keystore exsists");
			e.printStackTrace();
			System.exit(-1);
		}
		final LwM2mClient client = new LeshanClientBuilder()
				.addBindingModeTCPClient().secure().setSSLContext(context).configure().configure().
				setServerAddress(new InetSocketAddress(zatarHostname, zatarPort)).
				setObjectsInitializer(initializer).
				build();
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
			deviceModel = props.getProperty("device.model");
			deviceSerialNumber = props.getProperty("device.serial.number");
			deviceToken = props.getProperty("device.token");
			tlsProtocol = props.getProperty("tls.protocol");


			if (zatarHostname == null ||
					zatarPort == null ||
					deviceModel == null ||
					deviceSerialNumber == null ||
					deviceToken == null || 
					tlsProtocol == null) {
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
	}

}
