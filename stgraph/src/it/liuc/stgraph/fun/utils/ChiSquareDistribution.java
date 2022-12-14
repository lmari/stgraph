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
/*
Copyright 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
// This file has been modified to integrate it in STGraph, Copyright 2004-2012, Luca Mari.
package it.liuc.stgraph.fun.utils;

/**
 * ChiSquare distribution; See the <A HREF="http://www.cern.ch/RD11/rkb/AN16pp/node31.html#SECTION000310000000000000000"> math definition</A>
 * and <A HREF="http://www.statsoft.com/textbook/glosc.html#Chi-square Distribution"> animated definition</A>.
 * <dt>A special case of the Gamma distribution.
 * <p>
 * <tt>p(x) = (1/g(f/2)) * (x/2)^(f/2-1) * exp(-x/2)</tt> with <tt>g(a)</tt> being the gamma function and <tt>f</tt> being the degrees of freedom.
 * <p>
 * Valid parameter ranges: <tt>freedom &gt; 0</tt>.
 * <p> 
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b> 
 * <dt>
 * Method: Ratio of Uniforms with shift.
 * <dt>
 * High performance implementation. This is a port of <A HREF="http://wwwinfo.cern.ch/asd/lhc++/clhep/manual/RefGuide/Random/RandChiSquare.html">RandChiSquare</A> used in <A HREF="http://wwwinfo.cern.ch/asd/lhc++/clhep">CLHEP 1.4.0</A> (C++).
 * CLHEP's implementation, in turn, is based on <tt>chru.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>J.F. Monahan (1987): An algorithm for generating chi random variables, ACM Trans. Math. Software 13, 168-172.
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class ChiSquareDistribution {


	/**
	 * Get the probability distribution function.
	 */
	public static double pdf(double freedom, double x) {
		if(x <= 0.0) { throw new IllegalArgumentException(); }
		double logGamma = Fun.logGamma(freedom/2.0);
		return Math.exp((freedom/2.0 - 1.0) * Math.log(x/2.0) - x/2.0 - logGamma) / 2.0;
	}


	/**
	 * Get the cumulative distribution function.
	 */
	public static double cdf(double freedom, double x) {
		return Probability.chiSquare(freedom,x);
	}


	/**
	 * Get a random number from the distribution.
	 */
	public static double nextDouble(double freedom) {
		/******************************************************************
		 *                                                                *
		 *        Chi Distribution - Ratio of Uniforms  with shift        *
		 *                                                                *
		 ******************************************************************
		 *                                                                *
		 * FUNCTION :   - chru samples a random number from the Chi       *
		 *                distribution with parameter  a > 1.             *
		 * REFERENCE :  - J.F. Monahan (1987): An algorithm for           *
		 *                generating chi random variables, ACM Trans.     *
		 *                Math. Software 13, 168-172.                     *
		 * SUBPROGRAM : - anEngine  ... pointer to a (0,1)-Uniform        *
		 *                engine                                          *
		 *                                                                *
		 * Implemented by R. Kremer, 1990                                 *
		 ******************************************************************/

		double freedom_in = -1.0,b = 0,vm = 0,vp,vd = 0;
		double u,v,z,zz,r;

		if(freedom == 1.0) {
			for(;;) {
				u = Math.random();
				v = Math.random() * 0.857763884960707;
				z = v / u;
				if (z < 0) continue;
				zz = z * z;
				r = 2.5 - zz;
				if(z < 0.0) r = r + zz * z / (3.0 * z);
				if(u < r * 0.3894003915) return(z*z);
				if(zz > (1.036961043 / u + 1.4)) continue;
				if(2.0 * Math.log(u) < (- zz * 0.5 )) return(z*z);
			}
		}
		if(freedom != freedom_in) {
			b = Math.sqrt(freedom - 1.0);
			vm = - 0.6065306597 * (1.0 - 0.25 / (b * b + 1.0));
			vm = (-b > vm) ? -b : vm;
			vp = 0.6065306597 * (0.7071067812 + b) / (0.5 + b);
			vd = vp - vm;
			freedom_in = freedom;
		}
		for(;;) {
			u = Math.random();
			v = Math.random() * vd + vm;
			z = v / u;
			if(z < -b) continue;
			zz = z * z;
			r = 2.5 - zz;
			if(z < 0.0) r = r + zz * z / (3.0 * (z + b));
			if(u < r * 0.3894003915) return((z + b)*(z + b));
			if(zz > (1.036961043 / u + 1.4)) continue;
			if(2.0 * Math.log(u) < (Math.log(1.0 + z / b) * b * b - zz * 0.5 - z * b)) return((z + b)*(z + b));
		}
	}

}
