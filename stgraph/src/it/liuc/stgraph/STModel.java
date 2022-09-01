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

import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Edge;


/** A custom model that does not allow self-references. */
@SuppressWarnings("serial")
public class STModel extends DefaultGraphModel {


	/** Modifier of the standard behavior.
	 * @param edge the edge
	 * @param port the port
	 * @return true, if accepts source
	 * @see org.jgraph.graph.GraphModel#acceptsSource(java.lang.Object, java.lang.Object) */
	public final boolean acceptsSource(final Object edge, final Object port) { return (((Edge)edge).getTarget() != port); }


	/** Modifier of the standard behavior.
	 * @param edge the edge
	 * @param port the port
	 * @return true, if accepts target
	 * @see org.jgraph.graph.GraphModel#acceptsTarget(java.lang.Object, java.lang.Object) */
	public final boolean acceptsTarget(final Object edge, final Object port) { return (((Edge)edge).getSource() != port); }

}
