/*
 * Copyright (C) 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.gatech.cc.jcrasher.types;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads a Java runtime class, given a BCEL class or class name.
 * Does not throw any exceptions or errors but indicates problems
 * by returning null or empty sets.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ClassSourceImpl implements ClassSource {

  /* Singleton */
  protected static final ClassSource theInstance = new ClassSourceImpl();
  protected static final String NULL_CLASS_NAME =
      "<fb-crasher received a null class name>";
  
  protected final Set<String> couldNotDeepInit = new HashSet<String>();
  
  /**
   * @return singleton instance.
   */
  public static ClassSource instance() {
    return theInstance;
  }
  
  /**
   * To be only called by <clinit>.
   */
  protected ClassSourceImpl() {
    couldNotDeepInit.add(NULL_CLASS_NAME);
  }


  protected void logCouldNotInit(final String className) {
    couldNotDeepInit.add(className);
    System.out.println(
        "fb-crasher could not initialize class "+className+", skipping it."); 
  }

  protected void logCouldNotInitEnclosing(final String className) {
    couldNotDeepInit.add(className);
    System.out.println(
        "fb-crasher could not initialize enclosing class "+className+".");
  }
  
  
  /**
   * @return if we do not want to use the class (for being anonymous).
   */
  protected boolean isAnonymous(Class<?> in) {
    if (in==null) {
      return true;
    }
    
    try {
      if (in.isAnonymousClass()) {
        return true;
      }
    }
    catch (Throwable t) {
      /* Reflection crashed, we won't be able to use the class later. */
      return true;
    }
    
    return false;
  }
  
  
  /**
   * @param c having a non-null Class object does not imply that the underlying
   * class has been initialized!
   */
  protected boolean initializeDeep(final Class<?> c, boolean isUserClass) {
  	if (c==null)
  		return false;
  	
    String className = NULL_CLASS_NAME;
    if (c.getName()!=null) {
      className = c.getName();
    }
       
    if (couldNotDeepInit.contains(className)) {
      return false;
    }
    
    try {
      Class.forName(className); //initializes class className.
    }
    /* Might crash while locating, loading, linking, or initializing */
    catch (Throwable t) {
      if (isUserClass) {
        logCouldNotInit(className);
      }
      else {
        logCouldNotInitEnclosing(className);
      }
      return false;
    }
    
    /* Class initialized, but what about its enclosing class? */
    
    try {
      if (Modifier.isStatic(c.getModifiers())) {//static type does not need.
        return true;
      }
      
      final Class<?> enclosingClass = c.getEnclosingClass();
      if (enclosingClass==null) {               //top-level type does not need.
        return true;
      }
      return initializeDeep(enclosingClass, false);  
    }
    catch (Throwable e) {                       //accessing enclosing class.
      logCouldNotInitEnclosing(className);
      return false;
    }
  }
  
  /**
   * Logs if a class could not be initialized.
   */
  public boolean initializeDeep(final Class<?> c) {
    if (c==null)
    	return false;
  	
    if (c.isPrimitive()) {
      return true;  //JVM guarantees primitive types.
    }
    
    if (isAnonymous(c)) {
      return false;  //We do not like anonymous classes.
    }  
    
    return initializeDeep(c, true);
  }
  
  
  public Class<?> initializeDeep(final String pClassName) {
    /* Remember each class name that we could not initialize deep.
     * This allows us to return fast if a class name has not worked before. */
    
    String className = NULL_CLASS_NAME;
    if (pClassName!=null) {
      className = pClassName;
    }
    if (couldNotDeepInit.contains(className)) {
      return null;
    }    
    
    Class<?> res = null;    
    try {
      res = Class.forName(className);   //attempts to initialize class!
    } 
    /* Might crash while locating, loading, linking, or initializing */
    catch (Throwable t) {
      logCouldNotInit(className);
      return null;
    }
    
    /* Class and all its super-classes are initialized now.
     * The following are not necessarily initialized though:
     * <ul>
     * <li>Implemented or extended interfaces
     * <li>Static member types
     * <li>Enclosing type
     * 
     * http://java.sun.com/docs/books/jls/third_edition/html/execution.html#12.4
     */

    /* For inner class, also initialize the enclosing class. 
     * We will definitly need an enclosing instance later. */
    
    if (isAnonymous(res)) {
      return null;  //We do not like anonymous classes.
    }  
    
    if (!initializeDeep(res)) {
      /* res is initialized, but we could not initialize all enclosing
       * classes. We therefore cannot use res. */
      return null;
    }
    return res;
  }
}
