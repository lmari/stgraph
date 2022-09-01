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

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;


/**
 * Submenu handler.
 */
public class STSubMenu extends AbstractActionDefault {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The menu. */
	private JMenu menu;
	/** The actions. */
	private transient AbstractActionDefault[] actions;
	/** The items. */
	private transient Component[] items;


	/**
	 * Class constructor.
	 */
	public STSubMenu() { menu = new JMenu(); }


	/**
	 * Create and return a version of this action suitable for menu insertion.
	 * 
	 * @return version
	 */
	@Override
	public final Component prepareForMenu() { return menu; }


	/**
	 * Set the local.
	 * 
	 * @param localId the local id
	 */
	public final void setLocalId(final String localId) { menu.setText(STGraphC.getMessage("MENU_" + localId)); } //$NON-NLS-1$


	/**
	 * Set the actions.
	 * 
	 * @param _actions the _actions
	 */
	public final void setActions(final AbstractActionDefault[] _actions) {
		actions = _actions;
		items = new Component[actions.length];
		for(int i = 0; i < actions.length; i++) {
			menu.add(items[i] = actions[i].prepareForMenu());
		}
	}


	/**
	 * Get action.
	 *
	 * @param i the index
	 *
	 * @return the action
	 */
	public final AbstractActionDefault getAction(final int i) { return actions[i]; }


	/**
	 * Get item.
	 *
	 * @param i the index
	 *
	 * @return the item
	 */
	public final Component getItem(final int i) { return items[i]; }


	/**
	 * Get items.
	 *
	 * @return the items
	 */
	public final Component[] getItems() { return items; }


	/**
	 * Action method.
	 * 
	 * @param e the e
	 */
	@Override
	public final void actionPerformed(final ActionEvent e) { ; }

}
