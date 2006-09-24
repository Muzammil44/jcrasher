/*
 * TestCaseWriter.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.writer;

import java.io.File;

import edu.gatech.cc.jcrasher.plans.blocks.Block;

/**
 * Transforms a sequence of testcases into a Java output file.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface TestCaseWriter {
  
  /**
   * Writes a series of test cases to a new java file together
   * with a text that will be included as a comment.
   * 
   * @param doFilter wrap test case into a try-catch block that passes any
   * thrown exception to JCrasher's runtime filters.
   * 
   * @return written file.
   */
  public File writeTestFile(
      boolean doFilter,
      final Class testeeClass,
      final Block[] blocks,
      final String comment); 
}
