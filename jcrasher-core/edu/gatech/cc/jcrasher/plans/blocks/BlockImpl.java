/*
 * BlockImpl.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.blocks;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cc.jcrasher.plans.expr.Variable;

/**
 * Hides a code block intended to crash some method or constructor:
 * <ul>
 * <li>some statements to generate needed instances
 * <li>some statements to invoke methods/constructors under test
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class BlockImpl implements Block {

  /* method or constructor this block is intended to execute. */
  protected Member testee = null;

  /*
   * Block ::= {Statement*} Sequence of statements, intended to crash a
   * method/ constructor
   */
  protected final List<Statement> blockStmts = new ArrayList<Statement>();

  /* Strictly monotonic increasing counter of local identifiers */
  protected int localIDcount = 0;


  /**
   * Constructor
   */
  public BlockImpl(final Member m) {
    notNull(m);
    testee = m;
  }

  
  public Member getTestee() {
    return notNull(testee);
  }
  

  /**
   * @param pBlockStmts maybe-empty list, but never null
   */
  public void setBlockStmts(final Statement[] pBlockStmts) {
    notNull(pBlockStmts);
    
    blockStmts.clear();
    for (Statement stmt: pBlockStmts) {
      blockStmts.add(stmt);  
    }
  }



  /**
   * To be called from above, like CodeWriter
   * 
   * @param pIdent ident on which to put block closing bracket opening bracket
   *          is appended on line of parent construct
   * @return a specialized representation of the test block like:
   * <pre>
   * {
   *   A.m(null); 
   * }
   * </pre>
   */
  public String toString(final String pIdent, final Class<?> pClass) {
    StringBuilder sb = new StringBuilder("{");

    /* Get sequence of stmt strings */
    for (Statement blockStmt : blockStmts) {
      sb.append(NL);
      sb.append(pIdent+TAB+blockStmt.toString(pClass));
    }
    sb.append(NL);
    sb.append(pIdent+"}");

    return sb.toString();
    // TODO make context (current package) known in test case
    // so its element can strip the package name themselves
  }
  
  
  /**
   * @return a representative example
   */
  @Override
  public String toString() {
    return toString("", testee.getDeclaringClass());
  }


  /**
   * To be called from below, like Statement
   * 
   * TODO(csallner): cover with unit tests
   * 
   * @return an unused local var encoding pClass, e.g. (i1, s2, i3) for a
   *         sequence like: (int, String[], Integer)
   */
  public <V> Variable<V> getNextID(final Class<V> pClass) {
    notNull(pClass);
    Variable<V> res = null;
    localIDcount += 1; // new id

    if (pClass.equals(Void.class)) { // added 2004-08-03
      return new Variable<V>(pClass, "<void"
        + Integer.toString(localIDcount) + ">");
    }

    /* Leaf type represents array */
    Class<?> leafType = pClass;
    while (leafType.isArray() == true) {
      leafType = leafType.getComponentType();
    }

    /* simple name (of component type) */
    String leafName = leafType.getName();

    if (leafType.isPrimitive() == false) {
      /* Remove "p.Enc$" from "Enc$Nested" in class "p.Enc.Nested" */
      if (leafType.getDeclaringClass() != null) {
        String enclosingName = leafType.getDeclaringClass().getName(); // Enc
        leafName = leafName.substring(enclosingName.length() + 1, leafName
          .length());
      }

      /* top level class */
      else {
        /* Extract C from p.q.C */
        String[] leafNameParts = leafName.split("\\.");
        leafName = leafNameParts[leafNameParts.length - 1];
      }
    }

    res = new Variable<V>(pClass, leafName.toLowerCase().charAt(0)
      + Integer.toString(localIDcount));

    return notNull(res);
  }
  

  /**
   * Executes all statements of this block.
   */
  public Boolean execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    for (Statement stmt : blockStmts) {
      stmt.execute();
    }
    return Boolean.TRUE;
  }
}
