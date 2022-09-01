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

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STFileFilter;
import it.liuc.stgraph.util.STTools;

import java.io.File;

import javax.swing.JFileChooser;


/** File Open action. */
public class FileOpen extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public FileOpen() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public FileOpen(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() { setEnabled(true); }


	/** Action method. */
	@Override
	public final void exec() {
		STGraphC stc = STGraph.getSTC();
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(STGraphC.getMessage("MSG.TITLE.OPEN")); //$NON-NLS-1$
		fc.setFileFilter(new STFileFilter());
		if(stc.getCurrentDir() != null) { fc.setCurrentDirectory(stc.getCurrentDir()); }
		if(fc.showOpenDialog(stc) != JFileChooser.APPROVE_OPTION) { return; }
		String fileName = fc.getSelectedFile().getAbsolutePath();
		if(!(new File(fileName)).exists()) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR_WRONG_SYSTEM_FILE")); //$NON-NLS-1$
			return;
		}
		stc.setCurrentDir(fc.getCurrentDirectory());
		FileOpen.opener(fileName, false, true);

		if(STMenuKeyListener.isControlDown()) {
			//TODO possible handler for model cleaning...
		}
	}


	/** Model opener helper.
	 * @param fileName the filename of the model to open
	 * @param asResource is it a resource (i.e., a sample model)?
	 * @param updateFileList should recent file list be updated? */
	public static void opener(final String fileName, final boolean asResource, final boolean updateFileList) {
		STGraphC stc = STGraph.getSTC();
		stc.loadFile(stc.getInputStream(fileName, asResource), fileName, asResource);
		stc.validate();
		STTools.maximizeDesktop();
		if(updateFileList) {
			STConfigurator.addFile(fileName);
			STConfigurator.write();
			STConfigurator.repaintRecentItems();
		}
	}

}
