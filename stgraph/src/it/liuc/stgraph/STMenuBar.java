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
package it.liuc.stgraph;

import it.liuc.stgraph.action.STMacroSubMenu;
import it.liuc.stgraph.action.STSubMenu;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JMenuBar;


/** Handle the menu bar. */
@SuppressWarnings("serial")
public class STMenuBar extends JMenuBar {
	/** The menus. */
	private transient STMenu[] menus;
	/** The help cursor. */
	private Cursor helpCursor;
	/** The index of the library menu. */
	private int libraryIndex = -1;

	/** The ordinal number of the web menu. */
	public static int WEBMENU_NUM;


	/** Class constructor.
	 * @param menus the menus */
	public STMenuBar(final STMenu[] menus) {
		this.menus = menus;
		for(int i = 0; i < menus.length; i++) { add(menus[i]); }
		for(int i = 0; i < getMenuCount(); i++) {
			if(((STMenu)getMenu(i)).getLocalId().equals("LIBRARY")) { //$NON-NLS-1$
				libraryIndex = i;
				break;
			}
		}
		updateItems();
		Toolkit tk = Toolkit.getDefaultToolkit();
		helpCursor = tk.createCustomCursor(tk.createImage(STGraph.getSTC().getBaseIcon(STConfigurator.getProperty("SYSTEM.HELP.ICON"))), new Point(1, 1), "helpcursor"); //$NON-NLS-1$ //$NON-NLS-2$
		if(getMenuCount() == 9) { // if also "library" is there...
			WEBMENU_NUM = 6;
		} else {
			WEBMENU_NUM = 5;
		}
	}


	/** Get the help cursor.
	 * @return cursor */
	public final Cursor getHelpCursor() { return helpCursor; }


	/** Handle the activation and deactivation of the menu items in a centralized way. */
	public final void updateItems() {
		STSubMenu subMenu;
		STMacroSubMenu macroSubMenu;
		Component[] c;
		for(STMenu menu : menus) {
			if((c = menu.getItems()) != null) {
				for(int j = 0; j < c.length; j++) {
					menu.getAction(j).setEnabledOnState();
					menu.getItems()[j].setEnabled(menu.getAction(j).isEnabled());
					if(menu.getAction(j) instanceof STSubMenu) {
						subMenu = (STSubMenu)menu.getAction(j);
						for(int jj = 0; jj < subMenu.getItems().length; jj++) {
							subMenu.getAction(jj).setEnabledOnState();
							subMenu.getItem(jj).setEnabled(subMenu.getAction(jj).isEnabled());
						}
					} else if(menu.getAction(j) instanceof STMacroSubMenu) {
						macroSubMenu = (STMacroSubMenu)menu.getAction(j);
						if(macroSubMenu.getItems() != null) {
							for(int jj = 0; jj < macroSubMenu.getItems().length; jj++) {
								macroSubMenu.getAction(jj).setEnabledOnState();
								macroSubMenu.getItem(jj).setEnabled(macroSubMenu.getAction(jj).isEnabled());
							}
						}
					}
				}
			}
		}
		if(libraryIndex != -1) {
			STMenu menu = (STMenu)getMenu(libraryIndex);
			for(int j = 0; j < menu.getItemCount(); j++) {
				if(menu.getItem(j) instanceof STMenu) {
					STMenu m = (STMenu)menu.getItem(j);
					if(m.getItems() != null) {
						for(int jj = 0; jj < m.getItems().length; jj++) {
							m.getAction(jj).setEnabledOnState();
							m.getItem(jj).setEnabled(m.getAction(jj).isEnabled());
						}
					}
				}
			}
		}
	}

}
