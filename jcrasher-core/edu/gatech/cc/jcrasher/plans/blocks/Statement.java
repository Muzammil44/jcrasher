/*
 * Statement.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.blocks;

import edu.gatech.cc.jcrasher.plans.Executable;

/**
 * Hides a code statement intended to crash some method or constructor
 * <ul>
 * <li>to generate a needed instance
 * <li>to invoke some method to modify a instance (side-effect)
 * <li>to invoke some method/ constructor under test
 * 
 * Unless otherwise noted:
 * <ul>
 * <li>Each reference parameter of every method must be non-null.
 * <li>Each reference return value must be non-null.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.5
 */
public interface Statement extends Executable {

  /**
   * @return a specialized representation of the statement like:
   *   <ul>
   *   <li>A a = new A(null);
   *   <li>b.m(0);
   */
  public String toString(final Class testeeType);
}