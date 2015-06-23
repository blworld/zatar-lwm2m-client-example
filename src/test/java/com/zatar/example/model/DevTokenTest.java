package com.zatar.example.model;

import org.eclipse.leshan.client.resource.LwM2mInstanceEnabler;
import org.junit.Test;

public class DevTokenTest extends BaseInstanceEnablerTest {

	@Override
	protected LwM2mInstanceEnabler createEnabler() {
		return new DevToken();
	}

	@Test
	public void readOnTokenContent() {
		assertContentRead(0, "initial-dev-token");
	}

	@Test
	public void writeOnTokenNotAllowed() {
		assertNotAllowedWrite(0, "invalid-token");
	}

	@Test
	public void readOnValidatorNotAllowed() {
		assertNotAllowedRead(3);
	}

	@Test
	public void readOnMissingResourceNotFound() {
		assertNotFoundRead(150);
	}

	@Test
	public void writeOnMissingResourceNotFound() {
		assertNotFoundWrite(150, "missing-token");
	}

}
