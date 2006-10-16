/*
 * CodeGenFct.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.writer;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

/**
 * Functions commonly used to produce proper java code
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class CodeGenFct {


  /**
   * java.lang.Class --> java source name
   * 
   * TODO: Provided by Class.getCanonicalName in Java 1.5
   * 
   * @return name of class to be accepted by javac like A[][], A.I
   */
  public static String getName(final Class<?> pClass) {
    Class<?> myClass = notNull(pClass);
    String res = null;

    /* Leaf type represents array */
    int arrayDepth = 0;
    while (myClass.isArray() == true) {
      myClass = myClass.getComponentType();
      arrayDepth += 1;
    }

    /* simple name (of component type) */
    StringBuilder className = new StringBuilder(myClass.getName());

    /* Remove "p.Enc$" from "Enc$Nested" in class "p.Enc.Nested" */
    if (myClass.getDeclaringClass() != null) {
      int enclosingNameLength = myClass.getDeclaringClass().getName().length(); // Enc
      className.setCharAt(enclosingNameLength, '.');
    }

    /* Append [] times array-depth */
    for (int i = 0; i < arrayDepth; i++) {
      className.append("[]");
    }

    res = className.toString();

    return notNull(res);
  }



  /*
   * If array, convert to leaf type. Return top-most enclosing type.
   */
  protected static Class<?> getLeafTopLevelName(final Class<?> pType) {
    Class<?> type = notNull(pType);

    while (type.isArray()) {
      type = type.getComponentType(); // leaf type of interest
    }

    while (type.getDeclaringClass() != null) {
      type = type.getDeclaringClass(); // declaring type of interest
    }

    return type;
  }


  /**
   * @return type's name as it would appear in source code. If testee is in the
   *         same package as type then we strip the package from type's name.
   *         
   * TODO(csallner) test this for nested testee type.
   */
  public static String getName(final Class<?> type, final Class<?> testee) {
    notNull(type);
    notNull(testee);

    /*
     * Generally stripping java.lang from types in java.lang is a bad idea: If
     * the class under test is in a package that has a type Object, Class, etc.
     * then any reference to Object, Class, etc. is resolved to the user-type.
     */

    /* Package name of pClass */
    String classTopLevel = getLeafTopLevelName(type).getCanonicalName();
    String classPackage = "";
    if (classTopLevel.indexOf('.') >= 0) {
      classPackage = classTopLevel.substring(0, classTopLevel.lastIndexOf('.'));
    }

    /* Package name of testee */
    String testeeTopLevel = getLeafTopLevelName(testee).getCanonicalName();
    String testeePackage = "";
    if (testeeTopLevel.indexOf('.') >= 0) {
      testeePackage = testeeTopLevel.substring(0, testeeTopLevel
        .lastIndexOf('.'));
    }

    /* className */
    String className = getName(type);

    if (classPackage.equals(testeePackage) && classPackage.length() > 0) {
      className = className.substring(classPackage.length() + 1); // suppress
                                                                  // package
    }

    return className;
  }
}
