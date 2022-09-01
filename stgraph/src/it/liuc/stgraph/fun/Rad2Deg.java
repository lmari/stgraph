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
package it.liuc.stgraph.fun;

import org.nfunk.jep.type.Tensor;


/**
 * Radians to degrees function.
 */
public class Rad2Deg extends STMonadicFun {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Exec method.
	 * @param x the x
	 * @return the object */
	@Override final Tensor exec(final Tensor x) { return Tensor.newScalar(x.getValue() * 180 / Math.PI); }

}
