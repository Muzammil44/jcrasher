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

import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.FunctionCall;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.plans.expr.Variable;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;

/**
 * Tests edu.gatech.cc.jcrasher.plans.blocks.BlockImpl
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class BlockImplTest extends TestCase {
  
  protected FunctionCall testeeCall = null;
  protected FunctionCall testeeCrash = null;
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
    
    testeeCall = new ConstructorCall(loadeeConstructor, new Expression[0]);    
    testeeCrash = new MethodCall(
        loadeeStaticMeth,
        new Expression[]{new IntLiteral(3)});
    
    testeeCallStmt = new ExpressionStatement(testeeCall);
    testeeCrashStmt = new ExpressionStatement(testeeCrash);
    
    blockEmpty = new BlockImpl(loadeeConstructor);
    blockCall = new BlockImpl(loadeeConstructor);
    blockCrash = new BlockImpl(loadeeStaticMeth);
    
    blockCall.setBlockStmts(new Statement[]{testeeCallStmt});
    blockCrash.setBlockStmts(new Statement[]{testeeCrashStmt});
  }
  

  public void testBlockImpl() {
    try {
      new BlockImpl(null);
      fail("BlockImpl(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }


  public void testSetBlockStmts() {
    try {
      blockEmpty.setBlockStmts(null);
      fail("setBlockStmts(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }    
  }


  public void testToStringStringClassEmpty() {
    String res0 =
      "{"+NL+
      "}"; 
    assertEquals(res0, blockEmpty.toString("", Loadee.class));
    
    String res1 =
      "{"+NL+
      TAB+"}"; 
    assertEquals(res1, blockEmpty.toString(TAB, Loadee.class));
    
    String res2 =
      "{"+NL+
      TAB+TAB+"}"; 
    assertEquals(res2, blockEmpty.toString(TAB+TAB, Loadee.class));    
  }
  
  
  public void testToStringStringClassCall() {
    String res0 =
      "{"+NL+
      TAB+testeeCallStmt.toString(Loadee.class)+NL+
      "}"; 
    assertEquals(res0, blockCall.toString("", Loadee.class));
    
    String res1 =
      "{"+ NL+
      TAB+TAB+testeeCallStmt.toString(Loadee.class)+NL+
      TAB+"}"; 
    assertEquals(res1, blockCall.toString(TAB, Loadee.class));
    
    String res2 =
      "{"+NL+
      TAB+TAB+TAB+testeeCallStmt.toString(Loadee.class)+NL+
      TAB+TAB+"}"; 
    assertEquals(res2, blockCall.toString(TAB+TAB, Loadee.class));
    
    String res2Object =
      "{"+NL+
      TAB+TAB+TAB+testeeCallStmt.toString(Object.class)+NL+
      TAB+TAB+"}"; 
    assertEquals(res2Object, blockCall.toString(TAB+TAB, Object.class));    
  }
  

  public void testGetNextID() { 
    try {
      blockCall.getNextID(null);
      fail("getNextID(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    Variable v1 = blockCall.getNextID(int.class);
    Variable v2 = blockCall.getNextID(int.class);
    assertTrue(v1.toString(Object.class).equals(v1.toString(Object.class))); 
    assertFalse(v1.toString(Object.class).equals(v2.toString(Object.class)));
  }


  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    assertEquals(true, blockCall.execute());

    try {
      blockCrash.execute();
      fail("should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }
  }

}
