/*
 * DivByZero.java
 * 
 * Copyright 2006 Christoph Csallner and Yannis Smaragdakis.
 */
package trivia;

/**
 * Throws java.lang.ArithmeticException: / by zero
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class DivByZero {

	/**
	 * Crashes for i==0.
	 */
  public static double inverse(int i) {	
		return 1/i;
	}	
  
  public static double always(int i) {	
		return i/0;
	}	
}
