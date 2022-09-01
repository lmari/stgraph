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

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Shift function. */
public class Shift extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Shift() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters != 2 && curNumberOfParameters != 3) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"))); //$NON-NLS-1$
			return;
		}
		Tensor data = null;
		Tensor newValue = null;
		Tensor selector = null;
		boolean x = false;
		if(curNumberOfParameters == 2) {
			try {
				newValue = (Tensor)stack.pop();
				data = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
		} else {
			try {
				selector = (Tensor)stack.pop();
				newValue = (Tensor)stack.pop();
				data = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			if(selector.getOrder() != 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			int t = (int)selector.getValue();
			if(t != 0 && t != 1) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"))); //$NON-NLS-1$
				return;
			}
			x = (t == 1);
		}		
		if(data.getOrder() == 1 + newValue.getOrder()) {
			Object result = x ? data.concatenate(newValue) : newValue.concatenate(data);
			if(!(result instanceof Tensor)) { stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"))); return; } //$NON-NLS-1$
			result = ((Tensor)result).decatenate(1, x);
			if(result == null || !(result instanceof Tensor)) { stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"))); return; } //$NON-NLS-1$
			stack.push(result);
			return;
		}
		if(data.getOrder() == newValue.getOrder()) {
			Object result = x ? data.concatenate(newValue) : newValue.concatenate(data);
			if(!(result instanceof Tensor)) { stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"))); return; } //$NON-NLS-1$
			result = ((Tensor)result).decatenate(newValue.getLengthOfLastDimTensors(), x);
			if(result == null || !(result instanceof Tensor)) { stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"))); return; } //$NON-NLS-1$
			stack.push(result);
			return;
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"))); //$NON-NLS-1$
	}

}
