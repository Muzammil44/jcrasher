package edu.gatech.cc.junit;

import java.util.Enumeration;

import junit.framework.TestSuite;

/*
 * CreateJUnitAll.java
 * 
 * Copyright 2005 Christoph Csallner and Yannis Smaragdakis.
 */


/**
 * Collects all compiled JUnit test cases in cnc-bin directory
 * (these are the testee's test cases, not the CnC-generated ones).
 * Then creates a JUnitAll file, which executes these test cases.
 * 
 * Run from project's base directory
 */
public class CreateJUnitAll extends TestSuite {

//	/**
//	 * Main, ignore parameters
//	 */
//	public static void main(String[] args) {
//		JUnitAll junitAll = new JUnitAllImpl();
//		junitAll.create(CollectingTestSuite.getTestRootName());
//		
//		TestSuite suite = CollectingTestSuite.suite();
//		for (Enumeration e = suite.tests(); e.hasMoreElements();) {
//			TestSuite test = (TestSuite) e.nextElement();
//			junitAll.addTestSuite(test.getName());
//			/* TODO determine testX() methods and add them directly to suite*/
//		}
//		
//		junitAll.finish();
//	}
}
