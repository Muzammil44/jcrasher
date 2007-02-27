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

import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import client.sub.Loadee;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.FunctionCall;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.plans.expr.Variable;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.plans.stmt.BlockImpl;
import edu.gatech.cc.jcrasher.plans.stmt.ExpressionStatement;
import edu.gatech.cc.jcrasher.plans.stmt.Statement;

/**
 * Tests edu.gatech.cc.jcrasher.plans.stmt.BlockImpl
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class BlockImplTest extends TestCase {
  
  protected FunctionCall<Loadee> testeeCall = null;
  protected FunctionCall<Void> testeeCrash = null;
  protected Statement testeeCallStmt = null;
  protected Statement testeeCrashStmt = null;
  protected Block blockEmpty = null;
  protected Block blockCall = null;
  protected Block blockCrash = null;

  
  @Override
  protected void setUp() throws Exception { 
    super.setUp();

    Constructor<Loadee> loadeeConstructor =
        Loadee.class.getConstructor(new Class[0]);
    
    Method loadeeStaticMeth =
        Loadee.class.getMethod("staticMeth", new Class[]{int.class});
    
    testeeCall = new ConstructorCall<Loadee>(
    		Loadee.class, loadeeConstructor, new Expression[0]);    
    testeeCrash = new MethodCall<Void>(
    		Loadee.class, loadeeStaticMeth,
        new Expression[]{new IntLiteral(3)});
    
    testeeCallStmt = new ExpressionStatement<Loadee>(testeeCall);
    testeeCrashStmt = new ExpressionStatement<Void>(testeeCrash);
    
    blockEmpty = new BlockImpl(Loadee.class, loadeeConstructor, "");
    blockCall = new BlockImpl(Loadee.class, loadeeConstructor, "");
    blockCrash = new BlockImpl(Loadee.class, loadeeStaticMeth, "");
    
    blockCall.setBlockStmts(new Statement[]{testeeCallStmt});
    blockCrash.setBlockStmts(new Statement[]{testeeCrashStmt});
  }
  
  /***/
  public void testBlockImpl() {
    try {
      new BlockImpl(Loadee.class, null, "");
      fail("BlockImpl(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }

  /***/
  public void testSetBlockStmts() {
    try {
      blockEmpty.setBlockStmts(null);
      fail("setBlockStmts(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }    
  }

  /***/
  public void testToStringStringClassEmpty() {
    String res0 =
      "{"+NL+
      "}"; 
    assertEquals(res0, blockEmpty.toString());
    
//    String res1 =
//      "{"+NL+
//      TAB+"}"; 
//    assertEquals(res1, blockEmpty.toString(TAB, Loadee.class));
//    
//    String res2 =
//      "{"+NL+
//      TAB+TAB+"}"; 
//    assertEquals(res2, blockEmpty.toString(TAB+TAB, Loadee.class));    
  }
  
  /***/
  public void testToStringStringClassCall() {
    String res0 =
      "{"+NL+
      TAB+testeeCallStmt.text()+NL+
      "}"; 
    assertEquals(res0, blockCall.text());
    
//    String res1 =
//      "{"+ NL+
//      TAB+TAB+testeeCallStmt.toString(Loadee.class)+NL+
//      TAB+"}"; 
//    assertEquals(res1, blockCall.toString(TAB, Loadee.class));
//    
//    String res2 =
//      "{"+NL+
//      TAB+TAB+TAB+testeeCallStmt.toString(Loadee.class)+NL+
//      TAB+TAB+"}"; 
//    assertEquals(res2, blockCall.toString(TAB+TAB, Loadee.class));
    
//    String res2Object =
//      "{"+NL+
//      TAB+TAB+TAB+testeeCallStmt.toString(Object.class)+NL+
//      TAB+TAB+"}"; 
//    assertEquals(res2Object, blockCall.toString(TAB+TAB, Object.class));    
  }
  
  /***/
  public void testGetNextID() { 
    try {
      blockCall.getNextID(null);
      fail("getNextID(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    Variable<Integer> v1 = blockCall.getNextID(int.class);
    Variable<Integer> v2 = blockCall.getNextID(int.class);
    assertTrue(v1.text().equals(v1.text())); 
    assertFalse(v1.text().equals(v2.text()));
  }

  /***/
  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    assertEquals(Boolean.TRUE, blockCall.execute());

    try {
      blockCrash.execute();
      fail("should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }
  }

}