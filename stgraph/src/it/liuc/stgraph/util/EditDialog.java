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

import it.liuc.stgraph.STGraphC;

import javax.swing.JDialog;


/** Generate and control a window which is able to let the user interactively edit some data. */
@SuppressWarnings("serial")
public class EditDialog extends JDialog {
	/** The (private) instance of this object. */
	private static EditDialog thisDialog = null;
	/** The object that was previously edited by this dialog. */
	private static Object previousObject;
	/** The content of the current field as before its current editing. */
	private static Object previousContent;
	/** The switch to maintain whether there are some pending edit operations. */
	private static boolean dirty;


	/** Class constructor.
	 * <br>Private, since a single dialog can be created, by the <code>getInstance</code> method. */
	private EditDialog() {
		super();
		setResizable(true);
		setLocation(STGraphC.getContainer().getX() + 50, STGraphC.getContainer().getY() + 50);
		setIconImage(STGraphC.getSystemIcon());
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setAlwaysOnTop(true);
		setModalityType(ModalityType.DOCUMENT_MODAL);
	}


	/** Get the (single) instance of this dialog.
	 * @return the dialog */
	public static EditDialog getInstance() {
		if(thisDialog == null) { thisDialog = new EditDialog(); }
		return thisDialog;
	}


	/** Set the object that was previously edited by this dialog.
	 * @param previousObject the previousObject to set */
	public static void setPreviousObject(Object previousObject) { EditDialog.previousObject = previousObject; }


	/** Get the object that was previously edited by this dialog.
	 * @return the previousObject */
	public static Object getPreviousObject() { return previousObject; }


	/** Set whether there are some pending edit operations.
	 * @param dirty the dirty to set */
	public static void setDirty(boolean dirty) {
		EditDialog.dirty = dirty;
		String t = EditDialog.getInstance().getTitle();
		if(t == null) { return; }
		if(dirty) {
			if(!t.endsWith(STTools.ASTERISK)) { EditDialog.getInstance().setTitle(t + STTools.ASTERISK); }
		} else {
			if(t.endsWith(STTools.ASTERISK)) { EditDialog.getInstance().setTitle(t.substring(0, t.length() - 1)); }
		}
	}


	/** Get whether there are some pending edit operations.
	 * @return the dirty */
	public static boolean isDirty() { return dirty; }


	/** Set the content of the current field as before its current editing.
	 * @param previousContent the previousContent to set */
	public static void setPreviousContent(Object previousContent) { EditDialog.previousContent = previousContent; }


	/** Get the content of the current field as before its current editing.
	 * @return the previousContent */
	public static Object getPreviousContent() { return previousContent; }

}
