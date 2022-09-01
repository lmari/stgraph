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

import java.awt.Component;


/** Execute ToggleConnectToServer action. */
public class ExecuteToggleConnectToServer extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public ExecuteToggleConnectToServer() { ; }


	/** Class constructor.
	 * @param keyChar the key char
	 * @param modifiers the modifiers */
	public ExecuteToggleConnectToServer(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		if(graph != null) {
			setEnabled(graph.isForNet() && graph.isRunnable());
			if(!graph.isConnectedToNet()) {
				menuItem.setText(STGraphC.getMessage(id.toUpperCase() + "_MENUTEXT")); //$NON-NLS-1$
				setIcon1();
			} else {
				menuItem.setText(STGraphC.getMessage(id.toUpperCase() + "_ALT_MENUTEXT")); //$NON-NLS-1$
				setIcon2();
			}
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
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		if(!graph.isConnectedToNet()) {
			graph.connectToNet();
		} else {
			graph.disconnectFromNet();
		}
	}

}
