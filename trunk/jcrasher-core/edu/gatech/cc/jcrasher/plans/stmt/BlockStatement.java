/*
 * BlockStatement.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.stmt;

import edu.gatech.cc.jcrasher.plans.JavaCode;

/**
 * A statement, sequence of statements, or block.
 * A block is its own lexical scope and has its own brackets.
 * Statements themselves do not.
 * 
 * @param <T> type of the value returned by the statement.
 * This is typically the return type of the last expression.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.2
 */
public interface BlockStatement<T> extends JavaCode<T> {	
  //empty
}
