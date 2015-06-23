package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.response.ValueResponse;

public class ExecutableResourceEnabler extends BaseResourceEnabler {

	@Override
	public ValueResponse read() {
		return new ValueResponse(ResponseCode.METHOD_NOT_ALLOWED);
	}

}
