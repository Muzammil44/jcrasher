/*
 * Planner.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;


/**
 * Generalized planner creates blocks with chained plans and local variables:
 * <ul>
 * <li>Generates test blocks for any method/ constructor of a passed class.
 * <li>Hides class-wrapper- and plans- database, how instances are created, what
 * combination of methods are called etc.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface Planner {

  /**
   * Print the methods under test and the typeGraph found for creating test cases to
   * standard out, depending on the program's verbose level.
   * 
   * Default: No output Verbose: Print typeGraph used to create test cases. All:
   * Verbose and typeGraph found but not used.
   */
  public void flush();
}
