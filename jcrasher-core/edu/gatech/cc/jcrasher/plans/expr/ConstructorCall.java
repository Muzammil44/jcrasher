/*
 * ConstructorCall.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import edu.gatech.cc.jcrasher.writer.CodeGenFct;

/**
 * Wraps a constructor - Manages recursion to params of constructor
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @param <T> result type.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.9
 */
public class ConstructorCall<T> extends FunctionCall<T> {

  /**
   * Which plans generate params passed to construct this value -
   * zero-elem-array --> constructor takes no arguments - list of params in
   * order to construct value
   */
  protected Expression<?>[] paramPlans;

  /**
   * How to instantiate an enclosing instance e, on which we can do e.new X.
   * null iff not an inner class
   */
  protected Expression<?> enclosedBy = null;

  /**
   * Which constructing function has generated this value?
   */
  protected Constructor<T> constructor = null;

  /**
   * Check and init fields (except optional enclosing type). 
   */
  protected void initBase(
      Constructor<T> pConstructor,
      Expression[] pConstrParams) {
    
    constructor = notNull(pConstructor);
    paramPlans = notNull(pConstrParams);    
  }
  
  
  protected boolean needsEnclosingInstance() {
    Class<?> myOuterClass = returnType.getEnclosingClass();
    boolean isStatic = Modifier.isStatic(returnType.getModifiers()); 
    return ((myOuterClass!=null) && !isStatic);
  }

  
  /**
   * Constructor - not an inner class
   */
  public ConstructorCall(
  		Class<?> testeeType,
      Constructor<T> pConstructor,
      Expression<?>[] pConstrParams)
  {
  	super(pConstructor.getDeclaringClass(), testeeType);

    initBase(pConstructor, pConstrParams);
    
    /* check arguments for consistency */
     
    if (needsEnclosingInstance()) {
      throw new IllegalArgumentException(
          "Parameter implies that an enclosing instance is needed.");
    }
    
    //TODO(csallner): cover with unit test case    
    if(constructor.getParameterTypes().length != paramPlans.length) {
      throw new IllegalArgumentException(
          "wrong number of arguments");      
    }
  }

  /**
   * Constructor - an inner class
   */
  public ConstructorCall(
  		Class<?> testeeType,
      Constructor<T> pConstructor,
      Expression<?>[] pConstrParams,
      Expression<?> pEnclosing)
  {
  	super(pConstructor.getDeclaringClass(), testeeType);
  	
    initBase(pConstructor, pConstrParams);    
    enclosedBy = pEnclosing;
    
    /* check arguments for consistency */
    
    if (Modifier.isStatic(returnType.getModifiers())) {
      throw new IllegalArgumentException(
          "not for a static class");      
    }
    
    Class<?> myOuterClass = returnType.getEnclosingClass();
    if (myOuterClass==null) {
      throw new IllegalArgumentException(
          "Constructor is not member of an inner type");
    }
    
    check(needsEnclosingInstance());
    
    Class<?> planOuterClass = enclosedBy.getReturnType();
    if (!myOuterClass.isAssignableFrom(planOuterClass)) { //TODO(csallner)
      throw new IllegalArgumentException(
          "Plan for enclosing class is not for our enclosing class");
    }
    
    //TODO(csallner): cover with unit test case
    if(constructor.getParameterTypes().length != (paramPlans.length+1)) {
      throw new IllegalArgumentException(
          "wrong number of arguments");      
    }
  }


  public T execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {

    final Object[] args = new Object[paramPlans.length];
    for (int i=0; i<args.length; i++) {
      args[i] = paramPlans[i].execute();  
    }
    Object[] allArgs = args;  //optional enclosing instance included
    
    /* If inner class: Add an enclosing instance */    
    if (needsEnclosingInstance()) {
      notNull(enclosedBy);
      allArgs = new Object[args.length + 1];
      allArgs[0] = enclosedBy.execute();
      for (int i=0; i<args.length; i++) {
        allArgs[i+1] = args[i];  
      }
    }
     
    constructor.setAccessible(true);  //we want to invoke non-public constr.
    return notNull(constructor.newInstance(allArgs));
  }  
  
  
  /**
   * How to reproduce this value=object? Examples:
   * <ul>
   * <li>new A(new B(1), null)
   * <li>(new Outer()).new Inner(1)
   */
  public String text() {
    notNull(testeeType);    
    final StringBuilder res = new StringBuilder();
    
    /* Add enclosing instance -- if inner class */    

    if (needsEnclosingInstance()) {
      notNull(enclosedBy);
      res.append("("+enclosedBy.text()+").");
    }
    
    /* Constructor call (without enclosing instance) */
    
    res.append("new ");
    
    final String className =
        CodeGenFct.getName(constructor.getDeclaringClass(), testeeType); 
    /* Enc.Nested  -- fully qualified class-name */
    
    if (needsEnclosingInstance()) {  //Nested  --member name only
      final String memberName = 
          className.substring(className.lastIndexOf('.')+1);
      res.append(memberName);
    }
    else {  //Enc.Nested    --entire simple name
      res.append(className);
    }
    
    /* Parameter tail: recurse */
    
    res.append("(");    
    for (int i = 0; i < paramPlans.length; i++) {
      if (i > 0) {  //separator
        res.append(", ");
      }
      res.append(paramPlans[i].text()); // value
    }
    res.append(")");
    
    return notNull(res.toString());
  }
}
