package com.zatar.example.model;

import org.junit.Before;
import org.junit.Test;

public class DeviceTest extends BaseInstanceEnablerTest {

	@Before
	public void setup() {
		enabler = new Device();
	}

	@Test
	public void readOnManufacturerContent() {
		assertContentRead(0, "Zatar Example Devices Inc.");
	}

	@Test
	public void writeOnManufacturerNotAllowed() {
		assertNotAllowedWrite(0, "Some Other Manufacturer");
	}

	@Test
	public void executeOnManufacturerNotAllowed() {
		assertNotAllowedExecute(0, "Payload!");
	}

	@Test
	public void readOnModelContent() {
		assertContentRead(1, "zatarex1");
	}

	@Test
	public void readOnSerialNumberContent() {
		assertContentRead(2, "ZE98765");
	}

	@Test
	public void readOnRebootNotAllowed() {
		assertNotAllowedRead(4);
	}

	@Test
	public void writeOnRebootNotAllowed() {
		assertNotAllowedWrite(4, "???");
	}

	@Test
	public void executeOnRebootChanged() {
		assertChangedExecute(4, "");
	}

	@Test
	public void readOnUtcOffsetContent() {
		assertContentRead(14, "+05");
	}

	@Test
	public void writeOnUtcOffsetChanged() {
		assertChangedWrite(14, "-03");
		assertContentRead(14, "-03");
	}

	@Test
	public void readOnMissingResourceNotFound() {
		assertNotFoundRead(150);
	}

	@Test
	public void writeOnMissingResouceNotFound() {
		assertNotFoundWrite(150, "Whatever you are...");
	}

	@Test
	public void executeOnMissingResourceNotFound() {
		assertNotFoundExecute(150, "Missing resource...");
	}

}
