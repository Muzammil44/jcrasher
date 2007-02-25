/*
 * ShortLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;


/**
 * Holds Java-syntax of how to define a short value.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.1
 */
public class ShortLiteral extends PrimitiveLiteral<Short> {

  /**
   * Constructor
   */
  public ShortLiteral(short value) {
    super(Short.TYPE, Short.valueOf(value));
  }
  
  @Override
  public String toString() {
    return "(short)" + value.toString();
  }
}
