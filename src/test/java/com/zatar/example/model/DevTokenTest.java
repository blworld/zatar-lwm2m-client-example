package com.zatar.example.model;

import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.junit.Test;

public class DevTokenTest {

	@Test
	public void canCreate() {
		final LwM2mInstanceEnabler devToken = new DevToken();
	}

}
