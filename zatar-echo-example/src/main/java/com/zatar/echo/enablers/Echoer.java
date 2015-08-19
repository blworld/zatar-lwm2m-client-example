package com.zatar.echo.enablers;

import org.eclipse.leshan.ResponseCode;
import org.eclipse.leshan.client.resource.SimpleInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public class Echoer extends SimpleInstanceEnabler {

	private static String echoText = "hello";
	private static int echoCount = 1;

	@Override
	public ValueResponse read(final int resourceId) {
		switch (resourceId) {
			case 1:
				return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(1, Value.newStringValue(echoText)));
			case 2:
				return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(2, Value.newIntegerValue(echoCount)));
		}
		return super.read(resourceId);
	}

	@Override
	public LwM2mResponse write(final int resourceId, final LwM2mResource res) {
		switch (resourceId) {
			case 1:
				@SuppressWarnings("unchecked")
				final Value<String> newText = (Value<String>) res.getValue();
				echoText = newText.value;
				System.out.println("Echo Text set to `" + echoText + "'");
				fireResourceChange(resourceId);
				return new LwM2mResponse(ResponseCode.CHANGED);
			case 2:
				@SuppressWarnings("unchecked")
				final Value<Integer> newCount = (Value<Integer>) res.getValue();
				echoCount = newCount.value;
				System.out.println("Echo Count set to " + echoCount);
				fireResourceChange(resourceId);
				return new LwM2mResponse(ResponseCode.CHANGED);
		}
		return super.write(resourceId, res);
	}

	@Override
	public LwM2mResponse execute(final int resourceId, final byte[] payload) {
		if (resourceId == 0) {
			System.out.println("=====================================================");
			for (int i = 0; i < echoCount; i++) {
				System.out.println(echoText);
			}
			System.out.println("=====================================================");
			return new LwM2mResponse(ResponseCode.CHANGED);
		}
		return super.execute(resourceId, payload);
	}
}