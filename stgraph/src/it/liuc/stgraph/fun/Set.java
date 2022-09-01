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


/** Set function.
 * <br>It gives the execution method for the set(data,index1,index2,...,value) function,
 * equivalent to the data[index1,index2,...,value] operator.
 * <br>Data must be non-scalar and all indexes must be scalars or vectors. */
public class Set extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Set() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters < 3) {
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
			if(orders[i] == 0) {
				double t = tensors[i].getValue(); 
				tensors[i] = Tensor.newVector(1);
				tensors[i].setScalar(t, 0);
				orders[i] = 1;
				sizes[i] = 1;
			}
			if(i != 0 && i != curNumberOfParameters - 1 && orders[i] > 1) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
		}
		if(orders[0] == 0) { // scalar data --> err
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		if(tensors[0].getOrder() < curNumberOfParameters - 2) { // too many indexes --> err
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"))); //$NON-NLS-1$
			return;
		}
		int io = STGraph.getSTC().getCurrentlyComputedGraph().getIO();
		Tensor indexes = new Tensor(tensors[0].getDimensions());
		double[] v = indexes.getValues();
		for(int i = 0; i < indexes.getSize(); i++) { v[i] = i; }
		for(int i = 1; i < curNumberOfParameters - 1; i++) {
			if(sizes[i] != -1) {
				v = tensors[i].getValues();
				int[] p = new int[v.length];
				for(int j = 0; j < v.length; j++) { p[j] = (int)v[j] - io; }
				indexes = indexes.getSubTensorByMajorDim(p);
			}
			if(indexes.getOrder() != 1) { indexes = indexes.transpose(false); }
		}
		for(int i = curNumberOfParameters - 2; i < tensors[0].getOrder(); i++) { indexes = indexes.transpose(false); }
		if(indexes.getSize() > 1) { indexes = indexes.dimensionMinimize(); }
		Tensor values = tensors[curNumberOfParameters - 1];
		if(values.getSize() == 1) {
			Tensor result = new Tensor(tensors[0]);
			v = indexes.getValues();
			for(int i = 0; i < indexes.getSize(); i++) { result.setScalar(values.getValue(), (int)v[i]); }
			stack.push(result);
			return;
		}
		if(!indexes.hasSameDimensions(values)) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		Tensor result = new Tensor(tensors[0]);
		v = indexes.getValues();
		for(int i = 0; i < indexes.getSize(); i++) { result.setScalar(values.getScalar(i).getValue(), (int)v[i]); }
		stack.push(result);
		return;
	}

}
