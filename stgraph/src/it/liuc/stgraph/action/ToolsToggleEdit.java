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

import java.awt.Component;


/** Tools ToggleEdit action. */
public class ToolsToggleEdit extends AbstractActionDefault {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The state. */
	private boolean state = true;


	/** Default class constructor. */
	public ToolsToggleEdit() { ; }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		if(stc.isCurrentDesktop()) {
			STGraphImpl graph = stc.getCurrentGraph();
			if(graph.isEditable) {
				menuItem.setText(STGraphC.getMessage(id.toUpperCase() + "_MENUTEXT")); //$NON-NLS-1$
				setIcon1();
			} else {
				menuItem.setText(STGraphC.getMessage(id.toUpperCase() + "_ALT_MENUTEXT")); //$NON-NLS-1$
				setIcon2();
			}
			setEnabled(graph.isTopGraph() && !graph.resource && !(graph.getState() == STGraphImpl.STATE_STEPPING || graph.getState() == STGraphImpl.STATE_TIMING));
		} else { setEnabled(false); }
	}


	/** Create and return a version of this action suitable for toolbar insertion.
	 * @return version */
	@Override
	public final Component prepareForToolBar() { 
		if(!STGraphC.isApplet()) { return getToolBarSwitchButton(); }
		return null;
	}


	/** Action method. */
	@Override
	public final void exec() {
		STGraphExec currentGraph = STGraph.getSTC().getCurrentGraph();
		state = !currentGraph.isEditable;
		currentGraph.setAsEditable(!currentGraph.isEditable);
	}


	/** Get the current state.
	 * @return state */
	public final boolean getState() { return state; }

}
