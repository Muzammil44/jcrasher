/*
 * JUnitTestCaseWriter.java
 * 
 * Copyright 2002 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.writer;

import static edu.gatech.cc.jcrasher.Assertions.notNull;
import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;

import edu.gatech.cc.jcrasher.plans.blocks.Block;

/**
 * Generates a JUnit test case.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class JUnitTestCaseWriter extends AbstractJUnitTestWriter implements
    TestCaseWriter {
  

  /**
   * @return simple name of generated class = <simpleClassName>Test.
   * E.g., TesteeTest1 for (foo.bar.Testee, 1)
   */
  protected String getSimpleTestName(final Class testeeClass) {
    notNull(testeeClass);
    
//    String testeeName = testeeClass.getName();
//    int lastDotPos = testeeName.lastIndexOf('.') + 1;
//    check(lastDotPos>=0);
//
//    String simpleTesteeName = testeeName.substring(lastDotPos);
    
    return testeeClass.getSimpleName()+"Test";
  }
  
  
  /**
   * @return
   * <PRE>
   * public class TesteeTest1 extends ReInitializingTestCase {
   *    protected void setUp() { [..] }
   *    protected void tearDown() { [..] }    
   * </PRE> 
   */
  protected String getHeader(
      boolean doFilter,
      final Class testeeClass) {
    
    notNull(testeeClass);
    
    final String simpleTestName = getSimpleTestName(testeeClass);
    String qualSuperClassName = "junit.framework.TestCase";
    if (doFilter) {
      qualSuperClassName = "edu.gatech.cc.junit.FilteringTestCase";
    }
    String reinitCode = ""; //off by default
    if (doFilter) {
      reinitCode = 
        TAB+TAB+"/* Re-initialize static fields of loaded classes. */"+     NL+
        TAB+TAB+"edu.gatech.cc.junit.reinit.ClassRegistry.resetClasses();"+ NL;     
    }
    
    return
        "public class "+simpleTestName+
        " extends "+qualSuperClassName+" {"+                                NL+
        TAB+                                                                NL+
        getJavaDocComment("Executed before each testXXX().", TAB)+
        TAB+"protected void setUp() {"+                                     NL+
        reinitCode+
        TAB+TAB+"//TODO: my setup code goes here."+                         NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        getJavaDocComment("Executed after each testXXX().", TAB)+
        TAB+"protected void tearDown() throws Exception {"+                 NL+
        TAB+TAB+"super.tearDown();"+                                        NL+
        TAB+TAB+"//TODO: my tear down code goes here."+                     NL+
        TAB+"}"+                                                            NL;        
  }
  
  
  /**
   * @return
   * <PRE>
   * protected String getNameOfTestedMeth() { [..] }
   * public MyTestCase(String name) { [..] }
   * public static void main(String[] args) { [..] }
   * public static Test suite() { [..] }
   * </PRE> 
   */
  protected String getFooter(
      boolean doFilter,
      final Class testeeClass,
      final Block[] blocks) {    

    notNull(testeeClass);
    notNull(blocks);
    
    final String qualTesteeName = testeeClass.getName();
    final String simpleTestName = getSimpleTestName(testeeClass);
    final String testedMethName = getTestedMethName(blocks);
    final StringBuilder sb = new StringBuilder();
    
    if (doFilter && (testedMethName != null)) {
      /* override default in FilteringTestCase */
      sb.append(
        TAB+"protected String getNameOfTestedMeth() {"+                     NL+
        TAB+TAB+"return \""+qualTesteeName+"."+testedMethName+"\";"+        NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL);
    }
    sb.append(
        TAB+"public "+simpleTestName+"(String pName) {"+                    NL+
        TAB+TAB+"super(pName);"+                                            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static junit.framework.Test suite() {"+                 NL+
        TAB+TAB+"return new " +
                "junit.framework.TestSuite("+simpleTestName+".class);"+     NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static void main(String[] args) {"+                     NL+    
        TAB+TAB+"junit.textui.TestRunner.run("+simpleTestName+".class);"+   NL+
        TAB+"}"+                                                            NL);
    
    return sb.toString();
  }  
  
  
  /**
   * @return <init> for constructor and simple name for methods.
   */
  protected String getTestedMethName(final Block block) {
    notNull(block);
    
    if (block.getTestee() instanceof Constructor) {
      return "<init>";
    }
    return block.getTestee().getName();
  }


  /**
   * @return name of meth under test or <init> if common in blocks, null else.
   */
  protected String getTestedMethName(final Block[] blocks) {
    notNull(blocks);

    switch (blocks.length) {
      case 0:
        return null;
      case 1:
        return getTestedMethName(blocks[0]);
      default:
        String testedMethName = getTestedMethName(blocks[0]);
        for (Block block: blocks) {
          if (!getTestedMethName(block).equals(testedMethName)) {
            return null;
          }
        }
        return testedMethName;
    }
  }
    
  
  /**
   * @return
   * <pre>
   * public void test123() throws Throwable {
   *   try {
   *     P p = new P();
   *     C.funcUnderTest(p);
   *   }
   *   catch (Exception e) {
   *     dispatchException(e);
   *   }
   * </pre>
   */
  protected String getTestCases(
      boolean doFilter,
      final Class testee,
      final Block[] blocks) {
    
    notNull(testee);
    notNull(blocks);
    
    final StringBuilder sb = new StringBuilder();

    for (int i = 0; i < blocks.length; i++) {
      sb.append(                                                        NL+
          TAB+"public void test"+i+"() throws Throwable ");
      if(doFilter) {
        sb.append("{"+                                                  NL+
          TAB+TAB+"try");
      }
      
      sb.append(
          blocks[i].toString(doFilter? TAB+TAB : TAB, testee)+          NL);
      
      if(doFilter) {
        sb.append(
          TAB+TAB+"catch (Exception e) {dispatchException(e);}"+        NL+
          TAB+"}"+                                                      NL);
      }
    }
    
    return sb.toString();    
  }

  
  /**
   * @param doFilter if test case execution should filter any exception thrown.
   */
  public File writeTestFile(
      boolean doFilter,
      final Class testeeClass,
      final Block[] blocks,
      final String comment) {
    
    notNull(testeeClass);
    notNull(blocks);
    
    final String simpleTestName = testeeClass.getSimpleName()+"Test";
    final File outFile = CreateFileUtil.createOutFile(testeeClass, simpleTestName);    
    final FileWriter outWriter = createFileWriter(outFile);
    if (outWriter==null) {
      return null;
    }
    
    String content = 
        getPackageHeader(testeeClass)+                                  
        getJavaDocComment(comment, "")+
        getHeader(doFilter, testeeClass)+
        getTestCases(doFilter, testeeClass, blocks)+
        TAB+                                                            NL+
        TAB+                                                            NL+
        getFooter(doFilter, testeeClass, blocks)+
        "}";
    
    boolean success = false;
    success = writeToFileWriter(outWriter, content);
    if (!success) {
      return null;
    }
    
    success = closeFileWriter(outWriter);
    if (!success) {
      return null;
    }

    return outFile;
  }

}
