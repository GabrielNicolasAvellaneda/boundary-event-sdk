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
package com.boundary.sdk.event;

import java.io.File;

import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.boundary.sdk.event.BoundaryEventRouteBuilder;
import com.boundary.sdk.event.BoundaryRouteBuilder;
import com.boundary.sdk.event.RawEvent;

import static org.junit.Assume.*;

/**
 *
 * @version $Revision$
 */
public class RawEventToBoundaryEvent extends CamelTestSupport {
	
    @Produce(uri = TEST_IN)
    private ProducerTemplate producerTemplate;

	
	private static final String TEST_IN="direct:in";
	
	private static final String BOUNDARY_API_KEY="";
	private static final String BOUNDARY_ORG_ID="";
	
	@Before
    public void setUp() throws Exception {
        super.setUp();
    }

	@Ignore("Requires access to Boundary Server")
    @Override
    protected RouteBuilder[] createRouteBuilders() throws Exception {
    	RouteBuilder[] routes = new RouteBuilder[2];
    	
    	// Create the Boundary Event Route
		BoundaryEventRouteBuilder boundary = new BoundaryEventRouteBuilder();
		// Configure our properties
		boundary.setPassword(System.getenv("BOUNDARY_API_TOKEN"));
		boundary.setUser(System.getenv("BOUNDARY_EMAIL"));
		boundary.setFromUri(BoundaryEventRouteBuilder.DEFAULT_EVENT_TO_URI);
		
		routes[0] = boundary;
		routes[1] = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(TEST_IN)
                .marshal().serialization()
                .to(BoundaryRouteBuilder.DEFAULT_EVENT_TO_URI);
            }
        };
        
        return routes;
    }

	@Ignore
    @Test
    public void testDefaultEvent() throws Exception {
    	RawEvent event = RawEvent.getDefaultEvent();
    	producerTemplate.sendBody(event);
        Thread.sleep(2000);     
    }
}
