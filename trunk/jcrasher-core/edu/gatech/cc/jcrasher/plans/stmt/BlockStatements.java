/*
 * BlockStatements.java
 * 
 * Copyright 2007 Christoph Csallner and Yannis Smaragdakis.
 */
package edu.gatech.cc.jcrasher.plans.stmt;


/**
 * Possibly empty sequence of block statements.
 * 
 * @author csallner@gatech.edu (Christoph Csallner)
 * http://java.sun.com/docs/books/jls/third_edition/html/statements.html#14.2
 */
public interface BlockStatements extends BlockStatement {
	
	/**
	 * Append a block statement.
	 */
	void append(BlockStatement blockStatement);
}
