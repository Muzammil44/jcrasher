/*
 * NonExecutingCrasherTest.java
 *
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Constants.MAX_NR_TEST_CLASSES;
import static edu.gatech.cc.jcrasher.Constants.MAX_NR_TEST_METHS_PER_CLASS;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.math.BigInteger;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import client.Client;
import client.ClientReflect;
import client.MiniClient;
import client.MiniClientReflect;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.plans.stmt.BlockImpl;

/**
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class NonExecutingCrasherTest extends TestCase {
  
  final ClientReflect client = new ClientReflect();
  final MiniClientReflect miniClient = new MiniClientReflect();
  
  final NonExecutingCrasher crasher =
    new NonExecutingCrasher(new Class[]{Client.class, MiniClient.class});
  
  final int MAX_NR_TEST_CLASSES_ORIG = MAX_NR_TEST_CLASSES;
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    
    MAX_NR_TEST_CLASSES = MAX_NR_TEST_CLASSES_ORIG;
    crasher.planner.choosePlans();
  }
  
  /**
   * @throws AssertionFailedError if we detect two blocks
   * that print the same non-empty text.
   * TODO: We only compare adjacent blocks.
   */
  public void assertDifferentText(Block<?>[] blocks) {
    Block<?> oldBlock = 
      new BlockImpl<Integer>(MiniClient.class, miniClient.one(), TAB+TAB);
    
    for (Block<?> block: blocks)
    {
      assertTrue(block.text().length() > 0);
      assertFalse(oldBlock.text().equals(block.text()));
      oldBlock = block;
    }
  }
  
  public void testAssertDifferent() {
    Block<?>[] blocks = crasher.getRandomTestBlocks(
        MiniClient.class, BigInteger.valueOf(0), BigInteger.valueOf(1), 1);
    assertEquals(1, blocks.length);
    
    Block<?>[] sameBlocks = new Block[] { blocks[0], blocks[0] };
    try {
      assertDifferentText(sameBlocks);
      fail(); //AssertionFailedError expected.
    }
    catch (AssertionFailedError e) {
      /* expected */
    }
  }

  
  public void testChooseAll() {
    int[] testMethodsPicked = crasher.planner.getChosenPlans();
    crasher.planner.printStatistics();
    assertEquals(true, crasher.planner.isEveryAvailablePlan());
    assertEquals(2, testMethodsPicked.length);
    assertEquals(2187, testMethodsPicked[0]);
    assertEquals(1, testMethodsPicked[1]);
  }
  
  
  public void testChooseLimit2000() {
    MAX_NR_TEST_CLASSES = 4;
    int maxNrTestMethods = MAX_NR_TEST_CLASSES * MAX_NR_TEST_METHS_PER_CLASS;
    assertEquals(2000, maxNrTestMethods);
        
    crasher.planner.choosePlans();
    int[] testMethodsPicked = crasher.planner.getChosenPlans();
    crasher.planner.printStatistics();
    assertEquals(false, crasher.planner.isEveryAvailablePlan());
    assertTrue(testMethodsPicked[0] < maxNrTestMethods);
    assertEquals(1, testMethodsPicked[1]);
  }
  

  public void testChooseLimit1000() {
    MAX_NR_TEST_CLASSES = 2;
    int maxNrTestMethods = MAX_NR_TEST_CLASSES * MAX_NR_TEST_METHS_PER_CLASS;
    assertEquals(1000, maxNrTestMethods);
    
    crasher.planner.choosePlans();
    int[] testMethodsPicked = crasher.planner.getChosenPlans();
    crasher.planner.printStatistics();
    assertEquals(false, crasher.planner.isEveryAvailablePlan());
    assertTrue(testMethodsPicked[0] < maxNrTestMethods);
    assertEquals(1, testMethodsPicked[1]);
  }
  
  public void testChooseLimit500() {
    MAX_NR_TEST_CLASSES = 1;
    int maxNrTestMethods = MAX_NR_TEST_CLASSES * MAX_NR_TEST_METHS_PER_CLASS;
    assertEquals(500, maxNrTestMethods);
   
    crasher.planner.choosePlans();
    int[] testMethodsPicked = crasher.planner.getChosenPlans();
    crasher.planner.printStatistics();
    assertEquals(false, crasher.planner.isEveryAvailablePlan());
    assertTrue(testMethodsPicked[0] < maxNrTestMethods);
    assertEquals(1, testMethodsPicked[1]);
  }
  
  
  public void testEnumerate() {
    Block<?>[] blocks;
    blocks = crasher.enumerateTestBlocks(Client.class, 0, 5);
    assertEquals(5, blocks.length);
    assertDifferentText(blocks);
    
    blocks = crasher.enumerateTestBlocks(Client.class, 0, 600);
    assertEquals(600, blocks.length);
    assertDifferentText(blocks);

    blocks = crasher.enumerateTestBlocks(Client.class, 100, 7);
    assertEquals(7, blocks.length);
    assertDifferentText(blocks);
    
    blocks = crasher.enumerateTestBlocks(MiniClient.class, 0, 1);
    assertEquals(1, blocks.length);
    assertDifferentText(blocks);
    
    blocks = crasher.enumerateTestBlocks(MiniClient.class, 0, 0);
    assertEquals(0, blocks.length);
    
    blocks = crasher.enumerateTestBlocks(MiniClient.class, 0, 5);
    assertEquals(1, blocks.length);
    assertDifferentText(blocks);
    
    blocks = crasher.enumerateTestBlocks(MiniClient.class, 1, 5);
    assertEquals(0, blocks.length);
  }
  
  
  public void testRandom() {
    Block<?>[] blocks;
    
    blocks = crasher.getRandomTestBlocks(
        Client.class, BigInteger.valueOf(1800), BigInteger.valueOf(100), 7);
    assertEquals(7, blocks.length);
    assertDifferentText(blocks);
//    for (Block block: blocks)
//      System.out.println(block.text());
    
    blocks = crasher.getRandomTestBlocks(
        MiniClient.class, BigInteger.valueOf(0), BigInteger.valueOf(1), 1);
    assertEquals(1, blocks.length);
    
    blocks = crasher.getRandomTestBlocks(
        MiniClient.class, BigInteger.valueOf(0), BigInteger.valueOf(5), 1);
    assertEquals(1, blocks.length);
    
    try {
      blocks = crasher.getRandomTestBlocks(
          MiniClient.class, BigInteger.valueOf(0), BigInteger.valueOf(5), 5);
      fail();
    }
    catch(RuntimeException e) {
      /* Expected. */
    }
    
    blocks = crasher.getRandomTestBlocks(
        MiniClient.class, BigInteger.valueOf(1), BigInteger.valueOf(1), 1);
    assertEquals(0, blocks.length);
  }
}
