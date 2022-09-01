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
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STNode;

import java.util.ArrayList;

import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.type.Tensor;


/** GetCProp function. */
public class GetCProp extends STFunction implements CallbackEvaluationI {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public GetCProp() { numberOfParameters = -1; }


	/** Run method, based on lazy evaluation, so to allow even partial evaluation of its arguments.
	 * @param node the node
	 * @param pv the pv
	 * @return the object
	 * @throws ParseException the parse exception
	 */
	public Object evaluate(Node node, EvaluatorI pv) throws ParseException {
		checkStack(node);
		int numParams = node.jjtGetNumChildren();
		if(numParams == 1) { // get a cprop of the current node
			Object o = pv.eval(node.jjtGetChild(0));
			if(!(o instanceof String)) {        	
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); //$NON-NLS-1$
			}
			String param = (String)o;
			String s = STGraph.getSTC().getCurrentlyComputedNode().getCProperty(param);
			if(s == null) {
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); //$NON-NLS-1$
			}
			double d;
			try {
				d = Double.parseDouble(s);
			} catch (Exception e) {
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); //$NON-NLS-1$
			}
			return Tensor.newScalar(d);
		}
		if(numParams == 2) { // get a cprop of the specified node
			Node o1 = node.jjtGetChild(0);
			if(!(o1 instanceof ASTVarNode)) {        	
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); //$NON-NLS-1$
			}
			STNode defNode = STGraph.getSTC().getCurrentlyComputedGraph().getNodeByName(((ASTVarNode)o1).getName());
			if(defNode == null) {
				return STInterpreter.handleException(getException("ERR.FUN.NODE_NOT_FOUND")); //$NON-NLS-1$
			}
			STNode currNode = STGraph.getSTC().getCurrentlyComputedNode();
			ArrayList<STNode> nodeList = currNode.getDefiningNodesByExpressions();
			if(nodeList == null || nodeList.size() == 0) {
				return STInterpreter.handleException(getException("ERR.FUN.NODE_NOT_FOUND")); //$NON-NLS-1$
			}
			Object o2 = pv.eval(node.jjtGetChild(1));
			if(!(o2 instanceof String)) {        	
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); //$NON-NLS-1$
			}
			String param2 = (String)o2;
			boolean found = false;
			for(STNode n : nodeList) {
				if(n == defNode) {
					found = true;
					break;
				}
			}
			if(!found) {
				return STInterpreter.handleException(getException("ERR.FUN.NODE_NOT_FOUND")); //$NON-NLS-1$
			}
			String s = defNode.getCProperty(param2);
			if(s == null) {
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); //$NON-NLS-1$
			}
			double d;
			try {
				d = Double.parseDouble(s);
			} catch (Exception e) {
				return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE")); //$NON-NLS-1$
			}
			return Tensor.newScalar(d);
		}
		return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR); //$NON-NLS-1$
	}

}
