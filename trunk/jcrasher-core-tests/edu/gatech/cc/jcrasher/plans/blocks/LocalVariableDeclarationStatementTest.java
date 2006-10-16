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

import static java.lang.Boolean.TRUE;


/**
 * Tests LocalVariableDeclarationStatement
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class LocalVariableDeclarationStatementTest extends TestCase {

  protected Variable<Integer> intX = new Variable<Integer>(int.class, "x");
  protected Variable<String> stringY = new Variable<String>(String.class, "y");
  
  protected ConstructorCall<Loadee> loadeeConstructorCallTrue = null;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    loadeeConstructorCallTrue = new ConstructorCall<Loadee>(
        Loadee.class.getConstructor(new Class[]{boolean.class}),
        new Expression[]{new BooleanLiteral(true)});    
  }
  
  /**
   * 
   */
  public void testLocalVariableDeclarationStatement() throws Exception {
    try {
      new LocalVariableDeclarationStatement<Object>(null, null);
      fail("LocalVariableDeclarationStatement(null, null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new LocalVariableDeclarationStatement<Integer>(intX, null);
      fail("LocalVariableDeclarationStatement(.., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new LocalVariableDeclarationStatement<Integer>(null, new IntLiteral(-1));
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

  
  /**
   * 
   */
  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    LocalVariableDeclarationStatement<String> stringAssign =
        new LocalVariableDeclarationStatement<String>(stringY, new StringLiteral("*"));
    assertEquals(null, stringY.execute());
    Object res = stringAssign.execute();
    assertEquals(TRUE, res);
    assertEquals("*", stringY.execute());
    
    try {
      loadeeConstructorCallTrue.execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }     
  }


  /**
   * 
   */
  public void testToStringClass() {
    Expression<String> stringLit = new StringLiteral("*");
    LocalVariableDeclarationStatement<String> stringAssign =
        new LocalVariableDeclarationStatement<String>(stringY, stringLit);
    Class<Object> object = Object.class;
    assertEquals(
        "String "+stringY.toString(object)+" = "+stringLit.toString(object)+";",
        stringAssign.toString(object));
    
    Class<Loadee> loadee = Loadee.class;
    assertEquals(
        "java.lang.String "+stringY.toString(loadee)+" = "
            +stringLit.toString(loadee)+";",
        stringAssign.toString(loadee));    
  }

}
