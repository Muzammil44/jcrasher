/*
 * CopyCLinitClassLoader.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit.reinit;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

/**
 * CopyCLinitClassLoader
 * 
 * Before loading a class treat it as follows. 
 * Note that classes belonging to JUnit or JCrasher are excluded of the treatment.
 * 
 * 1. Expose <clinit>()
 * If the class has a <clinit>() method, this is copied to methods clreinit() and clinit(). 
 * The former method is the re-initializer, while the latter will be used as the original initializer. 
 * If no static initializer exists, empty clreinit() and clinit() methods are added to the class.
 * 
 * 2. Adjust clreinit()
 * The clreinit() method is modified to differ from the original static initializer 
 * to avoid attempting to reset final fields. Every time <clinit()> sets a final static field, 
 * in clreinit() the corresponding putstatic call is replaced by nop.
 * 
 * 3. Track <clinit>() call
 * A static initializer, <clinit>(), method is added to the class.
 * This static initializer calls the original static initialization code, clinit(). 
 * On return from that code, the static initializer registers the fact that 
 * static initialization ended for this class in a JCrasher-maintained data structure
 * edu.gatech.cc.jcrasher.testall.runtime.ClassRegistry. 
 * The ending order of initializations will be the same as the order of re-initializations before future tests.
 *
 * Christoph Csallner
 * 2002-06-24 last changed
 */
public class CopyCLinitClassLoader 
	extends org.apache.bcel.util.ClassLoader	
	implements junit.runner.TestSuiteLoader {

	/* exclude classes of list of packages from being modified */
	private final static String[] ignoredPackages = new String[] {
		"java.", "sun.", "junit.", 
		"edu.gatech.cc.jcrasher.testall.runtime."
	};

	/**
	 * Constructor
	 * 
	 * java.lang.ClassLoader.loadClass(String, boolean) only overridden by super-class.
	 *   Here we provide the names of packages to be ignored, i.e. for transitive loads.
	 */
	public CopyCLinitClassLoader() {
		super(ignoredPackages);	//will be loaded with system class-loader
	}
	
	
	
	/**
	 * Implements junit.runner.TestSuiteLoader.load(String)
	 * 
	 * Redirects load-request for top-level test class XTest (passed-to-JUnit UI) 
	 * from JUnit UI to BCEL class-loader. Note that classes referenced by XTest, 
	 * i.e. XTest1, XTest2, ..., the class X under test, and all transitively
	 * referenced classes are loaded by the JVM via loadClass(String, boolean).
	 * 
	 * Adapted from junit.runner.StandardTestSuiteLoader.
	 * 
	 * @param className name of test-suite, specified by user in JUnit UI
	 */
	public Class<?> load(String className) throws ClassNotFoundException {
		return super.loadClass(className, true);	//load and link class--don't register test-case
	}


	
	/**
	 * Adapted from junit.runner.StandardTestSuiteLoader
	 * Uses the system class loader to load the test class
	 */
	public Class reload(Class aClass) {
		//throws ClassNotFoundException {
		return aClass;
	}


	
	/*
	 * Check wether pClazz represents a TestCase class or a class 
	 * from an ignored package.
	 * Can be called before class wrapped by pClazz has been completely loaded
	 */
	protected boolean isExcludedClass(JavaClass pClazz) {
		if (!pClazz.isPublic()) {		//exclude if not public
			return true;
		}		
		if (pClazz.isInterface()) {		//exclude if interface
			return true;
		}
		try {
			JavaClass[] classes = pClazz.getSuperClasses();
			for(int i=0; i<classes.length; i++) {
				if (classes[i].getClassName().equals("junit.framework.TestCase")) {
					return true;
				}
			}
		}
		catch (Exception /*ClassNotFoundException*/ e) {
			//suppress
		}
		
		for(int i=0; i<ignoredPackages.length; i++) {
			if (pClazz.getPackageName() == ignoredPackages[i]) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Check wether pClazz represents a TestCase class or a class 
	 * from an ignored package.
	 * Should only be called after c has been loaded
	 */
	private boolean isExcludedClass(Class<?> c) {
		if (!Modifier.isPublic(c.getModifiers())) {		//exclude if not public
			return true;
		}
		if (c.isInterface()) {		//exclude if interface
			return true;
		}				
		if (junit.framework.TestCase.class.isAssignableFrom(c)) {
			return true;
		}
		for(int i=0; i<ignoredPackages.length; i++) {
			if (c.getName().startsWith(ignoredPackages[i])) {
				return true;
			}
		}	
		return false;
	}


	/*
	 * Return clazz's <clinit>(). 
	 */
	private MethodGen get_clinit(ClassGen clazz) {
		/* Skim thru all fub-methods to find <clinit>() */
		MethodGen _clinit = null;
		for(int i=0; i < clazz.getMethods().length; i++) {
			notNull(clazz.getMethods()[i]);
			notNull(clazz.getMethods()[i].getName());
			if (clazz.getMethods()[i].getName().equals("<clinit>")) {
				_clinit = new MethodGen(
					clazz.getMethods()[i],	//found <clinit>()
					clazz.getClassName(),		//class name
					clazz.getConstantPool());	//class constant pool; 
			}
		}
		/* (_clinit == null) iff class has no <clinit>() */
		return _clinit;
	}
	
	
	/*
 	 * Add an empty <clinit>() to clazz and return it.
 	 */
	private MethodGen add_clinit(ClassGen clazz) {
		InstructionList instrList = new InstructionList();
		instrList.append(InstructionConstants.RETURN);	// return from void stack			
		MethodGen _clinit = new MethodGen(
			Constants.ACC_STATIC,	//access flags: default visibility
			Type.VOID,						//return type
			new Type[0],						//param types
			new String[0],					//param names
			"<clinit>",
			clazz.getClassName(),
			instrList, 
			clazz.getConstantPool());
					
		/* Add _clinit to list of methods */
		_clinit.setMaxLocals();
		_clinit.setMaxStack();
		clazz.addMethod(_clinit.getMethod());
		
		return _clinit;
	}


	/*
	 * Copy <clinit>() to clinit() and clreinit().
	 * Modify clreinit() and add both to class.
	 */
	private void copy_clinit(MethodGen _clinit, ClassGen clazz) {
		MethodGen clinit = new MethodGen(
			_clinit.getMethod(),            //pre-fill new meth with <clinit>()
			clazz.getClassName(),		//class name
			clazz.getConstantPool());	//class constant pool
		MethodGen clreinit = new MethodGen(
			_clinit.getMethod(),            //pre-fill new meth with <clinit>()
			clazz.getClassName(),		//class name
			clazz.getConstantPool());	//class constant pool
				
		/* change modifiers to non-static _clinit() */
		clinit.setName("clinit");
		clinit.setAccessFlags(Constants.ACC_PUBLIC + Constants.ACC_STATIC);
		clreinit.setName("clreinit");
		clreinit.setAccessFlags(Constants.ACC_PUBLIC + Constants.ACC_STATIC);		

		/* Add clinit() to list of methods */
		clinit.setMaxLocals();
		clinit.setMaxStack();			
		clazz.addMethod(clinit.getMethod());
		
		/* Modify clreinit() to leave constants alone. */
		modifyclreinit(clreinit, clazz);
		clreinit.setMaxLocals();
		clreinit.setMaxStack();
		clazz.addMethod(clreinit.getMethod());	//Add modified clreinit()	
	}


	/*
	 * Replace putstatic with pop if final
	 */
	private void modifyclreinit(MethodGen clreinit, ClassGen clazz) {
		notNull(clreinit);
		
		/* replace putstatic with pop iff final field concerned */	
		for (Iterator i=clreinit.getInstructionList().iterator(); i.hasNext();) {
			InstructionHandle instrHandle = (InstructionHandle) i.next();
			
			/* Find putstatic instruction (opcode==179) and optim for 1 or 2 bytes */				
			short opCode = instrHandle.getInstruction().getOpcode();				
			if ((opCode == Constants.PUTSTATIC)
				|| (opCode == Constants.PUTSTATIC2_QUICK)
				|| (opCode == Constants.PUTSTATIC_QUICK)) {
				
				/* 
				 * Make sure that a constant field is set:
				 * - Follow index of putstatic into constantpool
				 * - Get corresponding field from class
				 * - AND hex access modifier bitstrings
				 * Type-cast below assumed to be safed as of if check above 
				 */
				CPInstruction instr = (CPInstruction) instrHandle.getInstruction();
				ConstantCP fieldConst = (ConstantCP) clazz.getConstantPool().getConstant(instr.getIndex());
				ConstantNameAndType fieldSig = (ConstantNameAndType) clazz.getConstantPool().getConstant(fieldConst.getNameAndTypeIndex());
				Field field = clazz.containsField(fieldSig.getName(clazz.getConstantPool().getConstantPool()));
				
				/* 
				 * @see org.apache.bcel.Constants, modifiers get ORed = added here
				 * final := (16)10 = (10)16 = (10000)2
				 */
				if ((field.getAccessFlags() & Constants.ACC_FINAL) == Constants.ACC_FINAL) {

					/* Replace PUTSTATUC by NOP, leave branch target etc intact */
					instrHandle.setInstruction(InstructionConstants.NOP);
				}
			}			
		}
	}
	


	/* <clinit>() taken from edu.gatech.cc.jcrasher.testall.test.State
		[Code(max_stack = 3, max_locals = 0, code_length = 45)
		0:    invokestatic	edu.gatech.cc.jcrasher.testall.test.State._clinit ()V (41)
		3:    getstatic		edu.gatech.cc.jcrasher.testall.test.State.class$0 Ljava/lang/Class; (43)
		6:    dup
		7:    ifnonnull		#38
		10:   pop
		11:   ldc		"[Ledu.gatech.cc.jcrasher.test.State$1$Inner;" (45)
		13:   invokestatic	java.lang.Class.forName (Ljava/lang/String;)Ljava/lang/Class; (51)
		16:   invokevirtual	java.lang.Class.getComponentType ()Ljava/lang/Class; (55)
		19:   dup
		20:   putstatic		edu.gatech.cc.jcrasher.testall.test.State.class$0 Ljava/lang/Class; (43)
		23:   goto		#38
		26:   new		<java.lang.NoClassDefFoundError> (57)
		29:   dup_x1
		30:   swap
		31:   invokevirtual	java.lang.Throwable.getMessage ()Ljava/lang/String; (63)
		34:   invokespecial	java.lang.NoClassDefFoundError.<init> (Ljava/lang/String;)V (67)
		37:   athrow
		38:   invokevirtual	java.lang.Class.getDeclaringClass ()Ljava/lang/Class; (70)
		41:   invokestatic	edu.gatech.cc.junit.reinit.ClassRegistry.register (Ljava/lang/Class;)V (76)
		44:   return    
	 */
//	public static void clinit(){};	// clinit()
//	static void _clinit() {	// <clinit>()
//		clinit();			// initialize class
//		class Inner{};	// TODO bad hack: create an inner class to get a reference to this class
//		edu.gatech.cc.junit.reinit.ClassRegistry.register(Inner.class.getDeclaringClass());
//	}


	
//	/* 
//	 * Modify <clinit>() to first call clinit() and then register class in ClassRegistry.
//	 * Replicate above _clinit()
//	 */
//	 private void modify_clinit(MethodGen _clinit, ClassGen clazz) {
//		 assert _clinit != null; 
//		InstructionList instrList = new InstructionList();		// new emtpy method
//		
//		/* 0:    invokestatic	edu.gatech.cc.jcrasher.testall.test.State._clinit ()V (41) */
//		instrList.append(new INVOKESTATIC(42));	//TODO pass index into constant pool
//				
//		// TODO
//		
//		instrList.append(InstructionConstants.RETURN);	// return from void stack							 
//	 }


	/**************************************************************************
	 * Called by ClassLoader.loadClass()
	 * right before a class is defined = loaded from bytecode into heap.
	 * 
	 * Modify user classes as described in class javadoc.
	 * 
	 * @param pClazz bytecodes as found on disk via class-path
	 * @return modified bytecodes, which should be loaded to a class object
	 */
	@Override
	protected JavaClass modifyClass(JavaClass pClazz) {
		/* Exclude testcase classes and classes from ignoredPackages. */
		if (isExcludedClass(pClazz)) {
			return pClazz;
		}
		
		/* If class has no <clinit>() then create an empty one. */	
		ClassGen clazz = new ClassGen(pClazz);	// wrap to enable modifications		
  		//ConstantPoolGen constPool = clazz.getConstantPool();
  		MethodGen _clinit = get_clinit(clazz);
  		if (_clinit == null) {
  			_clinit = add_clinit(clazz);
  		}
							
		/* Copy <clinit>() to clreinit() and clinit(), modify clreinit() */	
		copy_clinit(_clinit, clazz);
		
		/* Modify <clinit>() to first call clinit() and then register class in ClassRegistry. */
		//modify_clinit(_clinit, clazz);	//TODO
			
		clazz.update();
		//System.out.println("\tmodified " +pClazz.getClassName());	//FIXME debug		
				
		return clazz.getJavaClass();
	}
	
	
	
	/**
	 * Called by jvm to load classes needed by test-case.
	 * Register classes under test needed by test-case
	 */
	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		Class<?> res = super.loadClass(className, resolve);	//modify, load, link (iff resolve), don't initialize
		
		//FIXME debug print names of classes requested (may have been loaded previously)
		//System.out.println("\tloaded " +res.getName());
		
		/* Exclude testcase classes and classes from ignoredPackages. */
		if (isExcludedClass(res)) {
			return res;
		}

		/* Force initialization 
		 * TODO hack: register should be called from res.clinit()
		 * and clinit() should be called by <clinit>()---whenever the JVM calls <clinit>(). */
		try {
			Method clinit = res.getDeclaredMethod("clinit", new Class[0]);
			clinit.invoke(null, new Object[0]);	//static meth, zero params
		}
		catch (Exception e) {
			e.printStackTrace();
		}		

		ClassRegistry.register(res);
		//System.out.println("\tregistered " +res.getName());	//FIXME debug		
				
		return res;
	}
}
