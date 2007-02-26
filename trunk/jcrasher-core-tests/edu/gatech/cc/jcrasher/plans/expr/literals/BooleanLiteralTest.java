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
public class BooleanLiteralTest extends TestCase {

  protected final BooleanLiteral trueLit = new BooleanLiteral(true);
  protected final BooleanLiteral falseLit = new BooleanLiteral(false);

  /***/
  public void testGetReturnType() {
    assertEquals(Boolean.TYPE, trueLit.getReturnType());
    assertEquals(Boolean.TYPE, falseLit.getReturnType());
  }

  /***/
  public void testExecute() {
    assertEquals(Boolean.TRUE, trueLit.execute());
    assertEquals(Boolean.FALSE, falseLit.execute());
  }

  /***/
  public void testText() {
    assertEquals("true", trueLit.text());
    assertEquals("false", falseLit.text());
  }
  
  /***/
  public void testToString() {
    assertEquals(trueLit.toString(), trueLit.text());
    assertEquals(falseLit.toString(), falseLit.text());
  }  
}
