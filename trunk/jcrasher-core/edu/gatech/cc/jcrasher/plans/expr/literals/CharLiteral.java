/*
 * CharLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import org.apache.commons.lang.StringEscapeUtils;


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
   */
  public CharLiteral(char val) {
    super(Character.TYPE, Character.valueOf(val));
  }
  
  /**
   * TODO: Is this right?
   */
  protected String textInternal() {
    switch (value.charValue()) {
    case '\'':
      return "\\'"; 

    case '"':
      return "\"";
      
    default:
      return StringEscapeUtils.escapeJava(value.toString()); 
    }
  }

  @Override
  public String text() {
    return "'" + textInternal() + "'";
  }
}
