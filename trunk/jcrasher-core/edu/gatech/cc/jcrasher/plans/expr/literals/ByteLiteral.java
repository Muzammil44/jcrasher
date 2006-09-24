/*
 * ByteLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

/**
 * Holds Java-syntax of how to define a byte.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.1 
 */
public class ByteLiteral extends PrimitiveLiteral {

  /**
   * Constructor
   * 
   * @param val hardcoded primitive value, not via java-wrapper-constructor
   */
  public ByteLiteral(final byte val) {
    super(notNull(val));
  }

  
  public Class getReturnType() {
    return Byte.TYPE;
  }
  

  /**
   * How to reproduce this value=object?
   */
  @Override
  public String toString() {
    return "(byte)" + execute().toString();
  }
}