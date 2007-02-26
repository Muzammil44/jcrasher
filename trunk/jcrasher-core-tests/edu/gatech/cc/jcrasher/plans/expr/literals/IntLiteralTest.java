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
public class IntLiteralTest extends TestCase {

  protected final int _one = -1;
  protected final int zero = 0;
  protected final int one = 1;

  protected final IntLiteral _oneLit = new IntLiteral(_one);
  protected final IntLiteral zeroLit = new IntLiteral(zero);
  protected final IntLiteral oneLit = new IntLiteral(one);
  
  
  /***/
  public void testGetReturnType() {
    assertEquals(Integer.TYPE, _oneLit.getReturnType());
    assertEquals(Integer.TYPE, zeroLit.getReturnType());
    assertEquals(Integer.TYPE, oneLit.getReturnType());
  }

  /***/
  public void testExecute() {
    assertEquals(Integer.valueOf(_one), _oneLit.execute());
    assertEquals(Integer.valueOf(zero), zeroLit.execute());
    assertEquals(Integer.valueOf(one), oneLit.execute());
  }

  /***/
  public void testText() {
    assertEquals("-1", _oneLit.text());
    assertEquals("0", zeroLit.text());
    assertEquals("1", oneLit.text());
  }
  
  /***/
  public void testToString() {
    assertEquals(_oneLit.toString(), _oneLit.text());
    assertEquals(zeroLit.toString(), zeroLit.text());
    assertEquals(oneLit.toString(), oneLit.text());
  }
}
