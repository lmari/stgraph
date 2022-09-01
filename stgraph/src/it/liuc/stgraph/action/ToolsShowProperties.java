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

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;


/** Tools ShowProperties action. */
@SuppressWarnings("serial")
public class ToolsShowProperties extends AbstractActionDefault {


	/** Default class constructor. */
	public ToolsShowProperties() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public ToolsShowProperties(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override public final void setEnabledOnState() { setEnabled(STGraph.getSTC().isCurrentDesktop()); }


	/** Action method. */
	@Override public final void exec() {
		STGraphExec currentGraph = STGraph.getSTC().getCurrentGraph();
		Object o;
		if(currentGraph.getSelectionCount() > 0) {
			if(STTools.isNode(o = currentGraph.getSelectionCell())) { STTools.infoDataDialog.showMe(STGraphC.getMessage("NODE.DIALOG.SHOWTITLE"), ((STNode)o).getInfo(), true); } //$NON-NLS-1$
		} else { STTools.infoDataDialog.showMe(STGraphC.getMessage("SYSTEM.DIALOG.INFOTITLE"), currentGraph.toString(), true); } //$NON-NLS-1$
	}

}
