package com.zatar.example.model;

import static org.junit.Assert.assertEquals;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;
import org.junit.Before;
import org.junit.Test;

public class DeviceTest {

	private LwM2mInstanceEnabler dev;

	@Before
	public void setup() {
		dev = new Device();
	}

	@Test
	public void canReadManufacturer() {
		assertCorrectResource(0, "Zatar Example Devices Inc.");
	}

	@Test
	public void canReadModel() {
		assertCorrectResource(1, "zatarex1");
	}

	private void assertCorrectResource(final int resourceId, final String value) {
		final ValueResponse response = dev.read(resourceId);

		assertEquals(ResponseCode.CONTENT, response.getCode());
		assertEquals(new LwM2mResource(resourceId, Value.newStringValue(value)),
				response.getContent());
	}

}
