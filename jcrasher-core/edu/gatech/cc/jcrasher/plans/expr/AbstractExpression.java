/*
 * AbstractExpression.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

/**
 * Shared expression functionality.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class AbstractExpression<T> implements Expression<T> {

	protected Class<T> returnType;

	/**
	 * Constructor
	 * 
	 * @parm returnType never null.
	 */
	protected AbstractExpression(Class<T> returnType) {
		notNull(returnType);
		
		this.returnType = returnType;
	}

	
	public Class<T> getReturnType() {
		return returnType;
	}
	
  /**
   * @return the textual representation.
   */
  @Override
  public String toString() {
    return text();
  }
}
