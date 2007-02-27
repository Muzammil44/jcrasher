/*
 * PlanSpaceNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import edu.gatech.cc.jcrasher.plans.JavaCode;
import edu.gatech.cc.jcrasher.plans.expr.Expression;

/**
 * Node to access the plans of a (sub-) plan space, e.g.:
 * <ul>
 * <li>all plans of how to invoke a method, chaining up to a given depth
 * <li>all plans of how to obtain an instance of a type,
 * using chaining up to a given depth
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface PlanSpaceNode<T> {

  /**
   * Precond: true Postcond: cached sizes of all sub plan spaces to speed up
   * getPlan(int)
   * 
   * @return size of this sub plan space = nr different plans this plan space
   *         can return via getPlan(int)
   */
  public int getPlanSpaceSize();

  /**
   * Precond: 0 <= planIndex < getPlanSpaceSize() Postcond: no side-effects
   * 
   * @param planIndex the index of the plan according to the node's canonical
   *          order, taken from [0..getPlanSpaceSize()-1]
   * @param testeeType needed to emit test code tailored to testee.
   * @return plan according to the ordering semantics, never null
   */
  public Expression<?> getPlan(int planIndex, Class<?> testeeType);
}
