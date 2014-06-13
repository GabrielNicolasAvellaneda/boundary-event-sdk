/**
 * 
 */
package com.boundary.sdk.event.service;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.boundary.camel.component.ping.PingConfiguration;

/**
 * @author davidg
 *
 */
public class ServiceTestAggregateTest extends CamelSpringTestSupport  {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testSplitRequest() throws InterruptedException {
		MockEndpoint endPoint = getMockEndpoint("mock:service-check-request-split");
		endPoint.setExpectedMessageCount(3);
		
		Map<String,Object> properties = new HashMap<String,Object>();
		ServiceCheckRequest request = new ServiceCheckRequest();
		properties.put("serviceCheckRequest", request.getRequestId());
		PingConfiguration configuration = new PingConfiguration();
		configuration.setHost("localhost");
		request.addServiceTest(new ServiceTest<PingConfiguration>("test1",request.getRequestId(),configuration));
		request.addServiceTest(new ServiceTest<PingConfiguration>("test2",request.getRequestId(),configuration));
		request.addServiceTest(new ServiceTest<PingConfiguration>("test3",request.getRequestId(),configuration));
		
		template.sendBodyAndHeaders("direct:service-check-request-in",request, properties);
		
		endPoint.assertIsSatisfied();
	}

	@Ignore
	@Test
	public void testAggregate() throws InterruptedException {
		MockEndpoint endPoint = getMockEndpoint("mock:service-check-aggregate-out");
		MockEndpoint pingEndPoint = getMockEndpoint("mock:ping-out");

		endPoint.setExpectedMessageCount(1);
		pingEndPoint.setExpectedMessageCount(3);
		Map<String,Object> properties = new HashMap<String,Object>();
		
		ServiceCheckRequest request = new ServiceCheckRequest();
		properties.put("serviceCheckRequest",request.getRequestId());
		PingConfiguration configuration = new PingConfiguration();
		configuration.setHost("localhost");
		request.addServiceTest(new ServiceTest<PingConfiguration>("test1",request.getRequestId(),configuration));
		request.addServiceTest(new ServiceTest<PingConfiguration>("test2",request.getRequestId(),configuration));
		request.addServiceTest(new ServiceTest<PingConfiguration>("test3",request.getRequestId(),configuration));
		
		template.sendBodyAndHeaders("direct:service-check-request-in",request, properties);
		
		endPoint.assertIsSatisfied();
		pingEndPoint.assertIsSatisfied();
	}
	
	@Ignore
	@Test
	public void testPingCheck() throws InterruptedException {
		MockEndpoint endPoint = getMockEndpoint("mock:ping-out");
		endPoint.setExpectedMessageCount(1);
		
		PingConfiguration configuration = new PingConfiguration();
		
		configuration.setHost("localhost");
		
		template.sendBody("seda:ping-check", configuration);
		
		endPoint.assertIsSatisfied();
	}

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/service-check-aggregate-test.xml");
	}

}
