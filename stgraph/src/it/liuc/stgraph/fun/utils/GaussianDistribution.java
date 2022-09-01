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
//This file has been modified to integrate it in STGraph, Copyright 2004-2012, Luca Mari.
package it.liuc.stgraph.fun.utils;

/**
 * Normal (aka Gaussian) distribution; See the <A HREF="http://www.cern.ch/RD11/rkb/AN16pp/node188.html#SECTION0001880000000000000000"> math definition</A>
 * and <A HREF="http://www.statsoft.com/textbook/glosn.html#Normal Distribution"> animated definition</A>.
 * <pre>                       
 * 				   1                       2
 * 	  pdf(x) = ---------    exp( - (x-mean) / 2v ) 
 * 			   sqrt(2pi*v)
 * 
 * 							x
 * 							 -
 * 				   1        | |                 2
 * 	  cdf(x) = ---------    |    exp( - (t-mean) / 2v ) dt
 * 			   sqrt(2pi*v)| |
 * 						   -
 * 						  -inf.
 * </pre>
 * where <tt>v = variance = standardDeviation^2</tt>.
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b> Polar Box-Muller transformation. See 
 * G.E.P. Box, M.E. Muller (1958): A note on the generation of random normal deviates, Annals Math. Statist. 29, 610-611.
 * <p>
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class GaussianDistribution {


	/**
	 * Get the probability distribution function.
	 */
	public static double pdf(final double x, final double mean, final double variance) {
		double diff = x - mean;
		return (1.0 / Math.sqrt(2.0 * Math.PI * variance)) * Math.exp(-(diff * diff) / (2.0 * variance));
	}


	/**
	 * Get the cumulative distribution function.
	 */
	public static double cdf(final double x, final double mean, final double variance) {
		if(x > 0) { return 0.5 + 0.5 * errorFunction((x - mean) / Math.sqrt(2.0 * variance)); }
		return 0.5 - 0.5 * errorFunction((-(x - mean)) / Math.sqrt(2.0 * variance));
	}


	/**
	 * Get a random number from the distribution.
	 */
	public static double nextDouble(final double mean, final double standardDeviation) {
		// Uses polar Box-Muller transformation.
		double x, y, r, z;
		do {
			x = 2.0 * Math.random() - 1.0; 
			y = 2.0 * Math.random() - 1.0;		 
			r = x * x + y * y;
		} while(r >= 1.0);
		z = Math.sqrt(-2.0 * Math.log(r) / r);
		return mean + standardDeviation * y * z;
	}


	/**
	 * The errorFunction.
	 *
	 * @param x the x
	 *
	 * @return result
	 */
	static double errorFunction(final double x) { 
		double y, z;
		final double[] t = {
				9.60497373987051638749E0,
				9.00260197203842689217E1,
				2.23200534594684319226E3,
				7.00332514112805075473E3,
				5.55923013010394962768E4
		};
		final double[] u = {
				3.35617141647503099647E1,
				5.21357949780152679795E2,
				4.59432382970980127987E3,
				2.26290000613890934246E4,
				4.92673942608635921086E4
		};
		if(Math.abs(x) > 1.0) { return 1.0 - errorFunctionComplemented(x); }
		z = x * x;
		y = x * polevl(z, t, 4) / p1evl(z, u, 5);
		return y;
	}


	/**
	 * The errorFunctionComplemented.
	 *
	 * @param a the a
	 *
	 * @return result
	 */
	static double errorFunctionComplemented(final double a) {
		double x, y, z, p, q;
		double[] pp = {
				2.46196981473530512524E-10,
				5.64189564831068821977E-1,
				7.46321056442269912687E0,
				4.86371970985681366614E1,
				1.96520832956077098242E2,
				5.26445194995477358631E2,
				9.34528527171957607540E2,
				1.02755188689515710272E3,
				5.57535335369399327526E2
		};
		double[] qq = {
				1.32281951154744992508E1,
				8.67072140885989742329E1,
				3.54937778887819891062E2,
				9.75708501743205489753E2,
				1.82390916687909736289E3,
				2.24633760818710981792E3,
				1.65666309194161350182E3,
				5.57535340817727675546E2
		};
		double[] rr = {
				5.64189583547755073984E-1,
				1.27536670759978104416E0,
				5.01905042251180477414E0,
				6.16021097993053585195E0,
				7.40974269950448939160E0,
				2.97886665372100240670E0
		};
		double[] ss = {
				2.26052863220117276590E0,
				9.39603524938001434673E0,
				1.20489539808096656605E1,
				1.70814450747565897222E1,
				9.60896809063285878198E0,
				3.36907645100081516050E0
		};
		double maxlog =  7.09782712893383996732E2;
		x = a < 0.0 ? -a : a;
		if(x < 1.0) { return 1.0 - errorFunction(a); }
		z = -a * a;
		if(z < -maxlog) {
			if(a < 0) { return 2.0; }
			return 0.0;
		}
		z = Math.exp(z);
		if(x < 8.0) {
			p = polevl(x, pp, 8);
			q = p1evl(x, qq, 8);
		} else {
			p = polevl(x, rr, 5);
			q = p1evl(x, ss, 6);
		}
		y = (z * p) / q;
		if(a < 0) { y = 2.0 - y; }
		if(y == 0.0) {
			if(a < 0) { return 2.0; }
			return 0.0;
		}
		return y;
	}


	/**
	 * Evaluate the given polynomial of degree <tt>N</tt> at <tt>x</tt>, assuming coefficient of N is 1.0.
	 * Otherwise same as <tt>polevl()</tt>.
	 *
	 * @param x argument to the polynomial.
	 * @param coef the coefficients of the polynomial.
	 * @param n the degree of the polynomial.
	 *
	 * @return result
	 */
	static double p1evl(final double x, final double[] coef, final int n) {
		double ans;
		ans = x + coef[0];
		for(int i = 1; i < n; i++) { ans = ans * x + coef[i]; }
		return ans;
	}


	/**
	 * Evaluate the given polynomial of degree <tt>N</tt> at <tt>x</tt>.
	 *
	 * @param x argument to the polynomial.
	 * @param coef the coefficients of the polynomial.
	 * @param n the degree of the polynomial.
	 *
	 * @return result
	 */
	static double polevl(final double x, final double[] coef, final int n) {
		double ans;
		ans = coef[0];
		for(int i = 1; i <= n; i++) { ans = ans * x + coef[i]; }
		return ans;
	}

}
