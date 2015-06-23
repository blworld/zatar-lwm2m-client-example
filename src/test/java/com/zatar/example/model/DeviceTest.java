package com.zatar.example.model;

import static org.junit.Assert.assertEquals;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.LwM2mResponse;
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
	public void readOnManufacturerContent() {
		assertContentRead(0, "Zatar Example Devices Inc.");
	}

	@Test
	public void writeOnManufacturerNotAllowed() {
		assertNotAllowedWrite(0, "Some Other Manufacturer");
	}

	@Test
	public void executeOnManufacturerNotAllowed() {
		assertNotAllowedExecute(0, "Payload!");
	}

	@Test
	public void readOnModelContent() {
		assertContentRead(1, "zatarex1");
	}

	@Test
	public void readOnSerialNumberContent() {
		assertContentRead(2, "ZE98765");
	}

	@Test
	public void readOnUtcOffsetContent() {
		assertContentRead(14, "+05");
	}

	@Test
	public void writeOnUtcOffsetChanged() {
		assertChangedWrite(14, "-03");
		assertContentRead(14, "-03");
	}

	@Test
	public void readOnMissingResourceNotFound() {
		assertNotFoundRead(150);
	}

	@Test
	public void writeOnMissingResouceNotFound() {
		assertNotFoundWrite(150, "Whatever you are...");
	}

	private void assertContentRead(final int resourceId, final String value) {
		final ValueResponse response = dev.read(resourceId);

		assertEquals(ResponseCode.CONTENT, response.getCode());
		assertEquals(new LwM2mResource(resourceId, Value.newStringValue(value)),
				response.getContent());
	}

	private void assertNotFoundRead(final int resourceId) {
		final ValueResponse response = dev.read(resourceId);

		assertEquals(ResponseCode.NOT_FOUND, response.getCode());
	}

	private void assertChangedWrite(final int resourceId, final String newValue) {
		final LwM2mResponse response = dev.write(resourceId, new LwM2mResource(resourceId, Value.newStringValue(newValue)));

		assertEquals(ResponseCode.CHANGED, response.getCode());
	}

	private void assertNotAllowedWrite(final int resourceId, final String newValue) {
		final LwM2mResponse response = dev.write(resourceId, new LwM2mResource(resourceId, Value.newStringValue(newValue)));

		assertEquals(ResponseCode.METHOD_NOT_ALLOWED, response.getCode());
	}

	private void assertNotFoundWrite(final int resourceId, final String newValue) {
		final LwM2mResponse response = dev.write(resourceId, new LwM2mResource(resourceId, Value.newStringValue(newValue)));

		assertEquals(ResponseCode.NOT_FOUND, response.getCode());
	}

	private void assertNotAllowedExecute(final int resourceId, final String params) {
		final LwM2mResponse response = dev.execute(resourceId, params.getBytes());

		assertEquals(ResponseCode.METHOD_NOT_ALLOWED, response.getCode());
	}

}
