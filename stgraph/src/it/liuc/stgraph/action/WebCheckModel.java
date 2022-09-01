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

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphChecker;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.util.InfoDialog;
import it.liuc.stgraph.util.STTools;


/** WebCheckModel action. */
public class WebCheckModel extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public WebCheckModel() { ; }


	/** Create and return a version of this action suitable for menu insertion.
	 * @return version */
	@Override
	public final Component prepareForMenu() { return getMenuItem(false); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() { setEnabled(STGraph.getSTC().isCurrentDesktop() && STGraph.getSTC().getCurrentGraph().isForWeb()); }


	/** Action method. */
	@Override
	public final void exec() {
		STTools.infoTextDialog.showMe(STGraphC.getMessage("CHECK.WEBTITLE"), STGraphChecker.checkForWeb(STGraph.getSTC().getCurrentGraph()), true, InfoDialog.MODE_NODES); //$NON-NLS-1$
	}

}
