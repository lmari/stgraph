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
import it.liuc.stgraph.fun.utils.ChiSquareDistribution;
import it.liuc.stgraph.node.STData;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Chi square distribution function.
 * <br>Wrapping the class(es) only marginally adapted from the COLT library. */
public class ChiSquare extends STDistributionFun {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Get parameters.
	 * @param v the input vector: degrees of freedom
	 * @return output vector: degrees of freedom */
	@Override final double[] getParams(final Tensor v) { return new double[] {v.getValue()}; }


	/** Check parameters.
	 * @param v the input vector
	 * @return check
	 * @throws ParseException the parse exception */
	@Override final Object checkParams(final Tensor v) throws ParseException {
		if(v.getSize() != 1) { return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		if(v.getValue() <= 0) { return STInterpreter.handleException(getException("ERR.FUN.POSITIVE_VALUE_REQUIRED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		return null;
	}


	/** Get default parameters.
	 * @return default data */
	@Override final double[] getDefaultParams() { return new double[] { 5.0 }; } // degrees of freedom


	/** Get next value.
	 * @param data the data
	 * @return the object */
	@Override final Object exec(final double[] data) { return Tensor.newScalar(ChiSquareDistribution.nextDouble(data[0])); }


	/** Get a pdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec0(final double value, final double[] data) { return Tensor.newScalar(ChiSquareDistribution.pdf(data[0], value)); }


	/** Get a cdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec1(final double value, final double[] data) { return Tensor.newScalar(ChiSquareDistribution.cdf(data[0], value)); }


	/** Get an inverse cdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec2(final double value, final double[] data) { return Tensor.newScalar(invcdf(value, data[0])); }


	/** Inverse cdf
	 * @param x the x probability value
	 * @param n degrees of freedom
	 * @return the cdf value */
	private static double invcdf(final double x, final double n) { return Gamma.invcdf(x, n / 2, 2); }

}
