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

import java.io.File;
import java.util.Vector;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.snmp.SnmpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.snmp4j.smi.SmiManager;

public abstract class SnmpMessageProcessor implements Processor{
	
	private static Logger LOG = LoggerFactory.getLogger(SnmpMessageProcessor.class);	
	
	public static final String BOUNDARY_SMI_MANAGER="boundary.snmp.smi.manager";
	
    private SmiSupport smi;
        
    protected SmiManager getSmiManager() {
    	return this.smi.getSmiManager();
    }
	
	public SnmpMessageProcessor(String repositoryPath, String license) {
		// If the repositoryPath is null or zero length 
		// then bypass configuring the structured management information.
		if (repositoryPath != null && repositoryPath.length() > 0) {
			this.smi = new SmiSupport();
			this.smi.setLicense(license);
			File mibDirectory = new File(repositoryPath);
			this.smi.setRepository(mibDirectory.getAbsolutePath());
			this.smi.initialize();
			this.smi.loadModules();
			LOG.info("SMI initialized");
		}
		else {
			throw new IllegalStateException("MIB repository path is null or empty.");
		}
	}
}
