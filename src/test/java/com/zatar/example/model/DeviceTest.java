package com.zatar.example.model;

import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.junit.Test;

public class DeviceTest {

	@Test
	public void test() {
		final LwM2mInstanceEnabler dev = new Device();
		dev.read(0);
	}

}
