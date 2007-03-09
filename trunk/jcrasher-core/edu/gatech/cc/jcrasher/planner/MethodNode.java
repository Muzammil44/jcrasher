/*
 * MethodNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cc.jcrasher.Constants;
import edu.gatech.cc.jcrasher.Constants.PlanFilter;
import edu.gatech.cc.jcrasher.Constants.Visibility;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.types.ClassWrapperImpl;

/**
 * Node to access the plans of a method (sub-) plan space up to a given maximal
 * chaining depth. 
 * 
 * Knows how to concrete MethodCall
 * 
 * @param <T> return type.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class MethodNode<T> extends FunctionNode<T> {

  protected Method meth = null; // wrapped method


  /**
   * Constructor: create dependent type plan space nodes according to pMeth
   * 
   * @param pMeth method, whose plan space is to be traversed
   * @param pMaxRecursion How deep should we traverse the sub-tree?
   */
  public MethodNode(
      final Method pMeth, 
      int pMaxRecursion,
      final PlanFilter filter,
      final Visibility vis) {
    
    check(pMaxRecursion >= 1); // this method eats up one step in depth
    notNull(vis);
    notNull(pMeth);

    meth = pMeth;
    List<TypeNeededNode<?>> depNodes = new ArrayList<TypeNeededNode<?>>();

    /* First dimension: receiver instance */
    if (Modifier.isStatic(pMeth.getModifiers()) == false) { // non-static method
      Class<?> decClass = pMeth.getDeclaringClass();
      ClassWrapperImpl<?> vW = (ClassWrapperImpl) typeGraph.getWrapper(decClass); // receiver
      depNodes.add(new TypeNeededNode(vW, pMaxRecursion - 1, Constants
        .removeNull(filter), vis));
    }

    /* Second, .. n-th dimesion: Add each parameter */
    for (Class<?> paramType : pMeth.getParameterTypes()) {
      ClassWrapperImpl<?> pW = (ClassWrapperImpl) typeGraph.getWrapper(paramType);
      depNodes.add(new TypeNeededNode(pW, pMaxRecursion - 1, filter, vis));
    }

    /* create iterators for each type dimension and set field in super class */
    setParams(depNodes.toArray(new TypeNeededNode[depNodes.size()]));
  }



  /**
   * @param planIndex from [0..getPlanSpaceSize()-1]
   * @return concrete method plan according to index.
   * @see PlanSpaceNode#getPlan(int)
   */
  public Expression<T> getPlan(BigInteger planIndex, Class<?> testeeType) {
    Expression<?>[] depPlans = getParamPlans(planIndex, testeeType); // enforces our precondition

    /* Zero-dim ok, iff non-arg static meth */
    if (depPlans.length == 0) {
      return new MethodCall<T>(testeeType, meth, new Expression[0]);
    }// end traversal of zero-dim space


    /* Build method plan, distribute plans to receiver, params */

    check(depPlans.length > 0); // at least one dimension non-empty

    if (Modifier.isStatic(meth.getModifiers()) == false) { // first dim is
                                                              // receiver
      Expression<?>[] paramPlans = new Expression[depPlans.length - 1];
      for (int j = 0; j < paramPlans.length; j++) {
        paramPlans[j] = depPlans[j + 1];
      }
      return new MethodCall<T>(testeeType, meth, paramPlans, depPlans[0]);
    }
    
    /* Non-static method with >=1 arguments */
    return new MethodCall<T>(testeeType, meth, depPlans);
  }


  /**
   * @return the wrapped method
   */
  Method getMeth() {
    return meth;
  }


  /**
   * @return method signature
   */
  @Override
  public String toString() {
    return meth.toString();
  }

}
