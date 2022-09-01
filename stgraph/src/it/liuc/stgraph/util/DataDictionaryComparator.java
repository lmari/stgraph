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


/** Comparator for data dictionary.
 * <br>Only input and output nodes, with custom properties InputType, OutputType, and Group properly set,
 * are assumed to be compared. */
@SuppressWarnings("rawtypes")
public class DataDictionaryComparator implements Comparator {


	/** Comparison criterion.
	 * @param o1 the o1
	 * @param o2 the o2
	 * @return int */
	public final int compare(final Object o1, final Object o2) {
		STNode n1 = (STNode)o1;
		STNode n2 = (STNode)o2;
		String g1 = n1.getCProperty("Group"); //$NON-NLS-1$
		String g2 = n2.getCProperty("Group"); //$NON-NLS-1$
		int t1 = 0;
		int t2 = 0;
		if(dealAsInput(n1)) {
			int t = Integer.parseInt(n1.getCProperty("InputType")); //$NON-NLS-1$
			if(t == 3) {
				t1 = 1;
			} else if(t == 6) {
				t1 = 4;
			}
		} else {
			int t = Integer.parseInt(n1.getCProperty("OutputType")); //$NON-NLS-1$
			if(t == 2 || t == 3) {
				t1 = 2;
			} else if(t == 4 || t == 5) {
				t1 = 3;
			} else if(t == 6) {
				t1 = 0;
				g1 = "0"; //$NON-NLS-1$
			}
		}
		if(dealAsInput(n2)) {
			int t = Integer.parseInt(n2.getCProperty("InputType")); //$NON-NLS-1$
			if(t == 3) {
				t2 = 1;
			} else if(t == 6) {
				t2 = 4;
			}
		} else {
			int t = Integer.parseInt(n2.getCProperty("OutputType")); //$NON-NLS-1$
			if(t == 2 || t == 3) {
				t2 = 2;
			} else if(t == 4 || t == 5) {
				t2 = 3;
			} else if(t == 6) {
				t2 = 0;
				g2 = "0"; //$NON-NLS-1$
			}
		}
		if(g1.compareTo(g2) < 0) { return -1; }
		if(g1.compareTo(g2) == 0 && t1 < t2) { return -1; }

		String x1 = n1.getCProperty("Order"); //$NON-NLS-1$
		String x2 = n2.getCProperty("Order"); //$NON-NLS-1$
		if(!STTools.isEmpty(x1) && !STTools.isEmpty(x2)) {
			if(g1.compareTo(g2) == 0 && t1 == t2 && x1.compareTo(x2) < 0) { return -1; }
			return 1;
		}

		String d1 = n1.getCName();
		String d2 = n2.getCName();
		if(g1.compareTo(g2) == 0 && t1 == t2 && d1.compareTo(d2) < 0) { return -1; }
		return 1;
	}

	// required to properly deal with nodes that are both input and output
	// and considering that the nodes to be compared are input or output (but nothing else)
	private boolean dealAsInput(final STNode n) {
		if(!n.isInput()) { return false; }
		String s;
		if(STTools.isEmpty(s = n.getCProperty("InputType"))) { return false; } //$NON-NLS-1$
		int t = Integer.parseInt(s);
		if(t == 3 || t == 6) { return true; }
		return false;
	}

}
