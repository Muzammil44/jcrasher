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
package edu.gatech.cc.jcrasher.plans.blocks;


import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;
import edu.gatech.cc.jcrasher.plans.stmt.ExpressionStatement;

/**
 * Tests edu.gatech.cc.jcrasher.plans.stmt.ExpressionStatement
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ExpressionStatementTest extends TestCase {

  protected ConstructorCall<Loadee> loadeeConstructorCall = null;
  protected ConstructorCall<Loadee.Inner> innerConstructorCall = null;
  protected MethodCall<Integer> innerMethodCall = null;    
  protected MethodCall<Void> loadeeMethodCallInt = null;
  
  protected ExpressionStatement<Integer> innerMethodCallStmt = null;
  protected ExpressionStatement<Void> loadeeMethodCallIntStmt = null;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
     
    loadeeConstructorCall = new ConstructorCall<Loadee>(
        Loadee.class.getConstructor(new Class[0]), 
        new Expression[0]);
    innerConstructorCall = new ConstructorCall<Loadee.Inner>(
        Loadee.Inner.class.getConstructor(new Class[]{Loadee.class}), 
        new Expression[0],
        loadeeConstructorCall);
    innerMethodCall = new MethodCall<Integer>(
        Loadee.Inner.class.getMethod("innerMeth", new Class[0]),
        new Expression[0],
        innerConstructorCall);    
    loadeeMethodCallInt = new MethodCall<Void>(
        Loadee.class.getMethod("meth", new Class[]{int.class}),
        new Expression[]{new IntLiteral(5)},
        loadeeConstructorCall);
    
    innerMethodCallStmt =
        notNull(new ExpressionStatement<Integer>(innerMethodCall));
    loadeeMethodCallIntStmt =
        notNull(new ExpressionStatement<Void>(loadeeMethodCallInt));    
  }
  
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
  public void testToStringClass() {
    assertEquals(
      innerMethodCall.toString(Object.class)+";",
      innerMethodCallStmt.toString(Object.class));   
    assertEquals(
      innerMethodCall.toString(Loadee.class)+";",
      innerMethodCallStmt.toString(Loadee.class));    
    
    assertEquals(
      loadeeMethodCallInt.toString(Object.class)+";",
      loadeeMethodCallIntStmt.toString(Object.class));
    assertEquals(
      loadeeMethodCallInt.toString(Loadee.class)+";",
      loadeeMethodCallIntStmt.toString(Loadee.class));
  }

}
