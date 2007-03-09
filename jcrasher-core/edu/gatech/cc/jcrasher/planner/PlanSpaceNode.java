/*
 * PlanSpaceNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import java.math.BigInteger;

import edu.gatech.cc.jcrasher.plans.expr.Expression;

/**
 * Node to access the plans of a (sub-) plan space, e.g.:
 * <ul>
 * <li>all plans of how to invoke a method, chaining up to a given depth
 * <li>all plans of how to obtain an instance of a type,
 * using chaining up to a given depth
 * </ul>
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface PlanSpaceNode<T> {

  /**
   * Caches sizes of all sub plan spaces to speed up getPlan(int).
   * 
   * @return size of this sub plan space = nr different plans this plan space
   *         can return via getPlan(int)
   */
  public BigInteger getPlanSpaceSize();

  
  /**
   * Does not produce any side-effects.
   * 
   * @param planIndex the index of the plan according to the node's canonical
   *          order, taken from [0..getPlanSpaceSize()-1]
   * @param testeeType needed to emit test code tailored to testee.
   * @return plan according to the ordering semantics, never null
   */
  public Expression<? extends T> getPlan(BigInteger planIndex, Class<?> testeeType);
}
