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
package it.liuc.stgraph.action;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STNode;

import java.awt.Component;


/**
 * Tools ToggleShowGraph action.
 */
public class ToolsToggleShowGraph extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default class constructor.
	 */
	public ToolsToggleShowGraph() { ; }


	/**
	 * Control the enabling/disabling of the action on the basis of the system state.
	 */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		if(stc.isCurrentDesktop()) {
			STGraphImpl graph = stc.getCurrentGraph();
			if(graph.graphVisible) { setIcon1(); }
			else { setIcon2(); }
			setEnabled(true);
		} else { setEnabled(false); }
	}


	/**
	 * Create and return a version of this action suitable for toolbar insertion.
	 *
	 * @return version
	 */
	@Override
	public final Component prepareForToolBar() { return getToolBarSwitchButton(); }


	/**
	 * Action method.
	 */
	@Override
	public final void exec() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		graph.graphVisible = !graph.graphVisible;
		showHide();
		setEnabledOnState();
	}


	/**
	 * Helper method.
	 */
	public final void showHide() { 
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		for(STNode node : graph.getNodes()) { graph.getGraphLayoutCache().setVisible(node, graph.graphVisible); }
		if(graph.graphVisible) { graph.clearSelection(); }
	}

}
