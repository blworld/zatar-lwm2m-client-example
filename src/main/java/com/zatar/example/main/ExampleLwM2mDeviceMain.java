package com.zatar.example.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.leshan.client.LwM2mClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.ResourceModel.Operations;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.core.request.DeregisterRequest;
import org.eclipse.leshan.core.request.RegisterRequest;
import org.eclipse.leshan.core.response.RegisterResponse;

import com.zatar.example.model.DevToken;
import com.zatar.example.model.Device;

public class ExampleLwM2mDeviceMain {

	public static void main(final String[] args) {
		final Map<Integer, ObjectModel> objectModels = new HashMap<>();
		objectModels.put(3, createDeviceObjectModel());
		objectModels.put(23854, createDevTokenObjectModel());

		final LwM2mModel model = new LwM2mModel(objectModels);
		final ObjectsInitializer initializer = new ObjectsInitializer(model);

		initializer.setClassForObject(3, Device.class);
		initializer.setClassForObject(23854, DevToken.class);

		final LwM2mClient client = new LeshanClientBuilder()
				.setBindingMode(BindingMode.T)
				.setServerAddress(new InetSocketAddress("lwm2m", 5683))
				.setObjectsInitializer(initializer)
				.build(3, 23854);

		client.start();
		final RegisterResponse response = client.send(new RegisterRequest("example-endpoint"));
		final String registrationID = response.getRegistrationID();
		System.out.println("Registered with ID: " + registrationID);

		waitForInput();

		client.send(new DeregisterRequest(registrationID));
		client.stop();
	}

	private static ObjectModel createDeviceObjectModel() {
		final Map<Integer, ResourceModel> resources = new HashMap<Integer, ResourceModel>();
		resources.put(0, new ResourceModel(0, "Manufacturer", Operations.R, false, false, Type.STRING, "", "", ""));
		resources.put(1, new ResourceModel(1, "Model", Operations.R, false, false, Type.STRING, "", "", ""));
		resources.put(2, new ResourceModel(2, "Serial Number", Operations.R, false, false, Type.STRING, "", "", ""));
		resources.put(4, new ResourceModel(4, "Reboot", Operations.E, false, false, Type.STRING, "", "", ""));
		resources.put(14, new ResourceModel(14, "UTC Offset", Operations.RW, false, false, Type.STRING, "", "", ""));
		return new ObjectModel(3, "Device", "", false, true, resources);
	}

	private static ObjectModel createDevTokenObjectModel() {
		final Map<Integer, ResourceModel> resources = new HashMap<Integer, ResourceModel>();
		resources.put(0, new ResourceModel(0, "Token", Operations.R, false, false, Type.STRING, "", "", ""));
		resources.put(3, new ResourceModel(1, "Validation", Operations.W, false, false, Type.INTEGER, "", "", ""));
		return new ObjectModel(23854, "Zatar Device Token", "", false, true, resources);
	}

	private static void waitForInput() {
		try {
			System.in.read();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
