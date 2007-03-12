/*
 * NonExecutingPlanner.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Constants.MAX_NR_TEST_CLASSES;
import static edu.gatech.cc.jcrasher.Constants.MAX_NR_TEST_METHS_PER_CLASS;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import edu.gatech.cc.jcrasher.planner.CutPlannerImpl;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class NonExecutingCutPlanner extends CutPlannerImpl {

  protected Class<?>[] classes;
  
  protected boolean chosen = false;
  
  protected BigInteger testMethodsAvailableTotal = BigInteger.ZERO;
  
  protected int[] testMethodsSelected;
  protected int testMethodsSelectedTotal = 0;
  
  /**
   * The factor is only set to a meaningful value if !isEveryAvailablePlan().
   * The factor the exact division of the found test methods by our limit.
   */
  protected BigDecimal factor = BigDecimal.ZERO;

  
  /**
   * Constructor
   */
  public NonExecutingCutPlanner(Class<?>[] classes) {
    this.classes = classes;
    this.testMethodsSelected = new int[classes.length];  //sum <= two million.
    
    choosePlans();
  }
  
  
  /**
   * Pick every available test method for each class.
   */
  protected void setEveryAvailablePlan() {
    check(testMethodsAvailableTotal.bitLength()<32);
    
    for (int i=0; i<testMethodsSelected.length; i++)
    { /* total number of available tests much smaller than max(int). */
      BigInteger testMethodsAvailable = 
        getPlanSpace(classes[i]).getPlanSpaceSize();
      check(testMethodsAvailable.bitLength()<=32);
      testMethodsSelected[i] = testMethodsAvailable.intValue();
      testMethodsSelectedTotal += testMethodsSelected[i];
    }
    
    check(testMethodsAvailableTotal.intValue()==testMethodsSelectedTotal);
    chosen = true;
  }
  
  
  /**
   * To be called from constructor only.
   * 
   * @return a proportional number of test methods to be generated 
   * for each testee class.
   * The resulting array has as many entries as there are testees.
   * The sum of the elements is less than 
   * MAX_NR_TEST_CLASSES * MAX_NR_TEST_METHS_PER_CLASS.
   */
  protected void choosePlans() {
    testMethodsAvailableTotal = BigInteger.ZERO;
    testMethodsSelectedTotal = 0;
    
    BigInteger testMethodsLimit = BigInteger.valueOf(
        MAX_NR_TEST_CLASSES * MAX_NR_TEST_METHS_PER_CLASS); // two million.
    
    /* Number of different test cases available. */
    BigInteger[] testMethodsAvailable = new BigInteger[classes.length];
    for (int i=0; i<classes.length; i++) {
      /* TODO: This only reports the first MAX_INT methods per class. */
      testMethodsAvailable[i] =
        getPlanSpace(classes[i]).getPlanSpaceSize();
      testMethodsAvailableTotal =
        testMethodsAvailableTotal.add(testMethodsAvailable[i]);
    }
    
    if (testMethodsAvailableTotal.compareTo(testMethodsLimit) <= 0) {
      setEveryAvailablePlan();
      return;
    }
    
    /* More test cases available than we can export. Pick proportionally .*/
    BigDecimal availableDecimal = new BigDecimal(testMethodsAvailableTotal);
    BigDecimal limitDecimal = new BigDecimal(testMethodsLimit);
    /* Following terminates as limitDecimal is a mulitple of ten. */
    this.factor = availableDecimal.divide(limitDecimal); 

    for (int i=0; i<testMethodsSelected.length; i++) {
      BigDecimal scaled = 
        (new BigDecimal(testMethodsAvailable[i])).divide(factor, RoundingMode.DOWN);
      testMethodsSelected[i] = scaled.intValue();
    }

          
    /* Increase zero to one if there are some available methods */
    for (int i=0; i<testMethodsSelected.length; i++)
    {
      if (testMethodsSelected[i]==0)
        if (testMethodsAvailable[i].compareTo(BigInteger.ZERO) > 0)
          testMethodsSelected[i] = 1;
      
      testMethodsSelectedTotal += testMethodsSelected[i];  
    }
    
    chosen = true;
    return;
  }
  
  
  /**
   * @return total number of available test methods <= test method limit. 
   */
  public boolean isEveryAvailablePlan() {
    check(chosen);
    if (testMethodsAvailableTotal.bitLength()>=32)
      return false;
    
    return testMethodsAvailableTotal.intValue()<=testMethodsSelectedTotal;
  }
  
  
  /**
   * @return total number of available test methods <= test method limit. 
   */  
  public int[] getChosenPlans() {
    check(chosen);
    
    return testMethodsSelected;
  }
  
  
  /**
   * Prints statistics to standard out.
   */
  public void printStatistics() {
    check(chosen);  

    int[] nrSelectedFunctions = new int[classes.length];
    int functionsSelectedTotal = 0;
    int nrSelectedFunctionsLengthMax = 0;
    int[] nrDeclaredFunctions = new int[classes.length];
    int functionsDeclaredTotal = 0;
    int nrDeclaredFunctionsLengthMax = 0;
    
    int nameLengthMax = 0;     
    int nrSelectedTestsLengthMax = 0;
    int nrAvailableTestsLengthMax = 0;

    
    /* Determine maximum lenghts for pretty printing */
    for (int i=0; i<classes.length; i++) {
      int nameLength = classes[i].getName().length();
      if (nameLength > nameLengthMax)
        nameLengthMax = nameLength;
      
      nrSelectedFunctions[i] = getPlanSpace(classes[i]).getChildren().length;
      functionsSelectedTotal += nrSelectedFunctions[i];
      int nrSelectedFunctionsLength = Integer.toString(nrSelectedFunctions[i]).length();
      if (nrSelectedFunctionsLength > nrSelectedFunctionsLengthMax)
        nrSelectedFunctionsLengthMax = nrSelectedFunctionsLength;      
      
      nrDeclaredFunctions[i] = 
        classes[i].getDeclaredConstructors().length + classes[i].getDeclaredMethods().length;
      functionsDeclaredTotal += nrDeclaredFunctions[i];
      int nrDeclaredFunctionsLength = Integer.toString(nrDeclaredFunctions[i]).length();
      if (nrDeclaredFunctionsLength > nrDeclaredFunctionsLengthMax)
        nrDeclaredFunctionsLengthMax = nrDeclaredFunctionsLength;     
            
      int nrSelectedTestLength = Integer.toString(testMethodsSelected[i]).length(); 
      if (nrSelectedTestLength > nrSelectedTestsLengthMax)
        nrSelectedTestsLengthMax = nrSelectedTestLength;
      
      int nrAvailableTestLength =
        getPlanSpace(classes[i]).getPlanSpaceSize().toString().length();
      if (nrAvailableTestLength > nrAvailableTestsLengthMax)
        nrAvailableTestsLengthMax = nrAvailableTestLength;
    }

    System.out.println(
        "Generating for "+
        functionsSelectedTotal+" public of total "+functionsDeclaredTotal+
        " methods and constructors "+
        testMethodsSelectedTotal+" of "+testMethodsAvailableTotal+
        " found test methods:");
    
    for (int i=0; i<classes.length; i++) {
      System.out.printf(
              "%1$-"+nameLengthMax+"s for " +
              "%2$"+nrSelectedFunctionsLengthMax+"d of " +
              "%3$"+nrDeclaredFunctionsLengthMax+"d: " +
              "%4$"+nrSelectedTestsLengthMax+"d of " +
              "%5$"+nrAvailableTestsLengthMax+"d\n",
          classes[i].getName(),
          nrSelectedFunctions[i],
          nrDeclaredFunctions[i], 
          testMethodsSelected[i],
          getPlanSpace(classes[i]).getPlanSpaceSize());
    }
    
    System.out.println();
  }
  
  
  /**
   * @return plan index that corresponds to given method index.
   */
  public BigInteger projectToPlanIndex(int paramInt) {
    check(chosen);
    
    BigDecimal paramBig = BigDecimal.valueOf(paramInt);
    BigDecimal result = paramBig.multiply(factor);    
    
    return result.toBigInteger();
  }  
}
