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

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class StringLiteralTest extends TestCase {

  protected final String empty = "";
  protected final String funky = "$funky_";
  protected final String newline = "\n";
  protected final String space = " ";
  protected final String tick = "'";
  protected final String quote = "\"";
  protected final String backslash = "\\";
  
  protected final StringLiteral emptyLiteral = new StringLiteral(empty);
  protected final StringLiteral funkyLiteral = new StringLiteral(funky);
  protected final StringLiteral newlineLiteral = new StringLiteral(newline);
  protected final StringLiteral spaceLiteral = new StringLiteral(space);
  protected final StringLiteral tickLiteral = new StringLiteral(tick);
  protected final StringLiteral quoteLiteral = new StringLiteral(quote);
  protected final StringLiteral backslashLiteral = new StringLiteral(backslash);
  
  /***/
  public void testGetReturnType() {
    assertEquals(String.class, emptyLiteral.getReturnType());
    assertEquals(String.class, funkyLiteral.getReturnType());
  }

  /***/
  public void testExecute() {
    assertEquals(empty, emptyLiteral.execute());
    assertEquals(funky, funkyLiteral.execute());
  }
  
  /***/
  public void testText() {
//    System.out.println(newlineLiteral.text());
//    System.out.println(spaceLiteral.text());
//    System.out.println(tickLiteral.text());
//    System.out.println(quoteLiteral.text());
//    System.out.println(backslashLiteral.text());
    
    assertEquals("\"\"", emptyLiteral.text());
    assertEquals("\""+funky+"\"", funkyLiteral.text());
    assertEquals("\"\\n\"", newlineLiteral.text());
    assertEquals("\" \"", spaceLiteral.text());
    assertEquals("\"'\"", tickLiteral.text());
    assertEquals("\"\\\"\"", quoteLiteral.text());
    assertEquals("\"\\\\\"", backslashLiteral.text());
  }
  
  /***/
  public void testToString() {
    assertEquals(emptyLiteral.toString(), emptyLiteral.text());
    assertEquals(funkyLiteral.toString(), funkyLiteral.text());
  }

  /***/
  public void testStringLiteralNull() {
    try {
      new StringLiteral(null);
      fail("StringLiteral(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  }
}
