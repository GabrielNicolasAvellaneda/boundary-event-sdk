/**
 * Encapsulates the health check of a network service by:
 * <ol>
 * <li>PING - Uses ICMP ping to determine if the host is available<li>
 * <li>SOCKET - Opens a TCP socket of a well-known port
 * <li>HTTP - Makes an HTTP(S) request
 * </ol>
 */
package com.boundary.sdk.event.service;

import com.boundary.sdk.event.BoundaryRouteBuilder;

/**
 * @author davidg
 *
 */
public class PortRouteBuilder extends BoundaryRouteBuilder {
	
	private String host;
	private int port;
	private int delay;
	
	public PortRouteBuilder() {
		host = "localhost";
		port = 7;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return this.delay;
	}

	@Override
	public void configure() throws Exception {
        from("port://" + getHost() + ":" + getPort() + "/tcp?delay=" + getDelay())
        .process(new PortInfoToEventProcessor())
        .marshal().serialization()
        .to(getToUri());
	}
}
