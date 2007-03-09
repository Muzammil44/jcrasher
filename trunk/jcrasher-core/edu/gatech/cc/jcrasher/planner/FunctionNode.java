/*
 * FunctionNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.isNonNeg;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.math.BigInteger;

import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.types.TypeGraph;
import edu.gatech.cc.jcrasher.types.TypeGraphImpl;

/**
 * Node to access the plans of a method or constructor (sub-) plan space up to a
 * given maximal chaining depth.
 * <ul>
 * <li>Child plan spaces are all TypeNode.
 * <li>Knows how to transfer an index in a certain combination of child-plans.
 * </ul>
 * 
 * @param <T> return type.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class FunctionNode<T> implements ExpressionNode<T> {

	protected final TypeGraph typeGraph = TypeGraphImpl.instance();
	
  /**
   * Child types, i.e. receiver and param types up to our max depth - 1
   */
  protected TypeNeededNode<?>[] parameters;

  /**
   * Size of each child's plan space (given their max depth),  e.g.:
   * <ul>
   * <li>(3, 5, 2) --> own size = 30
   * <li>(0, 10, 10, 5) --> 0
   * </ul>
   */
  protected BigInteger[] paramSizes;
  
  /**
   * own plan space size = Product of childrens' plan space sizes.
   */
  protected BigInteger planSpaceSize = BigInteger.ZERO;
  
  /**
   * Caches size of the plan space size to the right.
   * Used to count in the plans.
   * 
   * E.g., for paramSizes (3, 5, 2) we get (5*2*1, 2*1, 1).
   */
  protected BigInteger[] canonicalSubSapceSizes;
  
  /**
   * Sets the function parameters.
   * To be called by extending classes only.
   * 
   * @param parameters The children to set
   */
  protected void setParams(TypeNeededNode<?>[] pChildren) {
    this.parameters = pChildren;
  }  
  

  /**
   * Retrieves childrens' plans (side-effect-free), according to the canonical
   * order. E.g., for sub spaces of sizes (4, 3, 7)
   * the indices are a*3*7 + b*7 + c*1, with
   * <ul>
   * <li>a element [0..3],
   * <li>b element [0..2],
   * <li>c element [0..6]
   * </ul>
   * 
   * Following are example results:
   * <ul>
   * <li>0:  (0, 0, 0)
   * <li>6:  (0, 0, 6)
   * <li>7:  (0, 1, 0) 
   * <li>83: (3, 2, 6)
   * </ul>
   * 
   * The dimensions are checked from left, for example, for 83:
   * <ul>
   * <li>83 / 3*7*1 = 3 + 20/21 --> children[0].getPlan(3)
   * <li>20 / 7*1 = 2 + 6/7 --> children[1].getPlan(2)
   * <li>6 / 1 = 6 --> children[2].getPlan(6)
   * </ul>
   * 
   * @param planIndex the index of the plan according to the node's canonical
   *          order, taken from [0..getPlanSpaceSize()-1]
   * @return childrens' plans according to the ordering semantics, never null
   */
  public Expression<?>[] getParamPlans(BigInteger planIndex, Class<?> testeeType) {
    check(isNonNeg(planIndex));
    check(planIndex.compareTo(getPlanSpaceSize()) < 0); //fills cache.
    /* terminated iff canonicalSubSapceSizes[any]==0 */

    Expression<?>[] res = new Expression[parameters.length]; // no children --> empty list

    /* Determine index in each child dimension */
    BigInteger currentIndex = planIndex; // index into remaining sub spaces
    for (int i = 0; i < res.length; i++) {
    	/* division, lower bounded index in child's dimension. */
      BigInteger childIndex = currentIndex.divide(canonicalSubSapceSizes[i]);
      res[i] = parameters[i].getPlan(childIndex, testeeType);
      
      /* leftover from division */
      currentIndex = 
        currentIndex.subtract(childIndex.multiply(canonicalSubSapceSizes[i]));
    }

    return res;
  }
  
  
  /**
   * Caches sizes of all sub plan spaces to speed up getPlan(int).
   * 
   * A method's or constructor's plan space size is the product of its child
   * plan spaces.
   * 
   * @return size of this sub plan space = nr different plans this plan space
   *         can return via getPlan(int)
   */
  public BigInteger getPlanSpaceSize() {
    notNull(parameters);

    if (paramSizes != null) //cache hit
      return planSpaceSize;
    
    /* Compute childrens' and own plan space sizes */
    
    paramSizes = new BigInteger[parameters.length];

    /* Compute child sizes recursively */
    for (int i = 0; i < parameters.length; i++) {
      paramSizes[i] = parameters[i].getPlanSpaceSize();
    }

    /* Multiply childrens' plan space sizes */
    BigInteger res = BigInteger.ONE; //no children: one plan for static non-arg meth
    for (BigInteger childSize : paramSizes) {
      res = res.multiply(childSize); // first zero will zero the entire result
    }
    planSpaceSize = res;

    /* Compute canonical sub space sizes for each dimesion */
    canonicalSubSapceSizes = new BigInteger[parameters.length];
    for (int i = parameters.length - 1; i >= 0; i--) {
      if (i == parameters.length - 1) //right-most counts by one. 
        canonicalSubSapceSizes[i] = BigInteger.ONE;
      else
        canonicalSubSapceSizes[i] = 
          paramSizes[i+1].multiply(canonicalSubSapceSizes[i+1]);
    }

    return planSpaceSize;
  }
}
