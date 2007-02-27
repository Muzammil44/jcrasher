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
package edu.gatech.cc.jcrasher.plans.stmt;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import client.Client;
import client.sub.Loadee;
import client.sub.LoadeeCall;
import client.sub.Needed;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;

/**
 * Tests edu.gatech.cc.jcrasher.plans.stmt.ExpressionStatement
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ExpressionStatementTest extends TestCase {

  protected LoadeeCall loadeeForClient = new LoadeeCall(Client.class);
  
  protected ConstructorCall<Loadee> loadeeConstructorCall = 
    loadeeForClient.constructor();
  
  protected ConstructorCall<Loadee.Inner> innerConstructorCall =
    loadeeForClient.innerConstructor(loadeeConstructorCall);
  
  protected MethodCall<Integer> innerMethodCall = 
    loadeeForClient.innerMeth(innerConstructorCall);
  
  protected MethodCall<Void> loadeeMethodCallInt = 
    loadeeForClient.meth(loadeeConstructorCall, 5);

  protected ExpressionStatement<Integer> innerMethodCallStmt = 
    new ExpressionStatement<Integer>(innerMethodCall);
  
  protected ExpressionStatement<Void> loadeeMethodCallIntStmt = 
    new ExpressionStatement<Void>(loadeeMethodCallInt);
  
  
  
  protected LoadeeCall loadeeForClientNeeded = new LoadeeCall(Needed.class);
  
  protected ConstructorCall<Loadee.Inner> innerConstructorCallNeeded =
    loadeeForClientNeeded.innerConstructor(loadeeForClientNeeded.constructor());
  
  protected MethodCall<Integer> innerMethodCallNeeded = 
    loadeeForClientNeeded.innerMeth(innerConstructorCallNeeded);  
  
  protected ExpressionStatement<Integer> innerMethodCallNeededStmt = 
    new ExpressionStatement<Integer>(innerMethodCallNeeded);
  
  
  /***/
  public void testExpressionStatement() {
    try {
      new ExpressionStatement<Integer>(null);
      fail("ExpressionStatement(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }


  /***/
  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    assertEquals(Boolean.TRUE, innerMethodCallStmt.execute());
    
    try {
      loadeeMethodCallIntStmt.execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }  
  }


  /***/
  public void testText() {
    assertEquals(
        loadeeMethodCallInt.text()+";",
        loadeeMethodCallIntStmt.text());
    
    assertEquals(
      innerMethodCall.text()+";",
      innerMethodCallStmt.text());   

    assertEquals(
        innerMethodCallNeeded.text()+";",
        innerMethodCallNeededStmt.text());       
  }

  
  /***/
  public void testToString() {
    assertEquals(
        loadeeMethodCallIntStmt.toString(),
        loadeeMethodCallIntStmt.text());
    
    assertEquals(
        innerMethodCallStmt.toString(),
      innerMethodCallStmt.text());   

    assertEquals(
        innerMethodCallNeededStmt.toString(),
        innerMethodCallNeededStmt.text());       
  }  
}
