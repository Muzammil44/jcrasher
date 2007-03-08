/*
 * JCrasher.java
 * 
 * Copyright 2002-2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;

import edu.gatech.cc.jcrasher.planner.ClassUnderTest;
import edu.gatech.cc.jcrasher.planner.Planner;
import edu.gatech.cc.jcrasher.planner.PlannerImpl;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.writer.JUnitAll;
import edu.gatech.cc.jcrasher.writer.JUnitAllImpl;
import edu.gatech.cc.jcrasher.writer.JUnitTestCaseWriter;
import edu.gatech.cc.jcrasher.writer.TestCaseWriter;

import static edu.gatech.cc.jcrasher.Constants.MAX_NR_TEST_METHS_PER_CLASS;


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
	
	protected final Planner planner = PlannerImpl.instance();
	
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
	}
	

  /**
   * @return number of test methods to be generated per class under test.
   * The resulting array has as many entries as there are testees.
   */
  protected int[] chooseNrTestMethodsPerTestee() {
    BigInteger testMethodsLimit = BigInteger.valueOf(
        Constants.MAX_NR_TEST_CLASSES * Constants.MAX_NR_TEST_METHS_PER_CLASS);
    
    /* Number of different test cases available. */
    BigInteger[] testMethodsAvailable = new BigInteger[classes.length];
    BigInteger testMethodsAvailableTotal = BigInteger.ZERO;
    for (int i=0; i<classes.length; i++) {
      testMethodsAvailable[i] =  planner.getPlanSpace(classes[i]).getNrTestMethodsAvailable();
      testMethodsAvailableTotal = testMethodsAvailableTotal.add(testMethodsAvailable[i]);
    }
   
    /* Number of test cases we will actually generate. */
    int[] testMethodsSelected = new int[classes.length];
    
    
    if (testMethodsAvailableTotal.compareTo(testMethodsLimit) <= 0) {
      /* total number of available tests much smaller than max(int). */
      for (int i=0; i<testMethodsSelected.length; i++)
      {
        check(testMethodsAvailable[i].bitLength()<=32);
        testMethodsSelected[i] = testMethodsAvailable[i].intValue();
      }
      return testMethodsSelected;
    }
    
    /* More test cases available than we can export. Pick some. */
    
    /* TODO: Following does not use up to 50 percent of the limit
     * if only a few more test cases are available than the limit. */
    BigInteger factorTooMany = testMethodsAvailableTotal.divide(testMethodsLimit);
    factorTooMany = factorTooMany.add(BigInteger.ONE);
    
    for (int i=0; i<testMethodsSelected.length; i++)
    {
      if (testMethodsAvailable[i].compareTo(
          BigInteger.valueOf(Constants.MAX_NR_TEST_METHS_PER_CLASS)) <= 0)
      { /* pick all available test cases if only few available */
        testMethodsSelected[i] = testMethodsAvailable[i].intValue();
        continue;
      }      
      
      /* Scale back number of available test cases */
      BigInteger scaled = testMethodsAvailable[i].divide(factorTooMany);
      check(scaled.bitLength()<=32);
      testMethodsSelected[i] = scaled.intValue();
    }
    return testMethodsSelected;    
  }
  


  protected Block<?>[] getRandomTestBlocks(
      Class<?> testeeClass,
      int startIndex,
      int indexStretch,
      int amount)
  {
    ClassUnderTest<?> classNode = planner.getPlanSpace(testeeClass); 
    Block<?>[] blocks = new Block<?>[amount];
    
    TreeMap<Integer, Object> indexMap = new TreeMap<Integer, Object>();
    while (indexMap.size() < amount) { //re-insert Integer as key
      int index = startIndex + (int) (Math.random() * (double)indexStretch);
      indexMap.put(new Integer(index), null);
    }
    
    /* Retrieve indices ordered from TreeMap */
    Iterator<Integer> indexIterator = indexMap.keySet().iterator();
    for (int i=0; i<amount; i++)
      blocks[i] = classNode.getBlock(indexIterator.next().intValue());
    
    return blocks;
  }
	
  
  
  /**
   * @return amount blocks enumerated from startIndex.
   */
  protected Block<?>[] enumerateTestBlocks(Class<?> pClass, int startIndex, int amount) {
    ClassUnderTest<?> classNode = planner.getPlanSpace(pClass); //from cache
    
    Block<?>[] blocks = new Block<?>[amount];
    for (int i=startIndex; i<blocks.length; i++)
      blocks[i] = classNode.getBlock(i);
    
    return blocks;
  }
	
	
	
	/**
   * @param nrTestMethodsPicked zero or positive.
   * @param testClassSeqNr 1,2,3, ..
   * @return test methods for pClass.
   * The length of the returned array is MAX_NR_TEST_METHS_PER_CLASS
   * or less (the remainder of the number of picked test methods).
	 */
	protected <T> Block<?>[] getTestBlocks(
      Class<T> testeeClass,
      int nrTestMethodsPicked,
      int testClassSeqNr)
  {
		notNull(testeeClass);
    check(nrTestMethodsPicked >= 0);
    check(testClassSeqNr > 0);
		
		ClassUnderTest<T> classNode = planner.getPlanSpace(testeeClass);	//from cache
    BigInteger nrTestMethosdAvailable = classNode.getNrTestMethodsAvailable();
    if(nrTestMethosdAvailable.compareTo(BigInteger.valueOf(nrTestMethodsPicked)) <= 0) {
      /* enumerate all available test methods */
      int startIndex = (testClassSeqNr-1) * MAX_NR_TEST_METHS_PER_CLASS;
      int amount = nrTestMethodsPicked - startIndex;
      if (amount>MAX_NR_TEST_METHS_PER_CLASS)
        amount = MAX_NR_TEST_METHS_PER_CLASS;
      return enumerateTestBlocks(testeeClass, startIndex, amount);
    }

    /* Pick random */
    
    if (nrTestMethosdAvailable.bitCount()>32)
      System.out.println(
          testeeClass.getName()+": JCrasher found more than MAX_INT test methods." +
          "TODO: We currently pick only from the first MAX_INT test methods.");
    
    /* TODO: Number truncated if > MAX_INT */
    int nrTestMethodsAvailableInt = nrTestMethosdAvailable.intValue();
    
    int startIndex = (testClassSeqNr-1) * MAX_NR_TEST_METHS_PER_CLASS;
    int amount = nrTestMethodsPicked - startIndex; 
    if (amount>MAX_NR_TEST_METHS_PER_CLASS)
      amount = MAX_NR_TEST_METHS_PER_CLASS;
    
    int factorTooManyAvailble = nrTestMethodsAvailableInt / nrTestMethodsPicked + 1;
    int startIndexFactored = startIndex * factorTooManyAvailble;
    int indexStretch = amount * factorTooManyAvailble; 
    
    return  getRandomTestBlocks(
        testeeClass,
        startIndexFactored,
        indexStretch,
        amount);
    }
  
  
  
  /**
   * Picks test methods and writes them to disk.
   */
  public void crashClasses() {      
    int[] nrTestMethods = chooseNrTestMethodsPerTestee();
    check(classes.length==nrTestMethods.length);
        
    /* Print statistics to standard out */
    for (int i=0; i<classes.length; i++)
      System.out.println(
          classes[i].getName()+" "+nrTestMethods[i]+
          " of "+planner.getPlanSpace(classes[i]).getNrTestMethodsAvailable());
    
    
    /* Generate aggregate test suite */
    if (Constants.OUT_DIR==null)
      junitAll.create(classes[0]);
    else
      junitAll.create(Constants.OUT_DIR.getAbsolutePath());
      
    
    /* Generate individual test classes */
    for (int i=0; i<classes.length; i++) {
      for (int testClassSeqNr=1; 
           (testClassSeqNr-1) * MAX_NR_TEST_METHS_PER_CLASS  < nrTestMethods[i];
           testClassSeqNr++)
      {

        /* Next block of up to max nr test/class as defined by sample */
        Block<?>[] blocks = 
          getTestBlocks(classes[i], nrTestMethods[i], testClassSeqNr);
        
        TestCaseWriter codeWriter = new JUnitTestCaseWriter(
            classes[i],
            "no comment",
            true,
            blocks,
            testClassSeqNr);
        codeWriter.write();
        
        junitAll.addTestSuite(classes[i].getName()+"Test"+testClassSeqNr);
      }
    }
    
    junitAll.finish();
  }  
}
