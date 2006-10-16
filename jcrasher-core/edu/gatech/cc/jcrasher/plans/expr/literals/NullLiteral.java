/*
 * NullLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Provides plan for null reference.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @param <T> type of null literal.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.7
 */
public class NullLiteral<T> implements Expression<T> {

  protected Class<T> returnType = null; // type of instance created by this plan

  public Class<T> getReturnType() {
    return notNull(returnType);
  }

  /**
   * Constructor, to be called from outside the inheritance hierarchy
   */
  public NullLiteral(final Class<T> type) {
    returnType = notNull(type);
  }

  /**
   * @return null.
   */
  public T execute() {
    return null;
  }  

  protected String getString(final String type) {
    notNull(type);
    return "(" + type + ")" + "null";
  }


  @Override
  public String toString() {
    notNull(returnType);
    return notNull(getString(CodeGenFct.getName(returnType)));
  }


  /**
   * How to reproduce this value=object? (Type) null
   */
  public String toString(final Class<?> testee) {
    notNull(testee);
    notNull(returnType);
    return notNull(getString(CodeGenFct.getName(returnType, testee)));
  }
}
