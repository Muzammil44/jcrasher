/*
 * MethodCall.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Wraps a method - Manages recursion to params and optional receiver instance
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @param <T> return type of method.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.12
 */
public class MethodCall<T> extends FunctionCall<T> {

  /**
   * Which plans generate params passed to construct this value -
   * zero-elem-array --> constructor takes no arguments - list of params in
   * order to construct value
   */
  protected Expression<?>[] paramPlans = null;

  /**
   * How to instantiate the object on which the method will be instantiated? -
   * null iff constructor or static method
   */
  protected Expression<?> receiverPlan = null;

  /**
   * Which method has generated this value?
   */
  protected Method meth = null;

  
  protected void initBase(Method pMeth, Expression<?>[] pConstrParams){
    notNull(pMeth);
    notNull(pConstrParams);

    meth = pMeth;
    paramPlans = pConstrParams;
    
    //TODO(csallner): cover with unit test case
    if(meth.getParameterTypes().length != paramPlans.length) {
      throw new IllegalArgumentException(
          "wrong number of arguments");      
    }
  }

  
  /**
   * Constructor - static method
   */
  public MethodCall(
  		Class<?> testeeType,
      Method pMeth, 
      Expression<?>[] pConstrParams)
  {
  	super((Class<T>) pMeth.getReturnType(), testeeType);
    initBase(notNull(pMeth), notNull(pConstrParams));

    /* check consistency of arguments */
    
    if (!Modifier.isStatic(meth.getModifiers())) {
      throw new IllegalArgumentException(
          "Not a static method");
    }
  }


  /**
   * Constructor - instance method
   */
  public MethodCall(
  		Class<?> testeeType,
      Method pMeth,
      Expression<?>[] pConstrParams,
      Expression<?> pReceiverPlan)
  {
  	super((Class<T>) pMeth.getReturnType(), testeeType);
  	
    initBase(notNull(pMeth), notNull(pConstrParams));
    receiverPlan = notNull(pReceiverPlan);
    
    /* check consistency of arguments */

    if (Modifier.isStatic(meth.getModifiers())) {
      throw new IllegalArgumentException(
          "Not an instance method");
    }
    
    Class<?> receiverType = meth.getDeclaringClass();
    if (!receiverType.isAssignableFrom(receiverPlan.getReturnType())) { //TODO(csallner)
      throw new IllegalArgumentException(
          "Receiver instance creation plan is not compatible with" +
          "method signature."); 
    }
  }


  /**
   * @return maybe null.
   */
  public T execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    /* instance meth needs a receiver */
    Object receiver = null;
    if (Modifier.isStatic(meth.getModifiers()) == false) {
      receiver = receiverPlan.execute(); 
    }
        
    final Object[] args = new Object[paramPlans.length];
    for (int i=0; i<args.length; i++) {
      args[i] = paramPlans[i].execute();  
    }

    /* passing null receiver for static method is ok */
    meth.setAccessible(true);   //we want to call non-public methods.
    return (T) meth.invoke(receiver, args);
  }  
  
  
  /**
   * How to reproduce this value=object? For example:
   * <ul>
   * <il>new A(new B(1), null)
   */
  public String text() {
    
    /* Fully qualified class-name: Enc.Nested */
    final String className = 
        CodeGenFct.getName(meth.getDeclaringClass(), testeeType);
    final StringBuilder res = new StringBuilder();

    /* ClassName.staticMeth( */
    if (Modifier.isStatic(meth.getModifiers())) {
      res.append(className + "." + meth.getName() + "(");
    }

    else { // (new Receiver()).instanceMeth(
      if (receiverPlan instanceof ConstructorCall) {
        res.append("(" + receiverPlan.text() + ")." + meth.getName()
            + "(");
      }
      else { // A.b().conMeth(
        res.append(receiverPlan.text() + "." + meth.getName() + "(");
      }
    }


    /* Parameter tail: recurse */
    for (int i = 0; i < paramPlans.length; i++) {
      if (i > 0) {
        res.append(", ");
      } // separator
      res.append(paramPlans[i].text()); // value
    }

    /* return assembeled String */
    res.append(")");
    return notNull(res.toString());
  }  
}
