package edu.gatech.cc.junit;
import static edu.gatech.cc.jcrasher.Assertions.check;

import java.io.File;
import java.io.IOException;

import junit.framework.TestSuite;

/*
 * CollectingTestSuite.java
 * 
 * Copyright 2005 Christoph Csallner and Yannis Smaragdakis.
 */


/**
 * Collects all compiled JUnit test cases in cnc-bin directory
 * (these are the testee's test cases, not the CnC-generated ones).
 * Then executes JUnit with these test cases.
 * 
 * Run from project's base directory
 */
public class CollectingTestSuite extends TestSuite {

	/**
	 * FIXME: Hardcoded directory name
	 */
	public static final String cncBinDir = "cnc-bin"; 
	
	protected static File getTestRoot(){
		File curDir = new File("./" +cncBinDir);  //project-basedir/cnc-bin
		check(curDir.exists());
		check(curDir.isDirectory());
		return curDir;
	}
	
	/**
	 * 
	 */
	public static String getTestRootName() {
		String res = null;
		try {
			res = getTestRoot().getCanonicalPath();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		try {
			suite = getTestSuite(getTestRoot(), suite, getTestRootName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return suite;
	}
	
	
	
	protected static TestSuite getTestSuite(File file, TestSuite result, String root) 
	throws IOException, ClassNotFoundException {		
		if (file.isDirectory()) {
			for (int i=0; i<file.listFiles().length; i++) {
				result = getTestSuite(file.listFiles()[i], result, root);
			}
		}		
		if (file.isFile() && 
				file.getName().endsWith("Test.class") && !file.getName().contains("$")) {
			String suiteName = file.getCanonicalPath();
			suiteName = suiteName.substring(root.length()+1, suiteName.lastIndexOf("."));
			suiteName = suiteName.replace('/','.').replace('\\','.');
			Class<?> suiteClass = Class.forName(suiteName);
			result.addTest(new TestSuite(suiteClass));
		}		
		return result;
	}
	
	
	/**
	 * Main, ignore parameters
	 */
	public static void main(String[] args) {
		(new junit.textui.TestRunner()).doRun(suite(), false);
	}
}
