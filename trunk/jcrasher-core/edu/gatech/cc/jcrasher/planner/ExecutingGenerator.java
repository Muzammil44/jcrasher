/*
 * ExecutingGenerator.java
 * 
 * Copyright 2006 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.MAX_TEST_CASES_TRIED_CLASS;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.gatech.cc.jcrasher.Constants;
import edu.gatech.cc.jcrasher.plans.Executable;
import edu.gatech.cc.jcrasher.plans.blocks.Block;

/**
 * Executes test cases before returning them.
 * 
 * @author		Christoph Csallner
 */
public class ExecutingGenerator implements Generator {
	
	
	protected static final Random random = new Random();


  /**
   * @return if we want to keep (export) a testCase.
   */
  protected boolean shouldExport(final Executable<?> testCase) {    
    notNull(testCase);
    
    try {
      testCase.execute();
    }
    
    /* Execution of test case threw exception, analyze cause */
    catch(InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause==null) { //dont know: let runtime filter.
        return true;
      }
      
      if (cause instanceof RuntimeException) {
        return true;		//we are interested in RuntimeException.
      }
      if (cause instanceof Error) {
        return true;    //we are interested in Error.
      }
      
      return false;     //not interested in checked exceptions.
    }

    catch(InstantiationException e) {
      return false;     //failed to run test properly, suppress.
    } 
    catch(IllegalAccessException e) {
      return false;     //failed to run test properly, suppress.
    }    
    
    catch(Throwable e) {
      return false;     //we failed to run the test case properly, suppress.
    }
    
    return false;       //no crash, not interested in this test case.
  }
	
	
	/**
	 * 
	 */
	public <T> List<Block> getBlocks(final Class<T> classUnderTest) {
    notNull(classUnderTest);
    
    final ClassUnderTest<T> classNode = new ClassUnderTestImpl<T>(
      classUnderTest,
      Constants.MAX_PLAN_RECURSION,
      Constants.VIS_TESTED,
      Constants.VIS_USED);
    
    final int testsAvailable = classNode.getPlanSpaceSize();
    final List<Block> testCasesSucceeded = new LinkedList<Block>();
    if (testsAvailable<=0) { //no or way too many potential test cases.
      return testCasesSucceeded;
    }
    
    int testsTried = MAX_TEST_CASES_TRIED_CLASS;
    if (testsTried>testsAvailable)
    	testsTried = testsAvailable;
    
    /* Try tests */
    for (int i=0; i<testsTried; i++) {
      final int testIndex = (testsTried<MAX_TEST_CASES_TRIED_CLASS? 
      		i : random.nextInt(testsAvailable));
      Block testCase = null;
      try {
        testCase = classNode.getBlock(testIndex);
      }
      catch(Throwable e) {
        /* Tried to access some non-initializable class or interface */
        continue;
      }
      if (shouldExport(testCase)) {
        testCasesSucceeded.add(testCase);
      }
    }

    return notNull(testCasesSucceeded);
	}

}
