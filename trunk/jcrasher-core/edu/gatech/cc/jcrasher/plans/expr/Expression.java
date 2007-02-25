/*
 * Expression.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import edu.gatech.cc.jcrasher.plans.JavaCode;

/**
 * Used to create a value in a test case.
 * Multiple expressions may return values of the same type.
 * 
 * <ul>
 * <li>ClassWrapper --> Class
 * <li>ClassWrapper --> Expression*
 * <li>Expression --> Instance
 * </ul>
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/expressions.html
 */
public interface Expression<T> extends JavaCode<T> {

  /**
   * @return type of instance created by this plan
   */
  public Class<T> getReturnType();
}
