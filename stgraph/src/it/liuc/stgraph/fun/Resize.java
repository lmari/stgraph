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


/** Resize tensor function. */
public class Resize extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Resize() { numberOfParameters = 2; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Tensor data = null;
		Tensor dims = null;
		try {
			dims = (Tensor)stack.pop();
			data = (Tensor)stack.pop();
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		if(data.getSize() == -1) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		if(dims.getOrder() == 0) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		int size = dims.getSize();
		if(size == -1) {
			stack.push(data.getScalar(0));
			return;
		}
		int[] intDims = new int[size];
		for(int i = 0; i < size; i++) { intDims[i] = (int)dims.getValues()[i]; }
		stack.push(data.resize(intDims, false));
		return;
	}

}
