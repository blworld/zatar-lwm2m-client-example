package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;

public class Device extends BaseInstanceEnabler {

	private static final String MANUFACTURER = "Zatar Example Devices Inc.";

	@Override
	public ValueResponse read(final int resourceId) {
		return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(0, Value.newStringValue(MANUFACTURER)));
	}

}
