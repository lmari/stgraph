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

import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

/** Custom menu key listener. */
public class STMenuKeyListener implements MenuKeyListener {
	/** The singleton instance of this class. */
	private static STMenuKeyListener mkl = new STMenuKeyListener();
	/** Has control key been pressed during the menu item selection?. */
	private static boolean controlDown;


	/** Hidden class constructor. */
	private STMenuKeyListener() { ; }


	/** Get the only instance of this class. */
	public static STMenuKeyListener getInstance() { return mkl; }

	public void menuKeyPressed(MenuKeyEvent e) { controlDown = e.isControlDown(); }

	public void menuKeyReleased(MenuKeyEvent e) { controlDown = false; }

	public void menuKeyTyped(MenuKeyEvent e) { ; }


	/** Get whether the control key has been pressed during the menu item selection.
	 * @return the controlDown */
	public static final boolean isControlDown() { return controlDown; }

}

