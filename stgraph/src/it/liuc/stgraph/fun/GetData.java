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
import it.liuc.stgraph.node.STData;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** GetData function. */
public class GetData extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public GetData() { numberOfParameters = 2; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Tensor param1 = null;
		Tensor param2 = null;
		try {
			param2 = (Tensor)stack.pop();
			param1 = (Tensor)stack.pop();
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		if((param1.isVector() && !param2.isVector()) || (param1.getOrder() == 2 && param1.getDimensions()[0] != param2.getOrder())) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		if(param1.getSize() == -1 || param2.getSize() == -1) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		Tensor result = Tensor.newVector();		
		if(param2.isVector()) {
			for(int i : param1.getValuesAsInt()) {
				result = (Tensor)result.concatenate(param2.getSubTensor(i - STGraph.getSTC().getCurrentlyComputedGraph().getIO()));
			}
		} else {
			for(int i = 0; i < param1.getDimensions()[1]; i++) {
				Tensor v = param1.getColumn(i);
				int s = v.getSize();
				int[] j = new int[s];
				for(int k = 0; k < s; k++) {
					j[k] = (int)(v.getScalar(k).getValue()) - STGraph.getSTC().getCurrentlyComputedGraph().getIO();
				}
				Tensor t2 = param2.getSubTensor(j);
				result = (Tensor)result.concatenate(t2);
			}
		}
		if(result.getSize() != -1) {
			stack.push(result);
			return;
		}
		stack.push(Tensor.newScalar(-1));
	}

}
