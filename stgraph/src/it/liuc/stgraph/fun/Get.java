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

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Get function.
 * <br>It gives the execution method for the get(data,index1,index2,...) function,
 * equivalent to the data[index1,index2,...] operator.
 * <br>Data must be non-scalar and all indexes must be scalars or vectors.
 * <br>The dimension of the returned tensor is minimized. */
public class Get extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Get() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters < 2) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"))); //$NON-NLS-1$
			return;
		}
		Object[] params = new Object[curNumberOfParameters];
		Tensor[] tensors = new Tensor[curNumberOfParameters];
		int[] orders = new int[curNumberOfParameters];
		int[] sizes = new int[curNumberOfParameters];
		for(int i = curNumberOfParameters - 1; i >= 0; --i) {
			params[i] = stack.pop();
			if(!(params[i] instanceof Tensor)) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			tensors[i] = (Tensor)params[i];
			orders[i] = tensors[i].getOrder();
			sizes[i] = tensors[i].getSize();
			if(i != 0 && orders[i] > 1) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
		}
		if(orders[0] == 0) { // scalar data --> err
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		if(tensors[0].getOrder() < curNumberOfParameters - 1) { // too many indexes --> err
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"))); //$NON-NLS-1$
			return;
		}
		if(tensors[0].getSize() == -1) { // empty array --> err
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_NULL_ARRAY"))); //$NON-NLS-1$
			return;
		}
		int io = STGraph.getSTC().getCurrentlyComputedGraph().getIO();
		Tensor result = new Tensor(tensors[0]);
		for(int i = 1; i < curNumberOfParameters; i++) {
			if(sizes[i] != -1) {
				double[] v = tensors[i].getValues();
				int[] p = new int[v.length];
				for(int j = 0; j < v.length; j++) { p[j] = (int)v[j] - io; }
				result = result.getSubTensorByMajorDim(p);
			}
			if(result.isEmpty()) {
				stack.push(result);
				return;
			}
			result = result.transpose(false);
		}
		for(int i = curNumberOfParameters - 1; i < tensors[0].getOrder(); i++) { result = result.transpose(false); }
		stack.push(result.dimensionMinimize());
		return;
	}

}
