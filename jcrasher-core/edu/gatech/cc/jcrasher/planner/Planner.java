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
  public <T> ClassUnderTest<T> getPlanSpace(Class<T> c);
  
  /**
   * Flushes the plan spaces known so far.
   * This includes only plans for the classes retrieved via
   * getPlanSpace.
   * 
   * <p>
   * Prints the methods under test and the typeGraph found for creating
   * test cases to standard out:
   * <ul>
   * <li>Default: No output
   * <li>Verbose: Print typeGraph used to create test cases.
   * <li>All: Verbose and typeGraph found but not used.
   * </ul>
   */
  public void flush();
}
