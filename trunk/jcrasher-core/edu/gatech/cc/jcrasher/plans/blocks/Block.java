/*
 * Block.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.blocks;

import java.lang.reflect.Member;

import edu.gatech.cc.jcrasher.plans.Executable;
import edu.gatech.cc.jcrasher.plans.expr.Variable;

/**
 * Hides a code block intended to crash some method or constructor
 * 
 * <ol>
 * <li>some statements to generate needed instances
 * <li>some statements to invoke methods/constructors under test
 * 
 * Unless otherwise noted:
 * <ul>
 * <li>Each reference parameter of every method must be non-null.
 * <li>Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.2
 */
public interface Block extends Executable<Boolean> {

  /**
   * To be called from above, like CodeWriter
   * 
   * @param pIdent ident on which to put block opening/ closing bracket
   * @param testee the class under test
   * @return a specialized representation of the test block like:
   *  { A.m(null); }
   */
  public String toString(final String pIdent, final Class<?> testee);


  /**
   * @return testee that this block contains code for calling.
   */
  public Member getTestee();


  /**
   * To be called from below, like Statement
   * 
   * @param pClass type for which we need a new id.
   * 
   * @return an unused local var encoding pClass, e.g. (i1, s2, i3) for a
   *         sequence like: (int, String[], Integer)
   */
  public <V> Variable<V> getNextID(final Class<V> pClass);

  /**
   * @param pBlockStmts (empty) list, but never null
   */
  public void setBlockStmts(final Statement[] pBlockStmts);
}
