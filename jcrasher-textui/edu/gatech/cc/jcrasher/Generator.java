/*
 * Generator.java
 * 
 * Copyright 2006 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import java.util.List;

import edu.gatech.cc.jcrasher.plans.stmt.Block;

/**
 * Generates test cases for a class.
 * 
 * TODO: Merge into Planner.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface Generator {

	/**
	 * Get test cases for c, but not more than maxAmount.
	 */
	public <T> List<Block> getBlocks(Class<T> c, int maxAmount);
}
