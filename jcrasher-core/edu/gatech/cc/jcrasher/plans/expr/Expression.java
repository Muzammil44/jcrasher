/*
 * Expression.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import edu.gatech.cc.jcrasher.plans.Executable;

/**
 * Used to create a value in a test case.
 * Multiple expressions may return values of the same type.
 * 
 * <ul>
 * <li>ClassWrapper --> Class
 * <li>ClassWrapper --> Expression*
 * <li>Expression --> Instance
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/expressions.html
 */
public interface Expression<T> extends Executable<T> {

  /**
   * @return type of instance created by this plan
   */
  public Class<T> getReturnType();

  /**
   * How to reproduce this value=object?
   * <ul>
   * <li>Value or (recursive) constructor-chain for user-output
   * <li>Example: new A(new B(1), null)
   * 
   * @param testee non-null fully qualified testee type.
   * We can use short type names for classes in the same package as the testee.
   */
  public String toString(final Class<?> testee);
}
