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
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.util.STTools;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.TransferHandler;


/** Edit Cut action. */
public class EditCut extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public EditCut() { ; }


	/** Class constructor.
	 * @param keyChar the key char
	 * @param modifiers the modifiers */
	public EditCut(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		STGraphImpl graph = stc.getCurrentGraph();
		setEnabled(stc.isCurrentDesktop() && graph.isEditable && !graph.isSelectionEmpty() && !STTools.isWidget(graph.getSelectionCell()));
	}


	/** Action method.
	 * @param e the e */
	@Override
	public final void actionPerformed(final ActionEvent e) {
		STGraphC stc = STGraph.getSTC();
		Action action = TransferHandler.getCutAction();
		try {
			action.actionPerformed(new ActionEvent(stc.getCurrentGraph(), e.getID(), e.getActionCommand(), e.getModifiers()));
			stc.setInClip(true);
			stc.refreshBars();
		} catch (Exception ex) { ; }
		STGraphC.setFocus();
	}

}
