/*
 * ConstructorNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cc.jcrasher.Constants;
import edu.gatech.cc.jcrasher.Constants.PlanFilter;
import edu.gatech.cc.jcrasher.Constants.Visibility;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.types.ClassWrapperImpl;

/**
 * Node to access the plans of a constructor (sub-) plan space up to a given
 * maximal chaining depth. Knows how to generate a concrete ConstructorCall
 * 
 * @param <T> type that declares the wrapped constructor.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ConstructorNode<T> extends FunctionNode<T> {

  protected Constructor<T> con = null; // wrapped constructor


  /**
   * Constructor: set dependent type plan space iterator according to pCon by
   * creating iterators, i.e., over param-classes
   * 
   * @param pCon constructor whose plan space is to be traversed
   * @param pMaxRecursion How deep should we traverse the sub-tree?
   * @param filter Are we allowed to use null?
   */
  public ConstructorNode(
      final Constructor<T> pCon, 
      int pMaxRecursion,
      final PlanFilter filter,
      final Visibility vis) {
    
    check(pMaxRecursion >= 1); // this method eats up one step in depth
    notNull(vis);
    notNull(pCon);

    con = pCon;
    List<TypeNeededNode<T>> depNodes = new ArrayList<TypeNeededNode<T>>();

    /*
     * First, .. n-th dimesion: Add each parameter Inner class: Reflection
     * returns enclosing type as first parameter
     */
    Class<?>[] paramsTypes = con.getParameterTypes();
    for (int j = 0; j < paramsTypes.length; j++) {
      ClassWrapperImpl<?> paramWrapper = 
        (ClassWrapperImpl<?>) typeGraph.getWrapper(paramsTypes[j]);

      /* Filter (null): First param might be the enclosing type */
      PlanFilter planFilter = filter;
      if ((j == 0)
        && (typeGraph.getWrapper(con.getDeclaringClass()).isInnerClass() == true)) {
        planFilter = Constants.removeNull(filter);
      }
      depNodes.add(new TypeNeededNode(paramWrapper, pMaxRecursion - 1, planFilter, vis));
    }
    setParams(depNodes.toArray(new TypeNeededNode[depNodes.size()]));
  }



  /**
   * Precond: 0 <= planIndex < getPlanSpaceSize()
   * 
   * @return concrete method plan according to index.
   * @see PlanSpaceNode#getPlan(int)
   */
  public Expression<T> getPlan(BigInteger planIndex, Class<?> testeeType) {
    Expression<?>[] depPlans = getParamPlans(planIndex, testeeType); // enforces our precondition

    /* distinguish inner class from params */
    if (typeGraph.getWrapper(con.getDeclaringClass()).isInnerClass()) {
      /* first dim is enclosing instance */
                                                                       
      check(depPlans.length >= 1);

      Expression<?>[] paramPlans = new Expression[depPlans.length - 1];
      for (int j = 0; j < paramPlans.length; j++) {
        paramPlans[j] = depPlans[j + 1];
      }
      return new ConstructorCall<T>(testeeType, con, paramPlans, depPlans[0]);
    } 
    
    /* Non-inner class constructor with >=0 arguments */
    return new ConstructorCall<T>(testeeType, con, depPlans);
  }


  /**
   * @return the wrapped constructor
   */
  Constructor<T> getCon() {
    return con;
  }


  /**
   * @return method signature
   */
  @Override
  public String toString() {
    return con.toString();
  }
}
