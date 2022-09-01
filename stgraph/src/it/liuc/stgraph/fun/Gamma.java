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
import it.liuc.stgraph.fun.utils.GammaDistribution;
import it.liuc.stgraph.node.STData;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Gamma distribution function.
 * <br>Wrapping the class(es) only marginally adapted from the COLT library. */
public class Gamma extends STDistributionFun {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Get parameters.
	 * @param v the input vector: alpha, lambda
	 * @return output vector: alpha, lambda */
	@Override final double[] getParams(final Tensor v) { return new double[] {v.getScalar(0).getValue(), v.getScalar(1).getValue()}; }


	/** Check parameters.
	 * @param v the input vector
	 * @return check
	 * @throws ParseException the parse exception */
	@Override final Object checkParams(final Tensor v) throws ParseException {
		if(v.getSize() != 2) { return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		if(v.getScalar(0).getValue() <= 0) { return STInterpreter.handleException(getException("ERR.FUN.ZERO_VALUE_NOTALLOWED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		if(v.getScalar(1).getValue() <= 0) { return STInterpreter.handleException(getException("ERR.FUN.ZERO_VALUE_NOTALLOWED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		return null;
	}


	/** Get default parameters.
	 * @return default data */
	@Override final double[] getDefaultParams() { return new double[] { 1.0, 1.0 }; } // alpha, lambda


	/** Get next value.
	 * @param data the data
	 * @return the object */
	@Override final Object exec(final double[] data) { return Tensor.newScalar(GammaDistribution.nextDouble(data[0], data[1])); }


	/** Get a pdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec0(final double value, final double[] data) { return Tensor.newScalar(GammaDistribution.pdf(data[0], data[1], value)); }


	/** Get a cdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec1(final double value, final double[] data) { return Tensor.newScalar(GammaDistribution.cdf(data[0], data[1], value)); }


	/** Get an inverse cdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec2(final double value, final double[] data) { return Tensor.newScalar(invcdf(value, data[0], data[1])); }


	/** Inverse cdf.
	 * @param x the x probability value
	 * @param a alpha
	 * @param b lambda
	 * @return the cdf value */
	static double invcdf(final double x, final double a, final double b) {
		if(x == 0) return 0.0;
		if(x == 1) return Double.POSITIVE_INFINITY;
		double y = (x < STParserTools.CT) ? Math.sqrt(STParserTools.CT) : a * b;
		double ty = y;
		double ny = 0.0;
		double h;
		for(int i = 0; i < 100; i++) {
			h = (GammaDistribution.cdf(a, b, ty) - x) / GammaDistribution.pdf(a, b, ty);
			ny = ty - h;
			if(ny <= STParserTools.CT) {
				ny = ty / 10;
				h = ty - ny;
			}
			if(Math.abs(h) < Math.sqrt(STParserTools.CT)) { break; }
			ty = ny;
		}
		return ny;
	}

}
