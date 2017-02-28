package com.amplifino.ds.issue.test;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

import com.amplifino.ds.issue.Route;

/**
 * This class can either be used as a bnd osgi test,
 * or as a gogo shell command to configure two destinations and routes. 
 *
 */
@Component(service=RouteTest.class, property={"osgi.command.scope=dsissue", "osgi.command.function=configure"}) 
public class RouteTest {
	
	private final BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
	
	@Test
	public void test() throws IOException, InvalidSyntaxException, InterruptedException {
		configure();
		Thread.sleep(1000L);
		List<Route> routes = getServices(Route.class);
		Assert.assertEquals(2,  routes.size());
		Assert.assertTrue(routes.stream().allMatch(Route::checkFilter));
	}
	
	public void configure() throws IOException, InterruptedException {
		ConfigurationAdmin configurationAdmin = getService(ConfigurationAdmin.class);
		addDestination(configurationAdmin, "destination1");
		addDestination(configurationAdmin, "destination2");
		addRoute(configurationAdmin, "route1" , "(name=destination1)");
		addRoute(configurationAdmin, "route1" , "(name=destination2)");
		System.out.println("Configured two destinations and two routes");
	}
	
	private void addDestination(ConfigurationAdmin configurationAdmin, String name) throws IOException {
		Configuration configuration = configurationAdmin.createFactoryConfiguration("com.amplifino.ds.issue.impl.DestinationImpl", "?");
		Dictionary<String, Object> props = new Hashtable<>();
		props.put("name", name);
		configuration.update(props);
	}
	
	private void addRoute(ConfigurationAdmin configurationAdmin, String name, String filter) throws IOException {
		Configuration configuration = configurationAdmin.createFactoryConfiguration("com.amplifino.ds.issue.impl.RouteImpl", "?");
		Dictionary<String, Object> props = new Hashtable<>();
		props.put("name", name);
		props.put("destinations.target", filter);
		configuration.update(props);
	}
	
	private <T> T getService(Class<T> clazz) throws InterruptedException {
		ServiceTracker<T, T> tracker = new ServiceTracker<>(context, clazz, null);
		tracker.open();
		return Objects.requireNonNull(tracker.waitForService(1000L));
	}
	
	private <T> List<T> getServices(Class<T> clazz) throws InvalidSyntaxException {
		return context.getServiceReferences(clazz, null).stream()
			.map(context::getService)
			.collect(Collectors.toList());
	}

}
