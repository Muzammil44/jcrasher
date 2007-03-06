/*
 * BlockImpl.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.stmt;

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
 * </ul>
 * 
 * <p>
 * Each reference parameter of every method must be non-null.
 * Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class BlockImpl<T> implements Block<T> {

  /* method or constructor this block is intended to execute. */
  protected Member member;
  protected Class<?> testeeType;
  protected String spaces;

  /*
   * Block ::= {Statement*} Sequence of statements, intended to crash a
   * method/ constructor
   */
  protected final List<BlockStatement> blockStmts = new ArrayList<BlockStatement>();

  /* Strictly monotonic increasing counter of local identifiers */
  protected int localIDcount = 0;


  /**
   * Constructor
   */
  public BlockImpl(Class<?> testeeType, Member m, String spaces) {
    this.member = notNull(m);
    this.testeeType = notNull(testeeType);
    this.spaces = notNull(spaces);
  }

  
  public Member getTestee() {
    return notNull(member);
  }
  

  /**
   * @param pBlockStmts maybe-empty list, but never null
   */
  public void setBlockStmts(final List<BlockStatement> blockStatements) {
    notNull(blockStatements);
    this.blockStmts.clear();
    this.blockStmts.addAll(blockStatements);
  }



  /**
   * To be called from above, like CodeWriter
   * 
   * @return a specialized representation of the test block like:
   * <pre>
   * {
   *   A.m(null); 
   * }
   * </pre>
   */
  public String text() {
    StringBuilder sb = new StringBuilder("{");

    /* Get sequence of stmt strings */
    for (BlockStatement blockStmt : blockStmts) {
      sb.append(NL);
      sb.append(spaces+TAB+blockStmt.text());
    }
    sb.append(NL);
    sb.append(spaces+"}");

    return sb.toString();
    // TODO make context (current package) known in test case
    // so its element can strip the package name themselves
  }
  
  
  @Override
  public String toString() {
    return text();
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
      return new Variable<V>(pClass, testeeType, "<void"
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

    String id = leafName.toLowerCase().charAt(0) + Integer.toString(localIDcount);
    res = new Variable<V>(pClass, testeeType, id);

    return notNull(res);
  }
  

  /**
   * Executes all statements of this block.
   */
  public T execute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    Object result = null;
    for (BlockStatement stmt : blockStmts) {
      result = stmt.execute();
    }
    return ((T) result);  //TODO: horrible hack.
  }
}
