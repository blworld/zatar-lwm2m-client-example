package com.zatar.example.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;

public class DevToken extends BaseZatarInstanceEnabler {

	public DevToken() {
		super(createEnablers());
	}

	private static Map<Integer, ResourceEnabler> createEnablers() {
		final HashMap<Integer, ResourceEnabler> resources = new HashMap<>();
		return resources;
	}

	@Override
	public ValueResponse read(final int resourceId) {
		if (resourceId == 0) {
			return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceId, Value.newStringValue("initial-dev-token")));
		}
		return new ValueResponse(ResponseCode.NOT_FOUND);
	}

}
