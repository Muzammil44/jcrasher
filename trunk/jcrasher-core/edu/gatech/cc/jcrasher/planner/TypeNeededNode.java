/*
 * TypeNeededNode.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cc.jcrasher.Constants;
import edu.gatech.cc.jcrasher.Constants.PlanFilter;
import edu.gatech.cc.jcrasher.Constants.Visibility;
import edu.gatech.cc.jcrasher.types.ClassWrapper;
import edu.gatech.cc.jcrasher.types.ClassWrapperImpl;

/**
 * Constructs a TypeNode from a ClassWrapper: extract all constructing functions
 * iff remaining recursion depth >0
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class TypeNeededNode<T> extends TypeNode<T> {

  protected final String name;

  /**
   * Constructor
   * <ol>
   * <li>Grab all (predefined) values
   * <li>Grab all constructing functions iff maxRecursion >= 1
   * </ol>
   * 
   * Precond: maxRecursion >= 0
   * 
   * @param pCW contains all values (and constructing functions)
   * @param remainingRecursion maximal length of function chain to be appended
   * @param filter is the invoking function interested i.e. in null?
   */
  public TypeNeededNode(
      final ClassWrapper<T> pCW, 
      int remainingRecursion,
      final PlanFilter filter,
      final Visibility visUsed) {
    
    notNull(pCW);
    notNull(visUsed);
    check(remainingRecursion >= 0);

    ((ClassWrapperImpl<T>) pCW).setIsNeeded();

    name = pCW.getWrappedClass().getName();
    final List<ExpressionNode<T>> childSpaces = new ArrayList<ExpressionNode<T>>();
    childSpaces.add(new LeafNode<T>(pCW.getPresetPlans(filter)));

    /* NON_NULL enforced above, transitively used values may be null */
    final PlanFilter newFilter = Constants.addNull(filter);

    /* functions only iff wanted and additional chaining allowed */
    if (remainingRecursion > 0) {

      /* same for class and all its implementing/ extending children */
      final List<Class<? extends T>> classes = 
        new ArrayList<Class<? extends T>>(pCW.getChildren());
      classes.add(pCW.getWrappedClass());

      for (Class<? extends T> c : classes) {
        ClassWrapper<?> cw = typeGraph.getWrapper(c);
        notNull(cw);

        if (!cw.isLibraryType()) { // not interested in JDK-defined
                                    // constructors
          for (Constructor<?> con : cw.getConstrs(visUsed)) {
            childSpaces.add(new ConstructorNode(
                con, remainingRecursion, newFilter, visUsed));
          }
        }

        /*
         * Interested in JDK-returning methods iff defined outside the JDK This
         * was ensured during findRules.
         */
        for (Method meth : cw.getConMeths()) { // constructing methods
          childSpaces.add(
              new MethodNode(meth, remainingRecursion, newFilter, visUsed));
        }
      }
    }

    /* set gathered child plan spaces in super class */
    setChildren(childSpaces.toArray(new ExpressionNode[childSpaces.size()]));
  }
  
  
  @Override
  public String toString() {
    return name;
  }
}
