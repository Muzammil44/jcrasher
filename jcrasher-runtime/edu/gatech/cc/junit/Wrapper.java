/*
 * Wrapper.java
 * 
 * Copyright 2004 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class Wrapper extends RuntimeException {
	protected RuntimeException e=null;

	/**
	 * Constructor
	 */
	public Wrapper(RuntimeException ex) {
		assert ex!=null;
		e = ex;
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
