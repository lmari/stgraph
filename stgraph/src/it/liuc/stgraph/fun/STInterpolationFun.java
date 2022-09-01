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


/** Abstract interpolation function. */
public abstract class STInterpolationFun extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public STInterpolationFun() { numberOfParameters = 3; }


	/** Execution frame.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Object param3 = stack.pop();
		Object param2 = stack.pop();
		Object param1 = stack.pop();
		if(!(param1 instanceof Tensor) || !(param2 instanceof Tensor) || !(param3 instanceof Tensor)) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		Tensor nx = (Tensor)param1;
		Tensor ny = (Tensor)param2;
		Tensor xx = (Tensor)param3;
		if(nx.getOrder() != 1 || ny.getOrder() != 1) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		Object r;
		if((r = init(nx, ny)) != null) {
			stack.push(r);
			return;
		}
		Tensor result = xx.clone();
		if(xx.getSize() == 0) {
			result.setScalar(exec(xx.getScalar(0).getValue()), 0);
		} else {
			for(int i = 0; i < xx.getSize(); i++) { result.setScalar(exec(xx.getScalar(i).getValue()), i); }
		}
		stack.push(result);
	}


	/** Abstract initialization method.
	 * @param nx the nx
	 * @param ny the ny
	 * @return result non-null if an exception is thrown
	 * @throws ParseException the parse exception */
	abstract Object init(final Tensor nx, final Tensor ny) throws ParseException;


	/** Abstract execution method.
	 * @param x the x
	 * @return result
	 * @throws ParseException the parse exception */
	abstract Object exec(final double x) throws ParseException;

}
