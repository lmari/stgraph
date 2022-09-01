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

import it.liuc.stgraph.STInterpreter;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** CustomFun alternate (meta)function.
 * <br>Use it by calling the constructor, e.g., as:
 * <br><code>STFunction f2 = new it.liuc.stgraph.fun.CustomFun2(this, "samplefun", "$a0*$a1");</code>
 * <br>typically in the <code>STInterpreter</code> constructor, so to allow it to add then <code>f2</code>
 * to the list of defined functions. 
 * <br>Afterwards, an expression such as <code>samplefun(2,3)</code> becomes usable in a node definition. */
public class CustomFun2 extends STFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private STInterpreter interpreter;
	private ArrayList<Node> node;
	private String expr;


	/** Class constructor.
	 * @param interpreter the interpreter
	 * @param name the name
	 * @param expr the expression */
	public CustomFun2(final STInterpreter interpreter, final String name, final String expr) {
		numberOfParameters = -1;
		this.interpreter = interpreter;
		setName(name);
		this.expr = expr;
	}


	public void preParse() { node = interpreter.preParseExpression(expr); }


	/** Run method.
	 * <br>This code is slightly modified from the one of <code>CustomFun</code>.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		STInterpreter interpreter = STGraph.getSTC().getCurrentlyComputedGraph().getInterpreter();
		interpreter.addVariable("$numArgs", Tensor.newScalar(curNumberOfParameters)); //$NON-NLS-1$
		Object[] params = new Object[curNumberOfParameters];
		for(int i = curNumberOfParameters - 1; i >= 0; i--) { params[i] = stack.pop(); }
		STInterpreter.cnop.push(Tensor.newScalar(curNumberOfParameters));
		STInterpreter.nde.push(Tensor.newScalar(STInterpreter.numDefExpr));
		STInterpreter.numDefExpr = 0;
		Vector stackImage = new Vector(); // the following call to getValueAsObject() clears the stack: therefore it must be saved... 
		while(!stack.empty()) { stackImage.add(stack.pop()); }
		Object[] o = new Object[STInterpreter.NUM_DUMMY_PARS];
		for(int i = 0; i < STInterpreter.NUM_DUMMY_PARS; i++) { o[i] = interpreter.getVarValue("$a" + i); } //$NON-NLS-1$
		for(int i = 0; i < curNumberOfParameters; i++) { interpreter.addVariable("$a" + i, params[i]); } //$NON-NLS-1$
		for(int i = 0; i < node.size() - 1; i++) { //this allows local variables in function definition
			interpreter.topNode = node.get(i);
			interpreter.getSymTab().makeVarIfNeeded("$v" + i, interpreter.getValueAsObject()); //$NON-NLS-1$
		}
		interpreter.topNode = node.get(node.size() - 1);
		Object result = interpreter.getValueAsObject();
		if(interpreter.hasError()) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		if(result instanceof String) {
			stack.push(STInterpreter.handleLocalizableException(getName(), result.toString()));
			return;			
		}
		for(int i = stackImage.size() - 1; i >= 0; i--) { stack.push(stackImage.get(i)); } // ... and then restored
		for(int i = 0; i < STInterpreter.NUM_DUMMY_PARS; i++) { interpreter.addVariable("$a" + i, o[i]); } //$NON-NLS-1$
		STInterpreter.cnop.pop();
		if(!STInterpreter.cnop.isEmpty()) { interpreter.addVariable("$numArgs", STInterpreter.cnop.lastElement()); } //$NON-NLS-1$
		if(!STInterpreter.nde.isEmpty()) { STInterpreter.numDefExpr = (int)STInterpreter.nde.lastElement().getValue(); }
		STInterpreter.nde.pop();
		stack.push(result);
	}

}
