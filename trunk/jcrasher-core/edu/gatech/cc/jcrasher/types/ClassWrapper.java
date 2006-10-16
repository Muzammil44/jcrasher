/*
 * ClassWrapper.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import edu.gatech.cc.jcrasher.Constants.PlanFilter;
import edu.gatech.cc.jcrasher.Constants.Visibility;
import edu.gatech.cc.jcrasher.plans.expr.Expression;

/**
 * Each class or interface X has exactly one ClassWrapper XW.
 * A global mapping: Class --> ClassWrapper holds all tupels (X, XW)*
 * 
 * <p>
 * ClassWrapper knows about each way to construct an instance x of X. The
 * "needs-type-for-construction"-relation, i.e.:
 * <ol>
 * <li> Constructors return an object of its type: X X(P*)
 * <li>Methods can return objects of some type: X Z.foo(P*) 
 * <li>Both for each implementing/extending class or interface
 * 
 * @param <T> wrapped type.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface ClassWrapper<T> {

  /**
   * @return true iff representing an innner class
   */
  public boolean isInnerClass();


  /**
   * Indicates if wrapped class is part of needed-for-construction-relation of
   * the passed classes. true iff - parameter, enclosing instance of any
   * function that constructs any class of interest. - child of a class of
   * interest (due to subtype-polymorphism).
   * 
   * Only for a class of interest (= having this flag on) will all ways to
   * cunstruct it be printed
   */
  public boolean isNeeded();


  /**
   * @return whether the wrapped type is defined by the JDK. This includes all
   *         simple types and all arrays.
   */
  public boolean isLibraryType();


  /**
   * @return the wrapped class
   */
  public Class<T> getWrappedClass();


  /**
   * Get known public non-abstract methods that return an object x.
   * 
   * Note that all y with "Y implements/ extends X" are valid, too and can be
   * obtained by calling this method on classwrappers representing these
   * child-classes.
   * 
   * @return each public method X Z.foo(P*) or an empty list.
   */
  public List<Method> getConMeths();

  /**
   * Get all known public constructors of this class, if non-abstract.
   * 
   * @return each public constuctor X(P*) or an empty list if abstract class.
   */
  public List<Constructor<T>> getConstrsVisGlobal();
  
  /**
   * @return constructors that have minimum visibility vis.
   */
  public List<Constructor<T>> getConstrs(Visibility vis);

  /**
   * Get user-predefined standard representative plans like 0, 1, -1, null
   * 
   * @param planFilter can exclude null for reference types
   * @return List of preset plans of wrapped type (= userdefined database).
   */
  public List<Expression<T>> getPresetPlans(final PlanFilter planFilter);

  /**
   * Get all implementing or extending child-classes.
   * 
   * @return all classes S with (S implements X) or (S extends X) or an empty
   *         list.
   */
  public List<Class<? extends T>> getChildren();
}
