/*
 * Planner.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;


/**
 * Maintains mapping from classes to JCrasher's plans how
 * to test classes.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface Planner {
  
  /**
   * @return plan space of class T.
   */ 
  public <T> TypeNode<T> getPlanSpace(Class<T> c);
  
}
