/*
 * JCrasher.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Assertions.check;
import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.FS;
import static edu.gatech.cc.jcrasher.Constants.PS;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 * Starts JCrasher
 *
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class JCrasher {
	
	protected static final String usage =
		"Usage: java edu.gatech.cc.jcrasher.JCrasher OPTION* (CLASS|PACKAGE)+\n" + 
		"Generate JUnit test case sources for every CLASS and all classes within\n" +
		"every PACKAGE and their sub-packages.\n" +
		"Example: java edu.gatech.cc.jcrasher.JCrasher p1.C p2\n\n" +

		"  -e, --execute        execute test cases while generating to suppress boring ones\n" +
		"  -d, --depth=INT      maximal depth of method chaining (default 3)\n" +
    "  -f, --files=INT      maximal nr of test files created (default 4000)\n" +
		"  -h, --help           print these instructions\n" +
		"  -j, --junitFiltering make generated test cases extend FilteringTestCase\n" +
		"  -l, --log            generate detailed log\n" +		
		"  -o, --outdir=DIR     where JCrasher writes test case sources to (default .)\n" +
    "  -s, --suppressNull   do not include any null literals in generated test cases.\n" +
		"  -v, --version        print version number\n";

  protected final static String copyright = 
    "(C) Copyright 2002-2007 Christoph Csallner and Yannis Smaragdakis.";
	protected static String name = "JCrasher (http://code.google.com/p/jcrasher/)";
	protected static String hint =
		"Try `java edu.gatech.cc.jcrasher.JCrasher --help' for more information.";

	/* TODO(csallner): evaluate the Java logging framework. */
	private final static Logger log =
		 Logger.getLogger(JCrasher.class.getName());
	
	/**
	 * Execute test cases to improve filtering.
	 */
	protected static boolean execute = false; 
	
	/**
	 * Set the log level globally.
	 */
	protected static void setLogLevel(Level level) {
		final Logger rootLogger = Logger.getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level); 
	}
	
	/**
	 * Print out cause of termination, hint and terminate
	 */
	protected static void die(String cause) {
  	System.err.println(
  			cause+"\n" +
				hint +"\n");
  	exit();
	}

	
	/**
	 * Print out hint and terminate
	 */
	protected static void die() {
  	System.err.println(hint +"\n");
  	exit();
	}	
	
	
	/**
	 * Terminate JCrasher.
	 */
	protected static void exit() {
		System.exit(0);
	}
	
	/**
	 * Load all classes from the jar file that are in one of the
	 * defined packages or their sub-packages.
	 * 
	 * @param packages ::= (package name)+
	 */
	protected static Set<Class<?>> loadFromJar(
			final String jarName,
			final Set<String> packages) {
		notNull(jarName);
		notNull(packages);
		check(packages.size()>0);
		
		log.fine("Searching " + jarName + " for classes in user-specified packages.");
		final Set<Class<?>> res = new HashSet<Class<?>>();
		
		try {
			final Enumeration<JarEntry> entries = (new JarFile(jarName)).entries();			
			while (entries.hasMoreElements()) {
				final JarEntry entry = entries.nextElement();
				if (!entry.getName().endsWith(".class")) {
					log.finer(entry + " is not a class.");
					continue;  //ignore entries that are not class files
				}
				String entryName = entry.getName().replace('/','.').replace('\\','.');
				entryName = entryName.substring(0, entryName.length()-6);  //remove .class suffix
				
				for (String pack: packages) {
					if (entryName.startsWith(pack+".")) {
						try {
							res.add(Class.forName(entryName));
							log.fine("Loaded "+entryName+" from "+jarName 
									+ " as a class belonging to user-specified " +pack+".");
						}
						catch (Throwable t) {
							log.fine("Could not load "+entryName+" from "+jarName 
									+ " (for user-specified " +pack+").");							
						}
					}
				}
			}
		}
		catch (Exception e) {
			/* ignore unusable classpath element */
			log.fine("Error reading " + jarName + ":");
			log.fine(e.toString());
		}
		return res;
	}


	
	/**
	 * Load all classes from directory dir.
	 * 
	 * @param pack name of package represented by dir
	 */
	protected static Set<Class<?>> loadFromDir(final File dir, final String pack) {
		notNull(dir);
		check(dir.exists());
		
		log.fine("Loading all classes from " + dir.getAbsolutePath());
		final Set<Class<?>> res = new HashSet<Class<?>>();
		
		final File[] elems = dir.listFiles();
		for (File elem: elems) {
			
			if (elem.getName().endsWith(".class")) {	//class file
				String simpleClassName = elem.getName().replace(PS, ".");
				simpleClassName = simpleClassName.substring(0,simpleClassName.length()-6);
				final String qualClassName = pack+"."+simpleClassName;
				try {
					res.add(Class.forName(qualClassName));
					log.fine("Loaded "+qualClassName+" from "+dir.getAbsolutePath() 
							+ " as a class belonging to user-specified " +pack+".");
				}
				catch (Throwable e) {
					log.fine("Could not load "+qualClassName+" from "+dir.getAbsolutePath() 
							+ " (for user-specified " +pack+").");	
				}
			}
			
			if (elem.isDirectory()) {	//recurse
				res.addAll(loadFromDir(elem, pack+"."+elem.getName()));
			}
		}
		
		return res;
	}
	
	
	
	/**
	 * Load all classes from the directory that are in one of the
	 * defined packages or their sub-packages.
	 * 
	 * @param userSpec ::= (package name)+
	 */
	protected static Set<Class<?>> loadFromDir(
			final String dirName,
			final Set<String> packages) {
		notNull(dirName);
		notNull(packages);
		check(packages.size()>0);
		
		log.fine("Searching " + dirName + " for classes in user-specified packages.");
		final Set<Class<?>> res = new HashSet<Class<?>>();
		
		for (String pack: packages) {
			final File dir = new File(dirName+FS+pack.replace(".",FS));
			if (dir.exists()) {
				/* Load all class files in dir and its sub-dirs */
				res.addAll(loadFromDir(dir, pack));
			}
		}
		return res;
	}	
	
	
	
	/**
	 * Load all classes found on the classpath that match userSpec.
	 * 
	 * @param userSpecs ::= (class name | package name)+
	 * package name means that the user wants to load all classes
	 * found in this package and all its sub-packages.
	 */
	protected Class<?>[] parseClasses(final String[] userSpecs) {
		final Set<Class<?>> res = new LinkedHashSet<Class<?>>();	//avoid multiple entires of same class

		/* First interpret each user-provided name as a class name.
		 * Standard classloader will find class of given name. */
		final Set<String> packageSpecs = new LinkedHashSet<String>();	//avoid multiple entires
		for (String userSpec: userSpecs) {
			try {
				res.add(Class.forName(userSpec));
				log.fine("Loaded "+userSpec+" directly from the classpath.");
			}
			catch (Exception e) {	//Could not be loaded as a class
				packageSpecs.add(userSpec);
				log.fine("Could not load "+userSpec+" directly as a class.");
			}
		}
		
		if (packageSpecs.size()==0) {	//Could load all elements of user spec
			log.fine("Loaded all user-provided identifiers as classes directly from the classpath.");
			return res.toArray(new Class[res.size()]);
		}
		
		
		/* Now interpret each user-provided name as a package name.
		 * We need to check every classpath entry ourselves for matching classes. */
		log.fine("Trying to interpret remaining identifiers as package names.");
		final String[] cpEntries = System.getProperty("java.class.path").split(PS);
		for (String cpEntry: cpEntries) {
			if (cpEntry.endsWith(".jar")) {	//Load classes from Jar
				res.addAll(loadFromJar(cpEntry, packageSpecs));
				continue;
			}
			//Load classes from directory
			res.addAll(loadFromDir(cpEntry, packageSpecs));
		}
		
		log.fine("Done loading user-specified classes.");
		return res.toArray(new Class[res.size()]);
	}
	

	
	/** 
	 * set Constants.MAX_PLAN_RECURSION according to user param 
	 */
	protected void parseDepth(final String arg) {
		int maxDepth = 0;
		try {
			maxDepth = Integer.parseInt(arg);
		}
		catch(NumberFormatException e) {
			die(arg +" must be greater than zero");
		}
		
		if (maxDepth <= 0) {
			die(arg +" must be greater than zero");
		}
		else {
			Constants.MAX_PLAN_RECURSION = maxDepth;
		}	  		
	}
  
  
  /** 
   * set Constants.MAX_NR_TEST_CLASSES according to user param 
   */
  protected void parseFiles(final String arg) {
    int maxFiles = 0;
    try {
      maxFiles = Integer.parseInt(arg);
    }
    catch(NumberFormatException e) {
      die(arg +" must be greater than zero");
    }
    
    if (maxFiles <= 0) {
      die(arg +" must be greater than zero");
    }
    else {
      Constants.MAX_NR_TEST_CLASSES = maxFiles;
    }       
  }
	
	
	/**
	 * Sets Constants.OUT_DIR according to user param.
   * 
   * Only public to allow access from Check 'n' Crash.
	 */
	public void parseOutDir(final String arg) {
		Constants.OUT_DIR = new File(arg);
		if (Constants.OUT_DIR.isDirectory()==false) {
			die(arg +" is not a directory.");
		}
	}
	
	
  /**
   * Allow other tools to use our parsing functionality
   * and give their name and hint.
   */
  public void setNameAndHint(String pName, String pHint) {
    name = pName;
    hint = pHint;
  }
  
  
	/* 
	 * Parse command line parameters using GNU GetOpt 
	 */
	protected Class<?>[] parse(final String[] args){
		LongOpt[] longopts = new LongOpt[]{
				new LongOpt("execute", LongOpt.NO_ARGUMENT, null, 'e'),
				new LongOpt("depth", LongOpt.REQUIRED_ARGUMENT, null, 'd'),
        new LongOpt("files", LongOpt.REQUIRED_ARGUMENT, null, 'f'),
				new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),				
				new LongOpt("junitFiltering", LongOpt.NO_ARGUMENT, null, 'j'),
				new LongOpt("log", LongOpt.NO_ARGUMENT, null, 'l'),
	   		new LongOpt("outdir", LongOpt.REQUIRED_ARGUMENT, null, 'o'),
        new LongOpt("suppressNull", LongOpt.NO_ARGUMENT, null, 's'),
				new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v')
	  };
	  Getopt g = new Getopt("JCrasher 2", args, "ed:f:hjlo:sv;", longopts);
	  int opt = 0;
	  while ((opt = g.getopt()) != -1) {
	  	switch (opt) {
	  		
	  		case 'e':  //--execute
	  			execute = true;
	  			break;
	  		
	  		case 'd':  //--depth .. maximum nesting depth.
	  			parseDepth(g.getOptarg());
	  			break;
          
        case 'f':  //--files .. maximum number of test files.
          parseFiles(g.getOptarg());
          break;          
	  			
	  		case 'j':  //--junitFiltering .. FilteringTestCase.
	  			Constants.JUNIT_FILTERING = true;
	  			break;
	  			
	  		case 'l':	//--log
	  			setLogLevel(Level.FINE); 
	  			break;
	      
	      case 'o':  //--outdir .. write test sources to.
	      	parseOutDir(g.getOptarg());
	      	break;

	      case 'h':  //--help .. print usage instructions.
	      	System.out.println(usage);
	      	exit();
	      	break;	//TODO(csallner): dead code.
	      
        case 's': //--suppressNull
          Constants.SUPPRESS_NULL_LITERALS = true; 
          break;  
          
	      case 'v':  //--version .. print version number.
	      	//TODO(csallner): get version from jar file name.
	      	//System.out.println(name);
	      	exit();
	      	break;	//TODO(csallner): dead code.
	      
	      case '?': 
	      	die();
	      	break;	//TODO(csallner): dead code.
	      	
	      default : //should not happen.
	      	log.severe("getopt() returned " +opt);
	      	die();
	  	}
	  }
	  
	  if (g.getOptind() >= args.length) {  //no class specified
	  	die("no class specified");
	  }
	  
		String[] classPackFromUser = new String[args.length-g.getOptind()];
		System.arraycopy(args, g.getOptind(), classPackFromUser, 0, classPackFromUser.length);
	  return parseClasses(classPackFromUser);
	}
	
	
	/**
	 * Main - called via jvm if started as an application
	 */
	public static void main(final String[] args) {
		setLogLevel(Level.SEVERE);	//only things that sould never occur.		
		System.out.println(name);
		System.out.println(copyright);
		
		/* Test planning time measurement. */
		final long startTime= System.currentTimeMillis();

		/* Load classes of given name with system class-loader */
		final JCrasher main = new JCrasher();
		final Class<?>[] classes = main.parse(args);
			
		/* Crash loaded class */
		if (classes!=null && classes.length>0) {
			final Crasher crasher = execute? 
					new ExecutingCrasher(classes) : new NonExecutingCrasher(classes);
			crasher.crashClasses();
		}
		else { 
			log.fine("Could not load any classes.");
		}
		
		/* Test planning time measurement. */
		final long endTime= System.currentTimeMillis();
		final long runTime= endTime-startTime;
		
		System.out.println("Run time: " + runTime + " ms.");
		//System.out.println(";" +runTime);		//for structured logging.
	}
}
