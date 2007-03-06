/*
 * TypeGraph.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.types;

import java.util.Set;

import edu.gatech.cc.jcrasher.Constants.Visibility;

/**
 * Singleton.
 * 
 * Stores the relation, filled and queried by CrasherImpl.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface TypeGraph {

  /**
   * For each passed [abstract] class and interface do: add wrapper to mapping;
   * queue all reachable types like meth-param, enclosed classes; create/ sign
   * in to parent-classwrapper; distribute declared constr/ meths to wrapper of
   * return types;
   * 
   * While (new wrapper added, which are potentially not yet processed) do:
   * Process added wrappers as above.
   */
  public void crawl(final Set<Class<?>> pClasses, final Visibility vis);


  /**
   * @return wrapper of class: fresh one created & inserted if not done yet
   */
  public <T> ClassWrapper<T> getWrapper(final Class<T> pClass);


  /**
   * @return all class-wrappers
   */
  public ClassWrapper<?>[] getWrappers();
}
