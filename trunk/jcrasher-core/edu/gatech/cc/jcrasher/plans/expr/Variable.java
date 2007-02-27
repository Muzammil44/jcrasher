/*
 * Variable.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;
import edu.gatech.cc.jcrasher.writer.CodeGenFct;


/**
 * Hides variable identifier.
 * To be used with LocalVariableDeclarationStatement.
 * 
 * @param <T> return type, the static type of the variable.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/typesValues.html#4.12
 */
public class Variable<T> extends SimpleExpression<T> {

	protected Class<?> testeeType;
  protected String identifier; // unique only inside the curernt context

  
  /**
   * Constructor, to be only called by a Block.
   */
  public Variable(
  		Class<T> returnType,
  		Class<?> testeeType,
  		String identifier)
  {
  	super(returnType, null);

  	this.testeeType = notNull(testeeType);
    this.identifier = notNull(identifier);
  }
  
  
  /**
   * Assigns a (dynamic) runtime value. This is not some Plan or Expression.
   * 
   * To be only called by LocalVariableDeclarationStatement while executing
   * a previously created plan.
   * 
   * @param pValue maybe null.
   */
  public void assign(T pValue) {
    /* null value ok */
    notNull(returnType);  
    if (pValue!=null && !returnType.isPrimitive()) {   //TODO(csallner) check primitive
      check(returnType.isAssignableFrom(pValue.getClass()));
    }
    
    value = pValue;
  }

  
  /**
   * @return variable name.
   */
  public String text() {
    return notNull(identifier);
  }
  
  /**
   * @return Type varName. Example: "int a".
   */
  public String textDeclaration() {
  	return CodeGenFct.getName(returnType,testeeType)+" "+text();
  }
}
