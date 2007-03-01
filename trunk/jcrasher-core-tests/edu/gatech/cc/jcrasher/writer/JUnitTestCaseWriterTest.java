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
import client.sub.Loadee;
import edu.gatech.cc.jcrasher.plans.expr.ConstructorCall;
import edu.gatech.cc.jcrasher.plans.expr.Expression;
import edu.gatech.cc.jcrasher.plans.expr.FunctionCall;
import edu.gatech.cc.jcrasher.plans.expr.MethodCall;
import edu.gatech.cc.jcrasher.plans.expr.literals.IntLiteral;
import edu.gatech.cc.jcrasher.plans.stmt.Block;
import edu.gatech.cc.jcrasher.plans.stmt.BlockImpl;
import edu.gatech.cc.jcrasher.plans.stmt.ExpressionStatement;
import edu.gatech.cc.jcrasher.plans.stmt.Statement;

/**
 * Tests edu.gatech.cc.jcrasher.writer.JUnitTestCaseWriter
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class JUnitTestCaseWriterTest extends TestCase {

  protected Class<?> inDefaultPackage = null;
  
  protected Block blockCall = null;
  protected Block blockCrash = null;
  protected Block[] blocks1 = null;
  protected Block[] blocks2 = null;

  
  @Override
  protected void setUp() throws Exception { 
    super.setUp();

    inDefaultPackage = Class.forName("InDefaultPackage");    
    
    
    FunctionCall<Loadee> testeeCall = null;
    FunctionCall<Void> testeeCrash = null;
    Statement testeeCallStmt = null;
    Statement testeeCrashStmt = null;
    
    Constructor<Loadee> loadeeConstructor =
        Loadee.class.getConstructor(new Class[0]);
    
    Method loadeeStaticMeth =
        Loadee.class.getMethod("staticMeth", new Class[]{int.class});
    
    testeeCall = new ConstructorCall<Loadee>(
    		Loadee.class,
    		loadeeConstructor, new Expression[0]);    
    testeeCrash = new MethodCall<Void>(
    		Loadee.class,
        loadeeStaticMeth,
        new Expression[]{new IntLiteral(3)});
    
    testeeCallStmt = new ExpressionStatement<Loadee>(testeeCall);
    testeeCrashStmt = new ExpressionStatement<Void>(testeeCrash);
    
    blockCall = new BlockImpl(Loadee.class,loadeeConstructor, TAB+TAB);
    blockCrash = new BlockImpl(Loadee.class,loadeeStaticMeth, TAB+TAB);
    
    blockCall.setBlockStmts(new Statement[]{testeeCallStmt});
    blockCrash.setBlockStmts(new Statement[]{testeeCrashStmt});
    
    blocks1 = new Block[] {blockCrash};
    blocks2 = new Block[] {blockCall, blockCrash};
  }
  
  /***/
  public void testConstructor() {
    try {
    	new JUnitTestCaseWriter(null, "", true, new Block[0]);
      fail("Constructor(null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }  	

    try {
    	new JUnitTestCaseWriter(inDefaultPackage, null, true, new Block[0]);
      fail("Constructor(.., null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
    try {
    	new JUnitTestCaseWriter(inDefaultPackage, "", true, null);
      fail("Constructor(.., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }  	    
  }
  
  /***/
  public void testGetSimpleTestName() {
  	JUnitTestCaseWriter<?> junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, new Block[0]);    
    assertEquals(
        "LoadeeTest",
        junitTestCaseWriter.getSimpleTestName());

    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, new Block[0], -1);    
    assertEquals(
        "LoadeeTest",
        junitTestCaseWriter.getSimpleTestName());    
    
    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, new Block[0], -1256);    
    assertEquals(
        "LoadeeTest",
        junitTestCaseWriter.getSimpleTestName());        
    
    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, new Block[0], 42);    
    assertEquals(
        "LoadeeTest42",
        junitTestCaseWriter.getSimpleTestName());    
    
    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee.StaticMember>(Loadee.StaticMember.class, "", true, new Block[0]);    
    assertEquals(
        "StaticMemberTest",
        junitTestCaseWriter.getSimpleTestName());

    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee.StaticMember>(Loadee.StaticMember.class, "", true, new Block[0], 17);    
    assertEquals(
        "StaticMemberTest17",
        junitTestCaseWriter.getSimpleTestName());
    
    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee.Inner>(Loadee.Inner.class, "", true, new Block[0]);    
    assertEquals(
        "InnerTest",
        junitTestCaseWriter.getSimpleTestName());
    
    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee.Inner>(Loadee.Inner.class, "", true, new Block[0], 11);    
    assertEquals(
        "InnerTest11",
        junitTestCaseWriter.getSimpleTestName());    
  }

  /***/
  public void testGetHeader() {
  	JUnitTestCaseWriter<Loadee> junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, new Block[0]);    

    assertEquals(
        "public class LoadeeTest"+
        " extends junit.framework.TestCase {"+                              NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed before each testXXX()."+                           NL+ 
        TAB+" */"+                                                          NL+
        //TODO: Make configurable
        //TAB+"@Override"+                                                    NL+
        TAB+"protected void setUp() {"+                                     NL+
        TAB+TAB+"//TODO: my setup code goes here."+                         NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed after each testXXX()."+                            NL+ 
        TAB+" */"+                                                          NL+
        //TODO: Make configurable
        //TAB+"@Override"+                                                    NL+
        TAB+"protected void tearDown() throws Exception {"+                 NL+
        TAB+TAB+"super.tearDown();"+                                        NL+
        TAB+TAB+"//TODO: my tear down code goes here."+                     NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getHeader());    
    
    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, new Block[0], 5);    
    assertEquals(
        "public class LoadeeTest5"+
        " extends junit.framework.TestCase {"+                              NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed before each testXXX()."+                           NL+ 
        TAB+" */"+                                                          NL+
        //TODO: Make configurable
        //TAB+"@Override"+                                                    NL+
        TAB+"protected void setUp() {"+                                     NL+
        TAB+TAB+"//TODO: my setup code goes here."+                         NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed after each testXXX()."+                            NL+ 
        TAB+" */"+                                                          NL+
        //TODO: Make configurable
        //TAB+"@Override"+                                                    NL+
        TAB+"protected void tearDown() throws Exception {"+                 NL+
        TAB+TAB+"super.tearDown();"+                                        NL+
        TAB+TAB+"//TODO: my tear down code goes here."+                     NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getHeader());  
    
    
  	junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, new Block[0]);        
    assertEquals(
        "public class LoadeeTest"+
        " extends edu.gatech.cc.junit.FilteringTestCase {"+                 NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed before each testXXX()."+                           NL+ 
        TAB+" */"+                                                          NL+
        //TODO: Make configurable
        //TAB+"@Override"+                                                    NL+
        TAB+"protected void setUp() {"+                                     NL+
        TAB+TAB+"/* Re-initialize static fields of loaded classes. */"+     NL+
        TAB+TAB+"edu.gatech.cc.junit.reinit.ClassRegistry.resetClasses();"+ NL+
        TAB+TAB+"//TODO: my setup code goes here."+                         NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
        TAB+"/**"+                                                          NL+
        TAB+" * Executed after each testXXX()."+                            NL+ 
        TAB+" */"+                                                          NL+
        //TODO: Make configurable
        //TAB+"@Override"+                                                    NL+
        TAB+"protected void tearDown() throws Exception {"+                 NL+
        TAB+TAB+"super.tearDown();"+                                        NL+
        TAB+TAB+"//TODO: my tear down code goes here."+                     NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getHeader());
  }

  /***/
  public void testGetFooter() {
  	JUnitTestCaseWriter<Loadee> junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, new Block[0]);        
    assertEquals(
    	  TAB+"/**"+																													NL+
    	  TAB+" * Constructor"+																								NL+
    	  TAB+" */"+																													NL+
        TAB+"public LoadeeTest(String pName) {"+                            NL+
        TAB+TAB+"super(pName);"+                                            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
    	  TAB+"/**"+																													NL+
    	  TAB+" * Easy access for aggregating test suite."+										NL+
    	  TAB+" */"+																													NL+            
        TAB+"public static junit.framework.Test suite() {"+                 NL+
        TAB+TAB+"return new " +
                "junit.framework.TestSuite(LoadeeTest.class);"+             NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
    	  TAB+"/**"+																													NL+
    	  TAB+" * Main"+																											NL+
    	  TAB+" */"+        																									NL+                        
        TAB+"public static void main(String[] args) {"+                     NL+    
        TAB+TAB+"junit.textui.TestRunner.run(LoadeeTest.class);"+           NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getFooter());
    
  	junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, blocks1);    
    assertEquals(
        TAB+"protected String getNameOfTestedMeth() {"+                     NL+
        TAB+TAB+"return \"client.sub.Loadee.staticMeth\";"+                 NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
    	  TAB+"/**"+																													NL+
    	  TAB+" * Constructor"+																								NL+
    	  TAB+" */"+																													NL+    		        
        TAB+"public LoadeeTest(String pName) {"+                            NL+
        TAB+TAB+"super(pName);"+                                            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
    	  TAB+"/**"+																													NL+
    	  TAB+" * Easy access for aggregating test suite."+										NL+
    	  TAB+" */"+        																									NL+        
        TAB+"public static junit.framework.Test suite() {"+                 NL+
        TAB+TAB+"return new " +
                "junit.framework.TestSuite(LoadeeTest.class);"+             NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
    	  TAB+"/**"+																													NL+
    	  TAB+" * Main"+																											NL+
    	  TAB+" */"+        																									NL+                
        TAB+"public static void main(String[] args) {"+                     NL+    
        TAB+TAB+"junit.textui.TestRunner.run(LoadeeTest.class);"+           NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getFooter());

  	junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, blocks2);    
    assertEquals(
    	  TAB+"/**"+																													NL+
    	  TAB+" * Constructor"+																								NL+
    	  TAB+" */"+																													NL+    		
        TAB+"public LoadeeTest(String pName) {"+                            NL+
        TAB+TAB+"super(pName);"+                                            NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
    	  TAB+"/**"+																													NL+
    	  TAB+" * Easy access for aggregating test suite."+										NL+
    	  TAB+" */"+        																									NL+
        TAB+"public static junit.framework.Test suite() {"+                 NL+
        TAB+TAB+"return new " +
                "junit.framework.TestSuite(LoadeeTest.class);"+             NL+
        TAB+"}"+                                                            NL+
        TAB+                                                                NL+
    	  TAB+"/**"+																													NL+
    	  TAB+" * Main"+																											NL+
    	  TAB+" */"+        																									NL+                        
        TAB+"public static void main(String[] args) {"+                     NL+    
        TAB+TAB+"junit.textui.TestRunner.run(LoadeeTest.class);"+           NL+
        TAB+"}"+                                                            NL,
        junitTestCaseWriter.getFooter());
    
    assertEquals(
        (new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, blocks2)).getFooter(),
        (new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, blocks2)).getFooter());
  }

  /***/
  public void testGetTestedMethNameBlock() {
    try {
    	JUnitTestCaseWriter<Loadee> junitTestCaseWriter =
    		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, new Block[0]);        
      junitTestCaseWriter.getTestedMethName((Block) null);
      fail("getTestedMethName(null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
    
  	JUnitTestCaseWriter<Loadee> junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, new Block[0]);        
    assertEquals(
        junitTestCaseWriter.getTestedMethName(blockCrash),
        "staticMeth");
    
    assertEquals(
        junitTestCaseWriter.getTestedMethName(blockCall),
        "<init>");    
  }


  /***/
  public void testGetTestedMethName() {
  	JUnitTestCaseWriter<Loadee> junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, blocks2);            
    assertEquals(
      junitTestCaseWriter.getTestedMethName(),
      null);

    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, blocks1);            
    assertEquals(
        junitTestCaseWriter.getTestedMethName(),
        "staticMeth");  
  }

  /***/
  public void testGetTestCases() {
  	JUnitTestCaseWriter<Loadee> junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, blocks1);            
    assertEquals(                                                   NL+
    	  TAB+"/**"+																									NL+
    	  TAB+" * JCrasher-generated test case."+											NL+
    	  TAB+" */"+        																					NL+                    		
        TAB+"public void test0() throws Throwable {"+               NL+
        TAB+TAB+"try"+
                blocks1[0].text()+
                                                                    NL+
        TAB+TAB+"catch (Exception e) {dispatchException(e);}"+      NL+
        TAB+"}"+                                                    NL,
        junitTestCaseWriter.getTestCases());

    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", true, blocks2);
    assertEquals(                                                   NL+
    	  TAB+"/**"+																									NL+
    	  TAB+" * JCrasher-generated test case."+											NL+
    	  TAB+" */"+        																					NL+    		
        TAB+"public void test0() throws Throwable {"+               NL+
        TAB+TAB+"try"+
                blocks2[0].text()+
                                                                    NL+
        TAB+TAB+"catch (Exception e) {dispatchException(e);}"+      NL+
        TAB+"}"+                                                    NL+
                                                                    NL+
        TAB+"/**"+																									NL+
        TAB+" * JCrasher-generated test case."+											NL+
        TAB+" */"+        																					NL+    		       
        TAB+"public void test1() throws Throwable {"+               NL+
        TAB+TAB+"try"+
                blocks2[1].text()+
                                                                    NL+
        TAB+TAB+"catch (Exception e) {dispatchException(e);}"+      NL+
        TAB+"}"+                                                    NL,
        junitTestCaseWriter.getTestCases());
    
    junitTestCaseWriter =
  		new JUnitTestCaseWriter<Loadee>(Loadee.class, "", false, blocks2);
    assertEquals(                                                   NL+
    	  TAB+"/**"+																									NL+
    	  TAB+" * JCrasher-generated test case."+											NL+
    	  TAB+" */"+        																					NL+    		    		
        TAB+"public void test0() throws Throwable "+
                blocks2[0].text()+             											NL+
                                                                    NL+                                                            
        TAB+"/**"+																									NL+
        TAB+" * JCrasher-generated test case."+											NL+
        TAB+" */"+        																					NL+    		               
        TAB+"public void test1() throws Throwable "+
                blocks2[1].text()+             											NL,
        junitTestCaseWriter.getTestCases());        
  }
}
