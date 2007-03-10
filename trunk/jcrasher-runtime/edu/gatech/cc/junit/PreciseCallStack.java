/*
 * PreciseCallStack.java
 * 
 * Copyright 2005 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.generic.Type;

/**
 * Convert between Throwable.getStackTrace() and
 * the precise method signature.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class PreciseCallStack {

	/**
	 * Convert fully qualified type name to simple name
	 */
	protected static String simpleName(String name) {
		int lastDotPos = name.lastIndexOf('.');
		
		if (lastDotPos<0) {return name;}
		return name.substring(lastDotPos+1);
	}
	
	
	protected static String methName(String className, org.apache.bcel.classfile.Method meth) {
		String res = meth.getName();
		if (res.equals("<init>")) {  //use constructor name instead of <init>
			res = simpleName(className);
		}
		return res;		
	}
	
	
	
	/**
	 * @param useSimpleName use simple type name for param types
	 * @return 	methName([typeName[,typeName]*])
	 */	
	protected static String methSig(
			String className,
			org.apache.bcel.classfile.Method meth,
			boolean useSimpleName)
	{
		String res = methName(className, meth) +"(";
		
		Type[] argTypes = meth.getArgumentTypes();
		for (int i=0; i<argTypes.length; i++) {
			if(i>0) {res+=",";}
			String typeName = argTypes[i].toString();
			
			if (useSimpleName) {
				res +=simpleName(typeName);
			}
			else {
				res +=typeName;
			}
		}
		
		return res +=")";
	}
	
	
	/**
	 * Translates (className, line number) from Throwable.getStackTrace()
	 * to a precise method signature.
	 * 
	 * @param useSimpleName use simple type name for param types
	 * @return 	methName([typeName[,typeName]*])
	 * 				| <method not found>
	 */
	public static String methSig(String className, int lineNr, boolean useSimpleName) {
		JavaClass clazz = null;
		try { 
			clazz = Repository.lookupClass(className);
		}
		catch (ClassNotFoundException e) {
			return "<method not found>";
		}
		
		org.apache.bcel.classfile.Method[] methods = clazz.getMethods();
		for (int i=0; i<methods.length; i++) {
			LineNumberTable lnt = methods[i].getLineNumberTable();
			if (lnt==null) {continue;}	//should not happen
			
			LineNumber[] nrs = lnt.getLineNumberTable();
			if (nrs==null || nrs.length==0) {continue;}  //should not happen
			
			if (nrs[0].getLineNumber()<=lineNr && lineNr<=nrs[nrs.length-1].getLineNumber()) {
				return methSig(className, methods[i], useSimpleName);
			}
		}
		
		return "<method not found>";
	}
	
	
	
	/**
	 * Reverse conversion: Precise --> Throwable.getStackTrace() style
	 * 
	 * @param sig ::= 
	 * 					typeName#methName([typeName[,typeName]*])
	 * @return 	typeName.methName 
	 * 				|	typeName.<init>
	 */
	public static String stackTraceStyle(String sig) {
		String type = sig.substring(0, sig.indexOf('#'));
		String meth = sig.substring(sig.indexOf('#')+1, sig.indexOf('('));
		
		String simple = type;
		if (type.lastIndexOf('.')>0) {
			simple = type.substring(type.lastIndexOf('.')+1, type.length());
		}
		if (meth.equals(simple)) {
			meth = "<init>";
		}
		
		return type +"." +meth;
	}	
}
