/*
 * ClassUnderTest.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import java.math.BigInteger;

import edu.gatech.cc.jcrasher.plans.stmt.Block;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class ClassUnderTest<T> extends TypeNode<T> {
	/**
   * Retrieve block with given index from the underlying class's plan space.
	 * 
	 * Precond: 0 <= planIndex < getPlanSpaceSize() Postcond: no side-effects
	 */
	public abstract Block<?> getBlock(BigInteger planIndex);
  
  @Override
  public ExpressionNode<T>[] getChildren() {
    return children;
  }
}
