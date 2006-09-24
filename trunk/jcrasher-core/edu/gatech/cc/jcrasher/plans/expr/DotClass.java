/*
 * DotClass.java
 * 
 * Copyright 2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Represents Object.class
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.8.2
 */
public class DotClass implements Expression {

  public Class getReturnType() {
    return Class.class;
  }

  public Object execute() {
    return Object.class;
  }
  
  public String toString(final Class testee) {
    notNull(testee);
    return notNull(CodeGenFct.getName(Object.class, testee)+".class");
  }

  /**
   * @return a representative example
   */
  @Override
  public String toString() {
    return toString(getReturnType());
  }
}
