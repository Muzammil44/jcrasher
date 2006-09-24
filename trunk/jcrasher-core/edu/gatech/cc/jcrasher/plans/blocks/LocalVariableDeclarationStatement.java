/*
 * LocalVariableDeclarationStatement.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.blocks;

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
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.4
 */
public class LocalVariableDeclarationStatement implements Statement {
  /*
   * var should be unique in the current context if obtained via
   * block.getNextID()
   */
  protected Variable var = null; // Type VariableDeclaratorId
  protected Expression varInitPlan = null; // VariableInitializer

  /**
   * Constructor
   */
  public LocalVariableDeclarationStatement(
      final Variable pID, 
      final Expression pPlan) {
    
    notNull(pID);
    notNull(pPlan);

    final Class idType = pID.getReturnType();
    final Class planType = pPlan.getReturnType();
    check(idType.isAssignableFrom(planType) //TODO(csallner) unknown statically
      ||  idType.isPrimitive()); //

    var = pID;
    varInitPlan = pPlan;
  }


  /**
   * @return true.
   */
  public Object execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    Object value = varInitPlan.execute();
    var.assign(value);
    return true;  //successfully executed assignment operation
  }  
  
  
  /**
   * @return a specialized representation of the statement, for example:
   * <ul>
   * <li>A a = new A(null);
   * <li>B b = a.m(0);
   */
  public String toString(final Class testee) {
    notNull(testee);
    
    final String className =
        CodeGenFct.getName(var.getReturnType(), testee);

    final String res = className + " " + var.toString(testee) + " = "
      + varInitPlan.toString(testee) + ";";

    return notNull(res);
  }
  
  
  /**
   * @return a representative example
   */
  @Override
  public String toString() {
    return toString(var.getReturnType());
  }
}