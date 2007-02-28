// Copyright 2006 Google Inc. All Rights Reserved.

package edu.gatech.cc.jcrasher;

/**
 * Makes assertion checking more elegant than explicit if statements. Avoids the
 * -disableassertions problem of Java's assert statements. Avoids copyright
 * issues of Google's Preconditions class.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class Assertions {

  /**
   * @throws NullPointerException iff (t==null)
   */
  public static <T> T notNull(final T t) {
    if (t == null) {
      throw new NullPointerException();
    	//throw new Error("notNull(null)");
    }
    return t;
  }


  /**
   * Checks if b holds. Call this method to check assertions like
   * pre- and post-conditions.
   * 
   * @throws IllegalStateException iff (b==false)
   */
  public static void check(boolean b) {
    if (b == false) {
      throw new IllegalStateException();
    }
  }
}
