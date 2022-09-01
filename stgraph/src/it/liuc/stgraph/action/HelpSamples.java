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

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;


/** Help show samples action. */
public class HelpSamples extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public HelpSamples() { ; }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	public final void setEnabledOnState() { setEnabled(true); }


	/** Action method. */
	public final void exec() {
		String text = STTools.file2HTMLString(STGraphC.getMessage("FILE.SAMPLES.NAME")); //$NON-NLS-1$
		if(text.length() > 0) {
			STTools.samplesDialog.showMe(STGraphC.getMessage("FILE.SAMPLES.TITLE"), text, true); //$NON-NLS-1$
		}
	}

}
