/*
 * MiniClientReflect.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package client;

import java.lang.reflect.Method;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class MiniClientReflect {
  
  public Method one() {
    try {
      return MiniClient.class.getMethod("one", new Class[0]);
    }
    catch(NoSuchMethodException e) {
      throw new NoSuchMethodError();
    }
  }
}
