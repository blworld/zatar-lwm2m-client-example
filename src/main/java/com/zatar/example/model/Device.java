package com.zatar.example.model;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public class Device extends BaseInstanceEnabler {

	private static final String SERIAL_NUMBER = "ZE98765";
	private static final String MANUFACTURER = "Zatar Example Devices Inc.";
	private static final String MODEL = "zatarex1";
	private final Set<Integer> resources;

	public Device() {
		resources = new HashSet<>();
		resources.add(0);
		resources.add(1);
		resources.add(2);
	}

	@Override
	public ValueResponse read(final int resourceId) {
		if (resourceExists(resourceId)) {
			return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceId, Value.newStringValue(readStringValue(resourceId))));
		} else {
			return new ValueResponse(ResponseCode.NOT_FOUND);
		}
	}

	@Override
	public LwM2mResponse write(final int resourceId, final LwM2mResource node) {
		if (resourceExists(resourceId)) {
			return new LwM2mResponse(ResponseCode.METHOD_NOT_ALLOWED);
		} else {
			return new LwM2mResponse(ResponseCode.NOT_FOUND);
		}
	}

	private boolean resourceExists(final int resourceId) {
		return resources.contains(resourceId);
	}

	private String readStringValue(final int resourceId) {
		switch(resourceId) {
			case 0:
				return MANUFACTURER;
			case 1:
				return MODEL;
			case 2:
				return SERIAL_NUMBER;
			default:
				return null;
		}
	}

}
