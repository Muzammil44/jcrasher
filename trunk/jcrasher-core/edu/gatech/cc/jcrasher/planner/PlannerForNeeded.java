/*
 * PlannerForNeeded.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

/**
 * Generates plan spaces for needed types.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface PlannerForNeeded extends Planner {

  /**
   * @return plan space of typeNeeded.
   */ 
  public <T> TypeNeededNode<T> getPlanSpace(Class<T> typeNeeded);
}
