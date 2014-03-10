package com.boundary.sdk;

import org.apache.camel.builder.RouteBuilder;

public class SNMPRoute extends RouteBuilder {

	public SNMPRoute() {
	}
	
	@Override
	public void configure() {
		//RouteBuilder r = new RouteBuilder();
		// body().append(getBody()).
		from("snmp:127.0.0.1:1162?protocol=udp&type=TRAP")
		.to("file://?fileName=snmptrap.log")
		.to("direct:event");
	}
}
