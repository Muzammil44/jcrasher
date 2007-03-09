/*
 * Constants.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import java.io.File;

/**
 * Project-wide constants.
 * 
 * ALL_CAP members may only be set during application setup.
 * They are user-defined constants.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class Constants {

	/**
	 * Filters plans.
	 */
  public enum PlanFilter {
  	/** null, new A(null), .. */
    ALL,
    
    /** new A(null), .. */
    NON_NULL,
    
    /** .. */
    NON_NULL_TRANS
  }

  
  /**
   * TODO(csallner): align with Java semantics.
   */
  public enum Visibility {  	
  	/** All members visible from anywhere = public (default) */
    GLOBAL,
    
    /** All members visible inside the same package */
    PACKAGE,
    
    /** Any visibility */
    ANY
  }

  
  /**
   * Verbosity levels
   */
  public enum Verbose {  	
  	/** do not print any type-graph */
    DEFAULT,
    
    /** print type-graph used for creating tests */
    VERBOSE,
    
    /** print entire type-graph */
    ALL
  }

  
  /**
   * Maximum number of test cases tried per testee class.
   * This is a soft limit, MAX_NR_TEST_CLASSES is more important.
   */
  public static int MAX_TEST_CASES_TRIED_CLASS = 1000; 
  
	/**
	 * How many test classes should be generated max?
   * Default set to 200 to enable fast compiling.
   * This will result in 100,000 test cases (given the
   * default setting of 500 test cases per test class).
   * The original JCrasher paper talked about two million.
   * You can overwrite this setting with --files.
	 */
  public static int MAX_NR_TEST_CLASSES = 200;
  
	/**
	 * How many test methods per generated test class:
	 * 1000 and junitMultiCL will generate OutOfMemoryError
	 */
  public static int MAX_NR_TEST_METHS_PER_CLASS = 500;
  
  /**
   * How much internal state shown to user.  
   */
  public static Verbose VERBOSE_LEVEL = Verbose.DEFAULT;


  /**
   * Library packages
   */
  public final static String[] LIBRARY_TYPES = new String[]{
    "java.", 
    "javax.", 
    "sun.", 
    "com.sun.", 
    "org.apache.", 
    "org.ietf.",
    "org.omg.", 
    "org.w3c.", 
    "sunw."};

  /**
   * How deep to search/ plan for each type?
   */
  public final static int MAX_PLAN_RECURSION_DEFAULT = 3;

  /**
   * To be exclusively set at-most-once by JCrasher on startup: 
   * TO BE USED AS A CONSTANT
   */
  public static int MAX_PLAN_RECURSION = 3;
  
  /**
   * Make JUnit test cases subclass FilteringTestCase.
   */
  public static boolean JUNIT_FILTERING = false;

  /**
   * Include null literals in preset values.
   */
  public static boolean SUPPRESS_NULL_LITERALS = false;  
  
  /**
   * Line separator
   */
  public final static String NL  = System.getProperty("line.separator");

  /**
   * Slash in Unix, backslash in Microsoft.
   */
  public final static String FS  = System.getProperty("file.separator");
  
  /**
   * Colon in Unix, semi-colon in Microsoft.
   */
  public final static String PS  = System.getProperty("path.separator");
  
  /**
   * Tab = two spaces.
   */
  public final static String TAB = "  ";

  /**
   * Directory in which to write generated test cases.
   */
  public static File OUT_DIR = null;
  
  /**
   * Minimum visibility of tested methods.
   */
  public static Visibility VIS_TESTED = Visibility.GLOBAL;
  
  /**
   * Minimum visibility of methods used to construct parameters.
   */  
  public static Visibility VIS_USED = Visibility.GLOBAL;
  

  /**
   * @return null is included by this filter
   */
  public static boolean isNullIncluded(PlanFilter f) {
    return PlanFilter.ALL.equals(f);
  }

  /**
   * Removes null from filter.
   */
  public static PlanFilter removeNull(PlanFilter f) {
    if (PlanFilter.ALL.equals(f)) {
      return PlanFilter.NON_NULL;
    }
    return f;
  }

  /**
   * Adds null to filter.
   */
  public static PlanFilter addNull(PlanFilter f) {
    if (PlanFilter.NON_NULL.equals(f)) {
      return PlanFilter.ALL;
    }
    return f;
  }
}
