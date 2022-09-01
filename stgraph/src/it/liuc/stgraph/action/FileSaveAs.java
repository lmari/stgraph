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

import javax.swing.JFileChooser;


/** File Save As action. */
public class FileSaveAs extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public FileSaveAs() { ; }


	/** Class constructor.
	 * @param keyChar the key char
	 * @param modifiers the modifiers */
	public FileSaveAs(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Action method. */
	@Override
	public final void exec() {
		String fn;
		STGraphC stc = STGraph.getSTC();
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(STGraphC.getMessage("MSG.TITLE.SAVEAS")); //$NON-NLS-1$
		fc.setFileFilter(new STFileFilter());
		if(stc.getCurrentDir() != null) { fc.setCurrentDirectory(stc.getCurrentDir()); }
		if(fc.showSaveDialog(stc) != JFileChooser.APPROVE_OPTION) { return; }
		stc.setCurrentDir(fc.getCurrentDirectory());
		fn = fc.getSelectedFile().getAbsolutePath();
		if(!fn.endsWith(STConfigurator.getProperty("FILEEXT.STANDARD")) && !fn.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) { fn += STConfigurator.getProperty("FILEEXT.STANDARD"); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		stc.getCurrentGraph().contextName = fn.substring(0, fn.lastIndexOf(System.getProperty("file.separator"))); //$NON-NLS-1$
		stc.getCurrentGraph().setTitle(fn.substring(1 + fn.lastIndexOf(System.getProperty("file.separator")), fn.length() - 4)); //$NON-NLS-1$
		STGraphC.getMultiDesktop().setTitleAt(STGraphC.getMultiDesktop().getSelectedIndex(), stc.getCurrentGraph().getTitle());
		stc.getCurrentGraph().setFileName(fn);
		stc.getCurrentGraph().save();
		stc.refreshBars();
		STConfigurator.addFile(fn);
		STConfigurator.write();
		STConfigurator.repaintRecentItems();
	}

}
