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


/** Log function. */
public class Log extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Log() { numberOfParameters = -1; } // variable: 1, log(p1), or 2, log_base_p2(p1)


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 1) {
			Tensor param = null;
			try {
				param = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STFunction.getType(param))); //$NON-NLS-1$
				return;
			}
			stack.push(log(param));
			return;
		}
		if(curNumberOfParameters == 2) {
			Tensor param1 = null;
			Tensor param2 = null;
			try {
				param2 = (Tensor)stack.pop();
				param1 = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STFunction.getType(param1))); //$NON-NLS-1$
				return;
			}
			if(param2.getOrder() != 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STFunction.getType(param1))); //$NON-NLS-1$
				return;
			}
			stack.push(divide(log(param1), log(param2)));
			return;
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
	}


	/** Utility method.
	 * @param param the param
	 * @return value
	 * @throws ParseException the parse exception */
	private Object log(final Tensor param) throws ParseException {
		if(param.getOrder() == 0) {
			double d = param.getValue();
			if(d <= 0.0) { return STInterpreter.handleException(getException("ERR.FUN.NEGATIVE_VALUE_NOTALLOWED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
			return Tensor.newScalar(Math.log(d));
		}
		int s = param.getSize();
		if(s == -1) { return new Tensor(param.getOrder()); }
		Tensor result = new Tensor(param);
		for(int i = 0; i < s; i++) { result.setScalar(Tensor.newScalar(Math.log(param.getScalar(i).getValue())), i); }
		return result;
	}


	/** Utility method.
	 * @param param1 the param1
	 * @param param2 the param2
	 * @return value
	 * @throws ParseException the parse exception */
	private Object divide(final Object param1, final Object param2) throws ParseException {
		if(!(param1 instanceof Tensor) || !(param2 instanceof Tensor)) {
			return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STFunction.getType(param1)); //$NON-NLS-1$
		}
		Tensor t = (Tensor)param1;
		double d = ((Tensor)param2).getValue();
		if(t.getOrder() == 0) { return Tensor.newScalar(t.getValue() / d); }
		int s = t.getSize();
		if(s == -1) { return new Tensor(t.getOrder()); }
		Tensor result = new Tensor(t);
		for(int i = 0; i < s; i++) { result.setScalar(Tensor.newScalar(t.getScalar(i).getValue() / d), i); }
		return result;
	}

}
