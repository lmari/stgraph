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
import it.liuc.stgraph.widget.STWidget;

import java.awt.Component;


/**
 * Tools ToggleShowWidgets action.
 */
public class ToolsToggleShowWidgets extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default class constructor.
	 */
	public ToolsToggleShowWidgets() { ; }


	/**
	 * Control the enabling/disabling of the action on the basis of the system state.
	 */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		if(stc.isCurrentDesktop()) {
			STGraphImpl graph = stc.getCurrentGraph();
			if(graph.widgetsVisible) { setIcon1(); }
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
		graph.widgetsVisible = !graph.widgetsVisible;
		showHide();
		setEnabledOnState();
	}


	/**
	 * Helper method.
	 */
	public final void showHide() { 
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		for(STWidget w : STGraph.getSTC().getCurrentGraph().getWidgets()) { w.setIconized(!graph.widgetsVisible); }
	}

}
