package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;

public class Device extends BaseInstanceEnabler {

	private static final String MANUFACTURER = "Zatar Example Devices Inc.";
	private static final String MODEL = "zatarex1";

	@Override
	public ValueResponse read(final int resourceId) {
		if (resourceId == 0 || resourceId == 1) {
			return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceId, Value.newStringValue(readStringValue(resourceId))));
		} else {
			return new ValueResponse(ResponseCode.NOT_FOUND);
		}
	}

	private String readStringValue(final int resourceId) {
		switch(resourceId) {
			case 0:
				return MANUFACTURER;
			case 1:
				return MODEL;
			default:
				return null;
		}
	}

}
