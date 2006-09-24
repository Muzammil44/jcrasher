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
	 */
	public void crashClasses(Class[] pClasses);
}
