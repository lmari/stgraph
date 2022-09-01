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

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** DefExpr operator. */
public class OpDefExpr extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public OpDefExpr() {
		numberOfParameters = 1;
		setName("{}"); //$NON-NLS-1$
	}


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void run(Stack stack) throws ParseException {
		checkStack(stack);
		Tensor expr = null; // bins
		try {
			expr = (Tensor)stack.pop();
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
		STInterpreter interpreter = graph.getInterpreter();
		interpreter.addVariable("$w" + STInterpreter.numDefExpr++, expr); //$NON-NLS-1$
		stack.push(expr);
	}

}
