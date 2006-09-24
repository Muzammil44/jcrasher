/*
 * ValueNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.cc.jcrasher.plans.expr.Expression;

/**
 * Node to access a list of values, i.e. has no children plan spaces.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ValueNode implements PlanSpaceNode {

  protected final List<Expression> plans = new ArrayList<Expression>();


  /**
   * Constructor
   */
  public ValueNode(final List<Expression> pPlans) {
    notNull(pPlans);
    plans.addAll(pPlans);
  }


  public int getPlanSpaceSize() {
    return plans.size();
  }

  public Expression getPlan(int planIndex) {
    return plans.get(planIndex);
  }

  
  @Override
  public String toString() {
    return plans.toString();
  }
}
