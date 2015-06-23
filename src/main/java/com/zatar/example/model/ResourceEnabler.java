package com.zatar.example.model;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ValueResponse;

public interface ResourceEnabler {

	ValueResponse read();
	LwM2mResponse write(LwM2mResource node);
	LwM2mResponse execute(byte[] params);

}