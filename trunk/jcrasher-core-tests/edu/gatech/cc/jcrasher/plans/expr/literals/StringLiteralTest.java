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
public class StringLiteralTest extends TestCase {

  protected final String empty = "";
  protected final String funky = "$funky_";
  
  protected final StringLiteral emptyLit = new StringLiteral(empty);
  protected final StringLiteral funkyLit = new StringLiteral(funky);
  
  
  public void testGetReturnType() {
    assertEquals(String.class, emptyLit.getReturnType());
    assertEquals(String.class, funkyLit.getReturnType());
  }
  
  public void testToStringClass() {
    assertEquals("\"\"", emptyLit.toString(Loadee.class));
    assertEquals("\""+funky+"\"", funkyLit.toString(Loadee.class));
  }

  public void testExecute() {
    assertEquals(empty, emptyLit.execute());
    assertEquals(funky, funkyLit.execute());
  }

  public void testStringLiteralNull() {
    try {
      new StringLiteral(null);
      fail("StringLiteral(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }

}