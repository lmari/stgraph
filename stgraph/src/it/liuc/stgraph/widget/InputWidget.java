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


/** Input widget. */
public interface InputWidget {
	/** The Constant PROP_SOURCE_OB. */
	String PROP_SOURCE_OB = "sourceob";	// reference to node //$NON-NLS-1$
	/** The Constant PROP_SOURCE_NA. */
	String PROP_SOURCE_NA = "source"; // node name //$NON-NLS-1$
	/** The Constant PROP_VALUE. */
	String PROP_VALUE = "widgetvalue"; // widget value //$NON-NLS-1$


	/** Return the current value of this widget.
	 * @return value */
	Object getValue();


	/** Return the next value of this widget.
	 * (Re)initialize the widget if required.
	 * @param isFirst the is first
	 * @return value */
	Object getNextValue(boolean isFirst);
	
	
	/** Set whether it is possible to interact with this widget.
	 * @param isFirst the is first */
	void setInteractable(boolean state);

}
