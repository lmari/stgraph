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
package it.liuc.stgraph.action;

import java.awt.Component;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STEdge;
import it.liuc.stgraph.node.STNode;


/** Analysis IncomingArrows action: select the arrow incoming the selected one. */
@SuppressWarnings("serial")
public class AnalysisIncomingArrows extends AbstractActionDefault {


	/** Create and return a version of this action suitable for menu insertion.
	 * @return version */
	@Override
	public final Component prepareForMenu() { return getMenuItem(false); }


	/** Default class constructor. */
	public AnalysisIncomingArrows() { ; }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		setEnabled(stc.isCurrentDesktop() && graph.getSelectionCount() == 1 && graph.getSelectionCell() instanceof STNode);
	}


	/** Action method. */
	@Override
	public final void exec() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		STEdge[] list = graph.getNodeEdges((STNode)graph.getSelectionCell(), STGraphImpl.EDGEMODE_IN);
		if(list == null || list.length == 0) return;
		graph.removeSelectionCell(graph.getSelectionCell());
		for(STEdge edge: list) {
			//edge.select(graph);
			graph.addSelectionCell(edge);
		}
	}

}
