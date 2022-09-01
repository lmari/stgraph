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

import it.liuc.stgraph.node.STNode;

import java.awt.Graphics;

import org.jgraph.graph.VertexRenderer;
import org.nfunk.jep.type.Tensor;


/** View handler for the Gauge widget. */
@SuppressWarnings("serial")
public class GaugeWidgetView extends WidgetView {
	/** The widget. */
	private GaugeWidget widget;


	/** Class constructor.
	 * @param widget the widget */
	public GaugeWidgetView(final STWidget widget) {
		super(widget);
		setDefaultWidgetBounds(GaugeWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		this.widget = (GaugeWidget)widget;
		setRenderer(new GaugeWidgetRenderer());
	}


	/** Renderer. */
	public class GaugeWidgetRenderer extends VertexRenderer {
		/** The datasource. */
		private STNode datasource = null;


		/** Class constructor. */
		public GaugeWidgetRenderer() { initView(); }


		/** Init view. */
		public final void initView() { datasource = (STNode)widget.getProperty(GaugeWidget.PROP_SOURCE_OB); }


		/** Paint.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			if(widget.frame == null) { return; }
			if(g != null) {
				Tensor v = null;
				if(datasource != null && (v = (Tensor)datasource.getValue()) != null) {
					widget.getGauge().setValue(v.getValue());
					widget.setTitle();
					widget.frame.paint(g);
				}
			}
		}
	}

}
