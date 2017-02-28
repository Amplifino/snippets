package com.amplifino.ds.issue.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

import com.amplifino.ds.issue.Destination;

@Component(configurationPolicy=ConfigurationPolicy.REQUIRE)
@Designate(ocd=DestinationConfiguration.class, factory=true)
public class DestinationImpl implements Destination {
	
	private DestinationConfiguration config;
	
	@Activate
	public void activate(DestinationConfiguration config) {
		this.config = config;
	}
	
	@Override
	public String getName() {
		return config.name();
	}
	
	@Override
	public String toString() {
		return "Destination " + getName();
	}

}
