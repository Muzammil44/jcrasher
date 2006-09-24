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
package edu.gatech.cc.jcrasher.plans.expr;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class DotClassTest extends TestCase {

  protected final DotClass dotClass = new DotClass();
  
  public void testGetReturnType() {
    assertEquals(Class.class, dotClass.getReturnType());
  }

  public void testExecute() {
    assertEquals(Object.class, dotClass.execute());
  }

  public void testToStringClass() {
    assertEquals("Object.class", dotClass.toString(String.class));
    assertEquals("java.lang.Object.class", dotClass.toString(Loadee.class));
  }

}
