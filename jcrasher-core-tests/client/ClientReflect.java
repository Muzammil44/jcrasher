/*
 * ClientReflect.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package client;

import java.lang.reflect.Method;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ClientReflect {
  
  public Method sum() {
    try {
      return Client.class.getMethod(
          "sum",
          new Class[]{
              int.class,
              int.class,
              int.class,
              int.class,
              int.class,
              int.class,
              int.class});
    }
    catch(NoSuchMethodException e) {
      throw new NoSuchMethodError();
    }
  }
}
