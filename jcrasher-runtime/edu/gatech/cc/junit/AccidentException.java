/*
 * AccidentException.java
 * 
 * Copyright 2004 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class AccidentException extends Wrapper {

	/**
	 * Constructor
	 */
	public AccidentException(RuntimeException ex) {
		super(ex);
	}
}
