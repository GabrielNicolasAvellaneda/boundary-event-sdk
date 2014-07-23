package com.boundary.sdk.event.snmp;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MIBCompilerTest {
	
	private MIBCompiler mibCompiler;
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		mibCompiler = new MIBCompiler();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private String [] generateArgs(String mibs,String repo) {
		return this.generateArgs(mibs,repo,"standard");
	}
	
	private String [] generateArgs(String mibs,String repo,String strictness) {
		ArrayList<String> s = new ArrayList<String>();
		s.add("-m");
		s.add(mibs);
		s.add("-r");
		s.add(repo);
		s.add("-s");
		s.add(strictness);
		String []args = new String[s.size()];
		s.toArray(args);
		return args;
	}


	@Test
	public void testCompileStandardMIBs() throws IOException {
		File mibDir= folder.newFolder("mib-repo");
		String [] args = generateArgs("src/test/snmp/SMIv2",mibDir.toString());
		mibCompiler.execute(args);
	}
	
	//@Ignore("Requires SNMP4J License")
	@Test
	public void testCompileEnterpriseMIB() throws IOException {
		File mibDir= folder.newFolder("mib-repo");
		System.setProperty(MIBCompiler.LICENSE,System.getenv("BOUNDARY_MIB_LICENSE"));
		String [] args = generateArgs("src/test/snmp/BOUNDARY-MIB.txt",mibDir.toString(),"lenient");
		mibCompiler.execute(args);
	}
	

}
