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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;
import edu.gatech.cc.jcrasher.plans.expr.literals.BooleanLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;


/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ConstructorCallTest extends TestCase {

  protected Constructor<Loadee> loadeeConstructor = null;
  protected Constructor<Loadee> loadeeConstructorInt = null;
  protected Constructor<Loadee> loadeeConstructorBoolean = null;
  protected Constructor<Loadee.Inner> innerConstructor = null;
  protected Constructor<Loadee.Inner> innerConstructorInt = null;
  protected Constructor<Loadee.StaticMember> staticMemberConstructor = null;
  protected Constructor<Loadee.StaticMember> staticMemberConstructorInt = null;
  protected ConstructorCall<Loadee> loadeeConstructorCall = null;
  protected ConstructorCall<Loadee> loadeeConstructorCallInt = null;
  protected ConstructorCall<Loadee> loadeeConstructorCallTrue = null;
  protected ConstructorCall<Loadee> loadeeConstructorCallFalse = null;
  protected ConstructorCall<Loadee.Inner> innerConstructorCall = null;
  protected ConstructorCall<Loadee.Inner> innerConstructorCallInt = null;
  protected ConstructorCall<Loadee.StaticMember> staticMemberConstructorCall = null;
  protected ConstructorCall<Loadee.StaticMember> staticMemberConstructorCallInt = null;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    loadeeConstructor = Loadee.class.getConstructor(new Class[0]);
    loadeeConstructorInt = Loadee.class.getConstructor(new Class[]{int.class});
    loadeeConstructorBoolean = 
        Loadee.class.getConstructor(new Class[]{boolean.class});
    
    innerConstructor =
        Loadee.Inner.class.getConstructor(new Class[]{Loadee.class});
    innerConstructorInt =
        Loadee.Inner.class.getConstructor(new Class[]{Loadee.class, int.class});
    staticMemberConstructor =
        Loadee.StaticMember.class.getConstructor(new Class[0]);
    staticMemberConstructorInt =
        Loadee.StaticMember.class.getConstructor(new Class[]{int.class});
    
    loadeeConstructorCall = new ConstructorCall<Loadee>(
        loadeeConstructor, 
        new Expression[0]);

    loadeeConstructorCallInt = new ConstructorCall<Loadee>(
        loadeeConstructorInt,
        new Expression[]{new IntLiteral(5)});
    
    loadeeConstructorCallTrue = new ConstructorCall<Loadee>(
        loadeeConstructorBoolean,
        new Expression[]{new BooleanLiteral(true)});    

    loadeeConstructorCallFalse = new ConstructorCall<Loadee>(
        loadeeConstructorBoolean,
        new Expression[]{new BooleanLiteral(false)});
    
    innerConstructorCall = new ConstructorCall<Loadee.Inner>(
        innerConstructor,
        new Expression[0],
        loadeeConstructorCall);
    
    innerConstructorCallInt = new ConstructorCall<Loadee.Inner>(
        innerConstructorInt,
        new Expression[]{new IntLiteral(7)},
        loadeeConstructorCallInt);
    
    staticMemberConstructorCall = new ConstructorCall<Loadee.StaticMember>(
        staticMemberConstructor,
        new Expression[0]);
  
    staticMemberConstructorCallInt = new ConstructorCall<Loadee.StaticMember>(
        staticMemberConstructorInt,
        new Expression[]{new IntLiteral(9)});        
  }
  
  /***/
  public void testConstructorCallConstructorExpressionArray() {
    try {
      new ConstructorCall<Loadee.Inner>(innerConstructor, new Expression[0]);
      fail("Inner type not allowed for this constructor");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    try {
      new ConstructorCall<Loadee.Inner>(null, null);
      fail("ConstructorCall(null, null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new ConstructorCall<Loadee>(loadeeConstructor, null);
      fail("ConstructorCall(.., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      new ConstructorCall<Loadee.Inner>(null, new Expression[0]);
      fail("ConstructorCall(null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    } 
  }
 
  /***/
  public void testConstructorCallConstructorExpressionArrayExpression() {
    try {
      new ConstructorCall<Loadee>(loadeeConstructor, new Expression[0], null);
      fail("ConstructorCall(.., .., null) not allowed");
    }    
    catch(RuntimeException e) {  //expected
    }    
    
    try {
      new ConstructorCall<Loadee.StaticMember>(
          staticMemberConstructor, new Expression[0], loadeeConstructorCall);
      fail("Static type not allowed for this constructor");
    }
    catch(RuntimeException e) {  //expected
    }

    try {
      new ConstructorCall<Loadee>(
          loadeeConstructor, new Expression[0], loadeeConstructorCall);
      fail("Top-level type not allowed for this constructor");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    try {
      new ConstructorCall<Loadee.Inner>(
          innerConstructor, new Expression[0], innerConstructorCall);
      fail("Enclosing type plan should match enclosing type of constructor");
    }
    catch(RuntimeException e) {  //expected
    }
  }

  /***/
  public void testGetReturnType() {
    assertEquals(Loadee.class, loadeeConstructorCall.getReturnType());
    assertEquals(Loadee.class, loadeeConstructorCallInt.getReturnType());
    assertEquals(Loadee.Inner.class, innerConstructorCall.getReturnType());
    assertEquals(Loadee.Inner.class, innerConstructorCallInt.getReturnType());
  }

  /***/
  public void testExecute() throws InstantiationException,
      IllegalAccessException, InvocationTargetException 
  {
    notNull(loadeeConstructorCall.execute());
    Loadee loadee5 = notNull(loadeeConstructorCallInt.execute());
    assertEquals(5, loadee5.fieldInt);
    
    try {
      loadeeConstructorCallTrue.execute();
      fail("Should have crashed");
    }
    catch(InvocationTargetException e) {  //expected
    }  
    loadeeConstructorCallFalse.execute();
    
    
    notNull(innerConstructorCall.execute());
    Loadee.Inner inner7 = 
        notNull(innerConstructorCallInt.execute());
    assertEquals(7, inner7.fieldInnerInt);

    notNull(staticMemberConstructorCall.execute());
    Loadee.StaticMember staticMember9 = 
        notNull(staticMemberConstructorCallInt.execute());
    assertEquals(9, staticMember9.fieldStaticMemberInt);    
    
    
    assertEquals(
      Loadee.class,
      (new Loadee.StaticMember()).getClass().getEnclosingClass());
    
    assertEquals(
      Loadee.class,
      ((new Loadee()).new Inner()).getClass().getEnclosingClass());    
  }

  /***/
  public void testToStringClass() {
    assertEquals(
        "new edu.gatech.cc.jcrasher.Loadee()",
        loadeeConstructorCall.toString(Object.class));
    
    assertEquals(
        "new edu.gatech.cc.jcrasher.Loadee(5)",
        loadeeConstructorCallInt.toString(Object.class));
  
    assertEquals(
        "new Loadee(5)",
        loadeeConstructorCallInt.toString(Loadee.class));
    
    assertEquals(
      "new Loadee(5)",
      loadeeConstructorCallInt.toString(Loadee.Inner.class));
    
    assertEquals(
      "new Loadee(5)",
      loadeeConstructorCallInt.toString(Loadee.StaticMember.class));    
  }
  
  /***/
  public void testToStringClassInner() {    
    assertEquals(
        "(new edu.gatech.cc.jcrasher.Loadee()).new Inner()",
        innerConstructorCall.toString(Object.class));
    
    assertEquals(
        "(new edu.gatech.cc.jcrasher.Loadee(5)).new Inner(7)",
        innerConstructorCallInt.toString(Object.class));
  
    assertEquals(
        "(new Loadee(5)).new Inner(7)",
        innerConstructorCallInt.toString(Loadee.class));
    
    assertEquals(
      "(new Loadee(5)).new Inner(7)",
      innerConstructorCallInt.toString(Loadee.Inner.class));
    
    assertEquals(
      "(new Loadee(5)).new Inner(7)",
      innerConstructorCallInt.toString(Loadee.StaticMember.class));    
  }
  
  /***/
  public void testToStringClassStaticMember() {    
    assertEquals(
        "new edu.gatech.cc.jcrasher.Loadee.StaticMember()",
        staticMemberConstructorCall.toString(Object.class));

    assertEquals(
        "new edu.gatech.cc.jcrasher.Loadee.StaticMember(9)",
        staticMemberConstructorCallInt.toString(Object.class));

    assertEquals(
        "new Loadee.StaticMember(9)",
        staticMemberConstructorCallInt.toString(Loadee.class));
    
    assertEquals(
      "new Loadee.StaticMember(9)",
      staticMemberConstructorCallInt.toString(Loadee.Inner.class));    
    
    assertEquals(
      "new Loadee.StaticMember(9)",
      staticMemberConstructorCallInt.toString(Loadee.StaticMember.class));    
  }

}
