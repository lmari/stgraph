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

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;

import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 * Submenu handler.
 */
public class STMacroSubMenu extends AbstractActionDefault {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The menu. */
	private JMenu menu;
	/** The actions. */
	private transient STMacro[] actions;
	/** The items. */
	private transient JMenuItem[] items;


	/**
	 * Class constructor.
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public STMacroSubMenu() {
		menu = new JMenu();
		try {
			Class[] c = STTools.getClassesInPackage("it.liuc.stgraph.macros", "lib/stgraphmacros.jar"); //$NON-NLS-1$ //$NON-NLS-2$
			if(c != null) {
				actions = new STMacro[c.length];
				items = new JMenuItem[c.length];
				for(int i = 0; i < c.length; i++) {
					actions[i] = (STMacro)c[i].newInstance();

					items[i] = new JMenuItem();
					items[i].addActionListener(actions[i]);
					items[i].setAction(actions[i]);
					items[i].setText(actions[i].getMenuItemText());
					menu.add(items[i]);
				}
			}
		} catch (Exception e) { ; }
	}


	/**
	 * Create and return a version of this action suitable for menu insertion.
	 *
	 * @return version
	 */
	@Override
	public final Component prepareForMenu() { return menu; }


	/**
	 * Get action.
	 *
	 * @param i the index
	 *
	 * @return the action
	 */
	public final STMacro getAction(final int i) { return actions[i]; }


	/**
	 * Get item.
	 *
	 * @param i the index
	 *
	 * @return the item
	 */
	public final JMenuItem getItem(final int i) { return items[i]; }


	/**
	 * Get items.
	 *
	 * @return the items
	 */
	public final JMenuItem[] getItems() { return items; }


	/**
	 * Set the local id.
	 * 
	 * @param localId the local id
	 */
	public final void setLocalId(final String localId) { menu.setText(STGraphC.getMessage("MENU_" + localId)); } //$NON-NLS-1$


	/**
	 * Control the enabling/disabling of the action on the basis of the system state.
	 */
	@Override
	public final void setEnabledOnState() { setEnabled(true); }

}
