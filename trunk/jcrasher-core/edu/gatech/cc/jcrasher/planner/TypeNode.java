/*
 * TypeNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.isNonNeg;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.math.BigInteger;

import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.types.TypeGraph;
import edu.gatech.cc.jcrasher.types.TypeGraphImpl;

/**
 * Node to access the plans of a type (sub-) plan space up to a given maximal
 * chaining depth.
 * <ul>
 * <li>Child plan spaces are all FunctionNode or LeafNode.
 * <li>Knows how to map an index to an index of one of its child-plans.
 * </ul>
 * 
 * Expression space depth recursion can only stop at a type, as a type decides whether
 * to recurse to another function or stick with (the predifened) values.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class TypeNode<T> implements PlanSpaceNode<T> {

	protected final TypeGraph typeGraph = TypeGraphImpl.instance();
	
  /**
   * Child types, i.e. receiver and param types up to our max depth - 1
   */
  protected ExpressionNode<T>[] children;
 
  /**
   * Size of each child's plan space (given their max depth), e.g.:
   * <ul>
   * <li>(3, 5, 2) implies own size of 10
   * <li>(0, 10, 10, 0, 5) implies own size of 25
   * </ul>
   */
  protected BigInteger[] childSizes;
    
  /**
   * Own plan space size = Sum of childrens' plan space sizes.
   */
  protected BigInteger planSpaceSize = BigInteger.ZERO;
  
  /**
   * Caches highest index for the given child. E.g.,
   * <ul>
   * <li>childSizes of (3, 5, 2) imply (2, 7, 9) = [0..2], [3..7], [8..9].
   * <li>childSizes of (0, 10, 10, 0, 5) imply (-1, 9, 19, 19, 24) 
   * = [0..-1], [0..9], [10..19], [20..19], [20..24].
   * </ul>
   */
  protected BigInteger[] childRanges;

  /**
   * Sets the children. To be called by extending classes only.
   * 
   * @param parameters The children to set
   */
  protected void setChildren(final ExpressionNode<T>[] pChildren) {
    this.children = pChildren;
  }

  protected ExpressionNode<T>[] getChildren() {
    return children;
  }

  
  /**
   * Caches sizes of all sub plan spaces to speed up getPlan(int).
   * 
   * A type's plan space size is the sum of its child plan spaces.
   * 
   * @return size of this sub plan space = nr different plans this plan space
   *         can return via getPlan(int)
   */
  public BigInteger getPlanSpaceSize() {
    notNull(children);
    
    if (childSizes != null) //cache hit.
      return planSpaceSize;      
      
    /* Compute childrens' and own plan space sizes recursively */
    childSizes = new BigInteger[children.length];
    childRanges = new BigInteger[children.length];
    for (int i = 0; i < children.length; i++) {
      childSizes[i] = children[i].getPlanSpaceSize();
      planSpaceSize = planSpaceSize.add(childSizes[i]);
      childRanges[i] = planSpaceSize.subtract(BigInteger.ONE);
    }

    return planSpaceSize;
  }

  
  /**
   * @return index into children array that is addressed by planIndex.
   */
  protected int getChildIndex(BigInteger planIndex) {
    check(isNonNeg(planIndex));
    check(planIndex.compareTo(getPlanSpaceSize()) < 0); //fills cache.

    for (int i=0; i<children.length; i++)
      if (planIndex.compareTo(childRanges[i]) <= 0) //found correct subrange.
        return i;
    
    throw new IllegalStateException("Please report to the JCrasher team."); 
  }
  
  /**
   * @return index in child's plan space.
   */
  protected BigInteger getChildPlanIndex(int child, BigInteger planIndex) {
    BigInteger offsetPrevChildren = BigInteger.ZERO;
    if (child>0)
      offsetPrevChildren = childRanges[child-1].add(BigInteger.ONE);
    return planIndex.subtract(offsetPrevChildren);    
  }

  
  /**
   * Retrieves childrens' plan (side-effect-free),
   * according to canonical ordering. E.g., for sub spaces of sizes
   * (3, 5, 2) we get childRanges of (2, 7, 9), which imply index ranges of
   * [0..2], [3..7], [8..9].
   * 
   * @param planIndex the index of the plan according to the node's canonical
   *          order, taken from [0..getPlanSpaceSize()-1].
   * @return childrens' plans according to the ordering semantics, never null.
   */
  public Expression<? extends T> getPlan(BigInteger planIndex, Class<?> testeeType) {
    check(isNonNeg(planIndex));
    check(planIndex.compareTo(getPlanSpaceSize()) < 0); //fills cache.
    
    int child = getChildIndex(planIndex);
    BigInteger childPlanIndex = getChildPlanIndex(child, planIndex);
    
    return children[child].getPlan(childPlanIndex, testeeType);
  }
}
