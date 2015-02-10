// Copyright 2014-2015 Boundary, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.boundary.sdk.event.snmp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class SNMPRouteTest {

        /**
         *
         */
        @Test
        public void testBindAddress() {
            String expectedBindAddress = "1.2.3.4";
	    SnmpTrapRouteBuilder builder = new SnmpTrapRouteBuilder();
            builder.setBindAddress(expectedBindAddress);
            assertEquals("check bind address",expectedBindAddress,builder.getBindAddress());
        }
	
	@Test
	public void testMibRepository() {
		String expectedPath = "foobar";
		SnmpTrapRouteBuilder builder = new SnmpTrapRouteBuilder();
		
		builder.setMibRepository(expectedPath);
		assertEquals("Check license",expectedPath,builder.getMibRepository());
	}

	@Test
	public void testLicense() {
		String expectedLicense = "foobar";
		SnmpTrapRouteBuilder builder = new SnmpTrapRouteBuilder();
		
		builder.setLicense(expectedLicense);
		assertEquals("Check license",expectedLicense,builder.getLicense());
	}

}
