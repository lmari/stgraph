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


/** Polyline interpolation function. */
public class PLine extends STInterpolationFun {
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
			if(i > 0 && xx[i] <= xx[i - 1]) { return STInterpreter.handleException(getException("ERR.FUN.EQUAL_XVALUES"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		}
		return null;
	}


	/** Exec method.
	 * @param x the x
	 * @return result
	 * @throws ParseException the parse exception */
	final Object exec(final double x) throws ParseException {
		double a;
		double b;
		if(x < xx[0]) {
			a = (yy[1] - yy[0]) / (xx[1] - xx[0]);
			b = yy[0] - xx[0] * a;
		} else if(x >= xx[np - 1]) {
			a = (yy[np - 1] - yy[np - 2]) / (xx[np - 1] - xx[np - 2]);
			b = yy[np - 2] - xx[np - 2] * a;
		} else {
			int i = 0;
			while(x >= xx[i]) {
				i++;
			}
			a = (yy[i] - yy[i - 1]) / (xx[i] - xx[i - 1]);
			b = yy[i - 1] - xx[i - 1] * a;
		}
		return Tensor.newScalar(a * x + b);
	}

}
