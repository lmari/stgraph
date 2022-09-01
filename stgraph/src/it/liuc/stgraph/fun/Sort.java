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

import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Sort function. */
public class Sort extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Sort() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 1) {
			Tensor pArray = null; // array to be sorted wrt its last dimension
			try {
				pArray = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			if(pArray.getSize() <= 0) {
				stack.push(pArray);
				return;
			}
			Tensor result = new Tensor(pArray);
			List list;
			for(int i = 0; i < result.getNumberOfLastDimTensors(); i++) {
				list = result.getLastDimTensor(i).getValuesAsList();
				java.util.Collections.sort(list);
				result.setLastDimTensor(list, i);
			}
			stack.push(result);
			return;
		}
		if(curNumberOfParameters == 2) {
			Tensor pArray = null; // array to be sorted wrt its last dimension
			Tensor pSelector = null; // selector: ==0 sort in direct order, ==1 sort in reverse order
			try {
				pSelector = (Tensor)stack.pop();
				pArray = (Tensor)stack.pop();
			} catch (Exception e) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			if(pSelector.getOrder() != 0) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			int selector = (int)pSelector.getValue();
			if(selector != 0 && selector != 1) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"))); //$NON-NLS-1$
				return;
			}
			if(pArray.getSize() <= 0) {
				stack.push(pArray);
				return;
			}
			Tensor result = new Tensor(pArray);
			List list;
			if(selector == 0) {
				for(int i = 0; i < result.getNumberOfLastDimTensors(); i++) {
					list = result.getLastDimTensor(i).getValuesAsList();
					java.util.Collections.sort(list);
					result.setLastDimTensor(list, i);
				}
			} else {
				for(int i = 0; i < result.getNumberOfLastDimTensors(); i++) {
					list = result.getLastDimTensor(i).getValuesAsList();
					java.util.Collections.sort(list, new ReverseComparator());
					result.setLastDimTensor(list, i);
				}
			}
			stack.push(result);
			return;
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"))); //$NON-NLS-1$
	}


	/** Comparator for reverse sort. */
	@SuppressWarnings("rawtypes")
	public class ReverseComparator implements Comparator {


		/** Comparison criterion.
		 * @param o1 the o1
		 * @param o2 the o2
		 * @return int */
		public final int compare(final Object o1, final Object o2) {
			double p1 = ((Number)o1).doubleValue();
			double p2 = ((Number)o2).doubleValue();
			if(p1 > p2) { return -1; }
			if(p1 == p2) { return 0; }
			return 1;
		}

	}

}
