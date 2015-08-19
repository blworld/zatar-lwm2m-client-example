package com.zatar.echo.enablers;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.SimpleInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.LwM2mResponse;
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

	@Override
	public LwM2mResponse write(final int resourceId, final LwM2mResource res) {
		if (resourceId == 1) {
			@SuppressWarnings("unchecked")
			final Value<Integer> value = (Value<Integer>) res.getValue();
			echoCount = value.value;
			System.out.println("Echo Count set to " + echoCount);
			fireResourceChange(resourceId);
			return new LwM2mResponse(ResponseCode.CHANGED);
		}
		return super.write(resourceId, res);
	}

}