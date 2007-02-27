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

import client.Client;
import client.sub.Loadee;
import client.sub.Needed;
import junit.framework.TestCase;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class VariableTest extends TestCase {

  protected final Variable<String> stringX = 
  	new Variable<String>(String.class, Client.class, "x");
  
  protected final Variable<Integer> intY =
  	new Variable<Integer>(int.class, Client.class, "y");
  
  protected final Variable<Map> mapZ =
  	new Variable<Map>(Map.class, Client.class, "z");
  
  protected final Variable<Loadee> loadeeForClient =
  	new Variable<Loadee>(Loadee.class, Client.class, "a");
  
  protected final Variable<Loadee> loadeeForNeeded =
  	new Variable<Loadee>(Loadee.class, Needed.class, "a");  
  
  
  /***/
  public void testGetReturnType() {
    assertEquals(String.class, stringX.getReturnType());
    assertEquals(int.class, intY.getReturnType());
    assertEquals(Map.class, mapZ.getReturnType());
  }

  /***/
  public void testVariableNull() {  
    try {
      new Variable<String>(null, Client.class, "a");
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new Variable<String>(String.class, null, "a");
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new Variable<String>(String.class, Client.class, null);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }    
  }

  /***/
  public void testAssign() {
    stringX.assign(null);    
    assertEquals(null, stringX.value);
    
    stringX.assign("");    
    assertEquals("", stringX.value);

    stringX.assign("hallo");    
    assertEquals("hallo", stringX.value);    
    
//    try {
//      stringX.assign(new Vector<String>());  
//      fail("Cannot assign Vector to a String");
//    }
//    catch(RuntimeException e) {  //expected
//    }    
    
    Integer one = Integer.valueOf(1);    
    intY.assign(one);    
    assertEquals(one, intY.value);
    
    mapZ.assign(null);    
    assertEquals(null, mapZ.value);
    
    HashMap<String, String> m = new HashMap<String, String>();
    mapZ.assign(m);
    assertEquals(m, mapZ.value);    
  }
  
  /***/
  public void testExecute() {
    stringX.assign(null);
    assertEquals(null, stringX.execute());
    stringX.assign("_");
    assertEquals("_", stringX.execute());
  }

  /***/
  public void testText() {
    assertEquals("x", stringX.text());
    assertEquals("y", intY.text());
    assertEquals("z", mapZ.text());
    assertEquals("a", loadeeForClient.text());
    assertEquals("a", loadeeForNeeded.text());
  }
  
  /***/
  public void testToString() {
    assertEquals(stringX.toString(), stringX.text());
    assertEquals(intY.toString(), intY.text());
    assertEquals(mapZ.toString(), mapZ.text());
    assertEquals(loadeeForClient.toString(), loadeeForClient.text());
    assertEquals(loadeeForNeeded.toString(), loadeeForNeeded.text());
  }

  /***/
  public void testTextDeclaration() {
    assertEquals("java.lang.String x", stringX.textDeclaration());
    assertEquals("int y", intY.textDeclaration());
    assertEquals("java.util.Map z", mapZ.textDeclaration());
    assertEquals("client.sub.Loadee a", loadeeForClient.textDeclaration());
    assertEquals("Loadee a", loadeeForNeeded.textDeclaration());
  }
}
