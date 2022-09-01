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

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** CustomFun (meta)function. */
public class CustomFun extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public CustomFun() { numberOfParameters = -1; }


	/** Run method.
	 * <br>By locally saving the dummy vars <code>$a<i>i</i></code>
	 * before the actual evaluation and restoring them immediately before returning the result,
	 * it simulates a reentrant stack management, so to allow nested calls.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 0) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		STInterpreter interpreter = STGraph.getSTC().getCurrentlyComputedGraph().getInterpreter();
		interpreter.addVariable("$numArgs", Tensor.newScalar(curNumberOfParameters - 1)); //$NON-NLS-1$
		Object[] params = new Object[curNumberOfParameters];
		for(int i = curNumberOfParameters - 1; i >= 0; i--) { params[i] = stack.pop(); }
		STInterpreter.cnop.push(Tensor.newScalar(curNumberOfParameters - 1));
		STInterpreter.nde.push(Tensor.newScalar(STInterpreter.numDefExpr));
		STInterpreter.numDefExpr = 0;
		Vector stackImage = new Vector(); // the following call to getValueAsObject() clears the stack: therefore it must be saved... 
		while(!stack.empty()) { stackImage.add(stack.pop()); }
		Object[] o = new Object[STInterpreter.NUM_DUMMY_PARS];
		for(int i = 0; i < STInterpreter.NUM_DUMMY_PARS; i++) { o[i] = interpreter.getVarValue("$a" + i); } //$NON-NLS-1$
		for(int i = 1; i < curNumberOfParameters; i++) { interpreter.addVariable("$a" + (i - 1), params[i]); } //$NON-NLS-1$
		interpreter.topNode = (Node)params[0];
		Object result;
		try {
			result = interpreter.getValueAsObject();
		} catch (Exception e) {
			result = null;
		}
		if(interpreter.hasError()) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
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
