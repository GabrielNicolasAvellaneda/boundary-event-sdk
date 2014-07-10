package com.boundary.sdk.event.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boundary.camel.component.ping.PingConfiguration;
import com.boundary.camel.component.port.PortConfiguration;
import com.boundary.sdk.event.BoundaryEventRouteBuilder;
import com.boundary.sdk.event.service.ServiceCheckRequest;
import com.boundary.sdk.event.service.ServiceTest;

public class ServiceCheckQuery implements Processor {
	
	private static Logger LOG = LoggerFactory.getLogger(ServiceCheckQuery.class);


	public ServiceCheckQuery() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Map<String,Object> map = message.getBody(Map.class);
		//TODO: Place to put these constants like serviceCheckId? Standard format, e.g. all lower case??
		int serviceCheckId = Integer.parseInt(map.get("serviceCheckId").toString());
		
		LOG.info(map.get("serviceCheckId").getClass().toString());
		
		LOG.info("serviceCheckId = " + serviceCheckId);
		LOG.info(map.toString());
		
		String sql = "select now()";

		message.setBody(sql);
	}
}
