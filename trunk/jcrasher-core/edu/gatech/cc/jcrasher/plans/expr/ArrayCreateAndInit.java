/*
 * ArrayCreateAndInit.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import edu.gatech.cc.jcrasher.types.ClassWrapper;
import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Initialization with some objects of component type
 * <ul>
 * <li>ClassWrapper --> Class
 * <li>ClassWrapper --> Expression*
 * <li>Expression --> Instance
 * </ul>
 * 
 * <p>
 * Each reference type parameter of each method must be non-null.
 * Each method returns a non-null value.
 * 
 * @param <T> type of created array.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/arrays.html#10.3
 */
public class ArrayCreateAndInit<T> extends ReferenceTypeExpression<T> {

  /**
   * Which plans generate array's components to initialize this value? -
   * zero-elem-array --> empty array - list of params in order to initialize
   * araray
   */
  protected Expression<?>[] componentPlans = null;

  /**
   * Type of leaf component, which is not an array e.g. int
   */
  protected Class<?> leafType = null;

  /**
   * Depth of array-tree
   */
  protected int dimensionality = 1;


  /**
   * Set leafType and dimensionality
   */
  protected void discoverLeafLevel() {   
    leafType = getReturnType().getComponentType();

    /* descend in array-tree */
    while (leafType.isArray() == true) {
      dimensionality += 1;
      leafType = leafType.getComponentType();
    }
  }


  /**
   * Constructor
   */
  public ArrayCreateAndInit(ClassWrapper<T> pCW, Class<?> testeeType) {
    this(pCW.getWrappedClass(), testeeType);
  }


  /**
   * Constructor to be used from Check 'n' Crash.
   */
  public ArrayCreateAndInit(Class<T> returnType, Class<?> testeeType) {
  	super(returnType, testeeType);
  	
    discoverLeafLevel();
  }


  /**
   * get plans for all components
   */
  protected Expression<?>[] getComponentPlans() {
    return notNull(componentPlans);
  }

  /**
   * set plans for all components
   */
  public void setComponentPlans(final Expression<?>[] pPlans) {
    componentPlans = notNull(pPlans);
  }

  
  public T execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException
  {
    T array = (T) Array.newInstance(
      getReturnType().getComponentType(),
      componentPlans.length
    );
    
    for (int i=0; i<componentPlans.length; i++) {
      Array.set(array, i, componentPlans[i].execute());
    }
    return notNull(array);
  }  
  

  /**
   * How to reproduce this value=object? - Value or (recursive)
   * initializer-chain for user-output after some mehtod-call crashed using this
   * object as param. - Example: {{11,12}, {21,22}}
   */
  public String text() {
    
    /* Constructor */
    final StringBuilder res = new StringBuilder();
    res.append("new " + CodeGenFct.getName(leafType, testeeType));

    /* print dimensionality times [] */
    for (int d = 0; d < dimensionality; d++) {
      res.append("[]");
    }

    res.append("{");

    /* Recurse to parameters */
    for (int i = 0; i < componentPlans.length; i++) {
      if (i > 0) {
        res.append(", ");
      } // separator
      res.append(componentPlans[i].text()); // value
    }

    /* return assembeled String */
    res.append("}");
    return notNull(res.toString());
  }
}