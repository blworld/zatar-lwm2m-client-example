package com.zatar.example.main;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.eclipse.leshan.client.LwM2mClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.RegisterResponse;

public class ExampleLwM2mDeviceMain {

	public static void main(final String[] args) {
		final ObjectsInitializer initializer = new ObjectsInitializer();
		initializer.setClassForObject(3, Device.class);

		final LwM2mClient client = new LeshanClientBuilder().
				setBindingMode(BindingMode.T).
				setServerAddress(new InetSocketAddress("lwm2m", 5683)).
				setObjectsInitializer(initializer).
				build(3);

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

	private static class Device extends BaseInstanceEnabler {

	}

}
