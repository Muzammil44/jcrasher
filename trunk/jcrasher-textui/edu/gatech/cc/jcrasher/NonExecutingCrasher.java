/*
 * JCrasher.java
 * 
 * Copyright 2002-2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.isArrayIndex;
import static edu.gatech.cc.jcrasher.Assertions.isNonNeg;
import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.MAX_NR_TEST_METHS_PER_CLASS;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.TreeMap;

import edu.gatech.cc.jcrasher.planner.ClassUnderTest;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.writer.JUnitAll;
import edu.gatech.cc.jcrasher.writer.JUnitAllImpl;
import edu.gatech.cc.jcrasher.writer.JUnitTestCaseWriter;
import edu.gatech.cc.jcrasher.writer.TestCaseWriter;


/**
 * <ul>
 * <li> Build up the "needs-type-for-construction" relation
 * <li> Build up plans thru type-space upto max. recursion depth
 * <li> Distribute nr test classes among the classes under test
 * <li> Come up with random sample over each class's plan space
 * </ul>
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class NonExecutingCrasher extends AbstractCrasher {
	
	protected NonExecutingPlanner planner;
	
	/**
	 * Aggregate test suite
	 */
	protected final JUnitAll junitAll = new JUnitAllImpl();
	
	/**
	 * Constructor
	 * 
	 * Crawl classes to discover the type graph they imply.
	 */
	public NonExecutingCrasher(Class<?>[] classes) {
		super(classes);
    
    planner = new NonExecutingPlanner(classes);
	}

  

  /**
   * Pick random values from arbitrary large interval.
   */
  protected Block<?>[] getRandomTestBlocks(
      Class<?> testeeClass,
      BigInteger startPlanIndex,
      BigInteger planIndexStretch,
      int amount)
  {
    notNull(testeeClass);
    check(isNonNeg(startPlanIndex));
    check(isNonNeg(planIndexStretch));
    check(amount>=0);
    check(planIndexStretch.compareTo(BigInteger.valueOf(amount)) >= 0);
    
    ClassUnderTest<?> classNode = planner.getPlanSpace(testeeClass);
    BigInteger maxTestCasesAvailable =  classNode.getNrTestMethodsAvailable();
    
    if (startPlanIndex.compareTo(maxTestCasesAvailable) >=  0) {
      System.out.println(
          "startIndex too big for getRandomTestBlocks("+testeeClass.getName()+")");
      return new Block[0];
    }
    
    BigInteger checkedAmount = BigInteger.valueOf(amount);
    if (startPlanIndex.add(checkedAmount).compareTo(maxTestCasesAvailable) > 0) {
      System.out.println(
          "amount too big for getRandomTestBlocks("+testeeClass.getName()+")");      
      checkedAmount = maxTestCasesAvailable.subtract(startPlanIndex);
    }
    check(isArrayIndex(checkedAmount));
    int amountInt = checkedAmount.intValue();
    
    BigInteger checkedStretch = planIndexStretch;
    if (startPlanIndex.add(planIndexStretch).compareTo(maxTestCasesAvailable) > 0) {
      /* Happens if we rounded up to select one test case */
      check(amount==1);
//      System.out.println(
//          "planIndexStretch too big for getRandomTestBlocks("+testeeClass.getName()+")");      
      checkedStretch = maxTestCasesAvailable.subtract(startPlanIndex);
    }
    
    Block<?>[] blocks = new Block<?>[amountInt];
    
    TreeMap<BigInteger, Object> indexMap = new TreeMap<BigInteger, Object>();
    while (indexMap.size() < amountInt) { //re-insert Integer as key
      BigDecimal random01 = BigDecimal.valueOf(Math.random());
      BigDecimal range = new BigDecimal(checkedStretch);
      BigDecimal random = random01.multiply(range);
      BigInteger index = startPlanIndex.add(random.toBigInteger());
      indexMap.put(index, null);
    }
    
    /* Retrieve indices ordered from TreeMap */
    Iterator<BigInteger> indexIterator = indexMap.keySet().iterator();
    for (int i=0; i<amountInt; i++)
      blocks[i] = classNode.getBlock(indexIterator.next());
    
    return blocks;
  }
	
  
  
  /**
   * @return amount blocks enumerated from startIndex.
   */
  protected Block<?>[] enumerateTestBlocks(
      Class<?> testeeClass,
      int testMethodStartIndex,
      int amount)
  {
    notNull(testeeClass);
    check(0<=testMethodStartIndex);
    check(amount>=0);
        
    ClassUnderTest<?> classNode = planner.getPlanSpace(testeeClass); //from cache
    int maxTestCasesAvailable = classNode.getNrTestMethodsAvailable().intValue();
    
    if (testMethodStartIndex >= maxTestCasesAvailable) {
      System.out.println(
          "startIndex too big for enumerateTestBlocks("+testeeClass.getName()+")");
      return new Block[0];
    }
    
    int checkedAmount = amount;
    if (testMethodStartIndex + amount > maxTestCasesAvailable) {
      System.out.println(
          "amount too big for enumerateTestBlocks("+testeeClass.getName()+")");
      checkedAmount = maxTestCasesAvailable - testMethodStartIndex;
    }
    
    Block<?>[] blocks = new Block<?>[checkedAmount];
    for (int i=0; i<blocks.length; i++)
      blocks[i] = classNode.getBlock(BigInteger.valueOf(testMethodStartIndex+i));
    
    return blocks;
  }
	
	
	
	/**
   * @param nrTestMethodsPicked zero or positive.
   * @param testClassSeqNr 1,2,3, ..
   * @return test methods for pClass.
   * The length of the returned array is MAX_NR_TEST_METHS_PER_CLASS
   * or less (the remainder of the number of picked test methods).
	 */
	protected Block<?>[] getTestBlocks(
      int testeeIndex,
      int testMethodStartIndex)
  {
	  Class<?> testeeClass = classes[testeeIndex];
    notNull(testeeClass);
    
    int nrTestMethodsPicked = planner.getChosenPlans()[testeeIndex];
    check(nrTestMethodsPicked >= 0);
    
    check(0 <= testMethodStartIndex);
    check(testMethodStartIndex < nrTestMethodsPicked);
		
    int amount = nrTestMethodsPicked - testMethodStartIndex;
    if (amount>MAX_NR_TEST_METHS_PER_CLASS)
      amount = MAX_NR_TEST_METHS_PER_CLASS;    
    
    if(planner.isEveryAvailablePlan())
      /* Enumerate all available test methods */
      return enumerateTestBlocks(testeeClass, testMethodStartIndex, amount);

    /* Pick random */
    BigInteger startPlanIndex = planner.projectToPlanIndex(testMethodStartIndex);
    BigInteger planIndexStretch = planner.projectToPlanIndex(amount); 
    
    return getRandomTestBlocks(
        testeeClass,
        startPlanIndex,
        planIndexStretch,
        amount);
    }
  
  
  
  /**
   * Picks test methods and writes them to disk.
   */
  public void crashClasses() {      
    planner.printStatistics();
    
    int[] nrTestMethods = planner.getChosenPlans();
    check(classes.length==nrTestMethods.length);
    
    /* Generate aggregate test suite */
    if (Constants.OUT_DIR==null)
      junitAll.create(classes[0]);
    else
      junitAll.create(Constants.OUT_DIR.getAbsolutePath());
      
    
    /* Generate individual test classes */
    for (int testeeIndex=0; testeeIndex<classes.length; testeeIndex++) {
      for (int testClassSeqNr=1; 
           (testClassSeqNr-1) * MAX_NR_TEST_METHS_PER_CLASS  < nrTestMethods[testeeIndex];
           testClassSeqNr++)
      {

        /* Next block of up to max nr test/class as defined by sample */
        Block<?>[] blocks = 
          getTestBlocks(testeeIndex, (testClassSeqNr-1) * MAX_NR_TEST_METHS_PER_CLASS);
        
        TestCaseWriter codeWriter = new JUnitTestCaseWriter(
            classes[testeeIndex],
            "no comment",
            true,
            blocks,
            testClassSeqNr);
        codeWriter.write();
        
        junitAll.addTestSuite(classes[testeeIndex].getName()+"Test"+testClassSeqNr);
      }
    }
    
    junitAll.finish();
  }  
}
