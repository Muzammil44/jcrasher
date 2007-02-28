/*
 * AbstractJUnitTestWriter.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.writer;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides basic JUnit test case writing functions
 * for the wrapped type.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public abstract class AbstractJUnitTestWriter<T> {
  
	protected final Class<T> testeeClass;
	protected final String comment;
	protected final boolean doAnnotate = false;	//Java 5 
  
	/**
	 * Constructor
	 */
	protected AbstractJUnitTestWriter(
			final Class<T> testeeClass,
			final String comment) {
		
		this.testeeClass = notNull(testeeClass);
		this.comment = notNull(comment);		
	}
	
  /**
   * Generates the "package bla;" header.
   * 
   * @param testeeClass for which we generate a JUnit test file.
   * 
   * @return one line terminated by \newline:
   * <pre>
   * package test.package;
   * 
   * </pre>
   * Empty string if testeeClass resides in the default package.
   */
  protected String getPackageHeader() {
    notNull(testeeClass);
    final StringBuilder sb = new StringBuilder();
    
    /* Extract p.q from p.q.C */
    final StringBuilder pack = new StringBuilder(""); // default package
    final String[] leafNameParts = testeeClass.getName().split("\\.");
    for (int i = 0; i < leafNameParts.length - 1; i++) {
      if (i > 0) {
        pack.append(".");
      }
      pack.append(leafNameParts[i]);
    }

    /* default package */
    
    if (pack.length()==0) {
      return "";
    }
    
    /* package test.package; */
    
    sb.append("package "+pack.toString()+";"+ NL+NL);
    return sb.toString();
  }
  
  /**
   * @return doAnnotate? "@Override" : ""
   */
  protected String getOverride() {
  	return doAnnotate? TAB+"@Override"+NL : "";
  }
  
  /**
   * @param content one or multiple \newline separated lines of text.
   * 
   * @return content trimmed and wrapped in a tabbed JavaDoc comment.
   * Replaces empty content with a todo statement. 
   * Terminated by single \newline.
   */
  protected String getJavaDocComment(
  		final String pContent, 
  		final String pTabs) 
  {
  	String content = pContent;
  	String tabs = pTabs;
  	
    if (tabs==null) {
      tabs = "";
    }
    
    if (content==null) { //empty comment.
      content = "";
    }

    content = content.trim();
    if ("".equals(content)) {
      content = "TODO: comment";
    }
    
    /* (?m) is an embedded flag that activates Pattern's MULTILINE mode:
     * http://java.sun.com/j2se/1.5.0/docs/api/
     * java/util/regex/Pattern.html#MULTILINE
     */    
    return
        tabs+"/**"+                                     NL+
        content.replaceAll("(?m)^", tabs+" * ")+        NL+
        tabs+" */"+                                     NL;    
  }
  
  
  /**
   * @return a file writer that writes to file.
   * null if it failed.
   */
  protected FileWriter createFileWriter(final File file) {
    notNull(file);
    
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter(file);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return fileWriter;
  }
  
  
  /**
   * @return success of writing content to fileWriter.
   */
  protected boolean writeToFileWriter(
      final FileWriter fileWriter, 
      final String content) {
    
    notNull(fileWriter);
    notNull(content);    
    
    try {
      fileWriter.write(content);
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  
  /**
   * @return success of closing fileWriter.
   */
  protected boolean closeFileWriter(final FileWriter fileWriter) {
    notNull(fileWriter);
    
    try {
      fileWriter.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    
    return true;
  }  
}
