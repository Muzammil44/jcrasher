/*
 * StringLiteral.java
 * 
 * Copyright 2002,2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import edu.gatech.cc.jcrasher.plans.expr.Expression;


/**
 * Holds a string literal.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.5
 */
public class StringLiteral implements Expression<String> {

  protected String value = null;

  /**
   * Constructor
   * 
   * @param pValue non-null String literal
   */
  public StringLiteral(final String pValue) {
    value = notNull(pValue);
  }
  
  
  public Class<String> getReturnType() {
    return String.class;
  }

  @Override
  public String toString() {
    return "\"" + value + "\"";
  }

  /**
   * @param testee ignored.
   */
  public String toString(Class<?> testee) {
  	notNull(testee);
    return toString();
  }
  
  public String execute() {
    return notNull(value);
  }  
}
