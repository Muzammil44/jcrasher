/*
 * ExpressionStatement.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.blocks;

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
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.8
 */
public class ExpressionStatement implements Statement {

  /**
   * Expression to invoke the wrapped fct
   */
  protected Expression fctPlan = null;

  /**
   * Constructor
   * 
   * Postcond: (fctPlan != null)
   */
  public ExpressionStatement(final FunctionCall pFctPlan) {
    fctPlan = notNull(pFctPlan);
  }

  
  /**
   * @return true.
   */
  public Object execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException
  {
    fctPlan.execute(); //ok if nobody messed with fctPlan
    return true;  //only reach here if execution went through okay. 
  }  
  
  
  /**
   * @return a specialized representation of the statement like:
   * <ul>
   * <li>b.m(0);
   * <li>new A();
   */
  public String toString(final Class testeeType) {
    notNull(testeeType);
    
    final String res = fctPlan.toString(testeeType)+";";

    return notNull(res);
  }
  
  /**
   * @return a representative example
   */
  @Override
  public String toString() {
    return fctPlan.toString()+";";
  }  
}
