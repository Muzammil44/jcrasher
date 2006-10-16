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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class VariableTest extends TestCase {

  protected final Variable stringX = new Variable<String>(String.class, "x");
  protected final Variable<Integer> intY = new Variable<Integer>(int.class, "y");
  protected final Variable<Map> mapZ = new Variable(Map.class, "z");
  
  /***/
  public void testGetReturnType() {
    assertEquals(String.class, stringX.getReturnType());
    assertEquals(int.class, intY.getReturnType());
    assertEquals(Map.class, mapZ.getReturnType());
  }

  /***/
  public void testVariableNull() {
    try {
      new Variable(null, null);
      fail("Variable(null, null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new Variable(String.class, null);
      fail("Variable(.., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new Variable(null, "x");
      fail("Variable(null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }    
  }

  /***/
  public void testAssign() {
    stringX.assign(null);    
    assertEquals(null, stringX.assignedValue);
    
    stringX.assign("");    
    assertEquals("", stringX.assignedValue);

    stringX.assign("hallo");    
    assertEquals("hallo", stringX.assignedValue);    
    
    try {
      stringX.assign(new Vector());  
      fail("Cannot assign Vector to a String");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    Integer one = 1;    
    intY.assign(one);    
    assertEquals(one, intY.assignedValue);
    
    mapZ.assign(null);    
    assertEquals(null, mapZ.assignedValue);
    
    mapZ.assign(new HashMap());
    assertEquals(new HashMap(), mapZ.assignedValue);    
  }
  
  /***/
  public void testExecute() {
    stringX.assign(null);
    assertEquals(null, stringX.execute());
    stringX.assign("_");
    assertEquals("_", stringX.execute());
  }

  /***/
  public void testToStringClass() {
    assertEquals("x", stringX.toString(Object.class));
    assertEquals("x", stringX.toString(Loadee.class));
    assertEquals("y", intY.toString(Object.class));
    assertEquals("y", intY.toString(Loadee.class));
    assertEquals("z", mapZ.toString(Object.class));
    assertEquals("z", mapZ.toString(Loadee.class));
  }

}
