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


/** GetIndex function. */
public class GetIndex extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public GetIndex() { numberOfParameters = 2; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Tensor param1 = null;
		Tensor param2 = null;
		try {
			param2 = (Tensor)stack.pop();
			param1 = (Tensor)stack.pop();
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
			return;
		}
		if(param1.isNotScalar()) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
			return;
		}
		Tensor result;
		double d = param1.getValue();
		if(param2.isScalar()) { // handle the peculiar case in which the array to look for is indeed a scalar
			double d2 = param2.getValue();
			result = Tensor.newVector(1);
			result.setScalar(d == d2 ? 0 : -1, 0);
			stack.push(result);
			return;
		}
		if(param2.getSize() == -1) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
			return;
		}
		int o = param2.getOrder();
		result = o == 1 ? Tensor.newVector() : new Tensor(new int[] {o, 0});
		int[][] indexes = Tensor.getIndexes(param2.getDimensions(), STGraph.getSTC().getCurrentlyComputedGraph().getIO());
		double[] v = param2.getValues();
		Tensor t;
		for(int i = 0; i < v.length; i++) {
			if(v[i] == d) {
				if(o == 1) {
					t = Tensor.newScalar(i);
				} else {
					t = Tensor.newVector(o);
					for(int j = 0; j < o; j++) { t.setScalar(indexes[j][i], j); }
				}
				result = (Tensor)result.concatenate(t);
			}
		}
		if(result.getSize() != -1) {
			stack.push(result);
			return;
		}
		result = Tensor.newVector(1);
		result.setScalar(-1, 0);
		stack.push(result);
	}

}
