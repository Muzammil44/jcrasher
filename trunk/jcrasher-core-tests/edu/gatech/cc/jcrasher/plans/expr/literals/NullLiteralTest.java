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
import client.Client;
import client.sub.Needed;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class NullLiteralTest extends TestCase {

  protected final NullLiteral<String> nullStringForClient = 
  	new NullLiteral<String>(String.class, Client.class);
  
  protected final NullLiteral<Needed> nullNeededForClient = 
  	new NullLiteral<Needed>(Needed.class, Client.class);
  
  protected final NullLiteral<Client> nullClientForClient = 
  	new NullLiteral<Client>(Client.class, Client.class);

  
  /***/
  public void testGetReturnType() {
    assertEquals(String.class, nullStringForClient.getReturnType());
    assertEquals(Needed.class, nullNeededForClient.getReturnType());
    assertEquals(Client.class, nullClientForClient.getReturnType());
  }


  /***/
  public void testExecute() {
    assertEquals(null, nullStringForClient.execute());
    assertEquals(null, nullNeededForClient.execute());
    assertEquals(null, nullClientForClient.execute());
  }

  /***/
  public void testText() {    
    assertEquals("(java.lang.String)null", nullStringForClient.text());
    assertEquals("(client.sub.Needed)null", nullNeededForClient.text());    
    assertEquals("(Client)null", nullClientForClient.text());
  }
  
  /***/
  public void testToString() {    
    assertEquals(nullStringForClient.toString(), nullStringForClient.text());
    assertEquals(nullNeededForClient.toString(), nullNeededForClient.text());    
    assertEquals(nullClientForClient.toString(), nullClientForClient.text());
  }  
}
