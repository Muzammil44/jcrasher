/*
 * PrimitiveLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import edu.gatech.cc.jcrasher.plans.expr.SimpleExpression;


/**
 * Represents how to instantiate a wrapper-object for a java primtive type.
 * 
 * <p>
 * Unless otherwise noted:
 * <ul>
 * <li>Each reference parameter of every method must be non-null.
 * <li>Each reference return value must be non-null.
 * </ul>
 * 
 * @param <T> boxed version of this literal's type.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10
 */
public abstract class PrimitiveLiteral<T> extends SimpleExpression<T> {
  
  /**
   * Constructor
   * 
   * @parm returnType never null.
   * @param value never null.
   */
  protected PrimitiveLiteral(Class<T> returnType, T value) {   
    super(notNull(returnType), notNull(value));
  }

  public String text() {
    return value.toString();
  }
}
