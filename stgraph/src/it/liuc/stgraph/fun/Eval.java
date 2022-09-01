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
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.util.STTools;

import java.util.Stack;

import org.nfunk.jep.ParseException;


/** Eval (meta)function. */
public class Eval extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Eval() { numberOfParameters = 1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Object param = stack.pop();
		if(!(param instanceof String)) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		STInterpreter interpreter = STGraph.getSTC().getCurrentGraph().getInterpreter();
		interpreter.parseExpression((String)param);
		Object r = interpreter.getValueAsObject();
		if(interpreter.hasError()) {
			stack.push(STInterpreter.handleException(STTools.replaceCRwithBR(interpreter.getErrorInfo())));
			return;
		}
		stack.push(r);
		return;
	}

}
