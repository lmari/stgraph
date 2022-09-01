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

import it.liuc.stgraph.STConfigurator;

import java.awt.Rectangle;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;


/** Superclass for widget views. */
@SuppressWarnings("serial")
public class WidgetView extends VertexView {
	/** The widget. */
	private STWidget widget;
	/** The default widget bounds. */
	private static Rectangle defaultWidgetBounds;
	/** The widget bounds. */
	private static Rectangle widgetBounds;
	/** The my renderer. */
	private transient CellViewRenderer myRenderer = null;


	/** Class constructor.
	 * @param widget the widget */
	public WidgetView(final STWidget widget) {
		super(widget);
		this.widget = widget;
	}


	/** Get the widget.
	 * @return the widget */
	public final STWidget getWidget() { return widget; }


	/** Set renderer.
	 * @param _myRenderer the renderer */
	public final void setRenderer(final CellViewRenderer myRenderer) { this.myRenderer = myRenderer; }


	/** Get renderer.
	 * @return the renderer */
	public final CellViewRenderer getRenderer() { return myRenderer; }


	/** Set default widget bounds.
	 * @param defaultWidgetBounds the default widget bounds */
	public static void setDefaultWidgetBounds(final Rectangle defaultWidgetBounds) { WidgetView.defaultWidgetBounds = defaultWidgetBounds; }


	/** Set default widget bounds.
	 * @param id the id */
	public static void setDefaultWidgetBounds(final String id) {
		String[] bounds = STConfigurator.getProperty(id).split(","); //$NON-NLS-1$
		setDefaultWidgetBounds(new Rectangle(Integer.parseInt(bounds[0]), Integer.parseInt(bounds[1]), Integer.parseInt(bounds[2]), Integer.parseInt(bounds[3])));
	}


	/** Get default widget bounds.
	 * @return the default widget bounds */
	public static Rectangle getDefaultWidgetBounds() { return defaultWidgetBounds; }


	/** Set widget bounds.
	 * @param widgetBounds the widget bounds */
	public final void setWidgetBounds(final Rectangle widgetBounds) {
		widget.frame.setBounds(widgetBounds);
	}


	/** Get widget bounds.
	 * @return the widget bounds */
	public final Rectangle getWidgetBounds() {
		if(widgetBounds == null) { return defaultWidgetBounds; }
		return widgetBounds;
	}

}
