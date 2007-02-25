/*
 * ByteLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;


/**
 * Holds Java-syntax of how to define a byte.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.1 
 */
public class ByteLiteral extends PrimitiveLiteral<Byte> {

  /**
   * Constructor
   */
  public ByteLiteral(byte value) {
    super(Byte.TYPE, Byte.valueOf(value));
  }
  

  /**
   * How to reproduce this value=object?
   */
  @Override
  public String text() {
    return "(byte)" + value.toString();
  }
}