/*
 * GroupedTestResult.java
 * 
 * Copyright 2003 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.junit.framework;

import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.util.Enumeration;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import edu.gatech.cc.junit.AccidentException;
import edu.gatech.cc.junit.FilteringTestCase;
import edu.gatech.cc.junit.IntendedException;
import edu.gatech.cc.junit.Wrapper;
import edu.gatech.cc.junit.textui.RaGTestRunner;
import edu.gatech.cc.junit.textui.RaGTestRunner.GroupMode;

/**
 * Suppresses all exceptions similar to previously recorded exceptions.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class GroupedTestResult extends TestResult {

  /**
   * FIXME: This will never be called, right?
   */
	protected boolean isMoreFocused(Wrapper t1, Wrapper t2) {
		return (
				isMoreFocused(t1.unwrap(), t2.unwrap()) &&
				t1 instanceof IntendedException &&
				t2 instanceof AccidentException);
	}
	
	/**
   * @return if stack1 has the same top element and is shorter than stack2.
   */
	protected boolean isMoreFocused(Throwable t1, Throwable t2) {
		notNull(t1);
		notNull(t2);
		StackTraceElement[] stack1 = t1.getStackTrace();
		StackTraceElement[] stack2 = t2.getStackTrace();
		notNull(stack1);
		notNull(stack2);
		
		return hasSameTop(stack1, stack2) && stack1.length<stack2.length;
	}
	
	
	/* @return true iff stack1 has the same element on the top of its stack
	 * as stack2 has on the top of its stack. */
	protected boolean hasSameTop(StackTraceElement[] stack1, StackTraceElement[] stack2) {
		notNull(stack1);
		notNull(stack2);		
		return stack1[0].equals(stack2[0]);
	}
	
  /**
   * Determine whether the top 20 elements of the two stack traces 
   * are of same size and pairwise equal.
   */
  protected boolean equal20(StackTraceElement[] stack1, StackTraceElement[] stack2) {   
    if ((stack1 == null) && (stack2 == null)) 
      return true;  //errors have a null stack trace

    if ((stack1 == null) || (stack2 == null))
      return false; //one stack is null, the other is not

    if (stack1.length < 20 || stack2.length < 20)
      return false;
    
    for (int i=0; i < 20; i++) {
      if (! stack1[i].equals(stack2[i])) {
        return false;   /* at least one different element */
      }
    }
    
    return true;
  } 
  
	
	/**
	 * Determine whether two stack traces stack1 and stack2 are 
	 * of same size and 
	 * the i-th element of stack1 equals the i-th element of stack2,
	 * ---where i denotes the stack of the methods under test, not 
	 * test-case-/ JUnit-/ Java-method invocations.
	 */
	protected boolean equal(StackTraceElement[] stack1, StackTraceElement[] stack2) {		
		if ((stack1 == null) && (stack2 == null))	
			return true;	//errors have a null stack trace

		if ((stack1 == null) || (stack2 == null))
			return false;	//one stack is null, the other is not
    
    
		if (stack1.length != stack2.length)
			return false;		/* two stack traces of different size are different */

    
		for (int i=0; i < stack1.length - FilteringTestCase.getStackLengthOfTest(); i++) {
			/* method under test has been previously filtered by JCrasher runtime */
			if (! stack1[i].equals(stack2[i])) {
				return false;		/* at least one different element */
			}
		}
		
		return true;				/* same length and method-under-test elements are equal. */
	}	
	
	
	/**
	 * Check whether a Throwable is ``the first of its kind'' (a prototype)
	 * or else another exception of same type and stacktrace has previously
	 * added to the failure list.
	 * In focused mode: returns the most focused failure that has the
	 * same top stack element. This failure can be less, equally, or more focused
	 * than t.
	 */
	protected GroupedTestFailure getPrototype (Throwable throwable) {
    if (throwable instanceof Wrapper)
      throwable = ((Wrapper)throwable).unwrap();
    
		GroupedTestFailure candidate= null;
		for (GroupedTestFailure prototypeFailure: (Vector<GroupedTestFailure>)fErrors) {
			Throwable prototypeThrowable = prototypeFailure.thrownException(); //p could be t's prototype.
			
      if (!prototypeThrowable.getClass().equals(throwable.getClass()))
        continue;
      
      /* Some throwable do not have a stacktrace */
      if (prototypeThrowable.getStackTrace()==null)
        return prototypeFailure;     
			
      
			switch (RaGTestRunner.GROUP_MODE) {
			  /* new CnC mode: agressive grouping. */
				case GROUP_FOCUSED:
					if (hasSameTop(prototypeThrowable.getStackTrace(), throwable.getStackTrace())) {
						if (candidate==null || isMoreFocused(prototypeThrowable, candidate.thrownException())) {
							candidate = prototypeFailure;
						}
					}
          break;
          
				/* old JCrasher SPE mode: same stack trace. */
				default:
          if (StackOverflowError.class.equals(throwable.getClass()) &&
              equal20(prototypeThrowable.getStackTrace(), throwable.getStackTrace()))
            return prototypeFailure;
					if (equal(prototypeThrowable.getStackTrace(), throwable.getStackTrace())) {
						return prototypeFailure;	/* found similar (prototype) exception. */							
					}
			}
		}
		if (RaGTestRunner.GROUP_MODE==GroupMode.GROUP_FOCUSED) {
			return candidate;
		}
		return null;  //could not find a prototype exception in failure list.
	}

	/**
	 * Gets the number of detected failures.
	 */
	public synchronized int prototypeFailureCount() {
		int count = 0;
		for (int i=0; i<fErrors.size(); i++) {
			if (((GroupedTestFailure) fErrors.elementAt(i)).isPrototype()) {
				count += 1;
			}
		}
		return count;
	}


	/**
	 * Adds an error to the list of errors. The passed in exception
	 * caused the error.
	 * 
	 * Note that an exception is called 
	 * + error in junit.framework.TestResult and junit.textui.TestRunner.
	 * + failure in junit.swingui.TestRunner.
	 * A junit.framework.AssertionFailedError is called
	 * + failure in all of the above.
	 */
	@Override
	public synchronized void addError(Test test, Throwable throwable) {
    
    /* Moved Error check here from FilteringTestCase.
     * Calling dispatchError might generate another stack overflow error.
     * Which would end up here regardless of any filtering in dispatchError. */
    if ((FilteringTestCase.SUPPRESS_ERRORS) && (throwable instanceof Error))
      return;
    
		GroupedTestFailure prototype = getPrototype(throwable);
		GroupedTestFailure failure = null;
		
		if (RaGTestRunner.GROUP_MODE==GroupMode.GROUP_FOCUSED && prototype!=null &&
				isMoreFocused(throwable, prototype.thrownException())) // unwrap after isMoreFocused
		{	/* make failure the new protype */
			if (throwable instanceof Wrapper)
        throwable = ((Wrapper)throwable).unwrap();

			failure = new GroupedTestFailure(test, throwable, null);
			prototype.parent = failure;
		}		
		else {
			if (throwable instanceof Wrapper)
        throwable = ((Wrapper)throwable).unwrap();

			failure = new GroupedTestFailure(test, throwable, prototype);
		}
		
		fErrors.addElement(failure);		
		for (Enumeration e= fListeners.elements(); e.hasMoreElements(); )
			((GroupedTestListener)e.nextElement()).addError(test, throwable, prototype);
	}
  

  
  /**
   * TODO: Should our tools ignore violations of JUnit's assertions?
   */
  @Override
  public synchronized void addFailure(Test test, AssertionFailedError t) {
    addError(test, t);
  }
}
