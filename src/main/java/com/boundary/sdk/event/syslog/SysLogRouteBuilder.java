package com.boundary.sdk.event.syslog;

import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.component.syslog.Rfc3164SyslogDataFormat;

import com.boundary.sdk.event.BoundaryRouteBuilder;
import com.boundary.sdk.event.UDPRouteBuilder;

public class SysLogRouteBuilder extends UDPRouteBuilder {
	
	private final String DEFAULT_SYSLOG_ROUTE_NAME="SYSLOG-ROUTE";
	private final int DEFAULT_SYSLOG_PORT = 1514;
	

	/**
	 * Constructor
	 */
	public SysLogRouteBuilder() {
		this.toUri = BoundaryRouteBuilder.DEFAULT_EVENT_TO_URI;
		this.routeId = DEFAULT_SYSLOG_ROUTE_NAME;
		this.port = DEFAULT_SYSLOG_PORT;
	}
	
	/**
	 * Builds route for handling message forwarded from syslog.
	 */
	@Override
	public void configure() {
		String uri = "netty:udp://127.0.0.1:" + port + "?sync=false&allowDefaultCodec=false";

        DataFormat syslogDataFormat = new Rfc3164SyslogDataFormat();
		RouteDefinition routeDefinition = from(uri);
		
		routeDefinition
		.startupOrder(startUpOrder)
		.routeId(routeId)
		.unmarshal(syslogDataFormat)
		.process(new SyslogToEventProcessor())
		.marshal().serialization()
        .to(toUri)
        ;
	}
}
