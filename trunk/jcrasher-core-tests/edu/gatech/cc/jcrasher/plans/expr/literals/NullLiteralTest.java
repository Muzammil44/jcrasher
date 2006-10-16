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
package edu.gatech.cc.jcrasher.plans.expr.literals;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class NullLiteralTest extends TestCase {

  protected final NullLiteral<String> nullString = 
  	new NullLiteral<String>(String.class);
  protected final NullLiteral<Loadee> nullLoadee = 
  	new NullLiteral<Loadee>(Loadee.class);
  
  /***/
  public void testGetReturnType() {
    assertEquals(String.class, nullString.getReturnType());
    assertEquals(Loadee.class, nullLoadee.getReturnType());
  }

  /***/
  public void testNullLiteralNull() {
    try {
      new NullLiteral<String>(null);
      fail("NullLiteral(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }

  /***/
  public void testExecute() {
    assertEquals(null, nullString.execute());
    assertEquals(null, nullLoadee.execute());
  }

  /***/
  public void testToStringClass() {
    assertEquals("(String)null", nullString.toString(String.class));
    assertEquals("(java.lang.String)null", nullString.toString(Loadee.class));
    
    assertEquals("(Loadee)null", nullLoadee.toString(Loadee.class));
    assertEquals("(edu.gatech.cc.jcrasher.Loadee)null", nullLoadee.toString(String.class));
  }
}
