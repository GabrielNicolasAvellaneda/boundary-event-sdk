package com.boundary.sdk.event;

import static org.junit.Assert.*;

import com.boundary.sdk.event.Status;

import org.junit.Test;

public class StatusTest {
	
	
	@Test
	public void testACKNOWLEDGED() {
		String s = "ACKNOWLEDGED";
		Status status = Status.fromString(s);
		assertTrue("Test OK status",status.toString() == s);
	}


	@Test
	public void testOK() {
		String s = "OK";
		Status status = Status.fromString(s);
		assertTrue("Test OK status",status.toString() == s);
	}
}
