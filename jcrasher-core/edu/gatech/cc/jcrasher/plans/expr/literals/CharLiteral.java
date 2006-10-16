/*
 * CharLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;


/**
 * Holds Java-syntax of how to define a char value.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.4
 */
public class CharLiteral extends PrimitiveLiteral<Character> {

  /**
   * Constructor
   * 
   * @param val hardcoded primitive value, not via java-wrapper-constructor
   */
  public CharLiteral(final char val) {
    super(Character.valueOf(val));
  }
  
  
  public Class<Character> getReturnType() {
    return Character.TYPE;
  }


  /**
   * How to reproduce this value=object?
   */
  @Override
  public String toString() {
    return "'" + execute().toString() + "'";
  }
}
