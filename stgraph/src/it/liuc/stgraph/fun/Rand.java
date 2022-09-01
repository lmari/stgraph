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


/** Random function. */
public class Rand extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Rand() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 0) {
			stack.push(Tensor.newScalar(Math.random()));
			return;
		}
		if(curNumberOfParameters == 1) {
			Tensor param = null;
			try {
				param = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
				return;
			}
			if(param.getOrder() != 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
				return;
			}
			double d = param.getValue();
			if(d <= 0.0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.NEGATIVE_VALUE_NOTALLOWED"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
				return;
			}
			stack.push(Tensor.newScalar(d * Math.random()));
			return;
		}
		if(curNumberOfParameters == 2) {
			Tensor param1 = null;
			Tensor param2 = null;
			try {
				param2 = (Tensor)stack.pop();
				param1 = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
				return;
			}
			if(param1.getOrder() != 0 || param2.getOrder() != 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
				return;
			}
			double d1 = param1.getValue();
			double d2 = param2.getValue();
			if(d1 >= d2) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
				return;
			}
			stack.push(Tensor.newScalar(d1 + (d2 - d1) * Math.random()));
			return;
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
	}

}
