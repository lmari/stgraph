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


/** Size function. */
public class Size extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Size() { numberOfParameters = 1; }


	/** Run method.
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
		int o = t.getOrder();
		if(o == 0) {
			stack.push(new Tensor());
			return;
		}
		Tensor result = Tensor.newVector(o);
		if(t.getDimensions() == null) {
			for(int i = 0; i < o; i++) { result.setSubTensor(new Tensor(), i); }
		} else {
			int[] d = t.getDimensions();
			for(int i = 0; i < o; i++) { result.setSubTensor(Tensor.newScalar(d[i]), i); }
		}
		stack.push(result.dimensionMinimize());
	}

}
