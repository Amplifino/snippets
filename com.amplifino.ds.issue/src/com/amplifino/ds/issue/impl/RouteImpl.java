package com.amplifino.ds.issue.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.annotations.Designate;

import com.amplifino.ds.issue.Destination;
import com.amplifino.ds.issue.Route;

@Component(configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
@Designate(ocd=RouteConfiguration.class, factory=true)
public class RouteImpl implements Route {
	
	private RouteConfiguration config;
	private Filter destinationFilter;
	private final List<ServiceReference<Destination>> destinations = new CopyOnWriteArrayList<>();
	
	@Reference(name="destinations", cardinality=ReferenceCardinality.AT_LEAST_ONE) 
	public void addDestination(ServiceReference<Destination> destination) {
		this.destinations.add(destination);
	}
	
	@Activate
	public void activate(RouteConfiguration config, BundleContext context) throws InvalidSyntaxException  {
		this.config = config;
		this.destinationFilter = context.createFilter(config.destinations_target());
		if (!checkFilter()) {
			System.out.format("DS injected invalid destination list containing %d destinations\n", destinations.size());
		}
	}

	@Override
	public boolean checkFilter() {
		return destinations.stream().allMatch(destinationFilter::match);
	}

}
