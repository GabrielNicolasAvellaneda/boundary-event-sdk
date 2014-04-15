package com.boundary.sdk;

import java.io.File;
import java.util.ArrayList;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SuppressWarnings("deprecation")
public class RawEventToJSONTest extends CamelSpringTestSupport {
	
    @Produce(uri = "direct:in")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint mockOut;

	@Override
	protected AbstractXmlApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/boundary-event-to-json.xml");
	}

	@Test
	public void testSetTitle() throws Exception {
		mockOut.setExpectedMessageCount(1);
		RawEvent event = new RawEvent();
		
		event.setTitle("Sample Event");		
		producerTemplate.sendBody(event);
		assertMockEndpointsSatisfied();
	}
	
	@Test
	public void testEmptyJSON() throws Exception {
		mockOut.setExpectedMessageCount(1);
		RawEvent event = new RawEvent();
		
		producerTemplate.sendBody(event);

		mockOut.equals("{}");
	}
}