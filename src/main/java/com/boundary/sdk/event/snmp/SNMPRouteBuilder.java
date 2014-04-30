package com.boundary.sdk.event.snmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boundary.sdk.event.RawEvent;
import com.boundary.sdk.event.UDPRouteBuilder;

/**
 * Route builder that receives SNMP messages into {@link RawEvent}
 * and then sends to Boundary event route.
 * 
 * @author davidg
 *
 */
public class SNMPRouteBuilder extends UDPRouteBuilder {
	
	private static Logger LOG = LoggerFactory.getLogger(SNMPRouteBuilder.class);
	
	private final int DEFAULT_SNMP_PORT=162;
	
	private String mibRepositoryPath;
	private String license;

	/**
	 * Default constructor
	 */
	public SNMPRouteBuilder() {
		this.port = DEFAULT_SNMP_PORT;
		this.mibRepositoryPath="";
		this.license = "";
	}
	
	/**
	 * Sets the path to the directory
	 * to the compiled MIBs.
	 * 
	 * @param path
	 */
	public void setMibRepository(String path) {
		this.mibRepositoryPath = path;
	}
	
	/**
	 * Returns the currently configured path
	 * to the compiled MIBs
	 * 
	 * @return {@link String} with the path to the MIB repository
	 */
	public String getMibRepository() {
		return this.mibRepositoryPath;
	}
	
	/**
	 * Sets the SNMP4J-SMI license key
	 * 
	 * @param license
	 */
	public void setLicense(String license) {
		this.license = license;
	}
	
	/**
	 * Returns the current value of the SNMP4J-SMI license key
	 * 
	 * @return {@link String} contain the configured license key
	 */
	public String getLicense() {
		return this.license;
	}
	
	/**
	 * Configuration for the SNMP route 
	 */
	@Override
	public void configure() {
		// TBD, verify if I have to provide host property
		// TBD, Should the trap listener be bound to port 0.0.0.0??
		// TBD, Support of TCP??
		String uri = "snmp:127.0.0.1:" + this.port + "?protocol=udp&type=TRAP";
		from(uri)
		.startupOrder(startUpOrder)
		.routeId(this.routeId)
		.to("log:" + this.getClass().toString() + "?level=INFO&showBody=true&showHeaders=true")
		.process(new SNMPToEventProcessor(mibRepositoryPath,license))
		.marshal().serialization()
		.to(toUri)
		;
	}
}
