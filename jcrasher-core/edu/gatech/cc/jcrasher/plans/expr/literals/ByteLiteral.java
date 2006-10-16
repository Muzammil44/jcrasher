/*
 * ByteLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;


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
public class ByteLiteral extends PrimitiveLiteral<Byte> {

  /**
   * Constructor
   * 
   * @param val hardcoded primitive value, not via java-wrapper-constructor
   */
  public ByteLiteral(final byte val) {
    super(Byte.valueOf(val));
  }

  
  public Class<Byte> getReturnType() {
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