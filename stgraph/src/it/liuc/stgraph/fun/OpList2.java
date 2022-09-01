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
import java.util.Vector;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** List2 operator. */
public class OpList2 extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public OpList2() {
		numberOfParameters = -1;
		setName("[]"); //$NON-NLS-1$
	}


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void run(Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 2) {
			Object param2 = stack.pop();
			Object param1 = stack.pop();
			if(!(param1 instanceof Tensor) || !(param2 instanceof Tensor)) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			Vector result = new Vector();
			double v1 = ((Tensor)param1).getValue();
			double v2 = ((Tensor)param2).getValue();
			if(v1 <= v2) {
				while(v1 <= v2) {
					result.add(Tensor.newScalar(v1));
					v1 += 1.0;
				}
			} else {
				while(v1 >= v2) {
					result.add(Tensor.newScalar(v1));
					v1 -= 1.0;
				}
			}
			stack.push(Tensor.newVector(result));
			return;
		}
		if(curNumberOfParameters == 3) {
			Object param3 = stack.pop();
			Object param2 = stack.pop();
			Object param1 = stack.pop();
			if(!(param1 instanceof Tensor) || !(param2 instanceof Tensor) || !(param3 instanceof Tensor)) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			Vector result = new Vector();
			double v1 = ((Tensor)param1).getValue();
			double v2 = ((Tensor)param2).getValue();
			double v3 = ((Tensor)param3).getValue();
			if(v1 <= v3) {
				if(v2 <= 0) {
					stack.push(new Tensor(1));
					return;
				}
				while(v1 <= v3) {
					result.add(Tensor.newScalar(v1));
					v1 += v2;
				}
			} else {
				if(v2 >= 0) {
					stack.push(new Tensor(1));
					return;
				}
				while(v1 >= v3) {
					result.add(Tensor.newScalar(v1));
					v1 += v2;
				}
			}
			stack.push(Tensor.newVector(result));
			return;
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
	}

}
