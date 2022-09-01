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
package it.liuc.stgraph.util;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;


/** The Class SerializableRectangle. */
@SuppressWarnings("serial")
public class SerializableRectangle extends Rectangle2D.Double implements Serializable {


	/** The Constructor.
	 * @param x the x
	 * @param y the y
	 * @param w the w
	 * @param h the h */
	public SerializableRectangle(final double x, final double y, final double w, final double h) { super(x, y, w, h); }

}
