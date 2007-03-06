/*
 * ClassWrapperImpl.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.types;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.gatech.cc.jcrasher.Constants;
import edu.gatech.cc.jcrasher.Constants.PlanFilter;
import edu.gatech.cc.jcrasher.Constants.Visibility;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.literals.NullLiteral;

/**
 * Each class or interface X has exactly one ClassWrapper XW. A global
 * mapping: Class --> ClassWrapper holds all tupels (X, XW)*
 * 
 * <p>
 * ClassWrapper knows all preset valus of its wrapped type and all found methods
 * or constructors, which return the wrapped type:
 * <ol>
 * <li>Constructors return an object of its type: X X(P*)
 * <li>Methods can return objects of some type: X Z.foo(P*)
 * <li>Both for each implementing/extending class or interface
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ClassWrapperImpl<T> implements ClassWrapper<T> {

  protected Class<T> wrappedClass = null; // Wrapped Class object

  /**
   * Methods declared anywhere as non-abstract that return the wrapped type
   */
  protected final List<Method> constrMeth = new ArrayList<Method>();


  /**
   * Some preset plans, e.g., {0, 1, -1}. NullLiteral is never included.
   */
  protected final List<Expression<T>> presetPlans = new ArrayList<Expression<T>>();


  /**
   * Child-classes of X: Class.getName() --> Class Holds all tupels (name,
   * class) where: (XImpl implements X) or (Y extends X)
   */
  protected final Hashtable<String, Class<? extends T>> children = 
  	new Hashtable<String, Class<? extends T>>();


  /**
   * Indicates if info of wrapped class has already been extracted and
   * disseminated to this and other wrappers
   */
  protected boolean isSearched = false;


  /**
   * Indicates if wrapped class is part of needed-for-construction-relation of
   * the passed classes. true iff - parameter, enclosing instance of any
   * function that constructs any class of interest. - child of a class of
   * interest (due to subtype-polymorphism).
   * 
   * Only for a class of interest (= having this flag on) will all ways to
   * cunstruct it be printed
   */
  protected boolean isNeeded = false;



  /**
   * Constructor
   */
  public ClassWrapperImpl(final Class<T> pClass) {
    wrappedClass = notNull(pClass);
    check(presetPlans.isEmpty());

    presetPlans.addAll(Arrays.asList((Expression<T>[]) PresetValues.getPreset(pClass)));
  }



  public boolean isLibraryType() {
    notNull(wrappedClass);

    if (wrappedClass.isPrimitive() || wrappedClass.isArray()) {
      return true;
    }

    for (int i = 0; i < Constants.LIBRARY_TYPES.length; i++) {
      if (wrappedClass.getName().indexOf(Constants.LIBRARY_TYPES[i]) >= 0) {
        /*
         * Changed startsWith to (indexOf >= 0) to suppress arrays and inner
         * types as well.
         */
        return true;
      }
    }

    return false;
  }



  /**
   * Indicates if wrapped class is part of needed-for-construction-relation of
   * the passed classes. true iff - parameter, enclosing instance of any
   * function that constructs any class of interest. - child of a class of
   * interest (due to subtype-polymorphism).
   * 
   * Only for a class of interest (= having this flag on) will all ways to
   * cunstruct it be printed
   */
  public boolean isNeeded() {
    return isNeeded;
  }

  /**
   * setty
   */
  public void setIsNeeded() {
    isNeeded = true;
  }


  /**
   * @return whether the wrapped class was searched for typeGraph
   */
  protected boolean isSearched() {
    return isSearched;
  }

  protected void setIsSearched() {
    this.isSearched = true;
  }


  /**
   * @return true iff representing an innner class
   */
  public boolean isInnerClass() {
    notNull(wrappedClass);
    
    if (Modifier.isStatic(wrappedClass.getModifiers())) {
      /* Static member class is never an inner class. */
      return false;
    }
    
    try {
      return wrappedClass.getDeclaringClass() != null;
    }
    catch(NoClassDefFoundError e) {
      /* reflection could not load enclosing class. */    
      return true;
    }        
  }


  /**
   * @return the wrapped class
   */
  public Class<T> getWrappedClass() {
    return wrappedClass;
  }



  /**
   * Gather all methods defined anywhere as non-abstract that return 
   * the wrapped type.
   * 
   * Note that all y with "Y implements/ extends X" are valid, too and can be
   * obtained by calling this method on classwrappers representing these
   * child-classes.
   * 
   * @return each public method X Z.foo(P*) or an empty list.
   */
  public List<Method> getConMeths() {
    return notNull(constrMeth);
  }
  
  
  /**
   * Get constructors of this class of param. visibiility, if non-abstract.
   * 
   * @param visUsed domain from which constructor needs to be visible as
   *          defined in
   *          edu.gatech.edu.gatech.cc.jcrasher.testall.eclipse.Constants (same
   *          package vs. globally)
   * @return each constuctor X(P*) according to pVisibility or an empty list if
   *         abstract class.
   */
  public List<Constructor<T>> getConstrs(final Visibility visUsed) {
    notNull(visUsed);    
    List<Constructor<T>> res = new ArrayList<Constructor<T>>();

    if (Modifier.isAbstract(wrappedClass.getModifiers())) {
      /* Abstract type does not have any constructors. */
      return notNull(res);
    }
    
    Constructor[] constructors = new Constructor[0];    
    try {
      constructors = wrappedClass.getDeclaredConstructors();
    }
    catch(NoClassDefFoundError e) {
      /* reflection could not load some needed class. */
      return notNull(res);
    }

    
    for (Constructor<T> con : constructors) { // Filter for visibility
      
      /* public-public */
      if ((Visibility.GLOBAL.equals(visUsed))
          && Modifier.isPublic(con.getModifiers())
          && Modifier.isPublic(con.getDeclaringClass().getModifiers())) {
          
        /* TODO(csallner): protected method visible in our package */
        res.add(con);
      }

      /* protected, default, public */
      if ((Visibility.PACKAGE.equals(visUsed))
          && (Modifier.isPrivate(con.getModifiers())==false)) {
        res.add(con);
      }
    }

    return notNull(res);
  }


  /**
   * Get all known public constructors of this class, if non-abstract.
   * 
   * @return each public constuctor X(P*) or an empty list if abstract class.
   */
  public List<Constructor<T>> getConstrsVisGlobal() {
    return getConstrs(Visibility.GLOBAL);
  }


  /**
   * Get user-predefined standard representative plans like 0, 1, -1, null
   * 
   * @param planFilter can exclude null for reference types
   * @return List of preset plans of wrapped type (= userdefined database).
   */
  public List<Expression<T>> getPresetPlans(final PlanFilter planFilter) {

    if (Constants.SUPPRESS_NULL_LITERALS)
      return presetPlans; //suppress all null literals.
    
    if (wrappedClass.isPrimitive()) { // no null for primitive
      return presetPlans;
    }

    /* Reference type */
    if (!Constants.isNullIncluded(planFilter)) { // null not desired
      return presetPlans;
    }
    final List<Expression<T>> withNull = 
        new ArrayList<Expression<T>>(presetPlans); // null desired
    withNull.add(new NullLiteral<T>(
        wrappedClass,
        Constants.class)); //FIXME: Breaks if testee from JCrasher package.
    return withNull;
  }



  /**
   * Get all implementing or extending child-classes.
   * 
   * @return all classes S with (S implements X) or (S extends X) or an empty
   *         list.
   */
  public List<Class<? extends T>> getChildren() {
    final List<Class<? extends T>> res = new ArrayList<Class<? extends T>>();
    for (Enumeration<Class<? extends T>> e = children.elements(); e.hasMoreElements();) {
      res.add(e.nextElement());
    }
    return res;
  }



  /**
   * Add a non-abstract constructing method, group by following categories:
   * <ul>
   * <li>public --> of interest to any testing code
   * <li>protected/ default, declared inside package p --> of interest
   * to testing code only if in same package
   * <li>private --> of interest to testing code only if accessiblity checks of
   * compilers are circumvented via reflection
   * 
   * @param pMeth method to be added
   */
  protected void addConstrMeth(final Method pMeth, final Visibility visUsed) {
    notNull(pMeth);
    notNull(visUsed);
    check(Modifier.isAbstract(pMeth.getModifiers()) == false);

    /* Public-public */
    if (Visibility.GLOBAL.equals(visUsed)
        && Modifier.isPublic(pMeth.getModifiers())
        && Modifier.isPublic(pMeth.getDeclaringClass().getModifiers())) {
      constrMeth.add(pMeth);
    }

    /* Protected */
    if (Visibility.PACKAGE.equals(visUsed)
          && !Modifier.isPrivate(pMeth.getModifiers())) {
      constrMeth.add(pMeth);
    }
  }


  /**
   * Add implementing/ extending child
   */
  protected void addChild(final Class<? extends T> pClass) {
    children.put(pClass.getName(), pClass);
  }

  /**
   * @return canonical name of wrapped class
   */
  @Override
  public String toString() {
    return wrappedClass.getCanonicalName();
  }
}
