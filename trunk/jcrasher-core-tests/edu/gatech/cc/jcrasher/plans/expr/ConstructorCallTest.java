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


import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import client.Client;
import client.sub.Loadee;
import client.sub.LoadeeCall;
import client.sub.LoadeeReflect;
import client.sub.Needed;


/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ConstructorCallTest extends TestCase {
  
	protected final LoadeeReflect loadeeReflect = new LoadeeReflect();
	protected final LoadeeCall loadeeForClient = 
		new LoadeeCall(Client.class);
	protected final LoadeeCall loadeeForNeeded = 
		new LoadeeCall(Needed.class);
	
	
  /***/
  public void testConstructorCall3() {
    try {
      new ConstructorCall<Loadee.Inner>(
      		Client.class,
      		loadeeReflect.innerConstructor(),
      		new Expression[0]);
      fail("Inner type not allowed for this constructor");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    try {
      new ConstructorCall<Loadee>(
      		null,
      		loadeeReflect.constructor(),
      		new Expression[0]);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new ConstructorCall<Loadee>(
      		Client.class,
      		null,
      		new Expression[0]);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new ConstructorCall<Loadee>(
      		Client.class,
      		loadeeReflect.constructor(),
      		null);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
  }
 
  /***/
  public void testConstructorCall4() {
    try {
      new ConstructorCall<Loadee.Inner>(
      		null,
      		loadeeReflect.innerConstructor(),
      		new Expression[0],
      		loadeeForClient.constructor());
      fail("null argument");
    }    
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new ConstructorCall<Loadee.Inner>(
      		Client.class,
      		null,
      		new Expression[0],
      		loadeeForClient.constructor());
      fail("null argument");
    }    
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new ConstructorCall<Loadee.Inner>(
      		Client.class,
      		loadeeReflect.innerConstructor(),
      		null,
      		loadeeForClient.constructor());
      fail("null argument");
    }    
    catch(RuntimeException e) {  //expected
    }
  	
    try {
      new ConstructorCall<Loadee.Inner>(
      		Client.class,
      		loadeeReflect.innerConstructor(),
      		new Expression[0],
      		null);
      fail("null argument");
    }    
    catch(RuntimeException e) {  //expected
    }
    

    try {
      new ConstructorCall<Loadee.StaticMember>(
      		Client.class,
      		loadeeReflect.staticMemberConstructor(),
          new Expression[0],
          loadeeForClient.constructor());
      fail("Static type not allowed for this constructor");
    }
    catch(RuntimeException e) {  //expected
    }

    try {
      new ConstructorCall<Loadee>(
      		Client.class,
      		loadeeReflect.constructor(),
          new Expression[0],
          loadeeForClient.constructor());
      fail("Top-level type not allowed for this constructor");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    try {
      new ConstructorCall<Loadee.Inner>(
      		Client.class,
          loadeeReflect.innerConstructor(),
          new Expression[0],
          loadeeForClient.innerConstructor(loadeeForClient.constructor()));
      fail("Enclosing type plan should match enclosing type of constructor");
    }
    catch(RuntimeException e) {  //expected
    }
  }

  /***/
  public void testGetReturnType() {
    assertEquals(
    		Loadee.class,
    		loadeeForNeeded.constructor().getReturnType());
    
    assertEquals(
    		Loadee.class,
    		loadeeForClient.constructor(1).getReturnType());
    
    
    assertEquals(
    		Loadee.Inner.class,
    		loadeeForClient.innerConstructor(loadeeForClient.constructor(1)).getReturnType());
    
    assertEquals(
    		Loadee.StaticMember.class,
    		loadeeForNeeded.staticMemberConstructor().getReturnType());
  }

  /***/
  public void testExecute() throws InstantiationException,
      IllegalAccessException, InvocationTargetException 
  {
    notNull(loadeeForClient.constructor().execute());
    Loadee loadee5 = notNull(loadeeForClient.constructor(5).execute());
    assertEquals(5, loadee5.fieldInt);
    
    try {
      loadeeForClient.constructor(true).execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }  
    loadeeForClient.constructor(false).execute();
    
    
    notNull(loadeeForClient.innerConstructor(loadeeForClient.constructor()).execute());
    Loadee.Inner inner7 = 
        notNull(loadeeForClient.innerConstructor(loadeeForClient.constructor(), 7).execute());
    assertEquals(7, inner7.fieldInnerInt);

    notNull(loadeeForClient.staticMemberConstructor().execute());
    Loadee.StaticMember staticMember9 = 
        notNull(loadeeForClient.staticMemberConstructor(9).execute());
    assertEquals(9, staticMember9.fieldStaticMemberInt);    
    
    
    assertEquals(
      Loadee.class,
      (new Loadee.StaticMember()).getClass().getEnclosingClass());
    
    assertEquals(
      Loadee.class,
      ((new Loadee()).new Inner()).getClass().getEnclosingClass());    
  }

  /***/
  public void testText() {
    assertEquals(
        "new client.sub.Loadee()",
        loadeeForClient.constructor().text());
    
    assertEquals(
        "new client.sub.Loadee(5)",
        loadeeForClient.constructor(5).text());
  
    assertEquals(
        "new Loadee(true)",
        loadeeForNeeded.constructor(true).text());    
  }
  
  
  /***/
  public void testTextInner() {    
    assertEquals(
        "(new client.sub.Loadee()).new Inner()",
        loadeeForClient.innerConstructor(loadeeForClient.constructor()).text());
    
    assertEquals(
        "(new client.sub.Loadee(false)).new Inner(-3)",
        loadeeForClient.innerConstructor(loadeeForClient.constructor(false), -3).text());       

    assertEquals(
        "(new Loadee(true)).new Inner(111)",
        loadeeForNeeded.innerConstructor(loadeeForNeeded.constructor(true), 111).text());       
  }
  
  /***/
  public void testTextStaticMember() {    
    assertEquals(
        "new client.sub.Loadee.StaticMember()",
        loadeeForClient.staticMemberConstructor().text());

    assertEquals(
        "new client.sub.Loadee.StaticMember(456)",
        loadeeForClient.staticMemberConstructor(456).text());

    assertEquals(
        "new Loadee.StaticMember(-45)",
        loadeeForNeeded.staticMemberConstructor(-45).text());

    assertEquals(
        "new Loadee.StaticMember()",
        loadeeForNeeded.staticMemberConstructor().text());
  }
    
  /***/
  public void testToString() {    
  	
    assertEquals(
    		loadeeForClient.innerConstructor(loadeeForClient.constructor(false), -3).toString(),
        loadeeForClient.innerConstructor(loadeeForClient.constructor(false), -3).text());       

    assertEquals(
    		loadeeForNeeded.innerConstructor(loadeeForNeeded.constructor(true), 111).toString(),
        loadeeForNeeded.innerConstructor(loadeeForNeeded.constructor(true), 111).text());
  	
    assertEquals(
    		loadeeForClient.staticMemberConstructor(456).toString(),
        loadeeForClient.staticMemberConstructor(456).text());

    assertEquals(
    		loadeeForNeeded.staticMemberConstructor(-45).toString(),
        loadeeForNeeded.staticMemberConstructor(-45).text());
  }
}
