/*
 * LoadeeCallForClient.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package client.sub;

import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.plans.expr.literals.BooleanLiteral;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;

/**
 * Method and constructor calls to test Client.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class LoadeeCall {

	protected final LoadeeReflect loadeeReflect = new LoadeeReflect();
	protected Class<?> testeeType;
	
	/**
	 * Constructor
	 */
	public LoadeeCall(Class<?> testeeType) {
		this.testeeType = testeeType;
	}
	
	
  /**
   * Loadee.Inner()
   */
  public ConstructorCall<Loadee.Inner> innerConstructor(ConstructorCall<Loadee> constructor) {
  	return new ConstructorCall<Loadee.Inner>(
  			testeeType,
  			loadeeReflect.innerConstructor(),
  			new Expression[0],
  			constructor);
  }
  
  
  /**
   * Loadee.Inner(int)
   */
  public ConstructorCall<Loadee.Inner> innerConstructor(
  		ConstructorCall<Loadee> constructor,
  		int value)
  	{
  	return new ConstructorCall<Loadee.Inner>(
  			testeeType,
  			loadeeReflect.innerConstructorInt(),
  			new Expression[]{new IntLiteral(value)},
  			constructor);
  }  	
	
	/**
	 * Loadee.Inner.innerMeth()
	 */
	public MethodCall<Integer> innerMeth(ConstructorCall<Loadee.Inner> constructor) {
		return new MethodCall<Integer>(
				testeeType,
				loadeeReflect.innerMeth(),
				new Expression[0],
				constructor);
	}
	
	
	/* Static Member Type */
	
	/**
	 * Loadee.StaticMember.staticMemberStaticMeth()
	 */
	public MethodCall<Integer> staticMemberStaticMethod() {
		return new MethodCall<Integer>(
				testeeType,
				loadeeReflect.staticMemberStaticMethod(),
				new Expression[0]);
	}
	
  /**
   * Loadee.StaticMember()
   */
  public ConstructorCall<Loadee.StaticMember> staticMemberConstructor() {
  	return new ConstructorCall<Loadee.StaticMember>(
  			testeeType,
  			loadeeReflect.staticMemberConstructor(),
  			new Expression[0]);
  }
  
  /**
   * Loadee.StaticMember(int)
   */
  public ConstructorCall<Loadee.StaticMember> staticMemberConstructor(int value) 
  {
  	return new ConstructorCall<Loadee.StaticMember>(
  			testeeType,
  			loadeeReflect.staticMemberConstructor(int.class),
  			new Expression[]{new IntLiteral(value)});
  }

  
	/**
	 * Loadee.StaticMember.staticMemberMeth()
	 */
	public MethodCall<Integer> staticMemberMeth(
			ConstructorCall<Loadee.StaticMember> constructor)
	{
		return new MethodCall<Integer>(
				testeeType,
				loadeeReflect.staticMemberMeth(),
				new Expression[0],
				constructor);
	}
	

	/* Loadee member method */
	
	
	/**
	 * Loadee.staticMeth()
	 */
	public MethodCall<Void> staticMeth() {
		return new MethodCall<Void>(
				testeeType,
				loadeeReflect.staticMeth(),
				new Expression[0]);
	}
	
	/**
	 * Loadee.staticMeth(int)
	 */
	public MethodCall<Void> staticMeth(int value) {
		return new MethodCall<Void>(
				testeeType,
				loadeeReflect.staticMeth(int.class),
				new Expression[]{new IntLiteral(value)});
	}
	
	/**
	 * Loadee()
	 */
  public ConstructorCall<Loadee> constructor(){
  	return new ConstructorCall<Loadee>(
  			testeeType,
  			loadeeReflect.constructor(),
  			new Expression[0]);
  }
  
  /**
   * Loadee(int)
   */
  public ConstructorCall<Loadee> constructor(int value) {
  	return new ConstructorCall<Loadee>(
  			testeeType,
  			loadeeReflect.constructor(int.class),
  			new Expression[]{new IntLiteral(value)});
  }

  /**
   * Loadee(boolean)
   */
  public ConstructorCall<Loadee> constructor(boolean value) {
  	return new ConstructorCall<Loadee>(
  			testeeType,
  			loadeeReflect.constructor(boolean.class),
  			new Expression[]{new BooleanLiteral(value)});
  }  

	
	/**
	 * (new Loadee()).intMeth() 
	 */
	public MethodCall<Integer> intMeth(ConstructorCall<Loadee> constructor){
		return new MethodCall<Integer>(
				testeeType,
				loadeeReflect.intMeth(),
				new Expression[0],
				constructor);
	}
	
	/**
	 * Loadee.meth()
	 */
	public MethodCall<Void> meth(ConstructorCall<Loadee> constructor) {
		return new MethodCall<Void>(
				testeeType,
				loadeeReflect.meth(),
				new Expression[0],
				constructor);
	}
	
	/**
	 * Loadee.meth(int) 
	 */
	public MethodCall<Void> meth(ConstructorCall<Loadee> constructor, int value) {
		return new MethodCall<Void>(
				testeeType,
				loadeeReflect.meth(int.class),
				new Expression[]{new IntLiteral(value)},
				constructor);
	}  

}
