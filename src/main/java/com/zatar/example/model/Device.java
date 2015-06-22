package com.zatar.example.model;

import java.util.HashMap;
import java.util.Map;

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
	private final Map<Integer, ResourceEnabler> resources;
	private String utcOffset;

	public Device() {
		resources = new HashMap<>();
		resources.put(0, new ResourceEnabler());
		resources.put(1, new ResourceEnabler());
		resources.put(2, new ResourceEnabler());
		resources.put(14, new ResourceEnabler());

		utcOffset = "+05";
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
			if (resourceId == 14) {
				@SuppressWarnings("unchecked")
				final Value<String> value = (Value<String>) node.getValue();
				utcOffset = value.value;
				return new LwM2mResponse(ResponseCode.CHANGED);
			} else {
				return new LwM2mResponse(ResponseCode.METHOD_NOT_ALLOWED);
			}
		} else {
			return new LwM2mResponse(ResponseCode.NOT_FOUND);
		}
	}

	private boolean resourceExists(final int resourceId) {
		return resources.containsKey(resourceId);
	}

	private String readStringValue(final int resourceId) {
		switch(resourceId) {
			case 0:
				return MANUFACTURER;
			case 1:
				return MODEL;
			case 2:
				return SERIAL_NUMBER;
			case 14:
				return utcOffset;
			default:
				return null;
		}
	}

	private class ResourceEnabler {

	}

}
