package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.LwM2mResponse;

public class WriteOnlyResourceEnabler extends BaseResourceEnabler {

	@Override
	public LwM2mResponse write(final LwM2mResource node) {
		// TODO: This should actually trigger something, or store something.
		return new LwM2mResponse(ResponseCode.CHANGED);
	}

}
