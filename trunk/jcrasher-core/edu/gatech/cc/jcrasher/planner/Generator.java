/*
 * Generator.java
 * 
 * Copyright 2006 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.planner;

import java.util.List;

import edu.gatech.cc.jcrasher.plans.blocks.Block;

/**
 * Generates test cases for a class.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public interface Generator {

	/**
	 * Get test cases for c.
	 */
	public List<Block> getBlocks(Class c);
}
