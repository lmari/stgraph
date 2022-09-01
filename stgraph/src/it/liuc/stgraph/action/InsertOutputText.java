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
import it.liuc.stgraph.widget.OutputTextWidget;


/** Insert OutputText action. */
public class InsertOutputText extends AbstractActionDefault {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Default class constructor. */
	public InsertOutputText() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public InsertOutputText(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Action method. */
	public final void exec() {
		STGraphC stc = STGraph.getSTC();
		OutputTextWidget widget = (OutputTextWidget)STFactory.widgetCreate(OutputTextWidget.ID);
		stc.getCurrentGraph().refreshGraph();
		stc.getCurrentGraph().setSelectionCell(widget);
		STGraphC.setFocus();
	}

}
