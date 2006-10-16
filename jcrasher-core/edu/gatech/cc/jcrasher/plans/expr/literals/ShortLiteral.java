/*
 * ShortLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;


/**
 * Holds Java-syntax of how to define a short value.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.1
 */
public class ShortLiteral extends PrimitiveLiteral<Short> {

  /**
   * Constructor
   * 
   * @param val hardcoded primitive value, not via java-wrapper-constructor
   */
  public ShortLiteral(final short val) {
    super(Short.valueOf(val));
  }
  
  public Class<Short> getReturnType() {
    return Short.TYPE;
  }

  /**
   * How to reproduce this value=object?
   */
  @Override
  public String toString() {
    return "(short)" + execute().toString();
  }
}
