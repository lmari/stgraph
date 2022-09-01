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

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.type.Tensor;


/** Iterator (meta)function.
 * <br>It automatically deals with the system variables <code>$0</code>, <code>$1</code> and <code>$i</code>.
 * <br>Note that instead it <i>does not</i> deal with the system variable <code>this</code>. */
public class Iter extends STFunction implements CallbackEvaluationI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private STInterpreter interpreter = null;
	private Object o0 = null;
	private Object o1 = null;
	private Object o2 = null;


	/** Class constructor. */
	public Iter() { numberOfParameters = 3; }


	/** Run method, based on lazy evaluation.
	 * <br>By locally saving the dummy vars <code>$0</code>, <code>$1</code> and <code>$i</code>
	 * before the actual evaluation and restoring them immediately before returning the result,
	 * it simulates a reentrant stack management, so to allow nested calls.
	 * @param node the node
	 * @param pv the pv
	 * @return the object
	 * @throws ParseException the parse exception */
	public final Object evaluate(final Node node, final EvaluatorI pv) throws ParseException {
		checkStack(node);
		STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
		interpreter = graph.getInterpreter();
		setBuffer();
		int io = graph.getIO();
		Object data = pv.eval(node.jjtGetChild(0));
		if(!(data instanceof Tensor)) {
			restoreBuffer();
			return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
		}
		Tensor zero = (Tensor)pv.eval(node.jjtGetChild(2)); // get the "zero" value
		Tensor t = (Tensor)data;
		int order = t.getOrder();
		Object result;
		Node toEval;
		// scalar
		if(order == 0) {
			interpreter.addVariable("$0", zero); //$NON-NLS-1$
			interpreter.addVariable("$1", io); //$NON-NLS-1$
			interpreter.addVariable("$i", io); //$NON-NLS-1$
			toEval = node.jjtGetChild(1);
			result = pv.eval(toEval);
			if(interpreter.hasError()) {
				restoreBuffer();
				return STInterpreter.handleException(getException("ERR.FUN.GENERIC"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
			}
			for(int i = 1; i < t.getValue(); i++) {
				interpreter.addVariable("$0", result); //$NON-NLS-1$
				interpreter.addVariable("$1", io + i); //$NON-NLS-1$
				interpreter.addVariable("$i", io + i); //$NON-NLS-1$
				result = pv.eval(toEval);
				if(interpreter.hasError()) {
					restoreBuffer();
					return STInterpreter.handleException(getException("ERR.FUN.GENERIC"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
				}
			}
			restoreBuffer();
			return result;
		}
		int size = t.getSize();
		if(size <= 0) {
			restoreBuffer();
			return zero;
		}
		// vector
		if(order == 1) {
			interpreter.addVariable("$0", zero); //$NON-NLS-1$
			interpreter.addVariable("$1", t.getScalar(0)); //$NON-NLS-1$
			interpreter.addVariable("$i", io); //$NON-NLS-1$
			toEval = node.jjtGetChild(1);
			result = pv.eval(toEval);
			if(interpreter.hasError()) {
				restoreBuffer();
				return STInterpreter.handleException(getException("ERR.FUN.GENERIC"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
			}
			for(int i = 1; i < size; i++) {
				interpreter.addVariable("$0", result); //$NON-NLS-1$
				interpreter.addVariable("$1", t.getScalar(i)); //$NON-NLS-1$
				interpreter.addVariable("$i", io + i); //$NON-NLS-1$
				result = pv.eval(toEval);
				if(interpreter.hasError()) {
					restoreBuffer();
					return STInterpreter.handleException(getException("ERR.FUN.GENERIC"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
				}
			}
			restoreBuffer();
			return result;
		}
		// n>1-order array
		int[] dimensions = t.getDimensions();
		int d = dimensions[order - 1];
		int p = 1;
		for(int i = 0; i < order - 1; i++) { p *= dimensions[i]; }
		Tensor running = null;
		int[] newDimensions = new int[order - 1];
		for(int i = 0; i < order - 1; i++) { newDimensions[i] = dimensions[i]; }
		running = new Tensor(newDimensions);
		for(int i = 0; i < p; i++) { running.setScalar(t.getScalar(i * d), i); }
		interpreter.addVariable("$0", zero); //$NON-NLS-1$
		interpreter.addVariable("$1", running); //$NON-NLS-1$
		interpreter.addVariable("$i", io); //$NON-NLS-1$
		toEval = node.jjtGetChild(1);
		result = pv.eval(toEval);
		if(interpreter.hasError()) {
			restoreBuffer();
			return STInterpreter.handleException(getException("ERR.FUN.GENERIC"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
		}
		for(int j = 1; j < d; j++) {
			for(int i = 0; i < p; i++) { running.setScalar(t.getScalar(i * d + j), i); }
			interpreter.addVariable("$0", result); //$NON-NLS-1$
			interpreter.addVariable("$1", running); //$NON-NLS-1$
			interpreter.addVariable("$i", io + j); //$NON-NLS-1$
			result = pv.eval(toEval);
			if(interpreter.hasError()) {
				restoreBuffer();
				return STInterpreter.handleException(getException("ERR.FUN.GENERIC"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
			}
		}
		restoreBuffer();
		return result;
	}


	/** Virtually push the system vars. */
	private void setBuffer() {
		o0 = interpreter.getVarValue("$0"); //$NON-NLS-1$
		o1 = interpreter.getVarValue("$1"); //$NON-NLS-1$
		o2 = interpreter.getVarValue("$i"); //$NON-NLS-1$
	}


	/** Virtually pop the system vars. */
	private void restoreBuffer() {
		interpreter.addVariable("$0", o0); //$NON-NLS-1$
		interpreter.addVariable("$1", o1); //$NON-NLS-1$
		interpreter.addVariable("$i", o2); //$NON-NLS-1$
	}

}
