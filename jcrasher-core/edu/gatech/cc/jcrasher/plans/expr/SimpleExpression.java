/*
 * SimpleLiteral.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.notNull;


/**
 * Literal that always prints the same.
 * By this we mean strings, primitive literals, and variables.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class SimpleExpression<T> extends AbstractExpression<T> {

	protected T value = null;     // result of plan execution
	
	/**
	 * Constructor
	 * 
	 * @param returnType never null.
	 * @param value maybe null.
	 */
	protected SimpleExpression(Class<T> returnType, T value) {
		super(notNull(returnType));
		
		this.value = value;
	}
		
	/**
	 * @return maybe null.
	 */
  public T execute() {
    return value;
  }  
}
