/*
 * JUnitAllImpl.java
 * 
 * Copyright 2005-2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.writer;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class JUnitAllImpl implements JUnitAll {

	protected FileWriter fw;  //file not yet created in file system
	
	
	/**
	 * Create JUnitAll.java that calls all test suites to be generated.
	 * @param testRoot directory in which JUnitAll.java should be created.
	 */
	public void create(String testRoot) {
		if (fw!=null) {return;}	//Already created.
		
		notNull(testRoot);
		
		File f = CreateFileUtil.createOutFile(testRoot, "JUnitAll");  //default package
		try {
			fw = new FileWriter(f);
	    fw.write(junitAllHeader);	//contents up until calls to test suites.
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Create JUnitAll.java that calls all test suites to be generated.
	 * @param c place JUnitAll.java in directory from which c was loaded
	 * in case no out-directory has been set explicitly.
	 */
	public void create(Class<?> c) {
		if (fw!=null) {return;}	//Already created.
		
		notNull(c);
		create(CreateFileUtil.getTestRoot(c));
	}
	
	
	/**
	 * Append invocation of test suite.
	 * 
	 * @param test has to have testX() methods, which JUnit automatically
	 * adds to the TestSuite
	 */
	public void addTestSuite(String test) {
		notNull(fw);
		notNull(test);
		
		try {
	    fw.write(TAB+TAB+"suite.addTestSuite(" +test +".class);"+NL);
		}
		catch(IOException e) {
			e.printStackTrace();
		}				
	}	
	
	
		
	/**
	 * Append invocation of test suite.
	 * 
	 * @param test must have a suite() method that returns a JUnit Test.
	 * @see junit.runner.BaseTestRunner#getTest(java.lang.String) 
	 */
	public void addTest(String test) {
		notNull(fw);
		notNull(test);
		
		try {
	    fw.write(TAB+TAB+"suite.addTest(" +test +".suite());"+NL);
		}
		catch(IOException e) {
			e.printStackTrace();
		}		
	}	
	
	
	
	/**
	 * Append invocation of test suite for testee c.
	 */
	public void addTest(Class<?> c) {
		notNull(c);
		addTest(c.getName() +"Test");
	}	

	
	
	/**
	 * Write closing code of JUnitAll.java.
	 */
	public void finish() {
		if (fw==null) {return;}  //nothing to finish
		
		try {
	    fw.write(junitAllFooter);
	    fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}		
	}

}
