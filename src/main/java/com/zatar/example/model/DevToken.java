package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;

public class DevToken extends BaseInstanceEnabler {

	@Override
	public ValueResponse read(final int resourceId) {
		if (resourceId == 0) {
			return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceId, Value.newStringValue("initial-dev-token")));
		}
		return new ValueResponse(ResponseCode.NOT_FOUND);
	}

}
