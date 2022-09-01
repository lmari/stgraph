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
package it.liuc.stgraph.node;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.util.STTools;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;


/** Gather a few methods related to edges. */
@SuppressWarnings("serial")
public class STEdge extends DefaultEdge {
	/** The Constant PROP_POINTS. */
	public static final String PROP_POINTS = "points"; // map of spline vertexes //$NON-NLS-1$

	/** The Constant DEFAULT_COLOR. */
	public static final Color DEFAULT_COLOR = Color.BLUE;

	/** The edge view. */
	private transient STEdgeView view = null;
	/** The name. */
	private String name = STTools.BLANK;
	/** The data source. */
	private STNode dataSource = null;


	/** Class constructor. */
	public STEdge() { super(); }


	/** Get a reference to the view for this edge.
	 * @return view */
	public STEdgeView getView() {
		if(view == null) { view = new STEdgeView(this); }
		return view;
	}


	/** Set the name of this edge.
	 * @param name the name */
	public final void setName(final String name) {
		if(STTools.isEmpty(name) || name.equals(STGraphC.getMessage("EDGE.DEFAULT_LABEL"))) { //$NON-NLS-1$
			setUserObject(this.name = STGraphC.getMessage("EDGE.DEFAULT_LABEL")); //$NON-NLS-1$
		} else {
			setUserObject(this.name = name);
		}
	}


	/** Get the name of this edge.
	 * @return name */
	public final String getName() { return name; }


	/** Set the label of this edge.
	 * @param label the name */
	public final void setLabel(final String label) { setUserObject(label); }


	/** Show the label of this edge.
	 * @param selected is selected */
	public final void showLabel(final boolean selected) {
		if(STTools.isEmpty(name) || name.equals(STGraphC.getMessage("EDGE.DEFAULT_LABEL"))) { //$NON-NLS-1$
			if(selected) {
				setUserObject(STGraphC.getMessage("EDGE.DEFAULT_LABEL")); //$NON-NLS-1$
			} else {
				setUserObject(STTools.BLANK);
			}
		} else {
			setUserObject(name);
		}
	}


	/** Get the source node of this edge.
	 * @return node */
	public final STNode getSourceNode() {
		DefaultPort dp = (DefaultPort)getSource(); 
		if(dp == null) { return null; }
		return (STNode)dp.getParent();
	}


	/** Get the name of the source node of this edge.
	 * @return node */
	public final String getSourceNodeName() {
		STNode node = getSourceNode();
		if(node == null) { return null; }
		return (String)node.getUserObject();
	}


	/** Get the target node of this edge.
	 * @return node */
	public final STNode getTargetNode() {
		DefaultPort dp = (DefaultPort)getTarget(); 
		if(dp == null) { return null; }
		return (STNode)dp.getParent();
	}


	/** Get the name of the target node of this edge.
	 * @return node */
	public final String getTargetNodeName() {
		STNode node = getTargetNode();
		if(node == null) { return null; }
		return (String)node.getUserObject();
	}


	/** Get the edge among the specified nodes, or null if they are not connected.
	 * @param source the source node
	 * @param target the target node
	 * @return the edge */
	public final static STEdge getEdge(final STNode source, final STNode target) {
		if(source == null || target == null) { return null; }
		STGraphImpl graph = source.getGraph();
		if(target.getGraph() != graph) { return null; }
		STEdge[] edges = graph.getEdges();
		if(edges == null || edges.length == 0) { return null; }
		for(STEdge edge : edges) {
			if(edge.getSourceNode() == source && edge.getTargetNode() == target) { return edge; }
		}
		return null;
	}


	/** Set the position of this edge label.
	 * @param p the p */
	public final void setLabelPosition(final Point2D p) { GraphConstants.setLabelPosition(getAttributes(), p); }


	/** Get the position of this edge label.
	 * @return position */
	public final Point2D getLabelPosition() { return GraphConstants.getLabelPosition(getAttributes()); }


	/** Set the data source.
	 * @param dataSource the dataSource to set */
	public void setDataSource(STNode dataSource) { this.dataSource = dataSource; }


	/** Get the data source.
	 * @return the dataSource */
	public STNode getDataSource() { return dataSource; }


	/** Set the color of this edge.
	 * @param color the color */
	public final void setColor(final Color color) {
		GraphConstants.setLineColor(getAttributes(), color);
		GraphConstants.setLineWidth(getAttributes(), (float)3.0);
		STGraph.getSTC().getCurrentGraph().getGraphLayoutCache().setVisible(this, true); // to force repaint...
	}


	/** Reset the color of this edge. */
	public final void resetColor() {
		GraphConstants.setLineColor(getAttributes(), DEFAULT_COLOR);
		GraphConstants.setLineWidth(getAttributes(), (float)1.0);
		STGraph.getSTC().getCurrentGraph().getGraphLayoutCache().setVisible(this, true); // to force repaint...
	}


	/** Select this edge, by properly scrolling the graph if required. 
	 * @param graph the graph */
	public final void select(final STGraphImpl graph) {
		graph.setSelectionCell(this);
		graph.scrollCellToVisible(this);
	}

}
