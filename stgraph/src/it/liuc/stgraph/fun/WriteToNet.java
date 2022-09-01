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
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STNode;

import java.util.Stack;

import lab.spacebrew.SpacebrewClient;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Write to net function. */
public class WriteToNet extends STFunction implements SpacebrewClient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public WriteToNet() { numberOfParameters = 1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Tensor param = (Tensor)stack.pop();
		STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
		if(graph.getState() != STGraphImpl.STATE_EDITING && graph.getState() != STGraphImpl.STATE_PRE_EVALUATING && !graph.isConnectedToNet()) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.UNREACHABLE_SERVER"), STData.VALUETYPE_SCALAR)); //$NON-NLS-1$
			return;
		}
		graph.resetLastError();
		STNode node = STGraph.getSTC().getCurrentlyComputedNode();
		graph.getConnector().send(node.getName(), STGraphExec.NET_DATATYPE, param.toString());
		stack.push(param);
		return;
	}

}
