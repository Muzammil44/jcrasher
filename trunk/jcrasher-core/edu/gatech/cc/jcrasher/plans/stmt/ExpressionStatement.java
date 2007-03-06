/*
 * ExpressionStatement.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.stmt;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.InvocationTargetException;

import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.FunctionCall;

/**
 * Hides a code statement to invoke a function (maybe causing side-effect).
 * 
 * <p>
 * We only need the following subset:
 * 
 * <code>
 * ExpressionStatement
 *     ::= MethodInvocation; | ClassInstanceCreationExpression;
 * </code>
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @param <T> type of the wrapped expression.  
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.8
 */
public class ExpressionStatement<T> implements Statement<T> {

  /**
   * Expression to invoke the wrapped fct
   */
  protected Expression<T> fctPlan = null;

  /**
   * Constructor
   * 
   * Postcond: (fctPlan != null)
   */
  public ExpressionStatement(final FunctionCall<T> pFctPlan) {
    fctPlan = notNull(pFctPlan);
  }

  
  /**
   * @return true.
   */
  public T execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException
  {
    return fctPlan.execute(); //ok if nobody messed with fctPlan
  }  
  
  
  /**
   * @return a specialized representation of the statement like:
   * <ul>
   * <li>b.m(0);
   * <li>new A();
   */
  public String text() {
  	return fctPlan.toString()+";";
  }

  @Override
  public String toString() {
    return text();
  }  
}
