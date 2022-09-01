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


/** Uniform distribution function. */
public class Uniform extends STDistributionFun {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Get parameters.
	 * @param v the input vector: mean, standard deviation
	 * @return output vector: left bound, right bound */
	@Override final double[] getParams(final Tensor v) {
		double mean = v.getScalar(0).getValue();
		double stddev = v.getScalar(1).getValue();
		double amp = 1.7321 * stddev;
		double a = mean - amp;
		double b = mean + amp;
		return new double[] {a, b};
	}


	/** Check parameters.
	 * @param v the input vector
	 * @return check
	 * @throws ParseException the parse exception */
	@Override final Object checkParams(final Tensor v) throws ParseException {
		if(v.getSize() != 2) { return STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		if(v.getScalar(1).getValue() <= 0) { return STInterpreter.handleException(getException("ERR.FUN.POSITIVE_VALUE_REQUIRED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		return null;
	}


	/** Get default parameters.
	 * @return default data */
	@Override final double[] getDefaultParams() { return new double[] { 0.0, 1,0 }; } // mean, standard deviation


	/** Get next value.
	 * @param data the data
	 * @return the object */
	@Override final Object exec(final double[] data) { return Tensor.newScalar((data[1] - data[0]) * Math.random() + data[0]); }


	/** Get a pdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec0(final double value, final double[] data) { return Tensor.newScalar(pdf(value, data[0], data[1])); }


	/** Get a cdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec1(final double value, final double[] data) { return Tensor.newScalar(cdf(value, data[0], data[1])); }


	/** Get an inverse cdf value.
	 * @param value the value
	 * @param data the data
	 * @return object */
	@Override final Object exec2(final double value, final double[] data) { return Tensor.newScalar(invcdf(value, data[0], data[1])); }


	/** PDF
	 * @param x the x value
	 * @param a left bound
	 * @param b right bound
	 * @return the PDF value */
	private static double pdf(final double x, final double a, final double b) {
		if(x < a || x > b) { return 0.0; }
		return 1 / (b - a);
	}


	/** CDF
	 * @param x the x value
	 * @param a left bound
	 * @param b right bound
	 * @return the CDF value */
	private static double cdf(final double x, final double a, final double b) {
		if(x < a) { return 0.0; }
		if(x > b) { return 1.0; }
		return (x - a) / (b - a);
	}


	/** Inverse CDF
	 * @param x the x probability value
	 * @param a left bound
	 * @param b right bound
	 * @return the CDF value */
	private static double invcdf(final double x, final double a, final double b) {
		if(x == 0) { return a; }
		if(x == 1) { return b; }
		return x * (b - a) + a;
	}

}
