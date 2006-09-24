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


import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.Variable;
import edu.gatech.cc.jcrasher.plans.expr.literals.BooleanLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.StringLiteral;


/**
 * Tests LocalVariableDeclarationStatement
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class LocalVariableDeclarationStatementTest extends TestCase {

  protected Variable intX = new Variable(int.class, "x");
  protected Variable stringY = new Variable(String.class, "y");
  
  protected ConstructorCall loadeeConstructorCallTrue = null;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    loadeeConstructorCallTrue = new ConstructorCall(
        Loadee.class.getConstructor(new Class[]{boolean.class}),
        new Expression[]{new BooleanLiteral(true)});    
  }
  
  
  public void testLocalVariableDeclarationStatement() throws Exception {
    try {
      new LocalVariableDeclarationStatement(null, null);
      fail("LocalVariableDeclarationStatement(null, null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new LocalVariableDeclarationStatement(intX, null);
      fail("LocalVariableDeclarationStatement(.., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new LocalVariableDeclarationStatement(null, new IntLiteral(-1));
      fail("LocalVariableDeclarationStatement(null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new LocalVariableDeclarationStatement(stringY, loadeeConstructorCallTrue);
      fail("not assignment compatible");
    }
    catch(RuntimeException e) {  //expected
    }    
  }

  
  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    LocalVariableDeclarationStatement stringAssign =
        new LocalVariableDeclarationStatement(stringY, new StringLiteral("*"));
    assertEquals(null, stringY.execute());
    Object res = stringAssign.execute();
    assertEquals(true, res);
    assertEquals("*", stringY.execute());
    
    try {
      loadeeConstructorCallTrue.execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }     
  }


  public void testToStringClass() {
    Expression stringLit = new StringLiteral("*");
    LocalVariableDeclarationStatement stringAssign =
        new LocalVariableDeclarationStatement(stringY, stringLit);
    Class testee = Object.class;
    assertEquals(
        "String "+stringY.toString(testee)+" = "+stringLit.toString(testee)+";",
        stringAssign.toString(testee));
    
    testee = Loadee.class;
    assertEquals(
        "java.lang.String "+stringY.toString(testee)+" = "
            +stringLit.toString(testee)+";",
        stringAssign.toString(testee));    
  }

}
