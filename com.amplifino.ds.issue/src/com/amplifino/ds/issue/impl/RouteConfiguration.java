package com.amplifino.ds.issue.impl;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface RouteConfiguration {

	String name();
	String destinations_target();
}
