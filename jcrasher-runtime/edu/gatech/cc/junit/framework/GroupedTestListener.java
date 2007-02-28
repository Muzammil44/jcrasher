/*
 * GroupedTestListener.java
 * 
 * Copyright 2003 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit.framework;

import junit.framework.Test;
import junit.framework.TestListener;

/**
 * GroupedTestListener overloads the addError method to pass the
 * the error's prototype.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * @version	$Id: $
 */
public interface GroupedTestListener extends TestListener {

	/**
	 * An exception t occurred and we know that it is similar to exception parent
	 * that occurred previously. Iff (parent == null) then t is the first of its kind.
	 */
	public void addError(Test test, Throwable t, GroupedTestFailure parent);
}
