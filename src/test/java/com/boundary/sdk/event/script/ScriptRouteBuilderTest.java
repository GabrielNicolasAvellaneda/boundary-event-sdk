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
package com.boundary.sdk.event.script;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Calendar.Builder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.syslog.SyslogFacility;
import org.apache.camel.component.syslog.SyslogMessage;
import org.apache.camel.component.syslog.SyslogSeverity;
import org.apache.camel.component.snmp.SnmpMessage;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.snmp4j.PDU;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.boundary.sdk.event.RawEvent;
import com.boundary.sdk.event.Severity;
import com.boundary.sdk.event.Status;
import com.boundary.sdk.event.snmp.SnmpMessageEvent;
import com.boundary.sdk.event.syslog.SyslogMessageEvent;

public class ScriptRouteBuilderTest extends CamelSpringTestSupport {
	
	private static final double DELTA = 1e-15;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Produce(uri = "direct:in")
	private ProducerTemplate in;

	@EndpointInject(uri = "mock:out")
	private MockEndpoint out;

	private SyslogMessageEvent syslogMessageEvent;

	private Date now;

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext(
				"META-INF/spring/test-script-route-builder.xml");
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		Builder dtBuilder = new Calendar.Builder();
		Calendar cal = dtBuilder.build();
		this.now = cal.getTime();
		
		SyslogMessage syslogMessage = new SyslogMessage();
		syslogMessage.setFacility(SyslogFacility.FTP);
		syslogMessage.setHostname("myweb.com");
		syslogMessage.setLocalAddress("172.16.0.1");
		syslogMessage.setLogMessage("myweb.com not responding to ping");
		syslogMessage.setRemoteAddress("10.10.62.36");
		syslogMessage.setSeverity(SyslogSeverity.EMERG);
		syslogMessage.setTimestamp(cal);
		
		syslogMessageEvent = new SyslogMessageEvent(syslogMessage);

	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		this.syslogMessageEvent = null;
	}

	@Test
	public void testLanguageName() {
		String expectedLanguageName = "groovy";
		ScriptRouteBuilder builder = new ScriptRouteBuilder();
		builder.setLanguageName(expectedLanguageName);

		assertEquals("check language name", expectedLanguageName,
				builder.getLanguageName());
	}

	@Test
	public void testContentCache() {
		boolean expectedContentCache = false;
		ScriptRouteBuilder builder = new ScriptRouteBuilder();
		builder.setContentCache(expectedContentCache);
		assertEquals("check content cache", expectedContentCache,
				builder.getContentCache());
	}

	@Test
	public void testCacheScript() {
		boolean expectedCacheScript = false;
		ScriptRouteBuilder builder = new ScriptRouteBuilder();
		builder.setCacheScript(expectedCacheScript);
		assertEquals("check cache script", expectedCacheScript,
				builder.getCacheScript());
	}

	@Test
	public void testTransform() {
		boolean expectedTransform = false;
		ScriptRouteBuilder builder = new ScriptRouteBuilder();
		builder.setTransform(expectedTransform);
		assertEquals("check transform script", expectedTransform,
				builder.getTransform());

	}

	@Test
	public void testScript() {
		String expectedScript = "classpath:example.jons";
		ScriptRouteBuilder builder = new ScriptRouteBuilder();
		builder.setScript(expectedScript);
		assertEquals("check script", expectedScript, builder.getScript());

	}

	@Test
	public void testToUri() {
		String expectedScript = "file:example.json";
		String expectedLanguageName = "javascript";
		boolean expectedCacheScript = false;
		boolean expectedContentCache = false;
		boolean expectedTransform = false;
		ScriptRouteBuilder builder = new ScriptRouteBuilder();
		builder.setScript(expectedScript);
		builder.setLanguageName(expectedLanguageName);
		builder.setCacheScript(expectedCacheScript);
		builder.setContentCache(expectedContentCache);
		builder.setTransform(expectedTransform);
		builder.configure();

		assertEquals("check toUri",
				"language:javascript:file:example.json?transform=false&contentCache=false&cacheScript",
				builder.getLanguageToUri());

	}

	@Test
	public void testSyslogToEvent() throws InterruptedException {
		out.expectedMessageCount(1);
		in.sendBodyAndHeaders(this.syslogMessageEvent,setScriptHeader("classpath:syslog-to-raw_event.js"));
		out.assertIsSatisfied();
		
		List<Exchange> exchanges = out.getExchanges();
		assertEquals("check exchange count",1,exchanges.size());
		Exchange exchange = exchanges.get(0);
		Message message = exchange.getIn();
		RawEvent e = message.getBody(RawEvent.class);
		assertNotNull("check event for not null",e);
		
		SyslogMessageEvent sme = this.syslogMessageEvent;
		
		assertEquals("check facility property",
				this.syslogMessageEvent.getFacility(),
				e.getProperties().get("facility"));
		assertTrue("check facility tag exists",
				e.getTags().contains(sme.getFacility()));
		
		assertEquals("check hostname property",
				sme.getHostname(),
				e.getProperties().get("hostname"));
		assertTrue("check hostname tag exists",
				e.getTags().contains(sme.getHostname()));
		assertEquals("check source ref",sme.getHostname(),e.getSource().getRef());
		assertEquals("check source type","host",e.getSource().getType());
		
		assertEquals("check message",sme.getLogMessage(),e.getMessage());
		assertEquals("check message property",sme.getLogMessage(),
				e.getProperties().get("message"));
		
		assertEquals("check remote address property",sme.getRemoteAddress(),
				e.getProperties().get("remote_address"));
		assertTrue("check remote address tag exists",
				e.getTags().contains(sme.getRemoteAddress()));
		
		assertEquals("check severity",Severity.CRITICAL,e.getSeverity());
		
		assertEquals("check status",Status.OPEN,e.getStatus());
		
		assertEquals("check number of fingerprint fields",3,e.getFingerprintFields().size());
		List<String> fp = new ArrayList<String>();
		fp.add("hostname");
		fp.add("facility");
		fp.add("message");
		assertTrue("check fingerprint fields in event",e.getFingerprintFields().containsAll(fp));
		assertTrue("check fingerprint fields in array",fp.containsAll(e.getFingerprintFields()));
		
		assertEquals("check createdAt",this.now,e.getCreatedAt());
		
		assertEquals("check title","Syslog message from: " + sme.getHostname(),e.getTitle());

		assertEquals("check sender ref","Syslog",e.getSender().getRef());
		assertEquals("check sender type","Boundary Event SDK",e.getSender().getType());
	}
	
	private PDU createV2Trap() {
		PDU pdu = new PDU();
		
		pdu.setType(PDU.TRAP);
		
		return pdu;
	}
	
	@Test
	public void testSNMPTrapToEvent() throws InterruptedException {
		PDU pdu = createV2Trap();
		SnmpMessageEvent snmpMessage = new SnmpMessageEvent(pdu);

		out.expectedMessageCount(1);
		
		out.getExchanges();
		in.sendBodyAndHeaders(snmpMessage,setScriptHeader("classpath:snmp-trap-to-event.js"));
		out.assertIsSatisfied();
		
		List<Exchange> exchanges = out.getExchanges();
		assertEquals("check exchange count",1,exchanges.size());
		Exchange exchange = exchanges.get(0);
		Message message = exchange.getIn();
		RawEvent e = message.getBody(RawEvent.class);
		assertNotNull("check event for not null",e);

	}
	
	private Map<String,Object> setScriptHeader(String script) {
		Map <String,Object> headers = new HashMap<String,Object>();
		headers.put("CamelLanguageScript",script);

		return headers;
	}
	
	@Test
	public void testScriptHeader() throws InterruptedException {
		out.expectedMessageCount(1);
		
		out.getExchanges();
		in.sendBodyAndHeaders(this.syslogMessageEvent,setScriptHeader("classpath:test-script-route-builder.json"));
		out.assertIsSatisfied();
	}
	
	@Test
	public void testScriptNotFound() throws InterruptedException  {
		exception.expect(CamelExecutionException.class);
		
		out.expectedMessageCount(1);
		
		out.getExchanges();
		in.sendBodyAndHeaders(this.syslogMessageEvent,setScriptHeader("classpath:foo.js"));
		out.assertIsSatisfied();
		
	}
	
	@Test
	public void testScriptEvent() throws InterruptedException {
		Builder builder = new Calendar.Builder();
		builder.setDate(2010, 5, 20);
		builder.setTimeOfDay(13, 0, 0,0);
		Calendar cal = builder.build();
		
		out.expectedMessageCount(1);
		ScriptEvent event = new ScriptEvent();
		
		out.getExchanges();
		in.sendBodyAndHeaders(event,setScriptHeader("classpath:test-script-event.js"));
		out.assertIsSatisfied();
		
		List<Exchange> exchanges = out.getExchanges();
		assertEquals("check exchange count",1,exchanges.size());
		Exchange exchange = exchanges.get(0);
		Message message = exchange.getIn();
		ScriptEvent newEvent = message.getBody(ScriptEvent.class);
		assertNotNull("check for not null",newEvent);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("red", "one");
		map.put("green","two");
		map.put("blue","three");
		
		List<String> list = new ArrayList<String>();
		list.add("alex");
		list.add("geddy");
		list.add("neal");
		
		ScriptEnum enumeration = ScriptEnum.YELLOW;
		
		ScriptObject object = new ScriptObject();
		object.setDate(cal.getTime());
		object.setString("Goodbye");
		object.setList(list);
		object.setMap(map);
		
		assertEquals("check date",cal.getTime(),newEvent.getDate());
		assertEquals("check string","Hello World!",newEvent.getString());
		assertEquals("check int",37,newEvent.getInteger());
		assertEquals("check decimal",9.81,newEvent.getDecimal(),DELTA);
		assertEquals("check map",map,newEvent.getMap());
		assertEquals("check list",list,newEvent.getList());
		assertEquals("check enumeration",enumeration,newEvent.getEnumeration());
		assertEquals("check object",object,newEvent.getObject());

	}

}
