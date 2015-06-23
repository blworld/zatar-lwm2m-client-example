package com.zatar.example.model;

import java.util.HashMap;
import java.util.Map;

public class Device extends BaseZatarInstanceEnabler {

	private static final String MANUFACTURER = "Zatar Example Devices Inc.";
	private static final String MODEL = "zatarex1";
	private static final String SERIAL_NUMBER = "ZE98765";
	private static final String INITIAL_UTC_OFFSET = "+05";

	public Device() {
		this.resources = createResourceEnablers();
	}

	private static Map<Integer, ResourceEnabler> createResourceEnablers() {
		final Map<Integer, ResourceEnabler> resources = new HashMap<>();
		resources.put(0, new ReadOnlyResourceEnabler(0, MANUFACTURER));
		resources.put(1, new ReadOnlyResourceEnabler(1, MODEL));
		resources.put(2, new ReadOnlyResourceEnabler(2, SERIAL_NUMBER));
		resources.put(4, new ExecutableResourceEnabler());
		resources.put(14, new ReadWriteResourceEnabler(14, INITIAL_UTC_OFFSET));
		return resources;
	}

}
