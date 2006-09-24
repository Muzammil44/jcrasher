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
import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.NullLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.StringLiteral;
import edu.gatech.cc.jcrasher.types.ClassWrapper;


/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ArrayCreateAndInitTest extends TestCase {

  protected final ArrayCreateAndInit int1_fieldsSetToNull = 
    new ArrayCreateAndInit(int[].class);
  protected final ArrayCreateAndInit int2_fieldsSetToNull = 
    new ArrayCreateAndInit(int[][].class);
  protected final ArrayCreateAndInit int10_fieldsSetToNull = 
    new ArrayCreateAndInit(int[][][][][][][][][][].class);  

  protected final ArrayCreateAndInit int10 = 
    new ArrayCreateAndInit(int[][][][][][][][][][].class);
  
  protected final ArrayCreateAndInit string3 = 
    new ArrayCreateAndInit(String[][][].class);
  
  protected final ArrayCreateAndInit set2 = 
    new ArrayCreateAndInit(Set[][].class);
  
  protected final ArrayCreateAndInit int1_length3 = 
    new ArrayCreateAndInit(int[].class);

  protected final ArrayCreateAndInit int2_length2 = 
    new ArrayCreateAndInit(int[][].class);  
  
  protected final int[] int1_length2_val = new int[]{0,1};
  protected final int[] int1_length3_val = new int[]{0,1,2};
  protected final int[] int1_length4_val = new int[]{0,1,2,3};
  
  /* {{0,1,2}, {0,1,2}} */
  protected final int[][] int2_length2_val = new int[][]{
      int1_length3_val, int1_length3_val};
  
  protected final String[] string1_length3_val = new String[]{null,"","hallo"};
  protected final ArrayCreateAndInit string1_length3 =
      new ArrayCreateAndInit(String[].class);
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    int1_fieldsSetToNull.leafType = null;
    int1_fieldsSetToNull.dimensionality = 1;
    
    int2_fieldsSetToNull.leafType = null;
    int2_fieldsSetToNull.dimensionality = 1;
    
    int10_fieldsSetToNull.leafType = null;
    int10_fieldsSetToNull.dimensionality = 1;
    
    int1_length3.setComponentPlans(new Expression[]{
      new IntLiteral(0), new IntLiteral(1), new IntLiteral(2)
    });
    
    int2_length2.setComponentPlans(new Expression[] {
        int1_length3, int1_length3});
    
    string1_length3.setComponentPlans(new Expression[] {
        new NullLiteral(String.class),
        new StringLiteral(""),
        new StringLiteral("hallo")});
  }


  public void testDiscoverLeafLevel() {
    int1_fieldsSetToNull.discoverLeafLevel();
    assertEquals(1, int1_fieldsSetToNull.dimensionality);
    assertEquals(int.class, int1_fieldsSetToNull.leafType);
    
    int2_fieldsSetToNull.discoverLeafLevel();
    assertEquals(2, int2_fieldsSetToNull.dimensionality);
    assertEquals(int.class, int2_fieldsSetToNull.leafType);
    
    int10_fieldsSetToNull.discoverLeafLevel();
    assertEquals(10, int10_fieldsSetToNull.dimensionality);
    assertEquals(int.class, int10_fieldsSetToNull.leafType);
  }

  
  public void testArrayCreateAndInitClassWrapperNull() {
    try {
      new ArrayCreateAndInit((ClassWrapper)null);
      fail("ArrayCreateAndInit(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }

  
  public void testArrayCreateAndInitClassNull() {
    try {
      new ArrayCreateAndInit((Class)null);
      fail("ArrayCreateAndInit(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }


  public void testArrayCreateAndInitClass() {
    assertEquals(10, int10.dimensionality);
    assertEquals(int.class, int10.leafType);
    
    assertEquals(3, string3.dimensionality);
    assertEquals(String.class, string3.leafType);
    
    assertEquals(2, set2.dimensionality);
    assertEquals(Set.class, set2.leafType);    
  }  
  
  
  public void testGetReturnType() {
    assertEquals(int[][][][][][][][][][].class, int10.getReturnType());    
    assertEquals(String[][][].class, string3.getReturnType());    
    assertEquals(Set[][].class, set2.getReturnType());
    assertEquals(int[].class, int1_length3.getReturnType());
  }


  public void testExecute() throws InvocationTargetException,
      IllegalAccessException, InstantiationException
  {
    assertTrue(Arrays.equals(
        int1_length3_val, (int[])int1_length3.execute()));
    assertFalse(Arrays.equals(
        int1_length4_val, (int[])int1_length3.execute()));
    assertFalse(Arrays.equals(
        new int[]{0,1,1}, (int[])int1_length3.execute()));
    assertFalse(Arrays.equals(
        int1_length2_val, (int[])int1_length3.execute()));
      
    assertTrue(Arrays.equals(
        string1_length3_val, 
        (String[])string1_length3.execute()));
      
    assertTrue(Arrays.deepEquals(
        int2_length2_val,
        (int[][])int2_length2.execute()));
  }

  
  public void testToStringClass() {
    assertEquals(
      "new String[]{(String)null, \"\", \"hallo\"}",
      string1_length3.toString(Object.class));

    assertEquals(
      "new java.lang.String[]{(java.lang.String)null, \"\", \"hallo\"}",
      string1_length3.toString(Loadee.class));    
    
    assertEquals(
        "new int[][]{new int[]{0, 1, 2}, new int[]{0, 1, 2}}",
        int2_length2.toString(Object.class));
    
    assertEquals(
      "new int[][]{new int[]{0, 1, 2}, new int[]{0, 1, 2}}",
      int2_length2.toString(Loadee.class));    
    }
}
