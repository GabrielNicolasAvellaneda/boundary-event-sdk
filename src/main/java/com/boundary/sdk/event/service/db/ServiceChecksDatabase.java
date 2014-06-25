package com.boundary.sdk.event.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boundary.camel.component.ping.PingConfiguration;
import com.boundary.camel.component.port.PortConfiguration;
import com.boundary.camel.component.ssh.SshxConfiguration;
import com.boundary.sdk.event.BoundaryEventRouteBuilder;
import com.boundary.sdk.event.service.ServiceCheckRequest;
import com.boundary.sdk.event.service.ServiceTest;

public class ServiceChecksDatabase implements Processor {
	
	private static Logger LOG = LoggerFactory.getLogger(ServiceChecksDatabase.class);


	public ServiceChecksDatabase() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		List<Map<String, Object>> list = message.getBody(List.class);
		for (Map<String,Object> row : list) {
			LOG.info(row.toString());
		}		
		String sdnDirectorHost = "192.168.137.11";
		ServiceCheckRequest request = new ServiceCheckRequest();
		PingConfiguration sdnDirectorPingTest = new PingConfiguration();
		sdnDirectorPingTest.setHost(sdnDirectorHost);
		
		SshxConfiguration plumgridProcessTest = new SshxConfiguration();
		plumgridProcessTest.setHost(sdnDirectorHost);
		plumgridProcessTest.setCommand("status plumgrid");
		plumgridProcessTest.setExpectedOutput("^plumgrid start/running, process\\s\\d+\n");
		
		SshxConfiguration plumgridSalProcessTest = new SshxConfiguration();
		plumgridSalProcessTest.setHost(sdnDirectorHost);
		plumgridSalProcessTest.setCommand("status plumgrid-sal");
		plumgridSalProcessTest.setExpectedOutput("^plumgrid-sal start/running, process\\s\\d+\n");

		SshxConfiguration nginxProcessTest = new SshxConfiguration();
		nginxProcessTest.setHost(sdnDirectorHost);
		nginxProcessTest.setCommand("status nginx");
		nginxProcessTest.setExpectedOutput("^nginx start/running, process\\s\\d+\n");

		ServiceTest<PingConfiguration> pingSDNDirector = new ServiceTest<PingConfiguration>(
				"Ping SDN Director Host","ping","SDN Director",request.getRequestId(),sdnDirectorPingTest);
		request.addServiceTest(pingSDNDirector);
		
		ServiceTest<SshxConfiguration> sshPlumgridProcess = new ServiceTest<SshxConfiguration>(
				"Check plumgrid process status","ssh","SDN Director",request.getRequestId(),plumgridProcessTest);
		request.addServiceTest(sshPlumgridProcess);
		
		ServiceTest<SshxConfiguration> sshPlumgridSalProcess = new ServiceTest<SshxConfiguration>(
				"Check plumgrid-sal process status","ssh","SDN Director",request.getRequestId(),plumgridProcessTest);
		request.addServiceTest(sshPlumgridSalProcess);
		
		ServiceTest<SshxConfiguration> sshNginxProcess = new ServiceTest<SshxConfiguration>(
				"Check nginx process status","ssh","SDN Director",request.getRequestId(),plumgridProcessTest);
		request.addServiceTest(sshNginxProcess);

		message.setBody(request);
	}
}
