package edu.gatech.cc.junit;

import static edu.gatech.cc.jcrasher.Assertions.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * TestCase that 
 * - eats up all checked exceptions.
 * - eats up exceptions possibly caused by JCrasher generated code.
 * - passes all other Throwables thru to JUnit.
 *
 * Automatic Testing: 
 * Crash java classes by passing inconvenient params
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * 2002-07-25 modified heuristic: NPE now like IAE, transitive called method needs to be public
 * 2004-08-08 trap and ignore testee calls to System.exit(int).
 */
public class FilteringTestCase extends TestCase {
	
  /**
   * Filters plans.
   */
  public enum FilterMode {

    /**
     * Do not suppress any RunTime exception based on its type.
     */
    ALL,
    
    /**
     * Classic JCrasher mode from SPE paper:
     * Suppress NPE, IllegalArg exception was thrown by the
     * method called directly from the test case.
     * In this case the test case probably provided illegal arguments.  
     */
    CLASSIC_SPE,
    
    /**
     *  Suppress all NPE, IllegalArg exceptions.
     */
    CLASSCAST_ARITHMETIC_ARRAYEXCEPTIONS
  }
  
  
  /**
   * How does the call stack look like?
   */
  public enum CallStack {
    
    TEST_TESTED_METH,
    
    TEST_TESTED_CLASS,
    
    TEST_OTHER_CLASS
  }
  
  	
	public static final List<String> ANNOTATED_LIST = new Vector<String>();
	public static boolean DIRECT_CALL_ONLY = false;
  public static boolean THROWN_BY_TESTED_METHOD_ONLY = false;
  public static FilterMode FILTER_MODE = 
    FilterMode.CLASSCAST_ARITHMETIC_ARRAYEXCEPTIONS;
	public static boolean SUPPRESS_ERRORS = false;	//hide java.lang.Error

	
	/**
	 * on which height of function frame stack are regular test sequences placed?
	 * 1 .. test123() {} is the only method invoked
	 * N .. slot 1 .. slot N-1 are jvm and calling junit framework
	 */
	private static int stackLengthOfTest = -1;

	/**
	 * To be executed after a test suite has been run.
	 * Different test suites have different test suite hierarchies, so
	 * test cases are placed on different heights on the stack. 
	 */
	public static void  resetStackPosOfTest() {
		stackLengthOfTest = -1;
	}

  /**
   * @return int >= 1: function stack frame height at which regular test method is placed
   * Needed by modified JUnit for exception grouping.
   */
  public static int getStackLengthOfTest() {
    return stackLengthOfTest;
  }
  
  /**************************************************************************
   * Executed by dispatchException() to 
   * determine the length of its function stack 
   * != pos in invocation frame array
   * 
   * @return the length of the client's (=caller) stack
   *         == callee's length - 1
   */
  private static int getMyStackLength() {

    /* discover height of function stack */
    class DummyException extends Exception {
      //nop
    }
    
    try {
      throw new DummyException();
    } catch (DummyException d) {
      int height = d.getStackTrace().length;
      return height-1;  //caller has put us on top of himself
    }
  }
  
	/**************************************************************************
	 * Constructor for FilteringTestCase.
	 * @param name
	 */
	public FilteringTestCase(String name) {
		super(name);
	}

  
  protected Class<? extends Throwable> getExpectedThrowable() {
    return null;
  }

  protected int getExpectedThrowingLineNumber() {
    return 0;
  }
  
  protected String getNameOfTestedMeth() {
    return null;
  }


	/**
	 * Determine if e belongs to group-1 from group-2 and process:
	 * group-1: fault of executing=throwing method (mostly thrown by jvm)
	 * group-2: fault of invoking method: executing method got bad precondition
	 * @param e is a runtime exception thrown by
	 * 	or passed on by a public method declared in a public class
	 */
	private boolean isBugException(RuntimeException e) {		
		/* group 1: jvm generated exception --> found a bug! */
		/* NPE from JCrasher 0.1.0 on treated equivallent to IllegalArgumentException. */
		return (e instanceof ArrayIndexOutOfBoundsException 
				|| e instanceof NegativeArraySizeException
				|| e instanceof ArrayStoreException
				|| e instanceof ClassCastException
				|| e instanceof ArithmeticException);
	}


	/**
	 * @return true if the element at stack position top minus pos (= stack[pos])
	 * 	may not be a public method/ constructor declared by a public class
	 */
	private boolean mayBeNonPublicPublic(RuntimeException e, int posFromTop) throws Exception {
		boolean res = false;
		
		StackTraceElement[] stack = e.getStackTrace();
		String className = stack[posFromTop].getClassName();
		String methName = stack[posFromTop].getMethodName();
					
		/* access class via reflection */
		Class<?> throwingClass = Class.forName(className);
					
		/* throwing class non-public --> done */
		if (Modifier.isPublic(throwingClass.getModifiers())==false) {
			res = true;				
		}
					
		/* throwing class public --> check if throwing method not-public */
		if (res==false) {
			Method[] methods = throwingClass.getDeclaredMethods();
						
			/* Any non-public method with the name of the 
			 * invoked throwing/passing meth --> done */
			for (int i=0; i<methods.length; i++) {
				if ((methods[i].getName() == methName) 
					&& (Modifier.isPublic(methods[i].getModifiers())==false)){
						
					res = true;
					break;	//done after found the first non-public method..
				}
			}
		}
		
		if (res==false) {
			/* Any non-public constructor declared by 
			 * the class of the invoked throwing/passing constructor --> done */
			Constructor[] constrs = throwingClass.getDeclaredConstructors();
			for (int i=0; i<constrs.length; i++) {
				if ((constrs[i].getName() == methName) 
					&& (Modifier.isPublic(constrs[i].getModifiers())==false)){
									
					res = true;
					break;	//done after found the first non-public constructor..
				}
			}
		}
		
		return res;	
	}
	
	
	
	/**
	 * @return true iff the method invocation frame at calledPosFromTop is a
	 * 	super or this constructor of callingPosFromTop.
	 */
	private boolean callToThisOrSuper(Exception e, int calledPosFromTop, int callingPosFromTop)
		throws Exception {
			
		boolean res = false;		
		StackTraceElement[] stack = e.getStackTrace();
		
		/* get called method from frame via reflection */
		String calledMethName = stack[calledPosFromTop].getMethodName();
		String calledClassName = stack[calledPosFromTop].getClassName();
		Class<?> calledClass = Class.forName(calledClassName);					

		/* get calling method from frame via reflection */				
		String callingMethName = stack[callingPosFromTop].getMethodName();
		String callingClassName = stack[callingPosFromTop].getClassName();					
		Class<?> callingClass = Class.forName(callingClassName);					

		/* true iff both methods are constructors and
		 * the called constructor is some super or this constructor of the calling constructor */
		if ((calledMethName == "<init>")
			&& (callingMethName == "<init>")
			&& (calledClass.isAssignableFrom(callingClass))) {
			
			res = true;					
		}
					
		return res;
	}
	


	/**
	 * @return the height of the stack slice between the 
	 * 	lowest public-public method or constructor called by the testsequence and the
	 * 	highest public-public method or constructor called by the test sequence.
	 * If the lowest public-public method is a constructor it is pushed farther upwards
	 * 	as long as upper are this or super constructors.
	 * If the highest public-public method is a constructor it is pushed farther downwards
	 * 	as long as it is a this or super constructor of the next lower one.
	 */
	private int getPublicToPublicSliceHeight(RuntimeException e) throws Exception {
		int adjustedTopPos = 0;	//point at top-most certainly public method in stacktrace-array
		int adjustedBottomPos = e.getStackTrace().length - stackLengthOfTest -1;	//index into stacktrace-array
		int testPos = e.getStackTrace().length - stackLengthOfTest;
		
		/* 
		 * find top-most public method in invocation stack:
		 * top-down subtract top stack frames from height iff may be non-public
		 * 
		 * Is adjustedTopPos meth really part of public interface to the library?
		 * We move pointer down the stack until we have reached 
		 * a public meth/ constr in public class.
		 * Terminated by the test method.
		 */
 		boolean adjustedTopMayBeNonPublic = true; 		
		while ((adjustedTopPos < testPos)
			&& (adjustedTopMayBeNonPublic)) {
				
			adjustedTopMayBeNonPublic = false;
			
			if (mayBeNonPublicPublic(e, adjustedTopPos)) {	//move farther
				adjustedTopPos += 1;
				adjustedTopMayBeNonPublic = true;
			}										
		}
		/* (adjustedTopPos <= testPos) */

		
		/* 
		 * check top-down if (adjusted) top element is for sure not a call to super()
		 * done iff reached test sequence
		 */
		boolean adjustedTopMayBeSuper = true;
		while ((adjustedTopPos < testPos)
			&& (adjustedTopMayBeSuper)) {
				
			adjustedTopMayBeSuper = false;
			
			if (callToThisOrSuper(e, adjustedTopPos, adjustedTopPos+1)) {
				
				adjustedTopMayBeSuper = true;
				adjustedTopPos += 1;
			}
		}
		/* (adjustedTopPos <= testPos) */


		/*
		 * Do the same bottom up if not already hit the test sequence
		 * or the single public method invoked by the test sequence
		 */
 		boolean adjustedBottomMayBeNonPublic = true; 		
		while ((adjustedBottomPos > adjustedTopPos)
			&& (adjustedBottomMayBeNonPublic)) {
				
			adjustedBottomMayBeNonPublic = false;
			
			if (mayBeNonPublicPublic(e, adjustedBottomPos)) {	//move farther
				adjustedBottomPos -= 1;
				adjustedBottomMayBeNonPublic = true;
			}										
		}
		/* (adjustedBottomPos >= adjustedTopPos) */		 

		boolean adjustedBottomMayCallSuper = true;
		while ((adjustedBottomPos > adjustedTopPos)
			&& (adjustedBottomMayCallSuper)) {
				
			adjustedBottomMayCallSuper = false;
			
			//e, called, calling
			if (callToThisOrSuper(e, adjustedBottomPos-1, adjustedBottomPos)) {
				
				adjustedBottomMayCallSuper = true;
				adjustedBottomPos -= 1;
			}
		}		
		/* (adjustedBottomPos >= adjustedTopPos) */	
		
		return adjustedBottomPos - adjustedTopPos + 1;
	}
	
		
	
	/* @return
	 *   TEST_TESTED_METH  .. test method -> testee method.
	 *   TEST_TESTED_CLASS .. test method -> another method of the tested class.
	 *   TEST_OTHER_CLASS  .. test method -> a method of some other class.
	 * -> stands for direct call
	 * This requires that the test case provides the name of the testee
	 * when calling getNameOfTest().
	 * The result is a little imprecise as there can be more methods of
	 * the same name. */
	protected CallStack testCalledTestee(RuntimeException e) {
		int testPos = e.getStackTrace().length - stackLengthOfTest;
		check(testPos>0);
		
		int calledPos = testPos - 1;		
		StackTraceElement calledFrame = e.getStackTrace()[calledPos];
		
		if (getNameOfTestedMeth()==null) 
      return CallStack.TEST_OTHER_CLASS;
		
		String calledMethName =
			calledFrame.getClassName() +"." +calledFrame.getMethodName();
		
		//TODO more precision: Write precise full signature in test case
		//		and use PreciseCallStack
		//String testedMethName = PreciseCallStack.stackTraceStyle(getNameOfTestedMeth());
		String testedMethName = getNameOfTestedMeth();
    
		if (testedMethName.equals(calledMethName))
      return CallStack.TEST_TESTED_METH;
    
		if (testedMethName.startsWith(calledFrame.getClassName()))
      return CallStack.TEST_TESTED_CLASS;
    
		return CallStack.TEST_OTHER_CLASS;
	}

	
	/**
   * Throw e wrapped if it occurred in a tested method.
	 * This means we ignore exceptions thrown by other classes.
   */
	protected void throwIfTested(RuntimeException e) throws Wrapper {
		switch (testCalledTestee(e)) {
			case TEST_TESTED_METH:  
        throw new IntendedException(e);
        
			case TEST_TESTED_CLASS:
        if (THROWN_BY_TESTED_METHOD_ONLY)
          return;
        throw new AccidentException(e);
      
			case TEST_OTHER_CLASS:
        return;
		}
	}
	
  
  /**
   * Traditional dispatch method of JCrasher.
   * 
   * @deprecated Naming is confusing, use throwIf instead.
   */
  @Deprecated
  protected void dispatchException(Throwable throwable) throws Throwable {
    throwIf(throwable);
  }
  
  
	/**
	 * Main entry to JCrasher runtime
	 * 
	 * Dispatches iff caught exception:
	 * + re-throw the same exception iff bug in function under test
	 * + eat up exception else: JCrasher provided bad precondition
	 * 
	 * Errors are suppressed:
	 * + by default
	 * + -showErrors -directCallOnly and not thrown by directly called method 
	 */
	protected void throwIf(Throwable throwable) throws Throwable {
		if (stackLengthOfTest<0) {  //get our position on the function frame stack.
			stackLengthOfTest = getMyStackLength() -1;	//we are called by test123()
		}
		
    /* Ignore errors and checked exceptions. */
    if (throwable instanceof Exception && 
        !(throwable instanceof RuntimeException))
      return;  //Always ignore checked exceptions.
    
    if (throwable instanceof ExitSecurityException)
      return;  //Always ignore System.exit violations.
    
    /* Ignore exceptions that differ from the expected exception. */
    if (getExpectedThrowable() != null)
      if (!throwable.getClass().isAssignableFrom(getExpectedThrowable()))
        return;
    
    /* Ignore exceptions that were not thrown at the expected line number. */
    if (getExpectedThrowingLineNumber() > 0)
      if (throwable.getStackTrace() != null && throwable.getStackTrace().length>0)
        if (throwable.getStackTrace()[0].getLineNumber() > 0)
          if (getExpectedThrowingLineNumber() != throwable.getStackTrace()[0].getLineNumber())
            return;
        
    /* Moved following if-statement to GroupedTestResult.addError.
     * Calling dispatchExeption may produce a stack overflow error itself
     * and we would not reach here for filtering it out. */
//    if ((throwable instanceof Error) && (!SHOW_ERRORS))
//      return;
    
			
		/* 0 <= height < |test| .. test0() not on top of stack frame? Should not happen -->  meaningless
		 * height == |test| .. test0() crashes: JCrasher fault --> meaningless */
		StackTraceElement[] stack = throwable.getStackTrace();
		
		/* Ignore if there is no stack trace */
		if (stack==null) {	
			return;
		}
		
		if (stack.length <= stackLengthOfTest) {
			return;
		}
		
		/* Suppress if throwable has been thrown by some method called by the
		 * method under test. */
		if (DIRECT_CALL_ONLY) {
			if (stack.length > stackLengthOfTest + 1) {
				return;
			}
		}
		
		if (throwable instanceof Error) {	//fine-grained treatment only for runtime exceptions
			throw throwable;
		}
		
		/* Treat unchecked (runtime) exceptions */
		RuntimeException e = (RuntimeException) throwable;
		
		if (FILTER_MODE==FilterMode.CLASSIC_SPE && !ANNOTATED_LIST.isEmpty()) {
			/* Bypass heuristic iff we have Daikon-inferred preconditions 
			 * for the method that caused the exception */
			StackTraceElement calledMeth = stack[stack.length-stackLengthOfTest-1];
			String methSig = calledMeth.getClassName() +"#" +PreciseCallStack.methSig(
					calledMeth.getClassName(),
					calledMeth.getLineNumber(),
					true);  //TODO use fully qualified param type name to remove imprecision
	
			if (ANNOTATED_LIST.contains(methSig)) {
				throw e;
			}
		}
			
		/* height > |testSeq| .. found a bug iff called some public-public method providing good input. */
		check(stack.length > stackLengthOfTest);
		int publicToPublicSliceHeight = getPublicToPublicSliceHeight(e);
						
		/* Done with pruning: see how thick the remaining middle frame-stack-slice is 
		 * height == |testSeq| --> we have had not a single public-public method on the stack
		 * --> meaningless */
		if (publicToPublicSliceHeight == 0) {return;}
			
		switch (FILTER_MODE) {
    
    case ALL:       //report any RuntimeException, regardless who called.
      throwIfTested(e);
      return;
            
		case CLASSCAST_ARITHMETIC_ARRAYEXCEPTIONS: //report any Bug-RuntimeException, regardless who called. 
			if (isBugException(e))
         throwIfTested(e); 
       return;
        
        
		/* classic JCrasher SPE mode: old filter, old grouping. */
		default:
			/* height== |funcUnderTest| =|testSeq|+1 --> determine "bug" from "bad precond"
			 * only iff we have called a public method - compare to protected service method
			 * invoked to generate a needed parameter. */
			if (publicToPublicSliceHeight == 1) {		
				if (isBugException(e)) {throw new IntendedException(e);} return;
			}
			
			/* height > |testSeq|+1 -->
			 * throw exception only if we found a transitively called public method/base-constructor */					
			if  (publicToPublicSliceHeight > 1) {
				throw e;
			}
		}
	}
}
