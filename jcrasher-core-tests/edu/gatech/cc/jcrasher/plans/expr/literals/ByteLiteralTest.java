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
public class ByteLiteralTest extends TestCase {
  
  protected final byte _one = (byte)-1;
  protected final byte zero = (byte)0;
  protected final byte one = (byte)1;

  protected final ByteLiteral _oneLit = new ByteLiteral(_one);
  protected final ByteLiteral zeroLit = new ByteLiteral(zero);
  protected final ByteLiteral oneLit = new ByteLiteral(one);
  
  /***/
  public void testGetReturnType() {
    assertEquals(Byte.TYPE, _oneLit.getReturnType());
    assertEquals(Byte.TYPE, zeroLit.getReturnType());
    assertEquals(Byte.TYPE, oneLit.getReturnType());
  }

  /***/
  public void testExecute() {
    assertEquals(Byte.valueOf(_one), _oneLit.execute());
    assertEquals(Byte.valueOf(zero), zeroLit.execute());
    assertEquals(Byte.valueOf(one), oneLit.execute());
  }

  /***/
  public void testToStringClass() {
    assertEquals("(byte)-1", _oneLit.toString(Loadee.class));
    assertEquals("(byte)0", zeroLit.toString(Loadee.class));
    assertEquals("(byte)1", oneLit.toString(Loadee.class));
  }

}
