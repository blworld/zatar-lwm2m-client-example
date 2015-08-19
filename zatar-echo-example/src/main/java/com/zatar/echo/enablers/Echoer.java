package com.zatar.echo.enablers;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.SimpleInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.ValueResponse;

public class Echoer extends SimpleInstanceEnabler {

	public static Integer echoCount;

	@Override
	public ValueResponse read(final int resourceId) {
		if (resourceId == 1) {
			return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(1, Value.newIntegerValue(echoCount)));
		}
		return super.read(resourceId);
	}

}