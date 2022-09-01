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
package it.liuc.stgraph.action;

import it.liuc.stgraph.STFactory;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.widget.InputTableWidget;


/** Insert InputTable action. */
public class InsertInputTable extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public InsertInputTable() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public InsertInputTable(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Action method. */
	public final void exec() {
		STGraphC stc = STGraph.getSTC();
		InputTableWidget widget = (InputTableWidget)STFactory.widgetCreate(InputTableWidget.ID);
		/* *** for unframed widgets ***
		SliderWidget2 widget = (SliderWidget2)STFactory.widget2Create(SliderWidget2.ID, SliderWidget2.defaultWidgetBounds);
		 */
		stc.getCurrentGraph().refreshGraph();
		stc.getCurrentGraph().setSelectionCell(widget);
		STGraphC.setFocus();
	}

}
