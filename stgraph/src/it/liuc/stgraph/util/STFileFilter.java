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
package it.liuc.stgraph.util;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraphC;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Extend FileFilter by accepting the stg files and the subdirectories.
 */
public class STFileFilter extends FileFilter {


	/**
	 * Get description.
	 *
	 * @return description
	 */
	public final String getDescription() { return STGraphC.getMessage("SYSTEM.FILETYPE.DESCRIPTION"); } //$NON-NLS-1$


	/**
	 * Set the acceptation condition.
	 *
	 * @param f the f
	 *
	 * @return result
	 */
	public final boolean accept(final File f) {
		if(f.isDirectory()) { return true; }
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf(STTools.DOT);
		if(i > 0 &&  i < s.length() - 1) { ext = s.substring(i).toLowerCase(); }
		if(ext != null && (ext.equals(STConfigurator.getProperty("FILEEXT.STANDARD")) || ext.equals(STConfigurator.getProperty("FILEEXT.COMPRESSED")))) { return true; } //$NON-NLS-1$ //$NON-NLS-2$
		return false;
	}

}
