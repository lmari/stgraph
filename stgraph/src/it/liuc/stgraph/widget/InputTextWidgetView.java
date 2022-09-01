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

import java.awt.Graphics;

import org.jgraph.graph.VertexRenderer;


/** View handler for the InputText widget. */
@SuppressWarnings("serial")
public class InputTextWidgetView extends WidgetView {

	/** The widget. */
	private InputTextWidget widget;


	/** Class constructor.
	 * @param widget the widget */
	public InputTextWidgetView(final STWidget widget) {
		super(widget);
		setDefaultWidgetBounds(InputTextWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		this.widget = (InputTextWidget)widget;
		setRenderer(new InputTextWidgetRenderer());
	}


	/** Renderer. */
	public class InputTextWidgetRenderer extends VertexRenderer {


		/** Paint method.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			if(g == null) { return; }
			widget.setTitle();
			widget.frame.paint(g);
		}
	}

}
