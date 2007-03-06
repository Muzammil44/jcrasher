/*
 * NullLiteral.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import edu.gatech.cc.jcrasher.plans.expr.ReferenceTypeExpression;
import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Provides plan for null reference.
 * 
 * Who needs a null literal? Be very careful to only
 * generate a null literal in a test case if you really want it
 * (i.e., only if a powerful program analysis has found an interesting
 * bug that requires a null literal).
 * It is easy to swamp the user with test cases that pass null
 * to methods under test. The user does not want to see those NPE.
 * 
 * @param <T> type of null literal.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.7
 */
public class NullLiteral<T> extends ReferenceTypeExpression<T> {

  /**
   * Constructor
   * 
	 * @param returnType never null.
	 * @param testeeType never null.
   */
  public NullLiteral(Class<T> returnType, Class<?> testeeType) {
  	super(returnType, testeeType);
  }

  /**
   * @return null.
   */
  public T execute() {
    return null;
  }  


  /**
   * How to reproduce this value=object? (Type) null
   */
  public String text() {
    return 
    	"("+CodeGenFct.getName(returnType,testeeType)+")null";
  }
}
