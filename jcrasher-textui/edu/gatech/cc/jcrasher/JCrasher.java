/*
 * JCrasher.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher;

import static edu.gatech.cc.jcrasher.Constants.FS;
import static edu.gatech.cc.jcrasher.Constants.PS;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
		
		"  -d, --depth=INT  maximal depth of method chaining (default 3)\n" +
		"  -o, --outdir=DIR where JCrasher writes test case sources to (default .)\n" +
		"  -v, --version    print version number\n" +
		"  -h, --help       print these instructions\n"
	;

	protected final static String name = "JCrasher 2";	
	protected final static String hint =
		"Try `java edu.gatech.cc.jcrasher.JCrasher --help' for more information."
	;

	
	/*
	 * Print out cause of termination, hint and terminate
	 */
	protected static void die(String cause) {
  	System.err.println(
  			name +": " +cause +"\n" +
				hint +"\n");
  	System.exit(0);
	}

	/*
	 * Print out hint and terminate
	 */
	protected static void die() {
  	System.err.println(hint +"\n");
  	System.exit(0);
	}	
	
	
	/**
	 * Load all classes from the jar file that are in one of the
	 * defined packages or their sub-packages.
	 * 
	 * @param packages ::= (package name)*
	 */
	protected static Set<Class> loadFromJar(String jarName, Set<String> packages) {
		assert jarName!=null;
		assert packages!=null;
		Set<Class> res = new HashSet<Class>();
		
		try {
			Enumeration<JarEntry> entries = (new JarFile(jarName)).entries();			
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (!entry.getName().endsWith(".class")) {  
					continue;  //ignore entries that are not class files
				}
				String entryName = entry.getName().replace('/','.').replace('\\','.');
				entryName = entryName.substring(0, entryName.length()-6);  //remove .class suffix
				
				for (String pack: packages) {
					if (entryName.startsWith(pack+".")) {
						try {
							res.add(Class.forName(entryName));
						}
						catch (Throwable t) {
							/* ignore misnamed class */
						}
					}
				}
			}
		}
		catch (Exception e) {
			/* ignore unusable classpath element */
		}
		return res;
	}


	
	/**
	 * Load all classes from directory dir.
	 * 
	 * @param pack name of package represented by dir
	 */
	protected static Set<Class> loadFromDir(File dir, String pack) {
		assert dir!=null && dir.exists();
		
		Set<Class> res = new HashSet<Class>();
		
		File[] elems = dir.listFiles();
		for (File elem: elems) {
			
			if (elem.getName().endsWith(".class")) {	//class file
				String cName = elem.getName().replace(PS, ".");
				cName = cName.substring(0,cName.length()-6); 
				try {
					res.add(Class.forName(pack+"."+cName));
				}
				catch (Throwable e) {
					/* ignore misnamed class */
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
	protected static Set<Class> loadFromDir(String dirName, Set<String> packages) {
		assert dirName!=null;
		assert packages!=null && packages.size()>0;
		
		Set<Class> res = new HashSet<Class>();
		
		for (String pack: packages) {
			File dir = new File(dirName+FS+pack.replace(".",FS));
			if (!dir.exists()) {
				continue;
			}
			/* Load all class files in dir and its sub-dirs */
			res.addAll(loadFromDir(dir, pack));
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
	protected Class[] parseClasses(String[] userSpecs) {
		Set<Class> res = new HashSet<Class>();	//avoid multiple entires of same class

		/* Load all classes specified by the user directly */
		Set<String> packageSpecs = new HashSet<String>();	//avoid multiple entires
		for (String userSpec: userSpecs) {
			try {
				res.add(Class.forName(userSpec));
			}
			catch (Exception e) {	//Could not be loaded as a class
				packageSpecs.add(userSpec);
			}
		}
		
		if (packageSpecs.size()==0) {	//Could load all elements of user spec
			return res.toArray(new Class[res.size()]);
		}
		
		
		
		/* Find all classes that match the user's package specs */
		String[] cpEntries = System.getProperty("java.class.path").split(PS);
		for (String cpElement: cpEntries) {
			if (cpElement.endsWith(".jar")) {	//Load classes from Jar
				res.addAll(loadFromJar(cpElement, packageSpecs));
				continue;
			}
			//Load classes from directory
			res.addAll(loadFromDir(cpElement, packageSpecs));
		}
		
		return res.toArray(new Class[res.size()]);
	}
	

	
	/** 
	 * set Constants.MAX_PLAN_RECURSION according to user param 
	 */
	protected void parseDepth(String arg) {
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
	 * set Constants.OUT_DIR according to user param
	 */
	protected void parseOutDir(String arg) {
		Constants.OUT_DIR = new File(arg);
		if (Constants.OUT_DIR.isDirectory()==false) {
			die(arg +" is not a directory.");
		}
	}
	
	
	/* 
	 * Parse command line parameters using GNU GetOpt 
	 */
	protected Class[] parse(String[] args){
		LongOpt[] longopts = new LongOpt[]{
				new LongOpt("depth", LongOpt.REQUIRED_ARGUMENT, null, 'd'),
	   		new LongOpt("outdir", LongOpt.REQUIRED_ARGUMENT, null, 'o'),
				new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
				new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v')
	  };
	  Getopt g = new Getopt("JCrasher", args, "d:ho:v;", longopts);
	  int opt = 0;
	  while ((opt = g.getopt()) != -1) {
	  	switch (opt) {
	  	
	  		case 'd':  //--depth .. maximum nesting depth.
	  			parseDepth(g.getOptarg());
	  			break;
	      
	      case 'o':  //--outdir .. write test sources to.
	      	parseOutDir(g.getOptarg());
	      	break;

	      case 'h':  //--help .. print usage instructions.
	      	System.out.println(usage);
	      	System.exit(0);
	      	break;	//TODO(csallner): dead code.
	      
	      case 'v':  //--version .. print version number.
	      	System.out.println(name);
	      	System.exit(0);
	      	break;	//TODO(csallner): dead code.
	      
	      case '?': 
	      	die();
	      	break;	//TODO(csallner): dead code.
	      	
	      default : //should not happen.
	      	die("getopt() returned " +opt);
	  	}
	  }
	  
	  if (g.getOptind() >= args.length) {  //no class specified
	  	die("no class specified");
	  }
	  
		String[] classPackFromUser = new String[args.length-g.getOptind()];
		System.arraycopy(args, g.getOptind(), classPackFromUser, 0, classPackFromUser.length);
	  return parseClasses(classPackFromUser);
	}
	
	
	
	/*************************************************************************
	 * Main - called via jvm if started as an application
	 */
	public static void main(String[] args) {
				
		/* Test planning time measurement. */
		long startTime= System.currentTimeMillis();

		/* Load classes of given name with system class-loader */
		JCrasher main = new JCrasher();
		Class[] classes = main.parse(args);
			
		/* Crash loaded class */
		if (classes!=null && classes.length>0) {
			Crasher crasher = new CrasherImpl();
			crasher.crashClasses(classes);
		}
		
		/* Test planning time measurement. */
		long endTime= System.currentTimeMillis();
		long runTime= endTime-startTime;
		System.out.println("Run time: " +runTime +" ms.");
		//System.out.println(";" +runTime);		//for structured logging.
	}
}
