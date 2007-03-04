/*
 * PresetValues.java
 * 
 * Copyright 2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.types;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Vector;

import edu.gatech.cc.jcrasher.Assertions;
import edu.gatech.cc.jcrasher.plans.expr.ArrayCreateAndInit;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.DotClass;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.literals.BooleanLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.ByteLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.CharLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.DoubleLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.FloatLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.LongLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.PrimitiveLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.ShortLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.StringLiteral;

/**
 * Set hardcoded values:
 * 
 * Set array of simple type like int[] using predefined simple values. So
 * instead of looking for int[] returning methods we settle for a few hardcoded
 * int[] like {}, {0}, {-1, 0}.
 * 
 * TODO: General treatment of simple type arrays of arbitrary dimension, see:
 * http://java.sun.com/docs/books/vmspec/2nd-edition/html/ClassFile.doc.html#14152
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class PresetValues {

  /* Primitive types */
  protected static final Expression[] booleanPlans = new Expression[]{
    new BooleanLiteral(false),
    new BooleanLiteral(true)};
  protected static final Expression[] bytePlans = new Expression[]{
    new ByteLiteral((byte) 0),
    new ByteLiteral((byte) 255)};
  protected static final Expression[] charPlans = new Expression[]{
    new CharLiteral(' '),
    new CharLiteral('\n')};
  protected static final Expression[] shortPlans = new Expression[]{
    new ShortLiteral((short) -1),
    new ShortLiteral((short) 0)};
  protected static final Expression[] intPlans = new Expression[]{
    new IntLiteral(-1),
    new IntLiteral(0),
    new IntLiteral(1)};
  protected static final Expression[] longPlans = new Expression[]{
    new LongLiteral(-1),
    new LongLiteral(0)};
  protected static final Expression[] floatPlans = new Expression[]{
    new FloatLiteral(-100.123456789f),
    new FloatLiteral(0.0f)};
  protected static final Expression[] doublePlans = new Expression[]{
    new DoubleLiteral(-1.123456789d),
    new DoubleLiteral(0.0d)};


  /* Complex types */
  protected static final Expression[] classPlans = new Expression[]{new DotClass()};
  protected static final Expression[] stringPlans = new Expression[]{
    new StringLiteral(""), new StringLiteral("\"\n\\.`\'@#$%^&/({<[|\\n:.,;")};


  /*
   * We use a fake hard-coded testee type of java.util.Vector
   * when the return type is in java.lang. This forces JavaCode.text() to
   * emit full qualified type name of the return type.
   */
  

  /*
   * new Object()
   */
  protected static Expression[] getObject() {
    Constructor<Object> new_Object = null;
    try {
      new_Object = Object.class.getDeclaredConstructor(new Class[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    notNull(new_Object);
    return new ConstructorCall[] {	//FIXME: Vector just a placeholder for real testee
    		new ConstructorCall<Object>(Vector.class, new_Object, new Expression[0])};
  }


  /*
   * new java.util.Hashtable() --implements java.util.Map
   */
  protected static Expression<Hashtable>[] getHashtable() {
    Constructor<Hashtable> con = null;
    try {
      con = Hashtable.class.getDeclaredConstructor(new Class[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    notNull(con);

    return new Expression[]{
    		new ConstructorCall<Hashtable>(Object.class, con, new Expression[0])
    };
  }


  /*
   * new java.util.Vector() --implements java.util.List
   */
  protected static Expression<Vector>[] getVector() {
    Constructor<Vector> con = null;
    try {
      con = java.util.Vector.class.getDeclaredConstructor(new Class[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
    notNull(con);

    return new Expression[]{new ConstructorCall<Vector>(Object.class, con, new Expression[0])};
  }


  /*
   * int[]
   */
  protected static Expression<int[]>[] getIntArray1() {
    Class<int[]> c = int[].class;
    ArrayCreateAndInit[] plans = new ArrayCreateAndInit[2];

    plans[0] = new ArrayCreateAndInit<int[]>(c, Vector.class); // {}
    plans[0].setComponentPlans(new PrimitiveLiteral[0]);
    plans[1] = new ArrayCreateAndInit<int[]>(c, Vector.class); // {-1}
    plans[1].setComponentPlans(new PrimitiveLiteral[]{new IntLiteral(-1)});
    return plans;
  }


  /*
   * String[]
   */
  protected static Expression<String[]>[] getStringArray1() {
    Class<String[]> c = String[].class;
    
    ArrayCreateAndInit[] plans = new ArrayCreateAndInit[3];
    plans[0] = new ArrayCreateAndInit<String[]>(c, Vector.class);
    plans[0].setComponentPlans(new StringLiteral[0]); // {}
    plans[1] = new ArrayCreateAndInit<String[]>(c, Vector.class);
    plans[1].setComponentPlans(new StringLiteral[]{ // {""}
        new StringLiteral("")});
    plans[2] = new ArrayCreateAndInit<String[]>(c, Vector.class);
    plans[2].setComponentPlans(new StringLiteral[]{ // {"\"","\n","'"}
        new StringLiteral("\""),
        new StringLiteral("\n"),
        new StringLiteral("'")});
    return plans;
  }


  /*
   * T[]
   */
  protected static <T> Expression<T>[] getEmptyArray(final Class<T> c) {
    ArrayCreateAndInit[] plans = new ArrayCreateAndInit[1];

    plans[0] = new ArrayCreateAndInit<T>(c, Assertions.class); // {}
    plans[0].setComponentPlans(new Expression[0]);

    return plans;
  }

  /**
   * @return possibly empty list of preset plans, but never null.
   */
  public static Expression[] getPreset(final Class<?> pClass) {
    /* Primitive */
    if (pClass.equals(boolean.class)) {
      return booleanPlans;
    }
    if (pClass.equals(byte.class)) {
      return bytePlans;
    }
    if (pClass.equals(char.class)) {
      return charPlans;
    }
    if (pClass.equals(short.class)) {
      return shortPlans;
    }
    if (pClass.equals(int.class)) {
      return intPlans;
    }
    if (pClass.equals(long.class)) {
      return longPlans;
    }
    if (pClass.equals(float.class)) {
      return floatPlans;
    }
    if (pClass.equals(double.class)) {
      return doublePlans;
    }

    /* Complex */
    if (pClass.equals(Class.class)) {
      return classPlans;
    }
    if (pClass.equals(Comparable.class)) {
      return stringPlans;
    }
    if (pClass.equals(String.class)) {
      return stringPlans;
    }
    if (pClass.equals(Object.class)) {
      return getObject();
    }

    if (pClass.equals(java.util.List.class)) {
      return getVector();
    }
    if (pClass.equals(java.util.Map.class)) {
      return getHashtable();
    }
    if (pClass.equals(java.util.Vector.class)) {
      return getVector();
    }

    /* Array */
     
    if (pClass.equals(int[].class)) {
      return getIntArray1();
    }
    
    if (pClass.equals(String[].class)) {
      return getStringArray1();
    }
    
    //FIXME: Add following back: 
    if (pClass.isArray()) {
      return getEmptyArray(pClass);
    }

    /* No preset plans for other complex types */
    return new Expression[0];
  }
}
