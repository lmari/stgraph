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

import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Linear interpolation function. */
public class Line extends STInterpolationFun {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double a;
	private double b;


	/** Init method.
	 * @param nx the nx
	 * @param ny the ny
	 * @return result
	 * @throws ParseException the parse exception */
	final Object init(final Tensor nx, final Tensor ny) throws ParseException {
		if(nx.getSize() != 2 || ny.getSize() != 2) { return STInterpreter.handleException(getException("ERR.FUN.TWOVALUES_REQUIRED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		double x0 = nx.getScalar(0).getValue();
		double x1 = nx.getScalar(1).getValue();
		if(x0 >= x1) { return STInterpreter.handleException(getException("ERR.FUN.EQUAL_XVALUES"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		double y0 = ny.getScalar(0).getValue();
		double y1 = ny.getScalar(1).getValue();
		a = (y1 - y0) / (x1 - x0);
		b = y0 - x0 * a;
		return null;
	}


	/** Exec method.
	 * @param x the x
	 * @return result
	 * @throws ParseException the parse exception */
	final Object exec(final double x) throws ParseException { return Tensor.newScalar(a * x + b); }

}
