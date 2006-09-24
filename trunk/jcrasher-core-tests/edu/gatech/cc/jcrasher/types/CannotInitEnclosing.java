package edu.gatech.cc.jcrasher.types;

public class CannotInitEnclosing {
  static int getI() {return getI(); }
  static int i = getI();  //throws Error!
  
  class Inner {
    
  }
}
