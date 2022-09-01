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
Copyright � 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
 */
//This file has been modified to integrate it in STGraph, Copyright 2004-2012, Luca Mari.
package it.liuc.stgraph.fun.utils;

/**
 * Exponential Distribution (aka Negative Exponential Distribution); See the <A HREF="http://www.cern.ch/RD11/rkb/AN16pp/node78.html#SECTION000780000000000000000"> math definition</A>
 * <A HREF="http://www.statsoft.com/textbook/glose.html#Exponential Distribution"> animated definition</A>.
 * <p>
 * <tt>p(x) = lambda*exp(-x*lambda)</tt> for <tt>x &gt;= 0</tt>, <tt>lambda &gt; 0</tt>.
 * <p>
 * Instance methods operate on a user supplied uniform random number generator; they are unsynchronized.
 * <dt>
 * Static methods operate on a default uniform random number generator; they are synchronized.
 * <p>
 *
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public class ExponentialDistribution {


	/**
	 * Get the probability distribution function.
	 */
	public static double pdf(final double x, final double lambda) {
		if(x < 0.0) { return 0.0; }
		return lambda * Math.exp(-x * lambda);
	}


	/**
	 * Get the cumulative distribution function.
	 */
	public static double cdf(final double x, final double lambda) {
		if(x <= 0.0) { return 0.0; }
		return 1.0 - Math.exp(-x * lambda);
	}


	/**
	 * Get a random number from the distribution.
	 */
	public static double nextDouble(final double lambda) {
		return -Math.log(Math.random()) / lambda;
	}

}
