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


/** Spline interpolation function. */
public class Spline extends STInterpolationFun {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int np;
	private double[] xx;
	private double[] yy;


	/** Init method.
	 * @param nx the nx
	 * @param ny the ny
	 * @return result
	 * @throws ParseException the parse exception */
	final Object init(final Tensor nx, final Tensor ny) throws ParseException {
		np = nx.getSize();
		if(np < 2 || ny.getSize() < 2) { return STInterpreter.handleException(getException("ERR.FUN.MINTWOVALUES_REQUIRED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		if(np != ny.getSize()) { return STInterpreter.handleException(getException("ERR.FUN.NOT_SAME_SIZE"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		xx = new double[np];
		yy = new double[np];
		for(int i = 0; i < np; i++) {
			xx[i] = nx.getScalar(i).getValue();
			yy[i] = ny.getScalar(i).getValue();
		}
		for(int k = 1; k <= np - 1; k++) {
			for(int i = 0; i <= np - 1 - k; i++) { yy[i] = (yy[i + 1] - yy[i]) / (xx[i + k] - xx[i]); }
		}
		return null;
	}


	/** Exec method.
	 * @param x the x
	 * @return result
	 * @throws ParseException the parse exception */
	final Object exec(final double x) throws ParseException {
		double y = yy[0];
		for(int i = 1; i <= np - 1; i++) { y = y * (x - xx[i]) + yy[i]; }
		return Tensor.newScalar(y);
	}

}
