package com.zatar.echo.enablers;

import org.eclipse.leshan.client.resource.SimpleInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.Value;
import org.eclipse.leshan.core.response.LwM2mResponse;

public class DeviceToken extends SimpleInstanceEnabler {

	public static String deviceToken;

	@Override
	public LwM2mResponse write(final int resourceId, final LwM2mResource resource) {
		if (resourceId == 1) {
			@SuppressWarnings("unchecked")
			final int value = ((Value<Integer>) resource.getValue()).value;
			switch (value) {
				case -1:
					System.out.println("Registration reset");
					break;
				case 0:
					System.out.println("Device token was rejected");
					break;
				case 1:
					System.out.println("Device token was accepted");
					break;
				default:
					System.out.println("Unrecognized validation value (" + value + ")");
					break;
			}
		}

		return super.write(resourceId, resource);
	}

}