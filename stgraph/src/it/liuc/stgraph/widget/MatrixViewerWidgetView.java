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
package it.liuc.stgraph.widget;

import it.liuc.stgraph.node.ValueNode;

import java.awt.Graphics;

import net.ericaro.surfaceplotter.DefaultSurfaceModel;
import net.ericaro.surfaceplotter.Mapper;

import org.jgraph.graph.VertexRenderer;
import org.nfunk.jep.type.Matrix;
import org.nfunk.jep.type.Tensor;


/** View handler for the Matrix viewer widget. */
@SuppressWarnings("serial")
public class MatrixViewerWidgetView extends WidgetView {
	/** The widget. */
	private MatrixViewerWidget widget;
	private Tensor data1;
	private Matrix data2;

	/** Class constructor.
	 * @param widget the widget */
	public MatrixViewerWidgetView(final STWidget widget) {
		super(widget);
		setDefaultWidgetBounds(MatrixViewerWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		this.widget = (MatrixViewerWidget)widget;
		setRenderer(new MatrixViewerWidgetRenderer());
	}


	/** Renderer. */
	public class MatrixViewerWidgetRenderer extends VertexRenderer {


		/** Paint.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			ValueNode source = widget.getSource();
			if(widget.frame == null || source == null || g == null) { return; }

			if(source.isMatrix()) {
				data1 = (Tensor)source.getValue();
				if(data1 == null) { return; }
				int rows = data1.getDimensions()[0] - 1;
				int cols = data1.getDimensions()[1] - 1;
				if(rows <= 0 || cols <= 0) { return; }
				DefaultSurfaceModel sm = widget.getPanelViewer().getModel();
				sm.setCalcDivisions(rows);
				sm.setDispDivisions(rows);
				sm.setXMin(0);
				sm.setXMax(cols);
				sm.setYMin(0);
				sm.setYMax(rows);
				sm.setMapper(new Mapper() {
					public float f1(float x, float y) {
						Tensor v0 = data1.getSubTensor((int)y, (int)x);
						return (float)(!v0.isNumber() ? 0 : v0.getValue());
					}
					public float f2(float x, float y) {
						return (float)(0.0);
					}
				});
				sm.plot().execute();
				widget.frame.paint(g);
				return;
			}
			if(source.isVector() && source.isVectorOutput()) {
				data2 = (Matrix)source.getValueHistory();
				if(data2 == null) { return; }
				int rows = data2.getRowCount() - 1;
				int cols = data2.getColumnCount() - 1;
				if(rows <= 0 || cols <= 0) { return; }
				DefaultSurfaceModel sm = widget.getPanelViewer().getModel();
				sm.setCalcDivisions(rows);
				sm.setDispDivisions(rows);
				sm.setXMin(0);
				sm.setXMax(cols);
				sm.setYMin(0);
				sm.setYMax(rows);
				sm.setMapper(new Mapper() {
					public float f1(float x, float y) {
						Tensor v0 = (Tensor)data2.get((int)y, (int)x);
						return (float)(!v0.isNumber() ? 0 : v0.getValue());
					}
					public float f2(float x, float y) {
						return (float)(0.0);
					}
				});
				sm.plot().execute();
				widget.frame.paint(g);
				return;
			}
		}
	}

}
