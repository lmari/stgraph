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

import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Shuffle vector function. */
public class Shuffle extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Shuffle() { numberOfParameters = 1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Tensor param = null;
		try {
			param = (Tensor)stack.pop();
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		if(param.getSize() <= 0) {
			stack.push(param);
			return;
		}
		Tensor result = new Tensor(param);
		for(int i = 0; i < result.getNumberOfLastDimTensors(); i++) {
			List list= result.getLastDimTensor(i).getValuesAsList();
			java.util.Collections.shuffle(list);
			result.setLastDimTensor(list, i);
		}
		stack.push(result);
		return;
	}

}
