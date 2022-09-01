/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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

import java.awt.Rectangle;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.widget.STWidget;


/** Tools ZoomIn action. */
@SuppressWarnings("serial")
public class ToolsZoomIn extends AbstractActionDefault {


	/** Default class constructor. */
	public ToolsZoomIn() { ; }


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public ToolsZoomIn(final String keyChar, final int modifiers) { super(keyChar, modifiers); }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() { setEnabled(STGraph.getSTC().isCurrentDesktop());
	}


	/** Action method. */
	@Override
	public final void exec() {
		double n = 1.2;
		STGraph.getSTC().getCurrentGraph().setScale(n * STGraph.getSTC().getCurrentGraph().getScale());
		if(STGraphC.isZoomWidgets()) {
			Rectangle r;
			for(STWidget widget : STGraph.getSTC().getCurrentGraph().getWidgets()) {
				r = widget.frame.getBounds();
				widget.frame.setBounds(new Rectangle((int)(n * r.x), (int)(n * r.y), (int)(n * r.width), (int)(n * r.height)));
			}
		}
		STGraphC.setFocus();
	}

}
