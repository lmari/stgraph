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
 * StudentT distribution (aka T-distribution); See the <A HREF="http://www.cern.ch/RD11/rkb/AN16pp/node279.html#SECTION0002790000000000000000"> math definition</A>
 * and <A HREF="http://www.statsoft.com/textbook/gloss.html#Student's t Distribution"> animated definition</A>.
 * <p>
 * <tt>p(x) = k  *  (1+x^2/f) ^ -(f+1)/2</tt> where <tt>k = g((f+1)/2) / (sqrt(pi*f) * g(f/2))</tt> and <tt>g(a)</tt> being the gamma function and <tt>f</tt> being the degrees of freedom.
 * <p>
 * Valid parameter ranges: <tt>freedom &gt; 0</tt>.
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 * <b>Implementation:</b>
 * <dt>
 * Method: Adapted Polar Box-Muller transformation.
 * <dt>
 * This is a port of <A HREF="http://wwwinfo.cern.ch/asd/lhc++/clhep/manual/RefGuide/Random/RandStudentT.html">RandStudentT</A> used in <A HREF="http://wwwinfo.cern.ch/asd/lhc++/clhep">CLHEP 1.4.0</A> (C++).
 * CLHEP's implementation, in turn, is based on <tt>tpol.c</tt> from the <A HREF="http://www.cis.tu-graz.ac.at/stat/stadl/random.html">C-RAND / WIN-RAND</A> library.
 * C-RAND's implementation, in turn, is based upon
 * <p>R.W. Bailey (1994): Polar generation of random variates with the t-distribution, Mathematics of Computation 62, 779-781.
 *
 * Modified / simplified (customized for STGraph) by lm
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class StudentTDistribution {


	/**
	 * Get the probability distribution function.
	 */
	public static double pdf(double freedom, double x) {
		double val = Fun.logGamma((freedom + 1) / 2) - Fun.logGamma(freedom / 2);
		double term = Math.exp(val) / Math.sqrt(Math.PI * freedom);
		return term * Math.pow((1 + x * x / freedom), -(freedom + 1) * 0.5);
	}


	/**
	 * Get the cumulative distribution function.
	 */
	public static double cdf(double freedom, double x) {
		return Probability.studentT(freedom,x);
	}


	/**
	 * Get a random number from the distribution; bypasses the internal state.
	 * @param degreesOfFreedom degrees of freedom.
	 * @throws IllegalArgumentException if <tt>a &lt;= 0.0</tt>.
	 */
	public static double nextDouble(double degreesOfFreedom) {
		/*
		 * The polar method of Box/Muller for generating Normal variates 
		 * is adapted to the Student-t distribution. The two generated   
		 * variates are not independent and the expected no. of uniforms 
		 * per variate is 2.5464.
		 *
		 * REFERENCE :  - R.W. Bailey (1994): Polar generation of random  
		 *                variates with the t-distribution, Mathematics   
		 *                of Computation 62, 779-781.
		 */
		if(degreesOfFreedom<=0.0) { throw new IllegalArgumentException(); }
		double u,v,w;
		do {
			u = 2.0 * Math.random() - 1.0;
			v = 2.0 * Math.random() - 1.0;
		} while((w = u * u + v * v) > 1.0);
		return(u * Math.sqrt( degreesOfFreedom * ( Math.exp(- 2.0 / degreesOfFreedom * Math.log(w)) - 1.0) / w));
	}

}
