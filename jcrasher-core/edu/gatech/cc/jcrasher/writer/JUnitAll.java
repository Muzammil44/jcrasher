/*
 * JUnitAll.java
 * 
 * Copyright 2005-2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.writer;


import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

/**
 * Generates a test suite that collects all test cases
 * generated for the classes under test.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface JUnitAll {
	
	/**
	 * File header
	 */
	public final static String junitAllHeader = 
		"import junit.framework.*;"+														NL+
																														NL+
		"/**"+																									NL+
		" * Collects all test cases generated for all classes."+NL+
		" */"+																									NL+
		"public class JUnitAll extends TestSuite {"+						NL+
																														NL+
		TAB+"public JUnitAll(String name) {"+										NL+
		TAB+TAB+"super(name);"+																	NL+
		TAB+"}"+																								NL+
																														NL+
		TAB+"public static Test suite() {"+											NL+
		TAB+TAB+"TestSuite suite = new TestSuite();"+						NL;
	
	/**
	 * EOF
	 */
	public final static String junitAllFooter = 
		TAB+TAB+"return suite;"+																NL+
		TAB+"}"+																								NL+
		"}"+																										NL;
	
	/**
	 * Create a new JUnitAll.java that calls all test suites to be generated.
	 * @param testRoot directory in which JUnitAll.java should be created.
	 */
	public void create(String testRoot);
	
	/**
	 * Create a new JUnitAll.java that calls all test suites to be generated.
	 * @param c place JUnitAll.java in directory from which c was loaded
	 * in case no out-directory has been set explicitly.
	 */
	public void create(Class<?> c);	
	
	/**
	 * Append invocation of test suite for testee c.
	 */
	public void addTest(Class<?> c);		
	
	
	/**
	 * Append invocation of test suite.
	 */
	public void addTest(String test);	
	
	
	/**
	 * Append invocation of test suite.
	 * 
	 * @param test has to have testX() methods, which JUnit automatically
	 * adds to the TestSuite
	 */
	public void addTestSuite(String test);
	
	
	/**
	 * Write closing code of JUnitAll.java.
	 */
	public void finish();		
}
