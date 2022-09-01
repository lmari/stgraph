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

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;


/** Help Help action. */
public class HelpHelp extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public HelpHelp() { ; }


	/** Class constructor.
	 * @param keyChar the key char
	 * @param modifiers the modifiers */
	public HelpHelp(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	public final void setEnabledOnState() { setEnabled(true); }


	/** Action method.
	 * @param e the e */
	@Override
	public final void actionPerformed(final ActionEvent e) {
		STGraphC stc = STGraph.getSTC();
		STGraphC.setHelp(!STGraphC.isHelp());
		try {
			if(STGraphC.isHelp()) { stc.setCursor(STGraphC.getMyMenuBar().getHelpCursor()); }
			else { stc.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); }
		} finally {
			stc.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
