/*
 * GroupedResultPrinter.java
 * 
 * Copyright 2003 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit.textui;

import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestFailure;
import junit.textui.ResultPrinter;
import edu.gatech.cc.junit.framework.GroupedTestFailure;
import edu.gatech.cc.junit.framework.GroupedTestListener;

/**
 * GroupedResultPrinter
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * @version	$Id: $
 */
public class GroupedResultPrinter extends ResultPrinter implements GroupedTestListener {

	/**
	 * Constructor
	 */
	public GroupedResultPrinter(PrintStream writer) {
		super(writer);
	}


	public void addError(Test test, Throwable t, GroupedTestFailure parent) {
		if (parent==null) {	//first, prototype
			getWriter().print("E");
		}
		else {		//we have already seen a similar exception.
			getWriter().print("e");
		}
	}


	/**
	 * Only print prototype exceptions.
	 * A prototype exception is the first in a group of similar exceptions.
	 */
	@Override
	public void printDefect(TestFailure testFailure, int count) {
		GroupedTestFailure gTestFailure = (GroupedTestFailure) testFailure;
		if (gTestFailure.isPrototype()) {
			printDefectHeader(gTestFailure, count);
			printDefectTrace(gTestFailure);
		}
		else {
			/* drop non-prototypes: do not print to screen. */
		}
	}
}
