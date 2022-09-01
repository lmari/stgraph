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


/** View handler for the ToggleIndicator widget. */
@SuppressWarnings("serial")
public class ToggleIndicatorWidgetView extends WidgetView {
	/** The widget. */
	private ToggleIndicatorWidget widget;


	/** Class constructor.
	 * @param widget the widget */
	public ToggleIndicatorWidgetView(final STWidget widget) {
		super(widget);
		setDefaultWidgetBounds(ToggleIndicatorWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		this.widget = (ToggleIndicatorWidget)widget;
		setRenderer(new ToggleIndicatorWidgetRenderer());
	}


	/** Renderer. */
	public class ToggleIndicatorWidgetRenderer extends VertexRenderer {
		/** The datasource. */
		private STNode datasource = null;


		/** Class constructor. */
		public ToggleIndicatorWidgetRenderer() { initView(); }


		/** Init view. */
		public final void initView() { datasource = (STNode)widget.getProperty(ToggleIndicatorWidget.PROP_SOURCE_OB); }


		/** Paint.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			Tensor v = null;
			if(datasource == null || (v = (Tensor)datasource.getValue()) == null || v.getValue() == 0.0) {
				widget.getIndicator().setLedOn(false);
			} else {
				widget.getIndicator().setLedOn(true);
			}
		}
	}

}
