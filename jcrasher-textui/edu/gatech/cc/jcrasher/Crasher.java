/*
 * Crasher.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

/**
 * Tries to crash a method and eventually reports about it afterwards.
 *
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface Crasher {


	/**
	 * Do bad things with the methods of these classes and talk about them.
	 * @param pClasses classes to find test cases for.
	 * @param execute are we allowed to execute the test cases
	 * we are generating.
	 */
	public void crashClasses(Class[] pClasses, boolean execute);
}
