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
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.Variable;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.StringLiteral;


/**
 * Tests LocalVariableDeclarationStatement
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class LocalVariableDeclarationStatementTest extends TestCase {

  protected final LoadeeCall loadeeCallForClient = new LoadeeCall(Client.class);
  protected final LoadeeCall loadeeCallForNeeded = new LoadeeCall(Needed.class);
	
  protected final String myString = "***";
  protected final Expression<String> stringLit = new StringLiteral(myString);
  
  protected final Variable<Integer> intX = 
    new Variable<Integer>(int.class, Client.class, "x");
  
  protected final Variable<String> stringY = 
    new Variable<String>(String.class, Client.class, "y");
 
  protected final Variable<Loadee> loadeeForClient =
    new Variable<Loadee>(Loadee.class, Client.class, "a");
  
  protected final Variable<Loadee> loadeeForNeeded =
    new Variable<Loadee>(Loadee.class, Needed.class, "a");
  
  
  /***/
  public void testLocalVariableDeclarationStatement() throws Exception {
    try {
      new LocalVariableDeclarationStatement<Integer>(null, new IntLiteral(4));
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }

    try {
      new LocalVariableDeclarationStatement<Integer>(intX, null);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
  }

  
  /***/
  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    
    LocalVariableDeclarationStatement<String> stringAssign =
        new LocalVariableDeclarationStatement<String>(stringY, stringLit);
    assertEquals(null, stringY.execute());
    
    Object res = stringAssign.execute();
    assertEquals(myString, res);
    assertEquals(stringLit.execute(), stringY.execute());
    
    try {
      loadeeCallForClient.constructor(true).execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }
  }


  /***/
  public void testText() {
    LocalVariableDeclarationStatement<String> stringAssign =
        new LocalVariableDeclarationStatement<String>(stringY, stringLit);

    assertEquals(
        stringY.textDeclaration()+" = "+stringLit.text()+";",
        stringAssign.text());
    
    LocalVariableDeclarationStatement<Loadee> loadeeClientAssign =
      new LocalVariableDeclarationStatement<Loadee>(loadeeForClient, loadeeCallForClient.constructor());
    assertEquals(
        loadeeForClient.textDeclaration()+" = "+loadeeCallForClient.constructor().text()+";",
        loadeeClientAssign.text());

    
    LocalVariableDeclarationStatement<Loadee> loadeeNeededAssign =
      new LocalVariableDeclarationStatement<Loadee>(loadeeForNeeded, loadeeCallForNeeded.constructor());
    assertEquals(
        loadeeForNeeded.textDeclaration()+" = "+loadeeCallForNeeded.constructor().text()+";",
        loadeeNeededAssign.text());    
  }
  
  
  /***/
  public void testToString() {
    LocalVariableDeclarationStatement<String> stringAssign =
        new LocalVariableDeclarationStatement<String>(stringY, stringLit);

    assertEquals(
        stringAssign.toString(),
        stringAssign.text());
    
    LocalVariableDeclarationStatement<Loadee> loadeeClientAssign =
      new LocalVariableDeclarationStatement<Loadee>(loadeeForClient, loadeeCallForClient.constructor());
    assertEquals(
        loadeeClientAssign.toString(),
        loadeeClientAssign.text());

    
    LocalVariableDeclarationStatement<Loadee> loadeeNeededAssign =
      new LocalVariableDeclarationStatement<Loadee>(loadeeForNeeded, loadeeCallForNeeded.constructor());
    assertEquals(
        loadeeNeededAssign.toString(),
        loadeeNeededAssign.text());    
  }
}
