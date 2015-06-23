package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.response.LwM2mResponse;

public class ExecutableResourceEnabler extends BaseResourceEnabler {

	@Override
	public LwM2mResponse execute(final byte[] params) {
		return new LwM2mResponse(ResponseCode.CHANGED);
	}

}
