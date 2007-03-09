/*
 * ExpressionNode.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

/**
 * Leaf node (literal) or intermediate node (method call expression).
 * 
 * @param <T> return type of the expression.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface ExpressionNode<T> extends PlanSpaceNode<T> {
  /* Empty */
}
