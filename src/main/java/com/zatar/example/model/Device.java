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
		resources.put(0, new ResourceEnabler(0, MANUFACTURER));
		resources.put(1, new ResourceEnabler(1, MODEL));
		resources.put(2, new ResourceEnabler(2, SERIAL_NUMBER));
		resources.put(14, new ResourceEnabler(14, "+05"));

		utcOffset = "+05";
	}

	@Override
	public ValueResponse read(final int resourceId) {
		final ResourceEnabler enabler = resources.get(resourceId);
		if (enabler == null) {
			return new ValueResponse(ResponseCode.NOT_FOUND);
		}
		return enabler.read();
	}

	@Override
	public LwM2mResponse write(final int resourceId, final LwM2mResource node) {
		final ResourceEnabler enabler = resources.get(resourceId);
		if (enabler != null) {
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

	private class ResourceEnabler {

		private final int id;
		private final String value;

		public ResourceEnabler(final int id, final String value) {
			this.id = id;
			this.value = value;
		}

		public ValueResponse read() {
			return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(id, Value.newStringValue(value)));
		}

	}

}
