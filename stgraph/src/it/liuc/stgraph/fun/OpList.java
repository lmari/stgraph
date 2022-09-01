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


/** List operator. */
public class OpList extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public OpList() {
		numberOfParameters = -1;
		setName("[]"); //$NON-NLS-1$
	}


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void run(Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 0) {
			stack.push(Tensor.newVector());
			return;
		}
		Object param = stack.pop(); //read the last parameter, to use it to infer the tensor order and size
		if(!(param instanceof Tensor)) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
			return;
		}
		Tensor t = (Tensor)param;
		if(t.getOrder() == 0) { //build a vector from scalar(s)
			Tensor result = Tensor.newVector(curNumberOfParameters);
			result.setScalar(t, curNumberOfParameters - 1);
			for(int i = curNumberOfParameters - 2; i >= 0; --i) { result.setScalar(stack.pop(), i); }
			stack.push(result);
			return;
		}
		int o = t.getOrder() + 1;
		if(t.getSize() <= 0) {
			stack.push(new Tensor(o));
			return;
		}
		/*
		int[] d = new int[o];
		d[0] = curNumberOfParameters;
		for(int i = 1; i < o; i++) { d[i] = t.getDimensions()[i - 1]; }
		Tensor result = new Tensor(d);
		result.setSubTensor(t, curNumberOfParameters - 1);
		for(int i = curNumberOfParameters - 2; i >= 0; --i) { result.setSubTensor((Tensor)stack.pop(), i); }
		 */
		//modified, so to maintain all vector values in matrix building with vectors of different sizes
		//note that it does not work for higher order arrays (see Tensor.setSubTensor())
		try {
			Tensor[] x = new Tensor[curNumberOfParameters];
			x[curNumberOfParameters - 1] = t;
			for(int i = curNumberOfParameters - 2; i >= 0; --i) { x[i] = (Tensor)stack.pop(); }
			int[] d = new int[o];
			d[0] = curNumberOfParameters;
			int k;
			for(int i = 1; i < o; i++) {
				d[i] = t.getDimensions()[i - 1];
				for(int j = 0; j < curNumberOfParameters - 1; j++) {
					if((k = x[j].getDimensions()[i - 1]) > d[i]) { d[i] = k; }
				}
			}
			Tensor result = new Tensor(d);
			for(int i = 0; i < curNumberOfParameters; i++) { result.setSubTensor(x[i], i); }
			//end modified
			stack.push(result);
			return;
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_ARRAY_DIMENSION"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
			return;
		}
	}

}
