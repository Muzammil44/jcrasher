/*
 * Wrapper.java
 * 
 * Copyright 2004 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

/**
 * Wrapper
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class Wrapper extends RuntimeException {
	protected RuntimeException e=null;

	public Wrapper(RuntimeException ex) {
		assert ex!=null;
		e = ex;
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return e.getStackTrace();
	}
	
	public RuntimeException unwrap() {
		return e;
	}
}
