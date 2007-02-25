/*
 * FunctionCall.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;


/**
 * Calls a method or constructor
 * 
 * @param <T> return type of function.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class FunctionCall<T> extends ReferenceTypeExpression<T> {

	/**
	 * Constructor
	 * 
	 * @param returnType never null.
	 * @param testeeType never null.
	 */
	protected FunctionCall(Class<T> returnType, Class<?> testeeType) {
		super(returnType, testeeType);
	}	
}
