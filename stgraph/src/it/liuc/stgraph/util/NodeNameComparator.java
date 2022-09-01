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

import it.liuc.stgraph.node.STNode;

import java.util.Comparator;


/**
 * Comparator for name of nodes as objects.
 */
@SuppressWarnings("rawtypes")
public class NodeNameComparator implements Comparator {


	/**
	 * Comparison criterion.
	 *
	 * @param o1 the o1
	 * @param o2 the o2
	 *
	 * @return int
	 */
	public final int compare(final Object o1, final Object o2) {
		String p1 = ((STNode)o1).getName();
		String p2 = ((STNode)o2).getName();
		return p1.compareTo(p2);
	}

}
