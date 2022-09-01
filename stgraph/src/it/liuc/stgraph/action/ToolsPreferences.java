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

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STPreferencesDialog;


/**
 * Tools Preferences action.
 */
@SuppressWarnings("serial")
public class ToolsPreferences extends AbstractActionDefault {


	/**
	 * Default class constructor.
	 */
	public ToolsPreferences() { ; }


	/**
	 * Control the enabling/disabling of the action on the basis of the system state.
	 */
	@Override
	public final void setEnabledOnState() { setEnabled(true); }


	/**
	 * Action method.
	 */
	@Override
	public final void exec() { ((STPreferencesDialog)STGraphC.getContext().getBean("STPreferencesDialog")).open(); } //$NON-NLS-1$

}
