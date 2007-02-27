/*
 * LocalVariableDeclarationStatement.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.stmt;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.InvocationTargetException;

import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.Variable;
import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Hides a code statement to generate a needed instance.
 * 
 * <p>
 * We need only need the following subset:
 * <code>
 * LocalVariableDeclarationStatement
 *     ::= Type VariableDeclaratorId = VariableInitializer;
 * </code>
 * 
 * <p>
 * Each reference type parameter of each method must be non-null.
 * Each method returns a non-null value.
 * 
 * @param <V> type of the wrapped variable, which may differ
 * from the type (Boolean) of the value of executing a statement (true).
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.4
 */
public class LocalVariableDeclarationStatement<V> implements BlockStatement {
  /*
   * var should be unique in the current context if obtained via
   * block.getNextID()
   */
  protected Variable<V> var = null; // Type VariableDeclaratorId
  protected Expression<? extends V> varInitPlan = null; // VariableInitializer

  /**
   * Constructor
   */
  public LocalVariableDeclarationStatement(
  		Class<?> testeeType,
      Variable<V> pID, 
      Expression<? extends V> pPlan) {
    
    notNull(pID);
    notNull(pPlan);

    final Class<V> idType = pID.getReturnType();
    final Class<? extends V> planType = pPlan.getReturnType();
    check(idType.isAssignableFrom(planType) //TODO(csallner) unknown statically
      ||  idType.isPrimitive()); //

    var = pID;
    varInitPlan = pPlan;
  }


  /**
   * @return true.
   */
  public Boolean execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    V value = varInitPlan.execute();
    var.assign(value);
    return Boolean.TRUE;  //successfully executed assignment operation
  }  
  
  
  /**
   * @return a specialized representation of the statement, for example:
   * <ul>
   * <li>A a = new A(null);
   * <li>B b = a.m(0);
   */
  public String text() {    
    return var.textDeclaration()+" = "+varInitPlan.text()+";";
  }
  
  
  /**
   * @return a representative example
   */
  @Override
  public String toString() {
    return text();
  }
}