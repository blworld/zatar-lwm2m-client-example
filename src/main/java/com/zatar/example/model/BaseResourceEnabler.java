package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public abstract class BaseResourceEnabler implements ResourceEnabler {

	@Override
	public ValueResponse read() {
		return new ValueResponse(ResponseCode.METHOD_NOT_ALLOWED);
	}

	@Override
	public LwM2mResponse write(final LwM2mResource node) {
		return new LwM2mResponse(ResponseCode.METHOD_NOT_ALLOWED);
	}

	@Override
	public LwM2mResponse execute(final byte[] params) {
		return new LwM2mResponse(ResponseCode.METHOD_NOT_ALLOWED);
	}

}
