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
package client.sub;

/**
 * Exists to get loaded in test cases. DO NOT CHANGE LINE NUMBERS!
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 */
public class Loadee {

	/***/public int fieldInt = 0;
  
  /***/public Loadee() { /* Empty */ }
  
  /***/public Loadee(int p) {
    fieldInt = p;
  }
  
  /***/public Loadee(boolean shouldCrash) {
    if (shouldCrash) {
      throw new NullPointerException();
    }
  }
  
  /***/public void meth() { /* Empty */ }
  
  /***/public void meth(int i) {
    throw new RuntimeException(Integer.toString(i));
  }
  
  /***/public int intMeth() {return 1;}
  
  /***/public static void staticMeth() { /* Empty */ }
  
  /***/public static void staticMeth(int i) {
    throw new NullPointerException(Integer.toString(i));
  }  
  
  
  /**
   * Inner class (non-static, by definition).
   * 
   * @author csallner@gatech.edu (Christoph Csallner)
   */
  public class Inner {
  	/***/public int fieldInnerInt = 0;
    
  	/***/public Inner() { /* Empty */ }
    
  	/***/public Inner(int p) {
      fieldInnerInt = p;
    }
    
  	/***/public int innerMeth() {return 1;}
  }
  
  
  /**
   * Static member class.
   * 
   * @author csallner@gatech.edu (Christoph Csallner)
   */  
  public static class StaticMember {
  	/***/public int fieldStaticMemberInt = 0;
    
  	/***/public StaticMember() { /* Empty */ }
    
  	/***/public StaticMember(int p) {
      fieldStaticMemberInt = p;
    }
    
  	/***/public int staticMemberMeth() {return 1;}
    
  	/***/public static int staticMemberStaticMeth() {return 1;}
  } 
}
