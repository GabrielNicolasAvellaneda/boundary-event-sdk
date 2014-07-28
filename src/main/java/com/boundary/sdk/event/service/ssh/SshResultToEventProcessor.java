// Copyright 2014 Boundary, Inc.
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
package com.boundary.sdk.event.service.ssh;

import static com.boundary.sdk.event.service.ServiceCheckPropertyNames.SERVICE_TEST_INSTANCE;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boundary.camel.component.ssh.SshxConfiguration;
import com.boundary.camel.component.ssh.SshxResult;
import com.boundary.sdk.event.RawEvent;
import com.boundary.sdk.event.Severity;
import com.boundary.sdk.event.Status;
import com.boundary.sdk.event.service.ServiceTest;

public class SshResultToEventProcessor implements Processor {
	
	private static Logger LOG = LoggerFactory.getLogger(SshResultToEventProcessor.class);

	public SshResultToEventProcessor() {

	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		
		// Get our result instances and get the output of the SSH command
		SshxResult result = message.getBody(SshxResult.class);
		String output = result.getOutput();

		ServiceTest<SshxConfiguration,SshxServiceModel> serviceTest = message.getHeader(SERVICE_TEST_INSTANCE,ServiceTest.class);
		SshxConfiguration configuration = serviceTest.getConfiguration();
		SshxServiceModel model = serviceTest.getModel();

		//
		// Create the RawEvent and populate with values
		//
		
		RawEvent event = new RawEvent();
		
		// Set the creation to now!
		event.setCreatedAt(new Date());
		
		// Host gets set from the host we ran the SSH against.
		event.getSource().setRef(configuration.getHost());
		event.getSource().setType("host");
		

		event.addFingerprintField("service");
		event.addFingerprintField("service-test");
		event.addFingerprintField("hostname");
		
		// Add the required properties
		String hostname = configuration.getHost();
		String expectedOutput = model.getExpectedOutput();
		String serviceName = serviceTest.getServiceName();
		event.addProperty("command",configuration.getCommand());
		event.addProperty("expected-output",expectedOutput);
		event.addProperty("output",output);
		event.addProperty("service-test",serviceTest.getName());
		event.addProperty("service-test-type",serviceTest.getServiceTestType());
		event.addProperty("service",serviceName);
		event.addProperty("hostname",hostname);
		event.addProperty("time-out",configuration.getTimeout());
		
		// Tag the service that was tested
		event.addTag(serviceTest.getServiceName());
		event.addTag(hostname);
		
		// Generate our title of the event
		// TODO: Service test provides a template from the available data??
		event.setTitle(serviceName + " - " + serviceTest.getName());

		// Set Severity, Status, and Message of the event based on the matching of expected output
		LOG.info("output: {}, expectedOutput: {}",output,expectedOutput);
		if (output.matches(expectedOutput)) {
			event.setSeverity(Severity.INFO);
			event.setStatus(Status.CLOSED);
			event.setMessage("Received expected output: " + expectedOutput);
		}
		else {
			event.setSeverity(Severity.WARN);
			event.setStatus(Status.OPEN);
			event.setMessage("Received unexpected output");
		}
		
		// Set Sender
		event.getSender().setRef("Service Health Check");
		event.getSender().setType("Boundary Event SDK");
		
		LOG.debug("RawEvent: " + event);
		
		message.setBody(event);
	}
	
//	private void sshStatusToEvent(ServiceTest<SshxConfiguration,SshxServiceModel> serviceTest,SshxResult result,RawEvent event) {
//
//	}

	
//	private String getOutputString(ByteArrayInputStream inputStream) {
//		StringBuffer sb = new StringBuffer();
//		List<String> lines = getOutputToList(inputStream);
//		
//		for (String line : lines) {
//			sb.append(line);
//		}
//		
//		return sb.toString();
//	}
	
	private List<String> getOutputToList(ByteArrayInputStream inputStream) {
		List<String> lines = new ArrayList<String>();
	
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return lines;
	}
}
