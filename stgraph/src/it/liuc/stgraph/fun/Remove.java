/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2022, Luca Mari.
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

import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Remove element function. */
@SuppressWarnings("serial")
public class Remove extends STFunction {


	/** Class constructor. */
	public Remove() { numberOfParameters = 2; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Tensor data = null;
		Tensor indexes = null;
		try {
			indexes = (Tensor)stack.pop();
			data = (Tensor)stack.pop();
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		int numData = data.getSize();
		if(numData <= 0) {
			stack.push(data);
			return;
		}
		if(indexes.getOrder() > 1) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		int numIndexes = indexes.getSize();
		if(numIndexes == -1) {
			stack.push(data);
			return;
		}
		int io = STGraph.getSTC().getCurrentlyComputedGraph().getIO();
		List list = indexes.getValuesAsList();
		java.util.Collections.sort(list);
		int n = list.size();
		int t = -1;
		int c;
		for(int i = n - 1; i >= 0; i--) { //remove the duplicates
			c = ((Double)list.get(i)).intValue();
			if(c == t) { list.remove(i); }
			t = c;
		}
		Tensor result = new Tensor(data);
		for(int i = list.size() - 1; i >= 0; i--) { result.removeOnFirstDim(((Double)list.get(i)).intValue() - io); }
		stack.push(result);
	}

}
