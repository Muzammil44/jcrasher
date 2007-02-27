/*
 * Crasher.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

/**
 * Generates test cases for the public methods and constructors 
 * of the classes specified by the user.
 *
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface Crasher {


	/**
	 * Generate test cases for classes under test.
	 */
	public void crashClasses();
}
