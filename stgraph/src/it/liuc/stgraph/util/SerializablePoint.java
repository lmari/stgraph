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

import java.awt.geom.Point2D;
import java.io.Serializable;


/** The Class SerializablePoint. */
@SuppressWarnings("serial")
public class SerializablePoint extends Point2D.Double implements Serializable {


	/** The Constructor.
	 * @param y the y
	 * @param x the x */
	public SerializablePoint(final double x, final double y) { super(x, y); }

}
