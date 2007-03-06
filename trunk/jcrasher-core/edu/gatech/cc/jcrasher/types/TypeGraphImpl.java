/*
 * TypeGraphImpl.java
 * 
 * Copyright 2002,2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.types;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import edu.gatech.cc.jcrasher.Constants.Visibility;

/**
 * Singleton.
 * <ul>
 * <li>Builds up relation from user-specified classes.
 * <li>Creates ClassWrappers and sets them up (protected field-access for flags)
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class TypeGraphImpl extends AbstractTypeGraph {

  /* Static members */

  protected static TypeGraph theInstance;


  /**
   * @return non-null singleton
   */
  public static TypeGraph instance() {
  	if (theInstance==null)
  		theInstance = new TypeGraphImpl();
  	
    return theInstance;
  }


  /**
   * Constructor to be called only from class initializer.
   */
  protected TypeGraphImpl() {
    /* Limit visibility */
  }

  /**
   * Queue super, enclosing, and enclosed types of cw
   */
  protected void queueFamily(final ClassWrapper<?> cw) {
    /*
     * Child-of-relation: Store and queue parents for search Class X: For each
     * interface S with "X implements S" do Interface X: For each interface S
     * with "X extends S" do
     */   
    for (Class<?> superInterface: cw.getWrappedClass().getInterfaces()) {
      if (!ClassSourceImpl.instance().initializeDeep(superInterface)) {
        continue;   //skip super interface we cannot fully initialize.
      }
      ClassWrapperImpl superIW = (ClassWrapperImpl) getWrapper(superInterface);
      superIW.addChild(cw.getWrappedClass());
    }
    // Q: Are transitive implemented interfaces returned as well?
    // A: No, gladly not [Class.getInterfaces()]
    // For class R with X extends R do
    Class<?> superClass = cw.getWrappedClass().getSuperclass();
    if (ClassSourceImpl.instance().initializeDeep(superClass)) {
      ClassWrapperImpl superCW = (ClassWrapperImpl) getWrapper(superClass);
      superCW.addChild(cw.getWrappedClass());
    }
    // else wrappedClass was an interface, a primitive type, Object, or void


    /* Queue nested classes for search */
    
    Class[] nestedClasses = new Class[0];
    try {
      nestedClasses = cw.getWrappedClass().getDeclaredClasses();
    }
    catch(Throwable e) {
      /* Java reflection crashed. */
    }
    
    for (Class<?> nestedClass: nestedClasses) {
      if (ClassSourceImpl.instance().initializeDeep(nestedClass)) {
        getWrapper(nestedClass);
      }
    }

    
    /* Queue nesting class for search */
    
    Class<?> nestingClass = null; 
    
    try {
      nestingClass = cw.getWrappedClass().getDeclaringClass();
    }
    catch(Throwable e) {
      /* Java reflection crashed. */
    }
    if (ClassSourceImpl.instance().initializeDeep(nestingClass)) {
      getWrapper(nestingClass);
    }    
  }


  /**
   * Add all typeGraph to wrapper of return type iff return type complex. This
   * implements the rule: JCrasher uses for any simple type (int, boolean, etc.)
   * only predefined values for test generation.
   */
  protected <T> void queueMethParams(final ClassWrapper<T> cw, final Visibility visUsed) {
    /*
     * Methods: store and queue params, return types for search for each
     * non-abstract declared (incl. overridden) method do
     */
    Method[] methods = new Method[0];
    try {
      methods = cw.getWrappedClass().getDeclaredMethods();
    }
    catch(Throwable e) {
      /* reflection crashed as some class is not loadable */
    }
    
    for (Method meth: methods) {
      Class<?> returnType = meth.getReturnType();        
      if (!ClassSourceImpl.instance().initializeDeep(returnType)) {
        continue;   //skip class we cannot fully initialize.
      }     

      ClassWrapperImpl rW = (ClassWrapperImpl) getWrapper(returnType);
      
      if ((Modifier.isAbstract(meth.getModifiers())==false) &&
          (rW.getWrappedClass().isPrimitive()==false)) {
        rW.addConstrMeth(meth, visUsed); //found rule for return type
      }

      for (Class<?> paramType : meth.getParameterTypes()) {
        
        if (ClassSourceImpl.instance().initializeDeep(paramType)) {
          getWrapper(paramType); // Queue param types even for non-public meths
        }                
      }
    }


    /* Constructors: Queue params for search */
    
    Constructor[] constructors = new Constructor[0];
    try {
      constructors = cw.getWrappedClass().getDeclaredConstructors();
    }
    catch(Throwable e) {
      /* reflection crashed as some class is not loadable */
    }
    
    for (Constructor<T> con: constructors) {
      for (Class<?> paramType : con.getParameterTypes()) {
        
        if (ClassSourceImpl.instance().initializeDeep(paramType)) {
          getWrapper(paramType); // Create wrapper for each param-type
        }           
      }
    }
  }



  /**
   * Add any rule defined by cw to the rule's return type iff cw is outside the
   * JDK. This implements: JCrasher uses for any JDK-defined type (arrays,
   * java.*, sun.*, etc.) only predefined values and typeGraph defined outside the
   * JDK for test generation.
   * 
   * Store functions, Store child-of relation, Queue all reachable types to
   * exhaust search-space (process non-public methods as well)
   */
  protected void findRules(final ClassWrapper<?> cw, final Visibility vis) {
    notNull(cw);
    notNull(vis);
    
    ((ClassWrapperImpl<?>) cw).setIsSearched();

    if (cw.isLibraryType()) {
      return;   //not interested in typeGraph defined by a JDK core type
    }

    queueMethParams(cw, vis);//add typeGraph defined by cw to rule's return type
    queueFamily(cw); //queue super, enclosing, and enclosed types
  }


  /**
   * @param visUsed visibility of methods used to generate test cases.
   */
  public void crawl(final Set<Class<?>> pClasses, final Visibility visUsed) {
    notNull(pClasses);
    notNull(visUsed);
    
    init(pClasses); // add classes specified by the user

    /* Iteratively extract functions and queue up found referenced types. Follow
     * all methods--do not restrict to public methods to be crashed. We want to
     * maximize the number of types we find. */
    boolean maybeMore = true;

    while (maybeMore) {
      maybeMore = false;

      for (ClassWrapper<?> wrapper : getWrappers()) {
        ClassWrapperImpl<?> cw = (ClassWrapperImpl<?>) wrapper;
        if (cw.isSearched() == false) {
          // add meths localy and to other (empty) wrappers
          findRules(cw, visUsed);
          maybeMore = true;
        }
      }
    }
  } 
}
