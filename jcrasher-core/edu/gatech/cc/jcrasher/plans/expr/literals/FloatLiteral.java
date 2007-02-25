/*
 * FloatLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;


/**
 * Holds Java-syntax of how to define a float value.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.2
 */
public class FloatLiteral extends PrimitiveLiteral<Float> {

  /**
   * Constructor
   */
  public FloatLiteral(float val) {
    super(Float.TYPE, Float.valueOf(val));
  }

  @Override
  public String text() {
    return value.toString() + "f";
  }
}
