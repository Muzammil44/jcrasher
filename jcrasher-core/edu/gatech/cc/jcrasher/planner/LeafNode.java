/*
 * ValueNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.isArrayIndex;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cc.jcrasher.plans.expr.Expression;

/**
 * Node to access a list of values, i.e. has no children plan spaces.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class LeafNode<T> implements ExpressionNode<T> {

  protected final List<Expression<T>> plans = 
  	new ArrayList<Expression<T>>();


  /**
   * Constructor
   */
  public LeafNode(List<Expression<T>> plans) {
    notNull(plans);
    this.plans.addAll(plans);
  }


  /**
   * Efficient if plans.size() <= 16.
   */
  public BigInteger getPlanSpaceSize() {
    return BigInteger.valueOf(plans.size());
  }

  
  public Expression<T> getPlan(BigInteger planIndex, Class<?> testeeType) {
    check(isArrayIndex(planIndex));

    return plans.get(planIndex.intValue());
  }

  
  @Override
  public String toString() {
    return plans.toString();
  }
}
