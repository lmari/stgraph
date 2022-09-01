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
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.TextNode;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.STWidget;


/** Edit Node Properties action. */
public class EditNodeProperties extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public EditNodeProperties() { ; }


	/** Class constructor.
	 * @param keyChar the key char
	 * @param modifiers the modifiers */
	public EditNodeProperties(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		STGraphImpl graph = stc.getCurrentGraph();
		setEnabled(stc.isCurrentDesktop() && graph.isEditable && graph.getSelectionCount() == 1);
	}


	/** Create and return a version of this action suitable for menu insertion.
	 * @return version */
	public final Component prepareForMenu() { return getMenuItem(true); }


	/** Action method. */
	@Override
	public final void exec() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		Object o = graph.getSelectionCell();
		if(STTools.isNode(o)) { ((STNode)o).openDialog(); }
		else if(STTools.isWidget(o)) { ((STWidget)o).openDialog(); }
		else if(STTools.isText(o)) { ((TextNode)o).openDialog(); }
		else if(STTools.isEdge(o)) { graph.startEditingAtCell(o); }
	}

}
