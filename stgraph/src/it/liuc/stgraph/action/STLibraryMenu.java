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
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JMenuItem;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STFactory;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STMenu;
import it.liuc.stgraph.node.EditPanel4Nodes;
import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;


/** Handle the dynamic library menu. */
public class STLibraryMenu extends STMenu {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public STLibraryMenu() {
		ArrayList<File> dirs = getDirectoriesInDir(ModelNode.MODELPATH);
		ArrayList<String> files = getFileNamesInDir(ModelNode.MODELPATH);
		if(dirs == null && files == null) { // the model directory could be empty or even non-existing...
			setActions(new AbstractActionDefault[1]);
			setItems(new JMenuItem[1]);
			AbstractActionDefault ac = new InsertEmpty();
			JMenuItem it = new JMenuItem();
			it.addActionListener(ac);
			it.setAction(ac);
			it.setText(STGraphC.getMessage("SYSTEM.LIB.EMPTY")); //$NON-NLS-1$
			add(it);
			setAction(0, ac);
			setItem(0, it);
			return;
		}
		int n;
		if(dirs != null && (n = dirs.size()) > 0) { // only one level of subdirs...
			String name;
			for(int i = 0; i < n; i++) {
				STMenu m = new STMenu();
				m.setText(name = dirs.get(i).getName());
				add(m);
				ArrayList<String> files2 = getFileNamesInDir(ModelNode.MODELPATH + "/" + name); //$NON-NLS-1$
				if(files2 != null && files2.size() > 0) { setMenuEntries(m, name + "/", files2); } //$NON-NLS-1$
			}
		}
		if(files != null && files.size() > 0) { setMenuEntries(this, STTools.BLANK, files); } // ... and after that files in the top dir
	}


	/** Set the menu entries.
	 * @param menu the menu
	 * @param dir the dir
	 * @param files the file list */
	private void setMenuEntries(final STMenu menu, final String dir, final ArrayList<String> files) {
		int n = files.size();
		menu.setActions(new AbstractActionDefault[n]);
		menu.setItems(new JMenuItem[n]);
		InsertCustomSubModel ac;
		JMenuItem it;
		String t;
		for(int i = 0; i < n; i++) {
			ac = new InsertCustomSubModel();
			it = new JMenuItem();
			it.addActionListener(ac);
			it.setAction(ac);
			t = files.get(i);
			t = t.substring(0, 1).toUpperCase() + t.substring(1, t.length() - 4);
			it.setText(t);

			//XXX hidden feature
			it.addMenuKeyListener(STMenuKeyListener.getInstance());

			ac.setModelName(dir + files.get(i));
			menu.add(it);
			menu.setAction(i, ac);
			menu.setItem(i, it);
		}
	}


	/** Create and return a version of this action suitable for menu insertion.
	 * @return version */
	@Override
	public final Component prepareForMenu() { return this; }


	/** Get the list of the filenames inside a specified directory.
	 * @param dirName name of a directory
	 * @return filenames */
	private ArrayList<String> getFileNamesInDir(final String dirName) {
		File dir = null;
		try {
			dir = new File(dirName);
		} catch (Exception e) {
			return null;
		}
		if(dir.list() == null || dir.list().length == 0) { return null; }
		ArrayList<String> files = new ArrayList<String>();
		for(String fileName : dir.list()) {
			if(fileName.endsWith(STConfigurator.getProperty("FILEEXT.STANDARD"))) { files.add(fileName); } //$NON-NLS-1$
		}
		if(files.size() == 0) { return null; }
		Collections.sort(files);
		return files;
	}


	/** Get the list of the directories inside a specified directory.
	 * @param dirName name of a directory
	 * @return directories */
	private ArrayList<File> getDirectoriesInDir(final String dirName) {
		File dir = null;
		try {
			dir = new File(dirName);
		} catch (Exception e) {
			return null;
		}
		if(dir.list() == null || dir.list().length == 0) { return null; }
		ArrayList<File> directories = new ArrayList<File>();
		for(File x : dir.listFiles()) {
			if(x.isDirectory() && !x.getName().startsWith(STTools.DOT)) { directories.add(x); }
		}
		if(directories.size() == 0) { return null; }
		Collections.sort(directories);
		return directories;
	}


	/** Insert a custom submodel action. */
	private class InsertCustomSubModel extends AbstractActionDefault {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/** Model name. */
		private String modelName;


		/** Set the model name.
		 * @param _modelName the model name */
		void setModelName(final String _modelName) { modelName = _modelName; }


		/** Action method. */
		public final void exec() {
			makeNode(STMenuKeyListener.isControlDown());
			STGraphC.setFocus();
		}


		/** Helper method.
		 * @param controlDown has control key been pressed during the item selection?
		 * @return result */
		public final STNode makeNode(final boolean controlDown) {
			STNode node = STFactory.nodeCreate4(null, STNode.NODE_MODEL, modelName.substring(0, modelName.length() - 4), true, controlDown);
			node.setIconFile(EditPanel4Nodes.DEFAULT_ITEM_FOR_LIBRARY_MODEL_ICON);
			return node;
		}

	}


	/** Insert an empty action. */
	private class InsertEmpty extends AbstractActionDefault {


		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/** Action method. */
		public final void exec() { STGraphC.setFocus(); }

	}

}
