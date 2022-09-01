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


/** Dec vector function. */
public class Dec extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Dec() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 2) {
			Tensor param1 = null;
			Tensor param2 = null;
			try {
				param2 = (Tensor)stack.pop();
				param1 = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			Object result = null;
			if(param1.getOrder() == 0 && param2.getOrder() > 0) {
				result = param2.decatenate((int)param1.getValue(), true);
			} else if(param2.getOrder() == 0 && param1.getOrder() > 0) {
				result = param1.decatenate((int)param2.getValue(), false);
			}
			if(result == null || !(result instanceof Tensor)) { stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"))); return; } //$NON-NLS-1$
			stack.push(result);
			return;
		}
		if(curNumberOfParameters == 3) {
			Tensor param1 = null;
			Tensor param2 = null;
			Tensor param3 = null;
			try {
				param3 = (Tensor)stack.pop();
				param2 = (Tensor)stack.pop();
				param1 = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			Object result = null;
			if(param1.getOrder() == 0 && param2.getOrder() > 0) {
				result = param2.decatenate((int)param1.getValue(), true, (int)param3.getValue());
			} else if(param2.getOrder() == 0 && param1.getOrder() > 0) {
				result = param1.decatenate((int)param2.getValue(), false, (int)param3.getValue());
			}
			if(result == null || !(result instanceof Tensor)) { stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"))); return; } //$NON-NLS-1$
			stack.push(result);
			return;
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
	}

}
