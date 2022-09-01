/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2022, Luca Mari.
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
package it.liuc.stgraph;

import java.awt.Component;
import java.awt.Font;

import it.liuc.stgraph.action.AbstractActionDefault;

import javax.swing.JMenu;
import javax.swing.JMenuItem;


/** Handle a single menu. */
@SuppressWarnings("serial")
public class STMenu extends JMenu {
	/** The actions. */
	private transient AbstractActionDefault[] actions;
	/** The items. */
	private transient Component[] items;
	/** The local id. */
	private String localId;


	/** Default class constructor. */
	public STMenu() { ; }


	/** Class constructor.
	 * @param actions the actions */
	public STMenu(final AbstractActionDefault[] actions) {
		this.actions = actions;
		items = new Component[actions.length];
		for(int i = 0; i < actions.length; i++) { add(items[i] = actions[i].prepareForMenu()); }
	}


	/** Set the actions.
	 * @param actions the actions */
	public final void setActions(final AbstractActionDefault[] actions) { this.actions = actions; }


	/** Get the actions.
	 * @return the actions */
	public final AbstractActionDefault[] getActions() { return actions; }


	/** Set the action.
	 * @param i the index
	 * @param action the action */
	public final void setAction(final int i, final AbstractActionDefault action) { actions[i] = action; }


	/** Get the action.
	 * @param i the index
	 * @return the action */
	public final AbstractActionDefault getAction(final int i) { return actions[i]; }


	/** Set the items.
	 * @param items the items */
	public final void setItems(final Component[] items) { this.items = items; }


	/** Get the items.
	 * @return the items */
	public final Component[] getItems() { return items; }


	/** Set the items.
	 * @param i the index
	 * @param item the item */
	public final void setItem(final int i, final Component item) { items[i] = item; }


	/** Set the local identifier.
	 * @param localId the local id */
	public final void setLocalId(final String localId) {
		this.localId = localId;
		setText(STGraphC.getMessage("MENU_" + localId)); //$NON-NLS-1$
		int size = getFont().getSize();
		try {
			size = Integer.parseInt(STConfigurator.getProperty("MENU.FONTSIZE")); //$NON-NLS-1$
		} catch (Exception e) { ; }
		setFont(new Font(STGraph.getMyFont(), Font.PLAIN, size));
	}


	/** Get the local identifier.
	 * @return the local id */
	public final String getLocalId() { return localId; }


	/** Return a newly created menu item.
	 * @return menu */
	protected Component prepareForMenu() {
		JMenuItem mi = new JMenu();
		return mi;
	}

}
