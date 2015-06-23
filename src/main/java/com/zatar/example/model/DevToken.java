package com.zatar.example.model;

import java.util.HashMap;
import java.util.Map;

public class DevToken extends BaseZatarInstanceEnabler {

	public DevToken() {
		super(createEnablers());
	}

	private static Map<Integer, ResourceEnabler> createEnablers() {
		final HashMap<Integer, ResourceEnabler> resources = new HashMap<>();
		resources.put(0, new ReadOnlyResourceEnabler(0, "initial-dev-token"));
		return resources;
	}

}
