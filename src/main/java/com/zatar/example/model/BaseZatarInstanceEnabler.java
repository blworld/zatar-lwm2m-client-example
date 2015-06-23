package com.zatar.example.model;

import java.util.Map;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;

public class BaseZatarInstanceEnabler extends BaseInstanceEnabler {

	protected Map<Integer, ResourceEnabler> resources;

}
