/**
 * 
 */
package com.boundary.sdk.event.snmp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

public class OidLookupTest {
	
    private static final Logger LOG = LoggerFactory.getLogger(OidLookupTest.class);

	
	private static SmiSupport smi;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String ber ="TCP-MIB::tcpCurrEstab.0";
		LOG.info("length: " + ber.length());
		
		smi = new SmiSupport();
		smi.setLicense(System.getenv("BOUNDARY_MIB_LICENSE"));
		smi.setRepository(System.getenv("BOUNDARY_MIB_REPOSITORY"));
		smi.initialize();
		smi.loadModules();

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		OID oid = new OID(".1.3.6.1.2.1.6.9.0");
		LOG.info("oid.getBERLength(): {}",oid.getBERLength());
	}
}
