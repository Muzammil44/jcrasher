/*
 * AccidentException.java
 * 
 * Copyright 2004 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

/**
 * AccidentException
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * @version	$Id: $
 */
public class AccidentException extends Wrapper {

	public AccidentException(RuntimeException ex) {
		super(ex);
	}
}
