package com.zatar.example.model;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;

public class ReadOnlyResourceEnabler extends BaseResourceEnabler {
	private final int id;
	private final String value;

	public ReadOnlyResourceEnabler(final int id, final String value) {
		this.id = id;
		this.value = value;
	}

	@Override
	public ValueResponse read() {
		return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(id, Value.newStringValue(value)));
	}

}