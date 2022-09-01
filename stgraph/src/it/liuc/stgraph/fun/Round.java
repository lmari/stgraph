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


/** Round operator. */
public class Round extends STDyadicFun {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public Round() { super(); }


	/** Exec method.
	 * Adapted from Jakarta Commons Math.
	 * @param x1 the x1
	 * @param x2 the x2
	 * @return the object */
	@Override final Tensor exec(final Tensor x1, final Tensor x2) {
		double d1 = x1.getValue();
		double d2 = x2.getValue();
		if(d2 == 0) { return Tensor.newScalar((int)(d1 + 0.5)); }
		double sign = Double.isNaN(d1) ? Double.NaN : ((d1 >= 0.0) ? 1.0 : -1.0);
		double factor = Math.pow(10.0, d2) * sign;
		double unscaled = d1 * factor;
		double fraction = Math.abs(unscaled - Math.floor(unscaled));
		unscaled = (fraction >= 0.5) ? Math.ceil(unscaled) : Math.floor(unscaled);
		return Tensor.newScalar(unscaled / factor);
	}

}
