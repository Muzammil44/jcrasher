/*
 * DotClass.java
 * 
 * Copyright 2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Represents Object.class
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.8.2
 */
public class DotClass extends ReferenceTypeExpression<Class> {

	/**
	 * Constructor
	 */
	public DotClass(Class<?> testeeType) {
		super(Class.class, testeeType);	//FIXME should be Object.class
	}

  public Class<Object> execute() {
    return Object.class;
  }
  
  public String text() {
    return CodeGenFct.getName(Object.class, testeeType)+".class";
  }
}
