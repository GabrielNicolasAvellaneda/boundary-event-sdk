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

import java.io.IOException;
import java.util.Date;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;

import com.snmp4j.smi.SmiManager;

public class TestLookup {
	
	public static void main(String []args) throws IOException {
		SmiSupport support = new SmiSupport();
		SmiManager smiManager = support.getSmiManager();
		
		support.setRepository("src/main/resources/mibrepository");
		support.setLicense("a8 29 19 b4 66 e5 4c 1f / LlSFSvNS");
		support.initialize();
		
		smiManager = support.getSmiManager();
		
		String [] modules = smiManager.listModules();
//		System.out.println(modules.length);
//		
//		System.out.println(new Date());
		
		for (String name :modules) {
			//System.out.println("Loading module: " + name);
			smiManager.loadModule(name);
		}
		
		System.out.println(new Date());
		
		String[] oids = {
				/* "1.3.6.1",
				"1.3.6.1.4.1.3.1.1",
				"1.3.6.1.2.1.1.3.0",
				"1.3.6.1.6.3.1.1.4.1.0",
				"1.3.6.1.6.3.1.1.4.1",
				"1.3.6.1.2.1.1.5.0",
				"1.2.3.4.5",
				"1.3.6.1.2.1.1.1.0",
				"0.0", */
				"enterprises.24927.8.1.1"};
		
		OID oid = new OID();
		for (String o :oids) {
			oid.setValue(o);
			System.out.println(o + " = " + oid);
			System.out.println(oid.getSyntaxString());
		}
		
//		System.out.println(SnmpConstants.snmpTrapOID);
//		System.out.println(SnmpConstants.snmpTrapOID.toDottedString());
//		System.out.println(new OID(SnmpConstants.snmpTrapOID.toDottedString() + ".3"));
//		System.out.print(SnmpConstants.linkDown.toDottedString());

	}

}
