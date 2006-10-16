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


/**
 * Initializes a Java class, given a BCEL class, Java class, or class name.
 * Does not throw any exceptions or errors but indicates problems
 * by returning null.
 * We never return an anonymous class since we have no use for it.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface ClassSource {

  
  /**
   * Tries to initialize c (and all its enclosing classes if c is an inner
   * class). 
   * 
   * @return if c could be initialized (or already was)
   * and all classes enclosing c if c is an inner class.
   * The following are not necessarily initialized though:
   * <ul>
   * <li>Implemented or extended interfaces
   * <li>Static member types
   */
  public boolean initializeDeep(final Class<?> c);
  
  /**
   * @param pClassName JVM-internal name as defined in 
   * {@link Class#getName()}.
   * For example,
   * <ul><li>Nested classes have to look like <code>Outer$Nested</code>.
   *     <li><code>[I</code> is an int array. 
   * 
   * @return class of given name or null if we could not
   * locate, load, link, or initialize the class (and all its enclosing classes
   * if it is an inner class).
   */
  public Class<?> initializeDeep(final String pClassName); 
}
