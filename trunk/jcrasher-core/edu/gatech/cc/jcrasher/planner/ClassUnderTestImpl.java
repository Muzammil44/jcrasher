/*
 * ClassUnderTestImpl.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.gatech.cc.jcrasher.Constants.PlanFilter;
import edu.gatech.cc.jcrasher.Constants.Visibility;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.plans.expr.Variable;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.plans.stmt.BlockImpl;
import edu.gatech.cc.jcrasher.plans.stmt.BlockStatement;
import edu.gatech.cc.jcrasher.plans.stmt.ExpressionStatement;
import edu.gatech.cc.jcrasher.plans.stmt.LocalVariableDeclarationStatement;

/**
 * Constructs a TypeNode a loaded class under test: extract all public
 * non-abstract methods and constructors currently 1:1 mapping from function
 * plan to block.
 * 
 * @param <T> wrapped class.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ClassUnderTestImpl<T> 
extends ClassUnderTest<T> {

  protected Class<T> wrappedClass = null;
  protected String testBlockSpaces = TAB+TAB;

  /**
   * Constructor to be used from outside JCrasher---to just use the
   * code-creation API.
   */
  public ClassUnderTestImpl() {
    /* Empty */
  }

  
  /**
   * Gives more flexibility - supportes non-public functions under test.
   */
  public ClassUnderTestImpl(
      final Class<T> c,
      int remainingRecursion,
      final Visibility visTested,
      final Visibility visUsed) {
    
    notNull(c);
    notNull(visTested);
    notNull(visUsed);
    check(remainingRecursion > 0);

    wrappedClass = c;

    /* collect sub plan spaces */
    List<FunctionNode<?>> childSpaces = new ArrayList<FunctionNode<?>>();
   
    /* Crash any declared public constructor iff class non-abstract */
    if (Modifier.isAbstract(c.getModifiers()) == false) {
      for (Constructor<T> con : (Constructor<T>[]) c.getDeclaredConstructors()) {// all declared
        //TODO align with Java semantics
        if (Visibility.PACKAGE.equals(visTested) &&
            !Modifier.isPrivate(con.getModifiers())) {
          childSpaces.add(new ConstructorNode<T>(con, remainingRecursion,
            PlanFilter.ALL, visUsed));          
        }
        
        if (Visibility.GLOBAL.equals(visTested) &&
            Modifier.isPublic(con.getModifiers())) {
          childSpaces.add(new ConstructorNode<T>(con, remainingRecursion,
            PlanFilter.ALL, visUsed));
        }
      }
    }

    /* Crash any declared public non-abstract method */
    for (Method meth : c.getDeclaredMethods()) {
      if (Modifier.isAbstract(meth.getModifiers())) {
        continue;
      }
          
      if (Visibility.PACKAGE.equals(visTested) &&
          !Modifier.isPrivate(meth.getModifiers())) {
        childSpaces.add(
            new MethodNode(meth, remainingRecursion, PlanFilter.ALL, visUsed));        
      }
      
      if (Visibility.GLOBAL.equals(visTested) &&
          Modifier.isPublic(meth.getModifiers())) {
        childSpaces.add(
            new MethodNode(meth, remainingRecursion, PlanFilter.ALL, visUsed));
      }
    }

    /* set gathered child plan spaces in super class */
    setChildren(childSpaces.toArray(new FunctionNode[childSpaces.size()]));    
  }
  

  protected Class<T> getWrappedClass() {
    return wrappedClass;
  }


  /**
   * Retrieve block with given index from the underlying class's plan space.
   * 
   * Precond: 0 <= planIndex < getPlanSpaceSize() Postcond: no side-effects
   */
  public Block<?> getBlock(BigInteger planIndex) {
    Block<?> res = null;
    
    /* retrieve function's childrens' plans of given index */
    int child = getChildIndex(planIndex);
    FunctionNode<?> node = (FunctionNode<?>) children[child];    
    BigInteger childPlanIndex = getChildPlanIndex(child, planIndex);
    
    Expression<?>[] paramPlans = node.getParamPlans(childPlanIndex, wrappedClass);

    if (node instanceof ConstructorNode) {  //TODO: hack
      ConstructorNode<T> conNode = (ConstructorNode<T>) node;
      res = getTestBlockForCon(conNode.getCon(), paramPlans);
    } 
    else {
      MethodNode<?> methNode = (MethodNode<?>) node;
      res = getTestBlockForMeth(methNode.getMeth(), paramPlans);
    }

    return notNull(res);
  }



  /**
   * Generalized planning creates a block given a constructor and plans to
   * invoke its needed types. - Declare needed instance variables and initialize
   * them with chain-plans - Invoke constructor under test on these local
   * variables
   * 
   * 2004-04-22 changed to public to allow access from ESC extension.
   */
  public Block<?> getTestBlockForCon(
      Constructor<T> pCon,
      Expression<?>[] curPlans)
  {
    notNull(pCon);
    notNull(curPlans);   

    final Class<T> testeeType = pCon.getDeclaringClass();
    final Block<?> b = new BlockImpl(testeeType, pCon, testBlockSpaces); // context for this combination

    /* Simple version: one stmt for each instance and exec fct */
    final BlockStatement<?>[] bs = new BlockStatement[curPlans.length + 1];

    /* Keep track of new created local instances: all needed */
    final Variable<?>[] ids = new Variable[curPlans.length];
    Class<?>[] paramsTypes = pCon.getParameterTypes();

    /* Generate local variable for each needed instance (-plan) */
    for (int i = 0; i < curPlans.length; i++) {
      ids[i] = b.getNextID(paramsTypes[i]); // A a = ...
      bs[i] = new LocalVariableDeclarationStatement(
      		ids[i],
      		curPlans[i]);
    }

    /*
     * Statement for constructor under test
     */
    /* Generate identifer to execute the con under test */
    //Variable<T> returnID = b.getNextID(pCon.getDeclaringClass()); // A a =
                                                                      // ...

    /*
     * Generate conPlan for con under test, which references all local
     * identifiers
     */
    ConstructorCall<T> conPlan = null;
    if (typeGraph.getWrapper(pCon.getDeclaringClass()).isInnerClass()) {
      /* first dim is enclosing instance */                              
      Expression[] paramPlans = new Expression[curPlans.length - 1];
      for (int j = 0; j < paramPlans.length; j++) {
        paramPlans[j] = ids[j + 1];
      }
      conPlan = new ConstructorCall<T>(testeeType, pCon, paramPlans, ids[0]);
    } 
    else { // Non-inner class constructor with >=1 arguments
      conPlan = new ConstructorCall<T>(testeeType, pCon, ids);
    }

    /* Last statement */
    bs[curPlans.length] = 
    	//new LocalVariableDeclarationStatement<T>(returnID, conPlan);
    	new ExpressionStatement<T>(conPlan);

    /*
     * Assemble and append generated block
     */
    List<BlockStatement> blockStatements = new LinkedList<BlockStatement>();
    for(BlockStatement blockStatement: bs)  //TODO: wasteful conversion.
      blockStatements.add(blockStatement);
    
    b.setBlockStmts(blockStatements);
    
    return b;
  }


  /**
   * Generalized planning creates a block given a method and plans to invoke its
   * needed types. - Declare needed instance variables and initialize them with
   * chain-plans - Invoke method under test on these local variables
   * 
   * 2004-04-22 changed to public to allow access from ESC extension.
   */
  public Block<?> getTestBlockForMeth(
      Method pMeth,
      Expression<?>[] curPlans) {
    
    notNull(pMeth);
    notNull(curPlans);
    
    final Class<T> testeeType = (Class<T>) pMeth.getDeclaringClass();
    final Block<?> b = new BlockImpl(testeeType, pMeth, testBlockSpaces); // context for this combination

    /* Simple version: one stmt for each instance and exec fct */
    BlockStatement<?>[] bs = new BlockStatement[curPlans.length + 1];

    /* Keep track of new created local param-instances */
    Expression<?>[] paramPlans = null;
    if (Modifier.isStatic(pMeth.getModifiers()) == false) { // first dim is
                                                            // receiver instance
      paramPlans = new Expression[curPlans.length - 1];
      for (int j = 0; j < paramPlans.length; j++) {
        paramPlans[j] = curPlans[j + 1];
      }
    } else {
      paramPlans = curPlans;
    }

    Class<?>[] paramsTypes = pMeth.getParameterTypes();
    Variable<?>[] paramIDs = new Variable[paramPlans.length];

    /* Generate local variable for each needed param instance (-plan) */
    for (int i = 0; i < paramIDs.length; i++) {
      paramIDs[i] = b.getNextID(paramsTypes[i]); // A a = ...
      bs[i] = new LocalVariableDeclarationStatement(paramIDs[i], paramPlans[i]);
    }


    /*
     * StatementExpressiong for method under test - TODO: generate local var iff
     * non-void meth
     */
    /*
     * Generate conPlan for meth under test, which references all param
     * identifiers
     */
    MethodCall<?> conPlan = null;
    if (Modifier.isStatic(pMeth.getModifiers()) == false) { // first dim is
                                                            // receiver instance

      /* Generate local variable for receiver */
      Variable<?> vID = b.getNextID(pMeth.getDeclaringClass()); // A a = ...
      bs[curPlans.length - 1] = 
      	new LocalVariableDeclarationStatement( 
      			vID,
      			curPlans[0]);

      conPlan = new MethodCall(testeeType, pMeth, paramIDs, vID);
    } else { // Static method with >=1 arguments
      conPlan = new MethodCall(testeeType, pMeth, paramIDs);
    }

    /* Last statement */
    bs[curPlans.length] = new ExpressionStatement(conPlan);


    /* Assemble and append generated block */
    List<BlockStatement> blockStatements = new LinkedList<BlockStatement>();
    for(BlockStatement blockStatement: bs)  //TODO: wasteful conversion.
      blockStatements.add(blockStatement);
    
    b.setBlockStmts(blockStatements);
    
    return b;
  }
  
  @Override
  public String toString() {
    return wrappedClass.getName();
  }
}
