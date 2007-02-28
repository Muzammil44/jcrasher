/*
 * Wrapper.java
 * 
 * Copyright 2004 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class Wrapper extends RuntimeException {
	protected RuntimeException e;

	/**
	 * Constructor
	 */
	public Wrapper(RuntimeException ex) {
		this.e = notNull(ex);
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return e.getStackTrace();
	}
	
	/**
	 * @return wrapped exception
	 */
	public RuntimeException unwrap() {
		return e;
	}
}
