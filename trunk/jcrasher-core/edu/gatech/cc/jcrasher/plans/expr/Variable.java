/*
 * Variable.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;


/**
 * Hides variable identifier.
 * To be used with LocalVariableDeclarationStatement.
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/typesValues.html#4.12
 */
public class Variable implements Expression {

  protected String identifier = null; // unique only inside the curernt context
  protected Class type = null; // type of the variable
  protected Object assignedValue = null;  //what we are referring to

  
  public Class getReturnType() {
    return notNull(type);
  }

  
  /**
   * Constructor, to be only called by a Block.
   */
  public Variable(final Class pType, final String pID) {
    type = notNull(pType);
    identifier = notNull(pID);
  }
  
  
  /**
   * Assigns a (dynamic) runtime value. This is not some Plan or Expression.
   * 
   * To be only called by LocalVariableDeclarationStatement while executing
   * a previously created plan.
   * 
   * @param value maybe null.
   */
  public void assign(final Object value) {
    /* null value ok */
    notNull(type);  
    if (value!=null && !type.isPrimitive()) {   //TODO(csallner) check primitive
      check(type.isAssignableFrom(value.getClass()));
    }
    
    assignedValue = value;
  }
  
    
  /**
   * @return maybe null.
   */
  public Object execute() {
    return assignedValue;
  }

  
  /**
   * Local var, generated by LocalVariableDeclarationStatement.
   */
  public String toString(final Class testee) {
    return notNull(identifier);
  }

  
  /**
   * @return a representative example
   */
  @Override
  public String toString() {
    return toString(getReturnType());
  }  
}
