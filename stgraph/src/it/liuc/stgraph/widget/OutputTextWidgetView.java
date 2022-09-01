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
import it.liuc.stgraph.util.STTools;

import java.awt.Graphics;

import org.jgraph.graph.VertexRenderer;
import org.nfunk.jep.type.Tensor;


/** View handler for the OutputText widget. */
@SuppressWarnings("serial")
public class OutputTextWidgetView extends WidgetView {
	/** The widget. */
	private OutputTextWidget widget;


	/** Class constructor.
	 * @param widget the widget */
	public OutputTextWidgetView(final STWidget widget) {
		super(widget);
		setDefaultWidgetBounds(OutputTextWidget.PREFIX + ".WIDGETBOUNDS");
		this.widget = (OutputTextWidget)widget;
		setRenderer(new OutputTextWidgetRenderer());
	}


	/** Renderer. */
	public class OutputTextWidgetRenderer extends VertexRenderer {
		/** The datasource. */
		private STNode datasource = null;


		/** Class constructor. */
		public OutputTextWidgetRenderer() { initView(); }


		/** Init view. */
		public final void initView() { datasource = (STNode)widget.getProperty(OutputTextWidget.PROP_SOURCE_OB); }


		/** Paint.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			Tensor v = null;
			if(datasource == null || (v = (Tensor)datasource.getValue()) == null) {
				widget.getIndicator().setText(STTools.BLANK);
			} else {
				widget.getIndicator().setText(datasource.getOutputText((int)v.getValue()));
			}
		}
	}

}
