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


/** Abstract distribution function. */
public abstract class STDistributionFun extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public STDistributionFun() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 0) { // get a random number extracted from the distribution whose parameters have the default value
			stack.push(exec(getDefaultParams()));
			return;
		}
		if(curNumberOfParameters == 1) { // get a random number extracted from the distribution of given parameters
			Object param = stack.pop();
			if(!Tensor.isVector(param)) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
				return;
			}
			Object check = checkParams((Tensor)param);
			if(check != null) {
				stack.push(check);
				return;
			}
			stack.push(exec(getParams((Tensor)param)));
			return;
		}
		if(curNumberOfParameters == 2 || curNumberOfParameters == 3) { // get the pdf, the cdf, or the inverse cdf
			double[] data = null;
			Object v = null;
			int selector = 0;
			if(curNumberOfParameters == 2) { // with default parameters
				Object param2 = stack.pop();
				Object param1 = stack.pop();
				if(!Tensor.isScalar(param2)) {
					stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
					return;
				}
				selector = (int)((Tensor)param2).getValue();
				if(selector != 0 && selector != 1 && selector != 2) {
					stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
					return;
				}
				data = getDefaultParams();
				v = param1;
			} else { // with specified parameters
				Object param3 = stack.pop();
				Object param2 = stack.pop();
				Object param1 = stack.pop();
				if(!Tensor.isVector(param1) || !Tensor.isScalar(param3)) {
					stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
					return;
				}
				selector = (int)((Tensor)param3).getValue();
				if(selector != 0 && selector != 1 && selector != 2) {
					stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
					return;
				}
				Object check = checkParams((Tensor)param1);
				if(check != null) {
					stack.push(check);
					return;
				}
				data = getParams((Tensor)param1);
				v = param2;
			}
			if(Tensor.isScalar(v)) {
				double value = ((Tensor)v).getValue();
				if(selector == 0) { // pdf
					stack.push(exec0(value, data));
					return;
				}
				if(selector == 1) { // cdf
					stack.push(exec1(value, data));
					return;
				}
				if(selector == 2) { // inverse cdf
					if(value < 0 || value > 1) {
						stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
						return;
					}
					stack.push(exec2(value, data));
					return;
				}
			}
			Tensor value = (Tensor)v;
			int size = value.getSize();
			Tensor result = new Tensor(value.getDimensions());
			if(selector == 0) { // pdf
				for(int i = 0; i < size; i++) { result.setScalar(exec0(value.getScalar(i).getValue(), data), i); }
				stack.push(result);
				return;
			}
			if(selector == 1) { // cdf
				for(int i = 0; i < size; i++) { result.setScalar(exec1(value.getScalar(i).getValue(), data), i); }
				stack.push(result);
				return;
			}
			if(selector == 2) { // inverse cdf
				double t;
				for(int i = 0; i < size; i++) {
					t = value.getScalar(i).getValue();
					if(t < 0 || t > 1) {
						stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
						return;
					}
					result.setScalar(exec2(t, data) ,i);
				}
				stack.push(result);
				return;
			}
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
	}


	/** Abstract execution method.
	 * @param v the input vector
	 * @return output vector */
	abstract double[] getParams(Tensor v);


	/** Abstract execution method.
	 * @param v the input vector
	 * @return check
	 * @throws ParseException the parse exception */
	abstract Object checkParams(Tensor v) throws ParseException;


	/** Abstract execution method.
	 * @return default data */
	abstract double[] getDefaultParams();


	/** Abstract execution method for next value.
	 * @param v the vector
	 * @return object */
	abstract Object exec(double[] v);


	/** Abstract execution method for pdf.
	 * @param value the value
	 * @param data the data
	 * @return object */
	abstract Object exec0(double value, double[] data);


	/** Abstract execution method for cdf.
	 * @param value the value
	 * @param data the data
	 * @return object */
	abstract Object exec1(double value, double[] data);


	/** Abstract execution method for inverse cdf.
	 * @param value the value
	 * @param data the data
	 * @return object */
	abstract Object exec2(double value, double[] data);

}
