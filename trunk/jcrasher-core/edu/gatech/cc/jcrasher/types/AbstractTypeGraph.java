/*
 * AbstractTypeGraph.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.types;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;


/**
 * Singleton hiding the storage of ClassWrappers.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class AbstractTypeGraph implements TypeGraph {


  /*****************************************************************************
   * Class.getName() --> constructing meths, constructors, and children
   * 
   * e.g. "java.util.Vector" --> {Vector Z.foo(), Vector(), etc}
   */
  protected final Hashtable<String, ClassWrapper<?>> class2wrapper = 
      new Hashtable<String, ClassWrapper<?>>();


  /**
   * Set user-specified types.
   * 
   * @param classes types specified by user to be crashed.
   */
  protected void init(final Set<Class<?>> classes) {
    for (Class<?> c : classes) { // put empty wrapper in mapping
      if (ClassSourceImpl.instance().initializeDeep(c)) {
        getWrapper(c);
      }         
    }
  }


  /**
   * @return all class-wrappers
   */
  public ClassWrapper<?>[] getWrappers() {
    Collection<ClassWrapper<?>> wrappers = class2wrapper.values();
    return wrappers.toArray(new ClassWrapper[wrappers.size()]);
  }



  /**
   * @return wrapper of class: create & insert if not done yet
   */
  public <T> ClassWrapper<T> getWrapper(final Class<T> pClass) {
    ClassWrapper<T> wrapper = null;
    wrapper = (ClassWrapper<T>) class2wrapper.get(pClass.getName());

    if (wrapper == null) { // create & insert if not done yet
      wrapper = new ClassWrapperImpl<T>(pClass);
      class2wrapper.put(pClass.getName(), wrapper);
    }

    return wrapper;
  }
}
