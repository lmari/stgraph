/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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

import java.awt.GridBagConstraints;
import java.awt.Insets;


/** Utility class to make the GridBagConstraints definitions shorter. */
@SuppressWarnings("serial")
public class GridConstraints extends GridBagConstraints {


	/** Class constructor.
	 * @param gridx the gridx
	 * @param gridy the gridy */
	public GridConstraints(final int gridx, final int gridy) {
		this.gridx = gridx;
		this.gridy = gridy;
		insets = new Insets(2, 2, 2, 2);
	}


	/** Class constructor.
	 * @param gridx the gridx
	 * @param gridy the gridy
	 * @param anchor the anchor */
	public GridConstraints(final int gridx, final int gridy, final int anchor) {
		this(gridx, gridy);
		this.anchor = anchor;
	}

}
