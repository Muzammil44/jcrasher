/*
 * LoadeeReflect.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package client.sub;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class LoadeeReflect {

  /**
   * Loadee.Inner(),
   */
  public Constructor<Loadee.Inner> innerConstructor() {
  	try{ 
  		return Loadee.Inner.class.getConstructor(Loadee.class);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
  }
	
  /**
   * Loadee.Inner(int)
   */
  public Constructor<Loadee.Inner> innerConstructorInt() {
  	try{ 
  		return Loadee.Inner.class.getConstructor(Loadee.class, int.class);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
  }
  	
	/**
	 * Loadee.Inner.innerMeth()
	 */
	public Method innerMeth() {
		try {
			return Loadee.Inner.class.getMethod("innerMeth", new Class[0]);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
	}
	
	
	/* Static Member Type */
	
	/**
	 * Loadee.StaticMember.staticMemberStaticMeth()
	 */
	public Method staticMemberStaticMethod() {
		try {
			return Loadee.StaticMember.class.getMethod(
		      "staticMemberStaticMeth", new Class[0]);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
	}
  
  /**
   * Loadee.StaticMember(int)
   */
  public Constructor<Loadee.StaticMember> staticMemberConstructor(Class...classes) {
  	try {
  		return Loadee.StaticMember.class.getConstructor(classes);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
  }

	
	/**
	 * Loadee.StaticMember.staticMemberMeth()
	 */
	public Method staticMemberMeth() {
		try {
			return Loadee.StaticMember.class.getMethod(
		      "staticMemberMeth", new Class[0]);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
	}
	

	/* Loadee member method */
	
	
	/**
	 * Loadee.staticMeth(),
	 * Loadee.staticMeth(int)
	 */
	public Method staticMeth(Class...classes) {
		try {
			return Loadee.class.getMethod("staticMeth", classes);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
	}
	
	/**
	 * Loadee(),
	 * Loadee(int),
	 * Loadee(boolean)
	 */
  public Constructor<Loadee> constructor(Class...classes) {
  	try {
  		return Loadee.class.getConstructor(classes);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
  }
  
	
	/**
	 * Loadee.intMeth() 
	 */
	public Method intMeth() {
		try {
			return Loadee.class.getMethod("intMeth", new Class[0]);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
	}
	
	/**
	 * Loadee.meth(),
	 * Loadee.meth(int)
	 */
	public Method meth(Class...classes) {
		try {
			return Loadee.class.getMethod("meth", classes);
		}
		catch(NoSuchMethodException e) {
			throw new NoSuchMethodError();
		}
	}	
}
