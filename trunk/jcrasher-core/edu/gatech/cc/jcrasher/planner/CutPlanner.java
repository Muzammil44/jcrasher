/*
 * CutPlanner.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

/**
 * Generates plan space for classes under test.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface CutPlanner extends Planner {

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
  
  /**
   * @return plan space of classUnderTest.
   */ 
  public <T> ClassUnderTest<T> getPlanSpace(Class<T> classUnderTest);
}
