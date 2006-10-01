/*
 * TestCaseWriter.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.writer;

import java.io.File;

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
   * @return written file.
   */
  public File write(); 
}
