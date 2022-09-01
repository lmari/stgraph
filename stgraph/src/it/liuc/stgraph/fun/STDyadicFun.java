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


/** Abstract dyadic operator / function.
 * <br>The currently implemented cases are:
 * <ul>
 * <li>f(x1,x2):
 *   <ul>
 *   <li>x1 or x2 is empty n>0-order (result: empty n-order)
 *   <li>x1 and x2 are scalars
 *   <li>x1 is scalar and x2 is n>0-order, or viceversa
 *   <li>x1 and x2 are n>0-order and have the same dimensions
 *   <li>x1 (n-1-order) can be "dimensionally composed" with x2 (n-order), or viceversa (the first n-1 dimensions must be equal)
 *   <li>***more cases could be added***
 *   </ul>
 * <li>f/(x), f|(x), f\(x):
 *   <ul>
 *   <li>x is empty n>0-order
 *   <li>x is scalar or with last dimension=1
 *   <li>x is n>0-order (general case)
 *   </ul>
 * </ul>
 */
public abstract class STDyadicFun extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public STDyadicFun() { numberOfParameters = -1; }


	/** Execution frame.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters != 2 && curNumberOfParameters != 3) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		if(curNumberOfParameters == 2) {
			Object param2 = stack.pop();
			Object param1 = stack.pop();
			if(param1 instanceof Tensor && param2 instanceof Tensor) {
				Tensor t1 = (Tensor)param1;
				int s1 = t1.getSize();
				if(s1 == -1) { // the first operand is empty: return it
					stack.push(t1);
					return;
				}
				Tensor t2 = (Tensor)param2;
				int s2 = t2.getSize();
				if(s2 == -1) { // the second operand is empty: return it
					stack.push(t2);
					return;
				}
				if(t1.getOrder() == 0) { // left operand is a scalar
					if(t2.getOrder() == 0) {
						stack.push(exec(t1.getScalar(0), t2.getScalar(0)));
						return;
					}
					Tensor result = new Tensor(t2.getDimensions());
					Tensor t = t1.getScalar(0);
					for(int i = 0; i < s2; i++) { result.setScalar(exec(t, t2.getScalar(i)), i); }
					stack.push(result);
					return;
				}
				if(t2.getOrder() == 0) { // right operand is a scalar
					Tensor result = new Tensor(t1.getDimensions());
					Tensor t = t2.getScalar(0);
					for(int i = 0; i < s1; i++) { result.setScalar(exec(t1.getScalar(i), t), i); }
					stack.push(result);
					return;
				}
				/*
				if(s1 <= 1) { // left operand is a scalar (or "equivalent")
					if(s2 <= 1) {
						stack.push(exec(t1.getScalar(0), t2.getScalar(0)));
						return;
					}
					Tensor result = new Tensor(t2.getDimensions());
					Tensor t = t1.getScalar(0);
					for(int i = 0; i < s2; i++) { result.setScalar(exec(t, t2.getScalar(i)), i); }
					stack.push(result);
					return;
				}
				if(s2 <= 1) { // right operand is a scalar (or "equivalent")
					Tensor result = new Tensor(t1.getDimensions());
					Tensor t = t2.getScalar(0);
					for(int i = 0; i < s1; i++) { result.setScalar(exec(t1.getScalar(i), t), i); }
					stack.push(result);
					return;
				}
				 */
				if(t1.hasSameDimensions(t2)) { // operands have the same dimensions
					Tensor result = new Tensor(t1.getDimensions());
					for(int i = 0; i < s1; i++) { result.setScalar(exec(t1.getScalar(i), t2.getScalar(i)), i); }
					stack.push(result);
					return;
				}
				int o1 = t1.getOrder();
				int o2 = t2.getOrder();
				if(o2 == o1 - 1) { // left operand (n-order) could be "dimensionally composed" with the right one (n-1-order) (the first n-1 dimensions must be equal)
					int[] d1 = t1.getDimensions();
					int[] d2 = t2.getDimensions();
					for(int i = 0; i < o2; i++) {
						if(d1[i] != d2[i]) {
							stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"), STData.VALUETYPE_MATRIX)); //$NON-NLS-1$
							return;
						}
					}
					Tensor result = new Tensor(d1);
					int d = t1.getLengthOfLastDimTensors();
					int p = t1.getNumberOfLastDimTensors();
					int k = 0;
					for(int i = 0; i < p; i++) {
						Tensor tt1 = t1.getLastDimTensor(i);
						Tensor tt2 = t2.getScalar(i);
						for(int j = 0; j < d; j++) { result.setScalar(exec(tt1.getScalar(j), tt2), k++); }
					}
					stack.push(result);
					return;
				}
				if(o1 == o2 - 1) { // left operand (n-1-order) could be "dimensionally composed" with the right one (n-order) (the first n-1 dimensions must be equal)
					int[] d1 = t1.getDimensions();
					int[] d2 = t2.getDimensions();
					for(int i = 0; i < o1; i++) {
						if(d1[i] != d2[i]) {
							stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"), STData.VALUETYPE_MATRIX)); //$NON-NLS-1$
							return;
						}
					}
					Tensor result = new Tensor(d2);
					int d = t2.getLengthOfLastDimTensors();
					int p = t2.getNumberOfLastDimTensors();
					int k = 0;
					for(int i = 0; i < p; i++) {
						Tensor tt2 = t2.getLastDimTensor(i);
						Tensor tt1 = t1.getScalar(i);
						for(int j = 0; j < d; j++) { result.setScalar(exec(tt1, tt2.getScalar(j)), k++); }
					}
					stack.push(result);
					return;
				}
				// ***more cases should be added***
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_SIZES"), STData.VALUETYPE_MATRIX)); //$NON-NLS-1$
				return;
			}
			if(param1 instanceof String && param2 instanceof Tensor) {
				String selector = (String)param1;
				Tensor input = (Tensor)param2;
				int size = input.getSize();
				int order = Math.max(input.getOrder(), 1); // to deal scalars as [1]vectors
				if(size < 0) { // if it is an empty n-order array...
					if(selector.equals("/")) { // if reduction... //$NON-NLS-1$
						stack.push(new Tensor(order - 1)); // ... return an empty (n-1)-order array (0.0 is the empty scalar)
					} else { // if pairscan or scan...
						stack.push(new Tensor(order)); // ... return an empty n-order array
					}
					return;
				}
				int[] dimensions = input.getDimensions();
				if(size == 0 || (dimensions[order - 1] == 1)) { // if it is a scalar or the last dimension has just one element...
					if(selector.equals("/")) { // if reduction... //$NON-NLS-1$
						stack.push(new Tensor(order - 1)); // ... return an empty (n-1)-order array
					} else if(selector.equals("|")) { // if pairscan... //$NON-NLS-1$
						stack.push(new Tensor(order)); // ... return an empty n-order array
					} else { // if scan...
						if(size == 0) { // ... return the input array
							stack.push(new Tensor(new double[] {input.getValue()}, new int[] {1}));
						} else {
							stack.push(new Tensor(input));
						}
					}
					return;
				}
				int d = input.getLengthOfLastDimTensors();
				int p = input.getNumberOfLastDimTensors();
				Tensor result = null;
				if(selector.equals("/")) { //$NON-NLS-1$
					int[] newDimensions = new int[order - 1];
					for(int i = 0; i < order - 1; i++) { newDimensions[i] = dimensions[i]; }
					result = new Tensor(newDimensions);
					for(int i = 0; i < p; i++) {
						Tensor data = input.getLastDimTensor(i);
						Tensor t = data.getScalar(0);
						for(int j = 1; j < d; j++) { t = (Tensor)exec(t, data.getScalar(j)); }
						result.setScalar(t, i);
					}
					stack.push(result);
					return;
				}
				if(selector.equals("|")) { //$NON-NLS-1$
					int[] newDimensions = new int[order];
					for(int i = 0; i < order - 1; i++) { newDimensions[i] = dimensions[i]; }
					newDimensions[order - 1] = dimensions[order - 1] - 1;
					result = new Tensor(newDimensions);
					int k = 0;
					for(int i = 0; i < p; i++) {
						Tensor data = input.getLastDimTensor(i);
						for(int j = 0; j < d - 1; j++) { result.setScalar(exec(data.getScalar(j), data.getScalar(j + 1)), k++); }
					}
					stack.push(result);
					return;
				}
				if(selector.equals("\\")) { //$NON-NLS-1$
					result = new Tensor(dimensions);
					for(int i = 0; i < p; i++) {
						Tensor data = input.getLastDimTensor(i);
						Tensor t = data.getScalar(0);
						result.setScalar(t, i * d);
						for(int j = 1; j < d; j++) { result.setScalar(t = (Tensor)exec(t, data.getScalar(j)), i * d + j); }
					}
					stack.push(result);
					return;
				}
			}
		} else { // three parameters, only in the case of indexed operators
			Object param3 = stack.pop();
			Object param2 = stack.pop();
			Object param1 = stack.pop();
			if(param1 instanceof String && param2 instanceof Tensor && param3 instanceof Tensor) {
				String selector = (String)param1;
				int dim = (int)((Tensor)param2).getValue();
				Tensor input = (Tensor)param3;
				int size = input.getSize();
				int order = Math.max(input.getOrder(), 1); // to deal scalars as [1]vectors
				if(dim < 0 || dim >= order) {
					stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
					return;
				}				
				if(size < 0) { // if it is an empty n-order array...
					if(selector.equals("/")) { // if reduction... //$NON-NLS-1$
						stack.push(new Tensor(order - 1)); // ... return an empty (n-1)-order array (0.0 is the empty scalar)
					} else { // if pairscan or scan...
						stack.push(new Tensor(order)); // ... return an empty n-order array
					}
					return;
				}
				int[] dimensions = input.getDimensions();
				if(size == 0 || (dimensions[dim] == 1)) { // if it is a scalar or the given dimension has just one element...
					if(selector.equals("/")) { // if reduction... //$NON-NLS-1$
						stack.push(new Tensor(order - 1)); // ... return an empty (n-1)-order array
					} else if(selector.equals("|")) { // if pairscan... //$NON-NLS-1$
						stack.push(new Tensor(order)); // ... return an empty n-order array
					} else { // if scan...
						if(size == 0) { // ... return the input array
							stack.push(new Tensor(new double[] {input.getValue()}, new int[] {1}));
						} else {
							stack.push(new Tensor(input));
						}
					}
					return;
				}
				int d = input.getLengthOfGivenDimTensors(dim);
				int p = input.getNumberOfGivenDimTensors(dim);
				Tensor result = null;
				if(selector.equals("/")) { //$NON-NLS-1$
					int[] newDimensions = new int[order - 1];
					int k = 0;
					for(int i = 0; i < order; i++) {
						if(i != dim) { newDimensions[k++] = dimensions[i]; }
					}
					result = new Tensor(newDimensions);
					for(int i = 0; i < p; i++) {
						Tensor data = input.getGivenDimTensor(i, dim);
						Tensor t = data.getScalar(0);
						for(int j = 1; j < d; j++) { t = (Tensor)exec(t, data.getScalar(j)); }
						result.setScalar(t, i);
					}
					stack.push(result);
					return;
				}
				if(selector.equals("|")) { //$NON-NLS-1$
					int[] newDimensions = new int[order];
					for(int i = 0; i < order - 1; i++) { newDimensions[i] = dimensions[i]; }
					newDimensions[order - 1] = dimensions[order - 1] - 1;
					result = new Tensor(newDimensions);
					int k = 0;
					for(int i = 0; i < p; i++) {
						Tensor data = input.getGivenDimTensor(i, dim);
						for(int j = 0; j < d - 1; j++) { result.setScalar(exec(data.getScalar(j), data.getScalar(j + 1)), k++); }
					}
					stack.push(result);
					return;
				}
				if(selector.equals("\\")) { //$NON-NLS-1$
					result = new Tensor(dimensions);
					for(int i = 0; i < p; i++) {
						Tensor data = input.getGivenDimTensor(i, dim);
						Tensor t = data.getScalar(0);
						result.setScalar(t, i * d);
						for(int j = 1; j < d; j++) { result.setScalar(t = (Tensor)exec(t, data.getScalar(j)), i * d + j); }
					}
					stack.push(result);
					return;
				}
			}
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
		return;
	}


	/** Abstract execution method.
	 * @param d1 the d1
	 * @param d2 the d2
	 * @return object
	 * @throws ParseException the parse exception */
	abstract Object exec(Tensor d1, Tensor d2) throws ParseException;

}
