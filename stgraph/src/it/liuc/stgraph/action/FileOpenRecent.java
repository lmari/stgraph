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

import java.io.File;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;


/** File Open Recent action. */
public class FileOpenRecent extends AbstractActionDefault {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Sequence of this action. */
	private String seq;


	/** Create and return a version of this action suitable for menu insertion.
	 * @return version */
	@Override
	public final Component prepareForMenu() { return getMenuItem(false); }


	/** Default class constructor. */
	public FileOpenRecent() { ; }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() { setEnabled(true); }


	/** Action method. */
	@Override
	public final void exec() {
		String fileName = STConfigurator.getProperty("RECENTFILE" + seq); //$NON-NLS-1$
		if(fileName != null) {
			if(!(new File(fileName)).exists()) {
				STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR_WRONG_SYSTEM_FILE")); //$NON-NLS-1$
				return;
			}
			FileOpen.opener(fileName, false, true);
		}
	}


	/** Set the sequence of this action.
	 * @param seq the seq */
	public final void setSeq(final String seq) { this.seq = seq; }

}
