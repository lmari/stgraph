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

import org.jgraph.graph.Port;

import it.liuc.stgraph.STFactory;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;


/** Insert Arrow action. */
public class InsertArrow extends AbstractActionDefault {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	STNode[] nodes = null;


	/** Default class constructor. */
	public InsertArrow() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public InsertArrow(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		if(!stc.isCurrentDesktop()) {
			setEnabled(false);
			return;
		}
		STGraphExec graph = stc.getCurrentGraph();
		int c = graph.getSelectionCount();
		if(c < 2) {
			setEnabled(false);
			return;
		}
		nodes = new STNode[2];
		int i = 0;
		for(Object o : graph.getSelectionCells()) {
			if(STTools.isNode(o)) {
				if(i < 2) { nodes[i] = (STNode)o; }
				i++;
			}
		}
		if(i != 2) {
			setEnabled(false);
			return;
		}
		setEnabled(true);
	}


	/** Action method. */
	public final void exec() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		boolean e1 = graph.checkEdge(nodes[0], nodes[1]);
		boolean e2 = graph.checkEdge(nodes[1], nodes[0]);
		if(!e1 && !e2) {
			STFactory.edgeCreate(STTools.BLANK, (Port)nodes[0].getFirstChild(), (Port)nodes[1].getFirstChild());
			return;
		}
		if(e1) {
			graph.removeEdge(nodes[0], nodes[1]);
			STFactory.edgeCreate(STTools.BLANK, (Port)nodes[1].getFirstChild(), (Port)nodes[0].getFirstChild());
			return;
		}
		graph.removeEdge(nodes[1], nodes[0]);
		STFactory.edgeCreate(STTools.BLANK, (Port)nodes[0].getFirstChild(), (Port)nodes[1].getFirstChild());
		STGraphC.setFocus();
	}

}
