/*
 * Block.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.stmt;

import java.lang.reflect.Member;
import java.util.List;

import edu.gatech.cc.jcrasher.plans.expr.Variable;

/**
 * Represents a code block, which is a possibly empty sequence
 * of block-statements. For JCrasher most blocks look like follows. 
 * <ol>
 * <li>Some statements to generate needed instances.
 * <li>Some statements to invoke methods/constructors under test.
 * </ol>
 * 
 * The block includes its brackets:
 * {
 *   int x = 3;
 *   C.meth(x);
 * }
 * 
 * Unless otherwise noted:
 * <ul>
 * <li>Each reference parameter of every method must be non-null.
 * <li>Each reference return value must be non-null.
 * </ul>
 * 
 * @param <T> return type of the last expression.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.2
 */
public interface Block<T> extends Statement<T> {

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
   * @param blockStatements (empty) list, but never null
   */
  public void setBlockStmts(final List<BlockStatement> blockStatements);
}
