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
import java.lang.reflect.Method;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class MethodCallTest extends TestCase {

  protected Method loadeeMethod = null;
  protected Method loadeeMethodInt = null;
  protected Method loadeeIntMethod = null;
  protected Method staticLoadeeMethod = null;
  protected Method staticLoadeeMethodInt = null;
  
  protected Method innerMethod = null;
  protected Method staticMemberMethod = null;
  protected Method staticMemberStaticMethod = null;
  
  protected MethodCall loadeeMethodCall = null;
  protected MethodCall loadeeMethodCallInt = null;
  protected MethodCall loadeeIntMethodCall = null;
  protected MethodCall staticLoadeeMethodCall = null;
  protected MethodCall staticLoadeeMethodCallInt = null;  

  protected MethodCall innerMethodCall = null;
  protected MethodCall staticMemberMethodCall = null;
  protected MethodCall staticMemberStaticMethodCall = null;  
  
  protected ConstructorCall loadeeConstructorCall = null;
  protected ConstructorCall innerConstructorCall = null;
  protected ConstructorCall staticMemberConstructorCall = null;
  protected IntLiteral int5Plan = new IntLiteral(5);
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    loadeeMethod = Loadee.class.getMethod("meth", new Class[0]);
    loadeeMethodInt = Loadee.class.getMethod("meth", new Class[]{int.class});
    loadeeIntMethod = Loadee.class.getMethod("intMeth", new Class[0]);
    staticLoadeeMethod = Loadee.class.getMethod("staticMeth", new Class[0]);
    staticLoadeeMethodInt = 
        Loadee.class.getMethod("staticMeth", new Class[]{int.class});
   
    innerMethod = Loadee.Inner.class.getMethod("innerMeth", new Class[0]);
    staticMemberMethod = Loadee.StaticMember.class.getMethod(
        "staticMemberMeth", new Class[0]);
    staticMemberStaticMethod = Loadee.StaticMember.class.getMethod(
        "staticMemberStaticMeth", new Class[0]);
    
    loadeeConstructorCall = new ConstructorCall(
        Loadee.class.getConstructor(new Class[0]), 
        new Expression[0]);
    innerConstructorCall = new ConstructorCall(
        Loadee.Inner.class.getConstructor(new Class[]{Loadee.class}), 
        new Expression[0],
        loadeeConstructorCall); 
    staticMemberConstructorCall = new ConstructorCall(
        Loadee.StaticMember.class.getConstructor(new Class[0]), 
        new Expression[0]);  
    
    loadeeMethodCall = new MethodCall(
        loadeeMethod, new Expression[0], loadeeConstructorCall);
    loadeeMethodCallInt = new MethodCall(
        loadeeMethodInt, new Expression[]{int5Plan}, loadeeConstructorCall);
    loadeeIntMethodCall = new MethodCall(
        loadeeIntMethod, new Expression[0], loadeeConstructorCall);
    staticLoadeeMethodCall = new MethodCall(
        staticLoadeeMethod, new Expression[0]);
    staticLoadeeMethodCallInt = new MethodCall(
        staticLoadeeMethodInt, new Expression[]{int5Plan});
    
    innerMethodCall = new MethodCall(
      innerMethod, new Expression[0], innerConstructorCall);
    staticMemberMethodCall = new MethodCall(
      staticMemberMethod, new Expression[0], staticMemberConstructorCall);
    staticMemberStaticMethodCall = new MethodCall(
      staticMemberStaticMethod, new Expression[0]);    
  }
  

  public void testMethodCallMethodExpressionArray() {  
    try {
      new MethodCall(null, null);
      fail("MethodCall(null, null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall(staticLoadeeMethod, null);
      fail("MethodCall(.., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    try {
      new MethodCall(null, new Expression[0]);
      fail("MethodCall(null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall(loadeeMethod, new Expression[0]);
      fail("Wrong constructor for instance method"); 
    }
    catch(RuntimeException e) {  //expected
    }    
  }


  public void testMethodCallMethodExpressionArrayExpression() {
    try {
      new MethodCall(loadeeMethod, new Expression[0], null);
      fail("MethodCall(.., .., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new MethodCall(
          staticLoadeeMethod, new Expression[0], loadeeConstructorCall);
      fail("Wrong constructor for instance method");
    }
    catch(RuntimeException e) {  //expected
    }       
  }
  

  public void testGetReturnType() {
    assertEquals(Void.TYPE, loadeeMethodCall.getReturnType());
    assertEquals(Void.TYPE, loadeeMethodCallInt.getReturnType());
    assertEquals(int.class, loadeeIntMethodCall.getReturnType());
    assertEquals(Void.TYPE, staticLoadeeMethodCall.getReturnType());
    assertEquals(Void.TYPE, staticLoadeeMethodCallInt.getReturnType());
    
    assertEquals(int.class, innerMethodCall.getReturnType());
    assertEquals(int.class, staticMemberMethodCall.getReturnType());
    assertEquals(int.class, staticMemberStaticMethodCall.getReturnType());
  }


  public void testExecute() throws InstantiationException,
  IllegalAccessException, InvocationTargetException {
    loadeeMethodCall.execute();
    try {
      loadeeMethodCallInt.execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }          
    Object res3 = loadeeIntMethodCall.execute();
    assertEquals(1, res3);
    
    staticLoadeeMethodCall.execute();
    try {
      staticLoadeeMethodCallInt.execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }  
   
    assertEquals(1, innerMethodCall.execute());
    assertEquals(1, staticMemberMethodCall.execute());
    assertEquals(1, staticMemberStaticMethodCall.execute());
  }


  public void testToStringClass() {
    assertEquals(
      "(new edu.gatech.cc.jcrasher.Loadee()).meth()",
      loadeeMethodCall.toString(Object.class));
    
    assertEquals(
      "(new Loadee()).meth()",
      loadeeMethodCall.toString(Loadee.class));    
    assertEquals(
      "(new Loadee()).meth(5)",
      loadeeMethodCallInt.toString(Loadee.class));
    assertEquals(
      "(new Loadee()).intMeth()",
      loadeeIntMethodCall.toString(Loadee.class));
    
    assertEquals(
      "edu.gatech.cc.jcrasher.Loadee.staticMeth()",
      staticLoadeeMethodCall.toString(Object.class));
    
    assertEquals(
      "Loadee.staticMeth()",
      staticLoadeeMethodCall.toString(Loadee.class));
    assertEquals(
      "Loadee.staticMeth(5)",
      staticLoadeeMethodCallInt.toString(Loadee.class));
  }

  
  public void testToStringClassNested() {
    assertEquals(
      "((new Loadee()).new Inner()).innerMeth()",
      innerMethodCall.toString(Loadee.class));
    assertEquals(
      "(new Loadee.StaticMember()).staticMemberMeth()",
      staticMemberMethodCall.toString(Loadee.class));
    assertEquals(
      "Loadee.StaticMember.staticMemberStaticMeth()",
      staticMemberStaticMethodCall.toString(Loadee.class));
    
    assertEquals(
      "((new edu.gatech.cc.jcrasher.Loadee()).new Inner()).innerMeth()",
      innerMethodCall.toString(Object.class));
    assertEquals(
      "(new edu.gatech.cc.jcrasher.Loadee.StaticMember()).staticMemberMeth()",
      staticMemberMethodCall.toString(Object.class));
    assertEquals(
      "edu.gatech.cc.jcrasher.Loadee.StaticMember.staticMemberStaticMeth()",
      staticMemberStaticMethodCall.toString(Object.class));
  }

}
