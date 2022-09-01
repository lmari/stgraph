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
package it.liuc.stgraph.fun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import it.liuc.stgraph.STGraphC;


/** Function menu class. */
public class FunctionMenu implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The name. */
	private String name;
	/** The items. */
	private transient Object[] items;


	/** Class constructor.
	 * @param name the name
	 * @param items the items */
	public FunctionMenu(final String name, final Object[] items) {
		this.name = name;
		this.items = items;
	}


	/** Get the items.
	 * @return the items */
	public final Object[] getItems() { return items; }


	/** Add the specified items to this menu.
	 * @param items the array of the items to add */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void addItems(final Object[] items) {
		ArrayList t = new ArrayList(this.items.length + items.length);
		for(int i = 0; i < this.items.length; i++) { t.add(i, this.items[i]); }
		for(int i = 0; i < items.length; i++) { t.add(this.items.length + i, items[i]); }
		Collections.sort(t, new FunctionComparator());
		this.items = new Object[this.items.length + items.length];
		for(int i = 0; i < this.items.length; i++) { this.items[i] = t.get(i); }
	}

	/** Comparator for custom functions. */
	@SuppressWarnings("rawtypes")
	public class FunctionComparator implements Comparator {

		/** Comparison criterion.
		 * @param o1 the o1
		 * @param o2 the o2
		 * @return int */
		public final int compare(final Object o1, final Object o2) {
			String p1 = o1.toString().toLowerCase();
			String p2 = o2.toString().toLowerCase();
			return p1.compareTo(p2);
		}
	}

	
	/** toString method.
	 * @return string */
	@Override
	public final String toString() { 
		if(name.startsWith("NODE.DIALOG.")) { return STGraphC.getMessage(name); } //$NON-NLS-1$
		return name;
	}

}
