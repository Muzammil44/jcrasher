/*
 * GroupedTestFailure.java
 * 
 * Copyright 2003 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit.framework;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestFailure;

/**
 * GroupedTestFailure adds a reference to a parent (prototype) exception
 * to TestFailure.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * @version	$Id: $
 */
public class GroupedTestFailure extends TestFailure {

	/* default: parent = prototype exception. */
	protected GroupedTestFailure parent = null;	
	
	
	/**
	 * Constructor
	 */
	public GroupedTestFailure(Test failedTest, Throwable thrownException, GroupedTestFailure parent) {
		super(failedTest, thrownException);
		this.parent = parent;
	}
	
	
	/**
	 * @return iff this failure represents a prototype exception.
	 */
	public boolean isPrototype() {
		return (parent == null);
	}
		
	
	/**
	 * Get parent (prototype) exception.
	 * @return null iff this failure represents a prototype exception. 
	 */
	protected GroupedTestFailure getParent() {
		return parent;
	}
	
	
	/*
	 *  Prune output: Remove last five stack trace entries as 
	 *  they are the same in every trace.
	 */
	@Override
	public String trace() {
		StringWriter stringWriter= new StringWriter();
		thrownException().printStackTrace(new PrintWriter(stringWriter));
		StringBuffer trace = stringWriter.getBuffer();
		
		int pos = trace.lastIndexOf("at sun.reflect.NativeMethodAccessorImpl.invoke0");
		if (pos>0)
			return trace.substring(0, pos).trim();
		
		return trace.toString();
	}	
}
