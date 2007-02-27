/*
 * ClassUnderTest.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import edu.gatech.cc.jcrasher.plans.stmt.Block;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface ClassUnderTest<T> extends PlanSpaceNode<T> {
	/**
   * Retrieve block with given index from the underlying class's plan space.
	 * 
	 * Precond: 0 <= planIndex < getPlanSpaceSize() Postcond: no side-effects
	 */
	public Block getBlock(int planIndex, Class<?> testeeType);
}
