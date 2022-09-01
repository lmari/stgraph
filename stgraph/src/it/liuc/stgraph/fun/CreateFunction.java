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

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;


/** Create function (meta)function. */
public class CreateFunction extends STFunction implements CallbackEvaluationI  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public CreateFunction() { numberOfParameters = 1; }


	/** Run method, based on lazy evaluation.
	 * @param node the node
	 * @param pv the pv
	 * @return the object
	 * @throws ParseException the parse exception */
	public final Object evaluate(final Node node, final EvaluatorI pv) throws ParseException { return node.jjtGetChild(0); }

}
