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
import it.liuc.stgraph.util.STTools;


/** File New action. */
public class FileNew extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public FileNew() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public FileNew(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() { setEnabled(true); }


	/** Action method. */
	@Override
	public final void exec() {
		try {
			STGraphC stc = STGraph.getSTC();
			new STDesktop(null, null, false, null, null);
			if(stc.getCurrentGraph() != null) { stc.getCurrentGraph().clearSelection(); }
			if(stc.getCurrentDesktop() != null) { STGraphC.getMultiDesktop().setSelectedComponent(stc.getCurrentDesktop()); }

			stc.validate();
			STTools.maximizeDesktop();

			stc.refreshBars();
			STGraphC.setFocus();
		} catch (Exception e) { ; }
	}

}
