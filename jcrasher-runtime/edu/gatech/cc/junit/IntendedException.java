/*
 * IntendedException.java
 * 
 * Copyright 2004 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class IntendedException extends Wrapper {
	
	/**
	 * Constructor
	 */
	public IntendedException(RuntimeException ex) {
		super(ex);
	}
}
