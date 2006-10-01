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
import junit.framework.TestCase;
import edu.gatech.cc.jcrasher.Loadee;

/**
 * Tests edu.gatech.cc.jcrasher.writer.AbstractJUnitTestWriter
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class AbstractJUnitTestWriterTest extends TestCase {

  protected Class inDefaultPackage = null;

  
  @Override
  protected void setUp() throws Exception { 
    super.setUp();
    
    inDefaultPackage = Class.forName("InDefaultPackage");
  }
  

  public void testConstructor() {
    try {
    	AbstractJUnitTestWriter junitTestWriter = 
      	new ConcreteJUnitTestWriter(null, "No comment");
      fail("Constructor(null, ..) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  	
    try {
    	AbstractJUnitTestWriter junitTestWriter = 
      	new ConcreteJUnitTestWriter(inDefaultPackage, null);
      fail("Constructor(.., null) not allowed");
    }
    catch(RuntimeException e) {  //expected
    }
  	
  }
  
  public void testGetPackageHeader() {
  	AbstractJUnitTestWriter junitTestWriter = 
    	new ConcreteJUnitTestWriter(inDefaultPackage, "No comment");
    assertEquals("", junitTestWriter.getPackageHeader());
    
    junitTestWriter = 
    	new ConcreteJUnitTestWriter(Loadee.class, "No comment");
    assertEquals(
        "package edu.gatech.cc.jcrasher;"+NL+NL,
        junitTestWriter.getPackageHeader());
    
    junitTestWriter = 
    	new ConcreteJUnitTestWriter(Loadee.StaticMember.class, "No comment");
    assertEquals(
        "package edu.gatech.cc.jcrasher;"+NL+NL,
        junitTestWriter.getPackageHeader());
    
    junitTestWriter = 
    	new ConcreteJUnitTestWriter(Loadee.Inner.class, "No comment");    
    assertEquals(
        "package edu.gatech.cc.jcrasher;"+NL+NL,
        junitTestWriter.getPackageHeader());
  }


  public void testJavadocComment() {
  	AbstractJUnitTestWriter junitTestWriter = 
    	new ConcreteJUnitTestWriter(Loadee.Inner.class, "bla");
    assertEquals(
        "/**"+            NL+
        " * Hello"+       NL+
        " */"+            NL,
        junitTestWriter.getJavaDocComment("Hello", ""));
    
    assertEquals(
        "/**"+            NL+
        " * Hello"+       NL+
        " */"+            NL,
        junitTestWriter.getJavaDocComment("Hello", null));
    
    assertEquals(
        TAB+"/**"+            NL+
        TAB+" * Hello"+       NL+
        TAB+" */"+            NL,
        junitTestWriter.getJavaDocComment("Hello", TAB));
    
    assertEquals(
        TAB+TAB+"/**"+            NL+
        TAB+TAB+" * Hello"+       NL+
        TAB+TAB+" */"+            NL,
        junitTestWriter.getJavaDocComment("Hello", TAB+TAB));
    
    assertEquals(
        TAB+TAB+"/**"+            NL+
        TAB+TAB+" * Hello"+       NL+
        TAB+TAB+" * World"+       NL+
        TAB+TAB+" */"+            NL,
        junitTestWriter.getJavaDocComment("Hello"+NL+"World", TAB+TAB));    

    assertEquals(
        "/**"+                  NL+
        " * TODO: comment"+     NL+
        " */"+                  NL,
        junitTestWriter.getJavaDocComment("", ""));
  }

  
  public class ConcreteJUnitTestWriter extends AbstractJUnitTestWriter {
  	ConcreteJUnitTestWriter(Class testeeClass, String comment) {
  		super(testeeClass, comment);
  	}
  }

}
