/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2018, Luca Mari.
 * 
 * STGraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 * 
 * STGraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with STGraph.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.liuc.stgraph.fun;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.util.TokenAwareEditor;

import java.io.Serializable;
import java.util.Stack;

import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;


/** Wrapper for function definition. */
public abstract class STFunction extends PostfixMathCommand implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The name. */
	private String name;


	/** Class constructor. */
	public STFunction() { ; }


	/** Check whether the stack is not null, and throw a ParseException if it is.
	 * @param stack the stack */
	@SuppressWarnings("rawtypes")
	@Override
	protected void checkStack(Stack stack) throws ParseException {
		if(null == stack) { throw new ParseException(getException("ERR.FUN.EMPTY_STACK")); } //$NON-NLS-1$
		STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
		if(graph != null && graph.isTracing() && graph.isStepped()) { STTools.tracingDialog.appendData("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + name + STTools.COLON + "&nbsp;" + stack + "&nbsp;&nbsp;", true); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if(TokenAwareEditor.isTraced() && TokenAwareEditor.isUnderEvaluation()) { TokenAwareEditor.appendTraceText(name + STTools.COLON + "&nbsp;" + stack + "<br>"); } //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Check the stack (for functions implementing CallbackEvaluationI).
	 * @param node the node */
	protected void checkStack(final Node node) {
		if(TokenAwareEditor.isTraced() && TokenAwareEditor.isUnderEvaluation()) { TokenAwareEditor.appendTraceText(name + STTools.COLON + "&nbsp;" + ((ASTFunNode)node).getChildrenAsString() + "<br>"); } //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Set the function name, by which this function is exposed to the interpreter.
	 * <br>(The registration is performed by calling the <code>addFunction</code> JEP method
	 * in the constructor <code>STInterpreter</code>)
	 * @param name the name */
	public final void setName(final String name) { this.name = name; }


	/** Get the function name, by which this function is exposed to the interpreter.
	 * @return name */
	public final String getName() { return name; }


	/** Return the constant specifying the type of the object.
	 * @param object the object
	 * @return type */
	public static int getType(final Object object) { return STData.getType(object); }


	/** Get the string describing the exception. 
	 * @param m the message
	 * @return the string */
	final String getException(final String m) { return getName() + STTools.COLON + STTools.SPACE + STInterpreter.getMsg(m); }


	/** Print out the function name.
	 * @return string */
	public final String toString() { return name; }

}
