/*
 * StringLiteral.java
 * 
 * Copyright 2002,2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import org.apache.commons.lang.StringEscapeUtils;

import edu.gatech.cc.jcrasher.plans.expr.SimpleExpression;


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
public class StringLiteral extends SimpleExpression<String> {

  /**
   * Constructor
   * 
   * @param value never null.
   */
  public StringLiteral(String value) {
    super(String.class, notNull(value));
  }

  public String text() {
  	return "\"" + StringEscapeUtils.escapeJava(value)+ "\"";
  }  
}
