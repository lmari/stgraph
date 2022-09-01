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


/** View handler for the Slider widget. */
@SuppressWarnings("serial")
public class SliderWidgetView extends WidgetView {
	/** The widget. */
	private SliderWidget widget;


	/** Class constructor.
	 * @param _widget the widget */
	public SliderWidgetView(final STWidget _widget) {
		super(_widget);
		setDefaultWidgetBounds(SliderWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		widget = (SliderWidget)_widget;
		setRenderer(new SliderWidgetRenderer());
	}


	/** Renderer. */
	public class SliderWidgetRenderer extends VertexRenderer {

		/** Paint method.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			if(g == null) { return; }
			STNode datasource = (STNode)widget.getProperty(InputWidget.PROP_SOURCE_OB);
			if(datasource != null) {
				widget.getSlider().setPaintTicks(true);
				widget.getSlider().setPaintLabels(true);
				
				//widget.getSlider().setEnabled(datasource.getGraph().getState() == 0 || datasource.getGraph().isFirstCall());
					
			}
			widget.setTitle();
			widget.frame.paint(g);
		}

	}

}
