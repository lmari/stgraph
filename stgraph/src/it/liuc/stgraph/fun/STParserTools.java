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

import org.nfunk.jep.type.Tensor;


/** Some parser tools. */
public final class STParserTools {
	/** The <code>true</code> constant. */
	public static final Tensor TRUE = Tensor.newScalar(1.0);
	/** The <code>false</code> constant. */
	public static final Tensor FALSE = Tensor.newScalar(0.0);
	/** The comparison tolerance constant, also acting as the threshold for <code>true</code> constant. */
	public static final double CT = 0.000001;


	/** Class constructor. */
	private STParserTools() { ; }


	/** Return <code>true</code> if the parameter is above the threshold of truth.
	 * @param v the v
	 * @return result */
	public static boolean isTrue(final double v) { return (v >= STParserTools.CT); }


	/** Return <code>true</code> if the parameter is below the threshold of falsehood.
	 * @param v the v
	 * @return result */
	public static boolean isFalse(final double v) { return (v < STParserTools.CT); }


	/** Return the boolean value corresponding to the negated parameter.
	 * @param v the v
	 * @return result */
	public static Tensor getNegatedBooleanValue(final double v) {
		if(v >= STParserTools.CT) { return STParserTools.FALSE; }
		return STParserTools.TRUE;
	}


	/** Return the boolean value <code>true</code> if the parameters are equal.
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return result */
	public static Tensor areEQ(final double v1, final double v2) {
		if(Math.abs(v1 - v2) <= STParserTools.CT) { return STParserTools.TRUE; }
		return STParserTools.FALSE;
	}


	/** Return the boolean value <code>true</code> if the parameters are not equal.
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return result */
	public static Tensor areNE(final double v1, final double v2) {
		if(Math.abs(v1 - v2) > STParserTools.CT) { return STParserTools.TRUE; }
		return STParserTools.FALSE;
	}

}
