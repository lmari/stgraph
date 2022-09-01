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
import java.awt.event.ActionEvent;

import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;


/**
 * Menu separator handler.
 */
public class STSeparator extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default class constructor.
	 */
	public STSeparator() { ; }


	/**
	 * Create and return a version of this action suitable for toolbar insertion.
	 *
	 * @return version
	 */
	@Override
	public final Component prepareForToolBar() {
		JToolBar.Separator sep = new JToolBar.Separator();
		sep.setOrientation(SwingConstants.VERTICAL);
		return sep;
	}


	/**
	 * Create and return a version of this action suitable for menu insertion.
	 * 
	 * @return version
	 */
	@Override
	public final Component prepareForMenu() { return new JSeparator(); }


	/**
	 * Action method.
	 * 
	 * @param e the e
	 */
	@Override
	public final void actionPerformed(final ActionEvent e) { ; }

}
