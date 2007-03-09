/*
 * CrasherTypesForConstruction.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Constants.MAX_TEST_CASES_TRIED_CLASS;

import java.util.List;

import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.writer.JUnitTestCaseWriter;
import edu.gatech.cc.jcrasher.writer.TestCaseWriter;

/**
 * Crawls testees for methods.
 * Then searches this type graph and generates random
 * test cases.
 *
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class ExecutingCrasher extends AbstractCrasher {	
	
	protected final ExecutingCutPlanner executingPlanner =
		new ExecutingCutPlanner();
	
	/**
	 * Constructor
	 * 
	 * @param classes to crash. 
	 */
	public ExecutingCrasher(Class[] classes) {
		super(classes);
	}


	/**
	 * Heart of JCrasher: Generate test cases for classes under test.
	 */
	public void crashClasses() {
		for (Class<?> c: classes) {			
			final List<Block> blockList = executingPlanner.getBlocks(c, MAX_TEST_CASES_TRIED_CLASS);
			final Block[] blocks = blockList.toArray(new Block[blockList.size()]);
			final TestCaseWriter testCaseWriter = new JUnitTestCaseWriter(
					c, "No comment", Constants.JUNIT_FILTERING, blocks);
			testCaseWriter.write();
		}
	}
}
