/*
 * DotClass.java
 * 
 * Copyright 2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

/**
 * Represents Object.class.
 * 
 * TODO: Generalize to user-defined class.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.8.2
 */
public class DotClass extends SimpleExpression<Class> {

	/**
	 * Constructor
	 */
	public DotClass() {
		super(Class.class, Object.class);
	}

  public String text() {
    return "java.lang.Object.class";
  }
}
