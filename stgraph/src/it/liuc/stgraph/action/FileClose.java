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

import it.liuc.stgraph.STDesktop;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.node.EditPanel4Nodes;

import javax.swing.JOptionPane;


/** File Close action. */
public class FileClose extends AbstractActionDefault {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The show msg. */
	private boolean showMsg = true; // default


	/** Default class constructor. */
	public FileClose() { ; }


	/** Class constructor.
	 * @param keyChar the key char
	 * @param modifiers the modifiers */
	public FileClose(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() {
		STGraphExec currentGraph = STGraph.getSTC().getCurrentGraph();
		if(currentGraph != null && currentGraph.resource) {
			setEnabled(true);
		} else {
			super.setEnabledOnState();
		}
	}


	/** Wrapper for the action method. */
	public final void execWithoutMsgs() {
		showMsg = false;
		exec();
		showMsg = true;
	}


	/** Action method. */
	@Override
	public final void exec() {
		STGraphC stc = STGraph.getSTC();
		STGraphExec currentGraph = stc.getCurrentGraph();
		if(stc.isCurrentDesktop()) {
			if(currentGraph.isModified) {
				if(EditPanel4Nodes.isOpen()) {
					if(EditPanel4Nodes.isDirty()) { // autosaving
						EditPanel4Nodes.node.getDialog().okHandler(EditPanel4Nodes.MODE_APPLY);
					}
					EditPanel4Nodes.node.getDialog().getButtonCancel().doClick();
				}

				int state = showMsg ? JOptionPane.showConfirmDialog(stc, STGraphC.getMessage("MSG.SAVE1"), STGraphC.getMessage("MSG.TITLE.CLOSE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) : JOptionPane.YES_OPTION; //$NON-NLS-1$ //$NON-NLS-2$
				if(state == JOptionPane.CANCEL_OPTION) { return; }
				if(state == JOptionPane.YES_OPTION) {
					if(currentGraph.getFileName() != null) { currentGraph.save(); }
					else { ((FileSaveAs)STGraphC.getAction("FileSaveAs")).exec(); } //$NON-NLS-1$
				}
			}
			stc.getCurrentDesktop().close();
		}
		if(stc.isCurrentDesktop()) { stc.setCurrentDesktop((STDesktop)STGraphC.getMultiDesktop().getSelectedComponent()); }		
		STGraphC.setFocus();
	}

}
