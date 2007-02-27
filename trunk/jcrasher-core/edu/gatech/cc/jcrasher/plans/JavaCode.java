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
package edu.gatech.cc.jcrasher.plans;

import java.lang.reflect.InvocationTargetException;

/**
 * Junk of Java code.
 * We can execute this junk of code  via Java reflection
 * or produce a textual representation.
 * 
 * @param <T> result type.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface JavaCode<T> {
  
  /**
   * Java code element, like a node of an abstract syntax tree.
   * 
   * @return maybe null.
   */
  public T execute() throws InstantiationException,
      IllegalAccessException, InvocationTargetException;
  
  
  /**
   * @return textual representation of code junk.
   */
  public String text();
}
