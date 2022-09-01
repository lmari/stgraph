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
package it.liuc.stgraph.node;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.jgraph.graph.CellHandle;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphContext;


/**
 * Edge view.
 * (mainly introduced to wrap the exceptions sometimes thrown by JGraph...)
 */
@SuppressWarnings("serial")
public class STEdgeView extends EdgeView {


	/**
	 * Class constructor.
	 *
	 * @param edge the edge
	 */
	public STEdgeView(final STEdge edge) {
		super(edge);
		renderer = new STEdgeRenderer();
	}


	@Override
	public CellHandle getHandle(final GraphContext context) {
		return new EdgeView.EdgeHandle(this, context) {
			public boolean isAddPointEvent(final MouseEvent event) { return event.isShiftDown(); } // points are added using shift-click
			public boolean isRemovePointEvent(final MouseEvent event) { return event.isShiftDown(); } // points are removed using shift-click
		};
	}


	@Override
	public Point2D getPoint(int index) {
		try {
			return super.getPoint(index);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * Renderer class.
	 */
	public class STEdgeRenderer extends EdgeRenderer {

		@Override
		protected Shape createShape() {
			try {
				return super.createShape();
			} catch (Exception e) {
				return null;
			}
		}


		@Override
		public Point2D getLabelPosition(EdgeView _view) {
			try {
				return super.getLabelPosition(_view);
			} catch (Exception e) {
				return null;
			}
		}


		@Override
		protected Point2D getLabelPosition(Point2D pos) {
			try {
				return super.getLabelPosition(pos);
			} catch (Exception e) {
				return null;
			}
		}

	}

}
