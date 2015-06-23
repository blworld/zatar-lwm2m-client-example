package com.zatar.example.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public class Device extends BaseInstanceEnabler {

	private static final String MANUFACTURER = "Zatar Example Devices Inc.";
	private static final String MODEL = "zatarex1";
	private static final String SERIAL_NUMBER = "ZE98765";
	private static final String INITIAL_UTC_OFFSET = "+05";
	private final Map<Integer, ResourceEnabler> resources;

	public Device() {
		resources = new HashMap<>();
		resources.put(0, new ReadOnlyResourceEnabler(0, MANUFACTURER));
		resources.put(1, new ReadOnlyResourceEnabler(1, MODEL));
		resources.put(2, new ReadOnlyResourceEnabler(2, SERIAL_NUMBER));
		resources.put(14, new ReadWriteResourceEnabler(14, INITIAL_UTC_OFFSET));
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
		if (enabler == null) {
			return new LwM2mResponse(ResponseCode.NOT_FOUND);
		}
		return enabler.write(node);
	}

	@Override
	public LwM2mResponse execute(final int resourceId, final byte[] params) {
		if (resources.get(resourceId) == null) {
			return new LwM2mResponse(ResponseCode.NOT_FOUND);
		}
		return new LwM2mResponse(ResponseCode.METHOD_NOT_ALLOWED);
	}

}
