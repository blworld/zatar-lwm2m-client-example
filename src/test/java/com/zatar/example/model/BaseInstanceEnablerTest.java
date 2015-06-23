package com.zatar.example.model;

import static org.junit.Assert.assertEquals;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public class BaseInstanceEnablerTest {

	protected LwM2mInstanceEnabler enabler;

	protected void assertContentRead(final int resourceId, final String value) {
		final ValueResponse response = enabler.read(resourceId);

		assertEquals(ResponseCode.CONTENT, response.getCode());
		assertEquals(new LwM2mResource(resourceId, Value.newStringValue(value)),
				response.getContent());
	}

	protected void assertNotFoundRead(final int resourceId) {
		final ValueResponse response = enabler.read(resourceId);

		assertEquals(ResponseCode.NOT_FOUND, response.getCode());
	}

	protected void assertNotAllowedRead(final int resourceId) {
		final ValueResponse response = enabler.read(resourceId);

		assertEquals(ResponseCode.METHOD_NOT_ALLOWED, response.getCode());
	}

	protected void assertChangedWrite(final int resourceId, final String newValue) {
		final LwM2mResponse response = enabler.write(resourceId, new LwM2mResource(resourceId, Value.newStringValue(newValue)));

		assertEquals(ResponseCode.CHANGED, response.getCode());
	}

	protected void assertNotAllowedWrite(final int resourceId, final String newValue) {
		final LwM2mResponse response = enabler.write(resourceId, new LwM2mResource(resourceId, Value.newStringValue(newValue)));

		assertEquals(ResponseCode.METHOD_NOT_ALLOWED, response.getCode());
	}

	protected void assertNotFoundWrite(final int resourceId, final String newValue) {
		final LwM2mResponse response = enabler.write(resourceId, new LwM2mResource(resourceId, Value.newStringValue(newValue)));

		assertEquals(ResponseCode.NOT_FOUND, response.getCode());
	}

	protected void assertChangedExecute(final int resourceId, final String params) {
		final LwM2mResponse response = enabler.execute(resourceId, params.getBytes());

		assertEquals(ResponseCode.CHANGED, response.getCode());
	}

	protected void assertNotAllowedExecute(final int resourceId, final String params) {
		final LwM2mResponse response = enabler.execute(resourceId, params.getBytes());

		assertEquals(ResponseCode.METHOD_NOT_ALLOWED, response.getCode());
	}

	protected void assertNotFoundExecute(final int resourceId, final String params) {
		final LwM2mResponse response = enabler.execute(resourceId, params.getBytes());

		assertEquals(ResponseCode.NOT_FOUND, response.getCode());
	}

}
