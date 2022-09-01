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

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.type.Tensor;


/** If function, with lazy evaluation. */
public class If extends STFunction implements CallbackEvaluationI {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public If() { numberOfParameters = -1; }


	/** Run method, based on lazy evaluation, so to allow even partial evaluation of its arguments.
	 * @param node the node
	 * @param pv the pv
	 * @return the object
	 * @throws ParseException the parse exception */
	public final Object evaluate(final Node node, final EvaluatorI pv) throws ParseException {
		checkStack(node);
		int numParams = node.jjtGetNumChildren();
		if(numParams == 1 || (numParams % 2) == 0) { // an odd number of params, greater than 1, is required
			return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
		}
		Object o;
		Tensor c;
		int order = 0;
		int size = 0;
		int[] dims = null;
		Tensor result = null;
		Tensor x = null;
		for(int i = 0; i < numParams - 1; i += 2) {
			o = pv.eval(node.jjtGetChild(i));
			if(!(o instanceof Tensor)) {
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
			}
			c = (Tensor)o;
			if(i == 0) {
				order = c.getOrder();
				size = c.getSize();
				dims = c.getDimensions();
				if(dims != null) {
					result = new Tensor(dims);
					x = new Tensor(dims);
				}
			}
			if(order == 0) { // if conditions are scalars, there are no constraints on values
				if(STParserTools.isTrue(c.getValue())) { return pv.eval(node.jjtGetChild(i + 1)); }
			} else {
				Tensor v = (Tensor)pv.eval(node.jjtGetChild(i + 1));
				int orderV = v.getOrder();
				if(orderV != 0 && (orderV != order || v.getSize() != size)) {
					return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
				}
				for(int j = 0; j < size; j++) {
					if(x.getScalar(j).getValue() != 1) {
						if(STParserTools.isTrue(c.getScalar(j).getValue())) {
							result.setScalar((orderV == 0 ? v.getScalar(0) : v.getScalar(j)), j);
							x.setScalar(1, j);
						}
					}
				}
			}
		}
		if(order == 0) { return pv.eval(node.jjtGetChild(numParams - 1)); }
		Tensor v = (Tensor)pv.eval(node.jjtGetChild(numParams - 1));
		int orderV = v.getOrder();
		if(orderV != 0 && (orderV != order || v.getSize() != size)) {
			return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
		}
		for(int j = 0; j < size; j++) {
			if(x.getScalar(j).getValue() != 1) {
				result.setScalar((orderV == 0 ? v.getScalar(0) : v.getScalar(j)), j);
			}
		}
		return result;
	}

}
