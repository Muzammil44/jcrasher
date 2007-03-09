/*
 * ClassRegistry.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit.reinit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

/**
 * ClassRegistry
 * 
 * 1.Keeps an accumulating list of the classes loaded by 
 *    the previously executed test-cases. (Keeps a list boolean
 *    flags indicating if a class has already been re-initialized.)
 * 2. Has the the ability to reset the values of the static fields
 *    of each of these classes to the default (before each test-case execution).
 * 3. Has the ability to execute the variable initializer of each static field
 *    (before each test-case execution).
 * 
 * Automatic Testing: 
 * Crash java classes by passing inconvenient params
 * 
 * Christoph Csallner
 * 2002-11-19 last modified
 */
public class ClassRegistry {

	private static List<Class<?>> classes = new Vector<Class<?>>();
	private final static Class[] zeroFormalParams = new Class[0];

	/*
	 * Sets a static non-final field to all-zeros = {null, 0, 0.0, false}
	 * 
	 * Precond:
	 * 1. f static
	 * 2. f non-final
	 * 
	 * @param Field of any type
	 */
	private static void zeroField(Field f) throws IllegalAccessException {
		f.setAccessible(true);	//suppress Java language access checking
		
		//FIXME debug
		//System.out.println("\tresetting " +f.getDeclaringClass().getName() +"." +f.getName() +" \"" +f.get(null) +"\"");			
		
		Class<?> fieldType = f.getType();	//switch case (field type)
		if (! fieldType.isPrimitive()) {f.set(null, null);}	//receiver-obj := null as static, value := null as reset
		else {	//primitive types
			if (fieldType.getName() == "boolean") {f.setBoolean(null, false);}
			if (fieldType.getName() == "byte") {f.setByte(null, (byte)0);}
			if (fieldType.getName() == "char") {f.setChar(null, (char)0);}
			if (fieldType.getName() == "short") {f.setShort(null, (short)0);}
			if (fieldType.getName() == "int") {f.setInt(null, 0);}
			if (fieldType.getName() == "long") {f.setLong(null, 0);}
			if (fieldType.getName() == "float") {f.setFloat(null, 0.0f);}
			if (fieldType.getName() == "double") {f.setDouble(null, 0.0d);}		
		}
		
		//FIXME debug
		//System.out.println(" --> \"" +f.get(null) +"\"");		
	}	 


	/*
	 * Reset the values of c's non-final static fields to the default = {null, 0, 0.0, false}
	 */
	private static void zeroStaticFields(Class<?> c) throws IllegalAccessException {
		Field[] fields = c.getDeclaredFields();	//get all (private, default, protected, public) fields declared by c
		for (int i=0; i<fields.length; i++) {
			int fieldModifiers = fields[i].getModifiers();
			if ((Modifier.isStatic(fieldModifiers))
				&& (!Modifier.isFinal(fieldModifiers))
				&& (!fields[i].getName().startsWith("class$"))) {	//hack: exclude fields starting with class$
					
				zeroField(fields[i]);
			}
		}
	}
	

	/*
	 * Reset the values of the non-final static fields of the classes loaded by 
     * the previously executed test-cases to the default = {null, 0, 0.0, false}
	 * 
	 * Called by TTest.setup() => resetClasses()
	 *    (before each test-case execution)
	 */
	protected static void zeroStaticFields() throws IllegalAccessException {
		for (int i=0; i<classes.size(); i++) {
			Class<?> c = classes.get(i);
			zeroStaticFields(c);
		}
	}

	 

	/*
	 * Re-Execute the variable initializer of each static field, which 
	 *    have all been compiled to <clinit>().
	 * 
	 * Called in the quick-and-dirty approach by 
	 * edu.gatech.cc.jcrasher.testall.runtime.ClassRegistry.resetClasses(), 
	 * which is called by TestCase.setup()---before each test-case execution.
	 * 
	 * Copy clreinit() of class initializer <clinit>() modified by BCEL-classloader:
	 * To avoid resetting final fields: Every time a static final field is set, the 
	 * corresponding putstatic call is replaced by pop.
	 * 
	 *  Should be executed at-most-once before execution of next test-case.
	 * 
	 * Precond:
	 * 1. Each non-final static field declared by this class has been reset to all-zeros.
	 */
	protected static void classInitializers() throws Exception {
		for (int i=0; i<classes.size(); i++) {
			Class<?> c = classes.get(i);
			Method clreinit = c.getDeclaredMethod("clreinit", zeroFormalParams);
			clreinit.invoke(null, new Object[0]);	//static meth, zero params
		}			
	}
	
	
	 
	/**
	 * BCEL-ClassLoader calls this method to register a loaded class in the runtime.
	 * The order in which classes are registered is preserved in the list.
	 */
	public static void register(Class<?> c) {
		assert c != null;		
		classes.add(c);	//appends c to the end of the list
	}
	
	
	
	/**
	 * JUnit TestCase.setup() calls this method to reset static state of all registered classes:
	 * 1. The static fields of the registered classes are set to all zero bits
	 *    using Java reflection.
	 * 2. The clinit() method is executed on each registered class---fixed
	 *    ordering as defined by list---quick-and-dirty.
	 */
	public static void resetClasses() {
		try {
			zeroStaticFields();
			
			//FIXME TODO: de-tangle quick-and-dirty approach from above register-reset-to-zero 
			//   which is needed for each load-time approach.
			classInitializers();
		}
		catch (Exception e) {	//should not happen
			e.printStackTrace();
		}
	}
}
