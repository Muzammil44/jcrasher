/*
 * CrasherTypesForConstruction.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.gatech.cc.jcrasher.planner.ExecutingGenerator;
import edu.gatech.cc.jcrasher.planner.Generator;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.types.TypeGraph;
import edu.gatech.cc.jcrasher.types.TypeGraphImpl;
import edu.gatech.cc.jcrasher.writer.JUnitTestCaseWriter;
import edu.gatech.cc.jcrasher.writer.TestCaseWriter;

/**
 * Crawls testees for methods.
 * Then searches this type graph and generates random
 * test cases.
 *
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class CrasherImpl implements Crasher {
	
	/**
	 * Database holding the relation needed for planning how to obtain
	 * an object via combinations of functions in type-space
	 */
	protected final TypeGraph typeSpace = TypeGraphImpl.instance();
	protected Generator generator;


	/**
	 * Main programs hands over list of classes to crash provided by user.
	 * - Discover the relation and print it to screen.
	 * - Plan to set max. depth.
	 */
	public void crashClasses(final Class[] classes, boolean execute) {
		notNull(classes);
		check(classes.length>0);
		
		generator = execute? new ExecutingGenerator(): null;	//TODO
		

		/* Build up needs-type-for-construction relation.
		 * How to (transitively) construct user-specified types:
		 * A(B(C()), -1) and only A user-specified */
		final Set<Class<?>> classSet = new HashSet<Class<?>>();
		for (Class<?> c : classes) {
			classSet.add(c);
		}
		
		typeSpace.crawl(classSet, Constants.VIS_USED);
		
		for (Class<?> c: classes) {			
			final List<Block> blockList = generator.getBlocks(c);
			final Block[] blocks = blockList.toArray(new Block[blockList.size()]);
			final TestCaseWriter testCaseWriter = new JUnitTestCaseWriter(
					c, "No comment", Constants.JUNIT_FILTERING, blocks);
			testCaseWriter.write();
		}
	}
}
