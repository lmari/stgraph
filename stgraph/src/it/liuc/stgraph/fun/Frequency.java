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


/** Frequency function. */
public class Frequency extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Frequency() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 2) {
			Tensor data = null; // data
			Tensor b = null; // bins
			try {
				b = (Tensor)stack.pop();
				data = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			if(data.getOrder() != 1 || b.getOrder() != 1) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_VECTOR"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			int numData = data.getSize();
			int numBins = b.getSize();
			if(numBins == 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_NONNULLVECTOR"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			int[] temp = new int[1 + numBins];
			for(int j = 0; j <= numBins; j++) { temp[j] = 0; }
			double[] bins = new double[numBins];
			for(int j = 0; j < numBins; j++) { bins[j] = b.getScalar(j).getValue(); }
			double v;
			try {
				int j;
				for(int i = 0; i < numData; i++) {
					v = data.getScalar(i).getValue();
					for(j = 0; j < numBins && v > bins[j]; j++) { ; }
					temp[j]++;
				}
				Tensor result = Tensor.newVector(1 + numBins);
				for(j = 0; j <= numBins; j++) { result.setScalar(temp[j], j); }
				stack.push(result);
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
		} else if(curNumberOfParameters == 3) {
			Tensor p1 = null; // new data
			Tensor distrib = null; // distribution
			Tensor bins = null; // bins
			try {
				bins = (Tensor)stack.pop();
				distrib = (Tensor)stack.pop();
				p1 = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			if(p1.getOrder() != 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_SCALAR"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			if(distrib.getOrder() != 1 || bins.getOrder() != 1) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_VECTOR"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			double v = p1.getValue();
			int numBins = bins.getSize();
			if(numBins == 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_NONNULLVECTOR"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			int numDistrib = distrib.getSize();
			if(numDistrib != numBins) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_SAMESIZED_VECTORS"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
			try {
				int j;
				for(j = 0; j < numBins && v > bins.getScalar(j).getValue(); j++) { ; }
				distrib.setScalar(distrib.getScalar(j).getValue() + 1, j);
				stack.push(distrib);
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
				return;
			}
		} else {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_VECTOR)); //$NON-NLS-1$
		}
	}

}
