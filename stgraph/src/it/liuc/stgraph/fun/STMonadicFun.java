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


import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Abstract monadic operator / function.
 * @version tensor */
public abstract class STMonadicFun extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public STMonadicFun() { numberOfParameters = 1; }


	/** Execution frame.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Object param = stack.pop();
		if(!(param instanceof Tensor)) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		Tensor t = (Tensor)param;
		int s = t.getSize();
		if(s == -1) {
			stack.push(t);
			return;
		}
		if(s == 0) {
			stack.push(exec(t));
			return;
		}
		Tensor result = new Tensor(t);
		for(int i = 0; i < s; i++) { result.setScalar(exec(t.getScalar(i)), i); }
		stack.push(result);
	}


	/** Abstract execution method.
	 * @param d the d
	 * @return object
	 * @throws ParseException the parse exception */
	abstract Object exec(Tensor d) throws ParseException;

}
