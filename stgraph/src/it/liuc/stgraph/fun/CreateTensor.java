/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.type.Tensor;


/** Create array function.
 * <br>It automatically deals with the system variables <code>$i0</code>, ... <code>$i5</code>. */
@SuppressWarnings("serial")
public class CreateTensor extends STFunction implements CallbackEvaluationI {


	/** Class constructor. */
	public CreateTensor() { numberOfParameters = 2; }


	/** Run method, based on lazy evaluation.
	 * @param node the node
	 * @param pv the pv
	 * @return the object
	 * @throws ParseException the parse exception */
	public final Object evaluate(final Node node, final EvaluatorI pv) throws ParseException {
		checkStack(node);
		Object param = pv.eval(node.jjtGetChild(0));
		if(!(param instanceof Tensor)) { return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); }
		Tensor t = (Tensor)param;
		int order = t.getOrder();
		if(order > 1) {
			return STInterpreter.handleException(getException("ERR.FUN.REQUIRES_VECTOR"), STData.VALUETYPE_VECTOR);
		}
		int numDims = order == 0 ? 1 : t.getSize(); // to allow the first parameter to be a scalar
		int[] dims = new int[numDims];
		int totalSize = 1;
		for(int i = 0; i < numDims; i++) {
			totalSize *= dims[i] = (int)t.getScalar(i).getValue();
			if(dims[i] < 0) { return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE")); }
		}
		if(totalSize == 0) { return new Tensor(numDims); }
		Node expr = node.jjtGetChild(1);
		STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
		STInterpreter interpreter = graph.getInterpreter();
		int[][] indexes = Tensor.getIndexes(dims, graph.getIO());
		Tensor result = new Tensor(dims);
		Object o;
		for(int i = 0; i < totalSize; i++) {
			for(int j = 0; j < numDims; j++) { interpreter.addVariable("$i" + j, indexes[j][i]); }

			if(i > 0) { interpreter.addVariable("$p0", result.getScalar(i - 1)); }
			if(i > 1) { interpreter.addVariable("$p1", result.getScalar(i - 2)); }
			if(i > 2) { interpreter.addVariable("$p2", result.getScalar(i - 3)); }

			o = pv.eval(expr);
			if(o instanceof Tensor) {
				Tensor tt = (Tensor)o;
				int s = tt.getSize();
				if(s == 0 || s == 1) {
					result.setScalar(tt.getScalar(0), i);
				} else {
					result.setScalar(0.0, i); // only scalars can be added
				}
			} else {
				result.setScalar(0.0, i); // only scalars can be added
			}
			if(interpreter.hasError()) { return STInterpreter.handleException(getException("ERR.FUN.GENERIC")); }
		}
		return result;
	}

}
