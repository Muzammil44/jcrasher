/*
 * JCrasher.java
 * 
 * Copyright 2002-2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import edu.gatech.cc.jcrasher.planner.ClassUnderTest;
import edu.gatech.cc.jcrasher.planner.Planner;
import edu.gatech.cc.jcrasher.planner.PlannerImpl;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
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
	

	/**
	 * FIXME: Replace by NonExecutingPlanner to hide PlannerImpl.
	 */
	protected final Planner planner = PlannerImpl.instance();
	
	/**
	 * Constructor
	 * 
	 * create (empty) typeSpace and distribute its reference
	 */
	public NonExecutingCrasher(Class[] classes) {
		super(classes);
		
	}

	
	/*
	 * @return array 0, 1, 2, .., spaceSzie-1
	 * 
	 * FIXME this seems like a waste of memory.
	 */
	 private int[] getExhaustiveEnumeration(int spaceSize) {
	 	int[] res = new int[spaceSize];
	 	for (int i=0; i<spaceSize; i++) {
	 		res[i] = i;
	 	}
	 	return res;
	 }
	 

	/*
	 * @return sorted array of nrIndices "random" integers ,
	 * 	values 0..spaceSize-1, no duplicates
	 */
	private int[] getRandomIndices(int spaceSize, int nrIndices) {
		
		//generate random indices until desired amount reached =
		//one random sample, no duplicate indices
		TreeMap indexMap = new TreeMap();
		while (indexMap.size() < nrIndices) {	//re-insert Integer as key
			indexMap.put(new Integer((int) (Math.random() * (double)spaceSize)), null);
		}
			
		//convert hash-list of indices to int-array
		int[] res = new int[indexMap.size()];
		Iterator indexIterator = indexMap.keySet().iterator();
		for (int i=0; i<res.length; i++) {
			res[i] = ((Integer) (indexIterator.next())).intValue();
		}
		
		return res;
	}
	


	public void crashClasses() {
		
		/* 
		 * Determine size of each class's plan-space --> sum = total space 
		 */
		int[] planSpaceSizes = new int[classes.length];
		int totalPlanSpaceSize = 0;
		for (int i=0; i<classes.length; i++) {
			planSpaceSizes[i] =  planner.getPlanSpace(classes[i]).getPlanSpaceSize();
			totalPlanSpaceSize += planSpaceSizes[i];
		}
		

		/* 
		 * Determine nrSamples per class 
		 */
		int[] nrSamples = new int[classes.length];
		
		/* "Exhaustive" test iff nrPlans < nrTestMethods */
		double methsPerPlan = 1.0;	//how many test-methods do we spend on a plan?
		
		/* We limit ourselves to fewer test-methods iff nrPlans >= nrTestMethods */
		int maxNrTestMeths = 
			Constants.MAX_NR_TEST_CLASSES * Constants.MAX_NR_TEST_METHS_PER_CLASS;
		if (totalPlanSpaceSize > maxNrTestMeths) {			
			methsPerPlan = (double)maxNrTestMeths / (double)totalPlanSpaceSize;
		}

		//FIXME debug: print statistic
		//System.out.println("class --> #plans --> #tests");
			
		/* Assign each class (at least) as many test-methods as it has plans */
		for (int i=0; i<classes.length; i++) {
			double methsPerClass = (double) Constants.MAX_NR_TEST_METHS_PER_CLASS;
			nrSamples[i] = (int) 
				(((double)planSpaceSizes[i] + methsPerClass/2) / 	//add half of max meth per class to emulate rounding
				methsPerClass* methsPerPlan);
			if (nrSamples[i] == 0) {
				nrSamples[i] += 1;
			}
			
			/* check if nrSamples[i] contains more tests than plans are available */
			int nrTests = nrSamples[i] * Constants.MAX_NR_TEST_METHS_PER_CLASS;			
			if (nrTests > planSpaceSizes[i]) {
				nrTests = planSpaceSizes[i];
				//"random" sample is an exhaustive enumeration of planspace
			}

			//class-under-test; plan-depth; #tests; #planspace
			System.out.print(
				";"+classes[i].getName()+
				";"+Constants.MAX_PLAN_RECURSION+
				";"+nrTests+
				";"+planSpaceSizes[i]
			);			
			//System.out.println(classes[i] +" --> \t" +planSpaceSizes[i] +" --> \t" +nrTests);
		}		
			
		/*
		 * Generate test classes, finally.
		 * Each test class is a random sample over the class's plan space.
	 	 */
		for (int i=0; i<classes.length; i++) {
			
			int nrIndices = Constants.MAX_NR_TEST_METHS_PER_CLASS * nrSamples[i];
			int[] sample = null;

			//more methods than plans in space?
			if (nrIndices >= planSpaceSizes[i])
				sample = getExhaustiveEnumeration(planSpaceSizes[i]);
			else {
				sample = getRandomIndices(planSpaceSizes[i], nrIndices);
				check(sample.length == nrIndices);
			}
			
			
			/* 
			 * split result into junks of max 500 for each test class
			 */
			int sliceCount = 0;
			int arrayCursor = 0;	//next block to be retrieved and printed

			while (arrayCursor < sample.length) {
				sliceCount += 1;
				
				/* Retrieve next block of up to max nr test/class as defined by sample */
				Block[] blocks = getTestBlocks(classes[i], sample, arrayCursor);
				
				TestCaseWriter codeWriter = new JUnitTestCaseWriter(
						classes[i],
						"no comment",
						true,
						blocks,
						sliceCount);
				codeWriter.write();
				
				arrayCursor += blocks.length;
			}
			//codeWriter.generateSuite(classes[i], sliceCount);	//create a management class
		}
	}
	
	
	
	
	/**
	 * Retrieve test blocks according of specified class and its indices into plan space.
	 * @param indices is guaranteed to be free of duplicates for entire class-space
	 * @param arrayCursor starting index into indices - retrieve from here upto
	 * 	max tests/ class = next block
	 * 
	 * TODO: Move to NonExecutingPlanner
	 */
	public <T> Block[] getTestBlocks(Class<T> pClass, int[] indices, int arrayCursor) {
		notNull(pClass);
		notNull(indices);
		Block[] res = null;
		
		ClassUnderTest<T> cNode = planner.getPlanSpace(pClass);	//from cache
		
		/* return the requested (part of the) test blocks */		
		List<Block> resV = new LinkedList<Block>();

		/* Retrieve distinct selected plans from plan space */
		for (int i=0; i+arrayCursor<indices.length && i<Constants.MAX_NR_TEST_METHS_PER_CLASS; i++) {
				resV.add(cNode.getBlock(indices[i+arrayCursor], pClass));
		}
		
		res = (Block[]) resV.toArray(new Block[resV.size()]);
		
		assert res != null;
		return res;
	}
}
