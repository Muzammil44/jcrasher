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

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import client.Client;
import client.sub.LoadeeCall;
import client.sub.LoadeeReflect;
import client.sub.Needed;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class MethodCallTest extends TestCase {

	protected final LoadeeReflect loadeeReflect = new LoadeeReflect();
	protected final LoadeeCall loadeeForClient = 
		new LoadeeCall(Client.class);
	protected final LoadeeCall loadeeForNeeded = 
		new LoadeeCall(Needed.class);

	
  /***/
  public void testMethodCall3() {  
    try {
      new MethodCall<Void>(
      		null,
      		loadeeReflect.staticMeth(),
      		new Expression[0]);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall<Integer>(
      		Client.class,
      		null,
      		new Expression[0]);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall<Integer>(
      		Client.class,
      		loadeeReflect.staticMeth(),
      		null);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    
    try {
      new MethodCall<Void>(Client.class, loadeeReflect.meth(), new Expression[0]);
      fail("Wrong constructor for instance method"); 
    }
    catch(RuntimeException e) {  //expected
    }    
  }

  /***/
  public void testMethodCall4() {
    try {
      new MethodCall<Void>(
      		null,
      		loadeeReflect.meth(),
      		new Expression[0],
      		loadeeForClient.constructor());
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall<Void>(
      		Client.class,
      		null,
      		new Expression[0],
      		loadeeForClient.constructor());
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall<Void>(
      		Client.class,
      		loadeeReflect.meth(),
      		null,
      		loadeeForClient.constructor());
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall<Void>(
      		Client.class,
      		loadeeReflect.meth(),
      		new Expression[0],
      		null);
      fail("null argument");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall<Integer>(
      		Client.class,
          loadeeReflect.staticMeth(),
          new Expression[0],
          loadeeForClient.constructor());
      fail("Wrong constructor for static method");
    }
    catch(RuntimeException e) {  //expected
    }       
  }
  
  /***/
  public void testGetReturnType() {
    assertEquals(
    		Void.TYPE,
    		loadeeForClient.meth(loadeeForClient.constructor()).getReturnType());
    assertEquals(
    		Void.TYPE,
    		loadeeForClient.meth(loadeeForClient.constructor(true)).getReturnType());    
    
    assertEquals(
    		Void.TYPE,
    		loadeeForClient.meth(loadeeForClient.constructor(true), 3).getReturnType());
    
    assertEquals(
    		int.class,
    		loadeeForClient.intMeth(loadeeForClient.constructor(true)).getReturnType());
    
    assertEquals(Void.TYPE, loadeeForClient.staticMeth().getReturnType());
    assertEquals(Void.TYPE, loadeeForClient.staticMeth(1).getReturnType());
    
    assertEquals(
    		int.class,
    		loadeeForClient.innerMeth(loadeeForClient.
    				innerConstructor(loadeeForClient.constructor())).getReturnType());
    
    assertEquals(
    		int.class,
    		loadeeForClient.staticMemberStaticMethod().getReturnType());
    
    assertEquals(
    		int.class,
    		loadeeForClient.staticMemberMeth(loadeeForClient.
    				staticMemberConstructor()).getReturnType());
  }

  
  /***/
  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
  	loadeeForClient.meth(loadeeForClient.constructor()).execute();
    try {
    	loadeeForClient.meth(loadeeForClient.constructor(), 3).execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }          
    Object res1 = loadeeForClient.intMeth(loadeeForClient.constructor()).execute();
    assertEquals(Integer.valueOf(1), res1);
    
    loadeeForClient.staticMeth().execute();
    try {
    	loadeeForClient.staticMeth(7).execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }  
   
    assertEquals(
    		Integer.valueOf(1),
    		loadeeForClient.innerMeth(loadeeForClient.
    				innerConstructor(loadeeForClient.constructor())).execute());
    
    assertEquals(
    		Integer.valueOf(1),
    		loadeeForClient.staticMemberMeth(loadeeForClient.
    				staticMemberConstructor()).execute());
    
    assertEquals(
    		Integer.valueOf(1),
    		loadeeForClient.staticMemberStaticMethod().execute());
  }

  /***/
  public void testText() {
    assertEquals(
    		"(new client.sub.Loadee()).meth()",
    		loadeeForClient.meth(loadeeForClient.constructor()).text());
    
    assertEquals(
    		"(new Loadee(-1)).meth()",
    		loadeeForNeeded.meth(loadeeForNeeded.constructor(-1)).text());        
    
    assertEquals(
        "(new client.sub.Loadee()).meth(4)",
        loadeeForClient.meth(loadeeForClient.constructor(), 4).text());
      
    assertEquals(
        "(new Loadee(-7)).meth(6)",
        loadeeForNeeded.meth(loadeeForNeeded.constructor(-7), 6).text());           
    
    assertEquals(
    		"(new Loadee(true)).intMeth()",
    		loadeeForNeeded.intMeth(loadeeForNeeded.constructor(true)).text());
    
    assertEquals(
    		"client.sub.Loadee.staticMeth()",
    		loadeeForClient.staticMeth().text());
    
    assertEquals(
        "Loadee.staticMeth()",
        loadeeForNeeded.staticMeth().text());

    assertEquals(
        "client.sub.Loadee.staticMeth(8)",
        loadeeForClient.staticMeth(8).text());
      
    assertEquals(
        "Loadee.staticMeth(9)",
        loadeeForNeeded.staticMeth(9).text());
  }

  /***/
  public void testTextNested() {
    assertEquals(
    		"((new client.sub.Loadee(4)).new Inner(3)).innerMeth()",
    		loadeeForClient.innerMeth(
    				loadeeForClient.innerConstructor(loadeeForClient.constructor(4), 3)).text());
    
    assertEquals(
    		"((new Loadee(false)).new Inner(-9)).innerMeth()",
    		loadeeForNeeded.innerMeth(
    				loadeeForNeeded.innerConstructor(loadeeForNeeded.constructor(false), -9)).text());
    

    assertEquals(
    		"(new client.sub.Loadee.StaticMember()).staticMemberMeth()",
    		loadeeForClient.staticMemberMeth(
    				loadeeForClient.staticMemberConstructor()).text());

    assertEquals(
        "(new Loadee.StaticMember(-5)).staticMemberMeth()",
        loadeeForNeeded.staticMemberMeth(
        		loadeeForNeeded.staticMemberConstructor(-5)).text());
    
    
    assertEquals(
    		"client.sub.Loadee.StaticMember.staticMemberStaticMeth()",
    		loadeeForClient.staticMemberStaticMethod().text());
    
    assertEquals(
    		"Loadee.StaticMember.staticMemberStaticMeth()",
        loadeeForNeeded.staticMemberStaticMethod().text());
  }

  /***/
  public void testToStringNested() {
    assertEquals(
    		loadeeForClient.innerMeth(
    				loadeeForClient.innerConstructor(loadeeForClient.constructor(4), 3)).toString(),
    		loadeeForClient.innerMeth(
    				loadeeForClient.innerConstructor(loadeeForClient.constructor(4), 3)).text());
    
    assertEquals(
    		loadeeForNeeded.innerMeth(
    				loadeeForNeeded.innerConstructor(loadeeForNeeded.constructor(false), -9)).toString(),
    		loadeeForNeeded.innerMeth(
    				loadeeForNeeded.innerConstructor(loadeeForNeeded.constructor(false), -9)).text());
    

    assertEquals(
    		loadeeForClient.staticMemberMeth(
    				loadeeForClient.staticMemberConstructor()).toString(),
    		loadeeForClient.staticMemberMeth(
    				loadeeForClient.staticMemberConstructor()).text());

    assertEquals(
        loadeeForNeeded.staticMemberMeth(
        		loadeeForNeeded.staticMemberConstructor(-5)).toString(),
        loadeeForNeeded.staticMemberMeth(
        		loadeeForNeeded.staticMemberConstructor(-5)).text());  
    
    
    assertEquals(
    		loadeeForClient.staticMemberStaticMethod().toString(),
    		loadeeForClient.staticMemberStaticMethod().text());
    
    assertEquals(
    		loadeeForNeeded.staticMemberStaticMethod().toString(),
        loadeeForNeeded.staticMemberStaticMethod().text());
  }
}
