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
import it.liuc.stgraph.STGraphPanel;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.TransferHandler;

import org.jgraph.graph.DefaultGraphCell;


/** Edit Paste action. */
public class EditPaste extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public EditPaste() { ; }


	/** Class constructor.
	 * @param keyChar the key char
	 * @param modifiers the modifiers */
	public EditPaste(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		setEnabled(stc.isCurrentDesktop() && stc.getCurrentGraph().isEditable && stc.isInClip());
	}


	/** Action method.
	 * @param e the e */
	@Override
	public final void actionPerformed(final ActionEvent e) {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		Action action = TransferHandler.getPasteAction();
		stc.isPasting = true;
		graph.clearSelection();
		try {
			action.actionPerformed(new ActionEvent(graph, e.getID(), e.getActionCommand(), e.getModifiers()));
		} catch (Exception ex) {
			/*
			System.out.println("*********************************");
			System.out.println(stc.getCurrentGraph());
			System.out.println(e.getID());
			System.out.println(e.getActionCommand());
			System.out.println(e.getModifiers());
			System.out.println("*********************************");
			 */
		}
		for(DefaultGraphCell n : STGraphPanel.pastedNodes) {
			if(graph.getSelectionCount() == 0) {
				graph.setSelectionCell(n);
			} else {
				graph.addSelectionCell(n);
			}
			// a dirty trick to properly refresh pasted nodes... 
			if(STTools.isNode(n)) { graph.getInterpreter().addVariable(((STNode)n).getName(), 0.0); }
			STTools.moveNodes(1, 0, false); STTools.moveNodes(-1, 0, false);
		}
		stc.isPasting = false;
		//STGraphC.setFocus();
	}

}
