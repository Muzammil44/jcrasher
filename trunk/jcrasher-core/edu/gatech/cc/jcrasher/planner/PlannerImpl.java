/*
 * PlannerImpl.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.VERBOSE_LEVEL;
import static edu.gatech.cc.jcrasher.types.TypeGraph.typeGraph;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import edu.gatech.cc.jcrasher.Constants.PlanFilter;
import edu.gatech.cc.jcrasher.Constants.Verbose;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.types.ClassWrapper;


/**
 * Generalized planner creates Blocks with chained plans and local variables:
 * Hides class-wrapper- and plans- database, how instances are created, what
 * combination of methods are called etc.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class PlannerImpl implements Planner {

  /* Cache constructed nodes */
  final protected Map<Class, ClassUnderTestImpl> plans = 
    new Hashtable<Class, ClassUnderTestImpl>();


  protected StringBuilder flushRules(boolean isTypeNeeded) {
    final StringBuilder sb = new StringBuilder();
    final ClassWrapper[] wrappers = typeGraph.getWrappers();
    
    for (ClassWrapper wrapper: wrappers) {
      if (wrapper.isNeeded() != isTypeNeeded) {
        continue; // not interested in printing
      }

      sb.append(NL + NL + wrapper.getWrappedClass().getCanonicalName());
      for (Expression value : wrapper.getPresetPlans(PlanFilter.ALL)) { // preset
                                                                  // values
        sb.append(NL + "\t" + value.toString(wrapper.getWrappedClass()));
      }
      if (!wrapper.isLibraryType()) { // not interested in JDK-defined
                                      // constructors
        for (Constructor con : wrapper.getConstrsVisGlobal()) {
          sb.append(NL + "\t" + con.toString()); // methods that return this
                                                  // type
        }
      }

      /*
       * Interested in JDK-returning methods iff defined outside the JDK This
       * was ensured during findRules.
       */
      for (Method meth : wrapper.getConMeths()) {
        sb.append(NL + "\t" + meth.toString()); // constructors
      }
    }
    return sb;
  }


  /**
   * To be called after a getX(c) method has been called for each class c under
   * test
   */
  public void flush() {

    if (Verbose.DEFAULT.equals(VERBOSE_LEVEL)) {
      return; // no output
    }

    /* Verbose.VERBOSE | Verbose.ALL */

    /* Each constructor/ method to be crashed = non-private, non-abstract */
    StringBuilder sb = new StringBuilder(
      "*** Methods and constructors under test:");
    for (ClassUnderTestImpl cPlan : plans.values()) {
      sb.append(NL + NL + cPlan.getWrappedClass().getCanonicalName()); // qualified
                                                                        // class
                                                                        // name

      for (PlanSpaceNode pNode : cPlan.getChildren()) {
        FunctionNode fNode = (FunctionNode) pNode;
        sb.append(NL + "\t" + fNode.toString()); // method or constructor under
                                                  // test
      }
    }


    /*
     * 2. Each value and public, non-abstract constructing function of each
     * needed instance and all of their children.
     */
    sb.append(NL + NL + NL + "*** Rules to create needed values:");
    sb.append(flushRules(true));

    if (Verbose.ALL.equals(VERBOSE_LEVEL)) {
      sb.append(NL + NL + NL + "*** Rules that were not needed:");
      sb.append(flushRules(false));
    }

    sb.append(NL + NL);
    System.out.println(sb);
  }
}
