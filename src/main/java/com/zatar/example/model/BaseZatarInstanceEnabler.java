package com.zatar.example.model;

import java.util.Map;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public class BaseZatarInstanceEnabler extends BaseInstanceEnabler {

	protected Map<Integer, ResourceEnabler> resources;

	public BaseZatarInstanceEnabler(final Map<Integer, ResourceEnabler> resources) {
		this.resources = resources;
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
		final ResourceEnabler enabler = resources.get(resourceId);
		if (enabler == null) {
			return new LwM2mResponse(ResponseCode.NOT_FOUND);
		}
		return enabler.execute(params);
	}

}
