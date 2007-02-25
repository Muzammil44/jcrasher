/*
 * BlockStatement.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.stmt;

import edu.gatech.cc.jcrasher.plans.Executable;

/**
 * A statement, sequence of statements, or block.
 * A block is its own lexical scope and has its own brackets.
 * Statements themselves do not.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.2
 */
public interface BlockStatement extends Executable<Boolean> {
	
  /**
   * @param testeeType Type of testee.
   * We can emit the simple name of types that
   * are in the same package as testeeType.
   * @return a specialized representation of this block statement.
   * Examples:
   *   <ul>
   *   <li><code>A a = new A(null);</code>
   *   <li><code>b.m(0);</code>
   *   <li><code>{ C.m(0); }</code>
   *   </ul>
   */
  public String toString(final Class<?> testeeType);
}
