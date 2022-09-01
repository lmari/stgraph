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

import java.awt.Component;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;


/** Analysis Show Computational Info action. */
@SuppressWarnings("serial")
public class AnalysisShowCompInfo extends AbstractActionDefault {


	/** Default class constructor. */
	public AnalysisShowCompInfo() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public AnalysisShowCompInfo(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Create and return a version of this action suitable for menu insertion.
	 * @return version */
	@Override
	public final Component prepareForMenu() { return getMenuItem(false); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() { setEnabled(STGraph.getSTC().isCurrentDesktop()); }


	/** Action method. */
	@Override
	public final void exec() {
		STTools.infoDataDialog.showMe(STGraphC.getMessage("SYSTEM.DIALOG.FULLINFOTITLE"), STGraph.getSTC().getCurrentGraph().getComputationalDescription(), true); //$NON-NLS-1$
	}

}
