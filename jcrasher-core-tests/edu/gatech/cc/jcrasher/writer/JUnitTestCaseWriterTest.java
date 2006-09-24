/*
 * Copyright (C) 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.gatech.cc.jcrasher.writer;

import static edu.gatech.cc.jcrasher.Constants.NL;
import static edu.gatech.cc.jcrasher.Constants.TAB;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;
import edu.gatech.cc.jcrasher.plans.blocks.Block;
import edu.gatech.cc.jcrasher.plans.blocks.BlockImpl;
import edu.gatech.cc.jcrasher.plans.blocks.ExpressionStatement;
import edu.gatech.cc.jcrasher.plans.blocks.Statement;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.FunctionCall;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;

/**
 * Tests edu.gatech.cc.jcrasher.writer.JUnitTestCaseWriter
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class JUnitTestCaseWriterTest extends TestCase {

  protected Class inDefaultPackage = null;
  protected JUnitTestCaseWriter junitTestCaseWriter = null;
  
  protected Block blockCall = null;
  protected Block blockCrash = null;
  protected Block[] blocks1 = null;
  protected Block[] blocks2 = null;

  
  @Override
  protected void setUp() throws Exception { 
    super.setUp();

    junitTestCaseWriter = new JUnitTestCaseWriter();
    inDefaultPackage = Class.forName("InDefaultPackage");    
    
    
    FunctionCall testeeCall = null;
    FunctionCall testeeCrash = null;
    Statement testeeCallStmt = null;
    Statement testeeCrashStmt = null;
    
    Constructor<Loadee> loadeeConstructor =
        Loadee.class.getConstructor(new Class[0]);
    
    Method loadeeStaticMeth =
        Loadee.class.getMethod("staticMeth", new Class[]{int.class});
    
    testeeCall = new ConstructorCall(loadeeConstructor, new Expression[0]);    
    testeeCrash = new MethodCall(
        loadeeStaticMeth,
        new Expression[]{new IntLiteral(3)});
    
    testeeCallStmt = new ExpressionStatement(testeeCall);
    testeeCrashStmt = new ExpressionStatement(testeeCrash);
    
    blockCall = new BlockImpl(loadeeConstructor);
    blockCrash = new BlockImpl(loadeeStaticMeth);
    
    blockCall.setBlockStmts(new Statement[]{testeeCallStmt});
    blockCrash.setBlockStmts(new Statement[]{testeeCrashStmt});
    
    blocks1 = new Block[] {blockCrash};
    blocks2 = new Block[] {blockCall, blockCrash};
  }
  
    
  
  public void testGetSimpleTestName() {
    try {
      junitTestCaseWriter.getSimpleTestName(null);
      fail("getSimpleTestName(null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }

    
    assertEquals(
        "LoadeeTest",
        junitTestCaseWriter.getSimpleTestName(Loadee.class));
    
    assertEquals(
        "StaticMemberTest",
        junitTestCaseWriter.getSimpleTestName(Loadee.StaticMember.class));
    
    assertEquals(
        "InnerTest",
        junitTestCaseWriter.getSimpleTestName(Loadee.Inner.class));    
  }


  public void testGetHeader() {
    try {
      junitTestCaseWriter.getHeader(false, null);
      fail("getHeader(.., null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }

    assertEquals(
        "public class LoadeeTest"+
        " extends junit.framework.TestCase {"+                              NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed before each testXXX()."+                           NL+ 
        TAB+" */"+                                                          NL+
        TAB+"protected void setUp() {"+                                     NL+
        TAB+TAB+"//TODO: my setup code goes here."+                         NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed after each testXXX()."+                            NL+ 
        TAB+" */"+                                                          NL+      
        TAB+"protected void tearDown() throws Exception {"+                 NL+
        TAB+TAB+"super.tearDown();"+                                        NL+
        TAB+TAB+"//TODO: my tear down code goes here."+                     NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getHeader(false, Loadee.class));    
    
    assertEquals(
        "public class LoadeeTest"+
        " extends edu.gatech.cc.junit.FilteringTestCase {"+                 NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed before each testXXX()."+                           NL+ 
        TAB+" */"+                                                          NL+
        TAB+"protected void setUp() {"+                                     NL+
        TAB+TAB+"/* Re-initialize static fields of loaded classes. */"+     NL+
        TAB+TAB+"edu.gatech.cc.junit.reinit.ClassRegistry.resetClasses();"+ NL+
        TAB+TAB+"//TODO: my setup code goes here."+                         NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed after each testXXX()."+                            NL+ 
        TAB+" */"+                                                          NL+      
        TAB+"protected void tearDown() throws Exception {"+                 NL+
        TAB+TAB+"super.tearDown();"+                                        NL+
        TAB+TAB+"//TODO: my tear down code goes here."+                     NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getHeader(true, Loadee.class));
  }


  public void testGetFooter() {
    try {
      junitTestCaseWriter.getFooter(true, Loadee.class, null);
      fail("getFooter(.., .., .., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    try {
      junitTestCaseWriter.getFooter(true, null, blocks1);
      fail("getFooter(.., null, .., ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    assertEquals(
        TAB+"public LoadeeTest(String pName) {"+                           NL+
        TAB+TAB+"super(pName);"+                                            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static junit.framework.Test suite() {"+                 NL+
        TAB+TAB+"return new " +
                "junit.framework.TestSuite(LoadeeTest.class);"+            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static void main(String[] args) {"+                     NL+    
        TAB+TAB+"junit.textui.TestRunner.run(LoadeeTest.class);"+          NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getFooter(false, Loadee.class, blocks1));
    
    assertEquals(
        TAB+"protected String getNameOfTestedMeth() {"+                     NL+
        TAB+TAB+"return \"edu.gatech.cc.jcrasher.Loadee.staticMeth\";"+        NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public LoadeeTest(String pName) {"+                           NL+
        TAB+TAB+"super(pName);"+                                            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static junit.framework.Test suite() {"+                 NL+
        TAB+TAB+"return new " +
                "junit.framework.TestSuite(LoadeeTest.class);"+            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static void main(String[] args) {"+                     NL+    
        TAB+TAB+"junit.textui.TestRunner.run(LoadeeTest.class);"+          NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getFooter(true, Loadee.class, blocks1));
    
    assertEquals(
        TAB+"public LoadeeTest(String pName) {"+                           NL+
        TAB+TAB+"super(pName);"+                                            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static junit.framework.Test suite() {"+                 NL+
        TAB+TAB+"return new " +
                "junit.framework.TestSuite(LoadeeTest.class);"+            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"public static void main(String[] args) {"+                     NL+    
        TAB+TAB+"junit.textui.TestRunner.run(LoadeeTest.class);"+          NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getFooter(true, Loadee.class, blocks2));
    
    assertEquals(
        junitTestCaseWriter.getFooter(true, Loadee.class, blocks2),
        junitTestCaseWriter.getFooter(false, Loadee.class, blocks2));
  }


  public void testGetTestedMethNameBlock() {
    try {
      junitTestCaseWriter.getTestedMethName((Block) null);
      fail("getTestedMethName(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    assertEquals(
        junitTestCaseWriter.getTestedMethName(blockCrash),
        "staticMeth");
    
    assertEquals(
        junitTestCaseWriter.getTestedMethName(blockCall),
        "<init>");    
  }


  public void testGetTestedMethNameBlockArray() {
    try {
      junitTestCaseWriter.getTestedMethName((Block[]) null);
      fail("getTestedMethName(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    assertEquals(
      junitTestCaseWriter.getTestedMethName(blocks2),
      null);
  
  assertEquals(
        junitTestCaseWriter.getTestedMethName(blocks1),
        "staticMeth");  
  }


  public void testGetTestCases() {
    try {
      junitTestCaseWriter.getTestCases(true, null, blocks1);
      fail("getTestCases(.., null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
      junitTestCaseWriter.getTestCases(true, Loadee.class, null);
      fail("getTestCases(.., .., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }    
    
    assertEquals(                                                   NL+
        TAB+"public void test0() throws Throwable {"+               NL+
        TAB+TAB+"try"+
                blocks1[0].toString(TAB+TAB, Loadee.class)+
                                                                    NL+
        TAB+TAB+"catch (Exception e) {dispatchException(e);}"+      NL+
        TAB+"}"+                                                    NL,
        junitTestCaseWriter.getTestCases(true, Loadee.class, blocks1));
    
    assertEquals(                                                   NL+
        TAB+"public void test0() throws Throwable {"+               NL+
        TAB+TAB+"try"+
                blocks2[0].toString(TAB+TAB, Loadee.class)+
                                                                    NL+
        TAB+TAB+"catch (Exception e) {dispatchException(e);}"+      NL+
        TAB+"}"+                                                    NL+
                                                                    NL+
        TAB+"public void test1() throws Throwable {"+               NL+
        TAB+TAB+"try"+
                blocks2[1].toString(TAB+TAB, Loadee.class)+
                                                                    NL+
        TAB+TAB+"catch (Exception e) {dispatchException(e);}"+      NL+
        TAB+"}"+                                                    NL,
        junitTestCaseWriter.getTestCases(true, Loadee.class, blocks2));
    
    assertEquals(                                                   NL+
        TAB+"public void test0() throws Throwable "+
                blocks2[0].toString(TAB, Loadee.class)+             NL+
                                                                    NL+
        TAB+"public void test1() throws Throwable "+
                blocks2[1].toString(TAB, Loadee.class)+             NL,
        junitTestCaseWriter.getTestCases(false, Loadee.class, blocks2));        
  }
}
