package com.boundary.sdk.event.snmp;

import org.apache.camel.LoggingLevel;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spi.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.mp.SnmpConstants;

import static org.apache.camel.LoggingLevel.*;


public class SnmpPollerRouteBuilder extends SNMPRouteBuilder {
	
	public final static String BOUNDARY_HOSTNAME="boundary.hostname";
	
	private static Logger LOG = LoggerFactory.getLogger(SnmpPollerRouteBuilder.class);
	
	private String communityRead;
	private String oids;
	private int delay;
	
	public SnmpPollerRouteBuilder() {
		communityRead="piston";
		delay = 5;
		setPort(161);
		setToUri("seda:metric-translate");
	}

	public void setOids(String oids) {
		this.oids = oids;
	}
	public String getOids() {
		return this.oids;
	}

	public void setCommunityRead(String communityRead) {
		this.communityRead = communityRead;
	}
	public String getCommunityRead() {
		return this.communityRead;
	}
	

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	private String getUri() {
		StringBuffer sb = new StringBuffer();
		sb.append("snmp:");
		sb.append(getBindAddress());
		sb.append(":");
		sb.append(getPort());
		sb.append("?protocol=udp");
		sb.append("&type=POLL");
		sb.append("&oids=" + getOids());
		sb.append("&snmpVersion=" + SnmpConstants.version2c);
		sb.append("&snmpCommunity=" + getCommunityRead());
		sb.append("&delay="+getDelay());
		LOG.debug("from: " + sb.toString());
		return sb.toString();
	}
	/**
	 * Configuration for the SNMP route 
	 */
	@Override
	public void configure() {
		DataFormat jaxb = new JaxbDataFormat("com.boundary.sdk.event.snmp");
		String uri = getUri();
		
		RouteDefinition routeDefinition = from(uri)
		.routeId(this.routeId)
		.setHeader(BOUNDARY_HOSTNAME, constant(this.getBindAddress()))
		.log(DEBUG,"body: ${body}")
		.to(this.getToUri())
		.unmarshal(jaxb)
		.to("mock:snmp-poller-out");
		
		// Setup startup order only if it had been configured
		if (this.getStartUpOrder() != 0) {
			routeDefinition.startupOrder(this.getStartUpOrder());
		}
	}
	
	public static void main(String [] args) {
		SnmpPollerRouteBuilder poller = new SnmpPollerRouteBuilder();
		
		System.out.println(poller.getUri());
	}

}
