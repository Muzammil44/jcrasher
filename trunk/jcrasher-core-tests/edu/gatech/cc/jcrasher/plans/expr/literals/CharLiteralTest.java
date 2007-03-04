/*
 * CharLiteralTest.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.expr.literals;

import junit.framework.TestCase;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class CharLiteralTest extends TestCase {

  protected final CharLiteral newlineLiteral = new CharLiteral('\n');
  protected final CharLiteral spaceLiteral = new CharLiteral(' ');
  protected final CharLiteral tickLiteral = new CharLiteral('\'');
  protected final CharLiteral quoteLiteral = new CharLiteral('"');
  protected final CharLiteral backslashLiteral = new CharLiteral('\\');
  
  
  /***/
  public void testGetReturnType() {
    assertEquals(char.class, newlineLiteral.getReturnType());
  }
  
  public void testText() {
//    System.out.println(newlineLiteral.text());
//    System.out.println(tick.text());
//    System.out.println(quote.text());
//    System.out.println(backslash.text());
    
    assertEquals("'\\n'", newlineLiteral.text());
    assertEquals("' '", spaceLiteral.text());
    assertEquals("'\\''", tickLiteral.text());
    assertEquals("'\"'", quoteLiteral.text());
    assertEquals("'\\\\'", backslashLiteral.text());    
  }
  
  public void testToString() {
    assertEquals(newlineLiteral.toString(), newlineLiteral.text());
    assertEquals(spaceLiteral.toString(), spaceLiteral.text());
    assertEquals(tickLiteral.toString(), tickLiteral.text());
    assertEquals(quoteLiteral.toString(), quoteLiteral.text());
    assertEquals(backslashLiteral.toString(), backslashLiteral.text());
  }
}
