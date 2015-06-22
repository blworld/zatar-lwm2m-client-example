package com.zatar.example.model;

import static org.junit.Assert.assertEquals;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;
import org.junit.Test;

public class DeviceTest {

	@Test
	public void canReadManufacturer() {
		final LwM2mInstanceEnabler dev = new Device();

		final ValueResponse response = dev.read(0);

		assertEquals(ResponseCode.CONTENT, response.getCode());
		assertEquals(new LwM2mResource(0, Value.newStringValue("Zatar Example Devices Inc.")),
				response.getContent());
	}

	@Test
	public void canReadModel() {
		final LwM2mInstanceEnabler dev = new Device();

		final ValueResponse response = dev.read(1);

		assertEquals(ResponseCode.CONTENT, response.getCode());
		assertEquals(new LwM2mResource(1, Value.newStringValue("zatarex1")),
				response.getContent());
	}

}
