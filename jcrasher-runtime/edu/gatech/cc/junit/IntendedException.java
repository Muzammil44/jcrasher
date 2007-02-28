/*
 * IntendedException.java
 * 
 * Copyright 2004 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

/**
 * IntendedException
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * @version	$Id: $
 */
public class IntendedException extends Wrapper {
	
	public IntendedException(RuntimeException ex) {
		super(ex);
	}
}
