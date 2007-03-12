/*
 * ExecutingPlanner.java
 * 
 * Copyright 2006 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.MAX_TEST_CASES_TRIED_CLASS;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.NotImplementedException;

import edu.gatech.cc.jcrasher.planner.ClassUnderTest;
import edu.gatech.cc.jcrasher.planner.ClassUnderTestImpl;
import edu.gatech.cc.jcrasher.planner.CutPlanner;
import edu.gatech.cc.jcrasher.planner.CutPlannerImpl;
import edu.gatech.cc.jcrasher.plans.JavaCode;
import edu.gatech.cc.jcrasher.plans.stmt.Block;

/**
 * Executes test cases before returning them.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ExecutingCutPlanner {
	
	protected static final CutPlanner cutPlanner = CutPlannerImpl.instance();
	protected static final Random random = new Random();


  /**
   * @return if we want to keep (export) a testCase.
   */
  protected boolean shouldExport(final JavaCode<?> testCase) {    
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
	public <T> List<Block> getBlocks(final Class<T> classUnderTest, int maxAmount) {
    notNull(classUnderTest);
    
    final ClassUnderTest<T> classNode = cutPlanner.getPlanSpace(classUnderTest);     
    final BigInteger testsAvailableBig = classNode.getPlanSpaceSize();
    if (testsAvailableBig.bitCount()>32)
      throw new NotImplementedException(
          "Too many potential test methods. Try to reduce the --depth parameter.");
    
    final int testsAvailable = testsAvailableBig.intValue();
    final List<Block> testCasesSucceeded = new LinkedList<Block>();
    if (testsAvailable<=0) { //no or way too many potential test cases.
      return testCasesSucceeded;
    }
    
    int testsTried = maxAmount;
    if (testsTried>testsAvailable)
    	testsTried = testsAvailable;
    
    /* Try tests */
    for (int i=0; i<testsTried; i++) {
      final int testIndex = (testsTried<MAX_TEST_CASES_TRIED_CLASS? 
      		i : random.nextInt(testsAvailable));
      Block testCase = null;
      try {
        testCase = classNode.getBlock(BigInteger.valueOf(testIndex));
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
