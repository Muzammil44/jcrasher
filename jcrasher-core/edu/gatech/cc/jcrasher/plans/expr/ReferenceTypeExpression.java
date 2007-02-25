/*
 * ReferenceTypeExpression.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

/**
 * Expression that involves some reference type
 * (other than String).
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class ReferenceTypeExpression<T> extends AbstractExpression<T> {

	protected Class<?> testeeType;	//To print correctly.
	
	
	/**
	 * Constructor
	 * 
	 * @param returnType never null.
	 * @param testeeType never null.
	 */
	protected ReferenceTypeExpression(Class<T> returnType, Class<?> testeeType) {
		super(returnType);

		this.testeeType = notNull(testeeType);
	}	
}
