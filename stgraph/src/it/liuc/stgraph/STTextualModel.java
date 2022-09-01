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
package it.liuc.stgraph;

import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;

import java.io.FileWriter;
import java.io.PrintWriter;


/**
 * Tools for textual modeling.
 */
public class STTextualModel {


	/**
	 * Export the minimal computational info on the specified model.
	 * <br>Each line of the target text (.stc) file has a <code>key=value</code> format,
	 * with the constraints:
	 * <li>the info on each node is maintained in two or more lines;
	 * <li>for a node named <code>node1</code> the first line must be
	 * <code>node1-info=aux|state|stateout[+out|+vout]][+global]</code>
	 * <li>the following lines can have the keys <code>node1-out</code>, <code>node1-x0</code>,
	 * and <code>node1-trans</code>
	 * <br>Currently info on submodel nodes is not exported.
	 *
	 * @param model the model whose info must be exported
	 */
	public final static void export(final STGraphExec model) {
		String fn = model.getFileName();
		if(fn == null) { return; }
		int l = fn.length();
		if(fn.substring(l - 3, l).toLowerCase().equals(STConfigurator.getProperty("FILEEXT.STANDARD"))) { fn = fn.substring(0, l - 3) + STConfigurator.getProperty("FILEEXT.TEXTUAL"); } //$NON-NLS-1$ //$NON-NLS-2$
		else { fn += STConfigurator.getProperty("FILEEXT.TEXTUAL"); } //$NON-NLS-1$
		try {
			PrintWriter file = new PrintWriter(new FileWriter(fn, false), true);
			ValueNode vn;
			for(STNode node : model.getNodes()) {
				vn = (ValueNode)node;
				file.println(getHeader(vn));
				if(node.isAlgebraic()) {
					file.println(node.getName() + "-out = " + vn.getExpression()); //$NON-NLS-1$
				} else if(node.isState()) {
					file.println(node.getName() + "-x0 = " + vn.getStateInit()); //$NON-NLS-1$
					file.println(node.getName() + "-trans = " + vn.getStateTransition()); //$NON-NLS-1$
					if(vn.isStateWithOutput()) {
						file.println(node.getName() + "-out = " + vn.getExpression()); //$NON-NLS-1$
					}
				}
				file.println();
			}
			file.flush();
			file.close();
			System.out.println("file export: done..."); //$NON-NLS-1$
		} catch (Exception e) { e.printStackTrace(); }

	}



	/**
	 * Helper method for generating the header information to be exported.
	 *
	 * @param n the node
	 *
	 * @return string with header info
	 */
	private static String getHeader(final ValueNode n) {
		String result = n.getName() + "-info = ";
		if(n.isAlgebraic()) { result += "aux"; }
		else if(n.isStateWithOutput()) { result += "stateout"; }
		else { result += "state"; }
		if(n.isVectorOutput()) { result += "+vout"; }
		else if(n.isOutput()) { result += "+out"; }
		if(n.isGlobal()) { result += "+glob"; }
		return result;
	}

}
