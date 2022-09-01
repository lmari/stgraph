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
package it.liuc.stgraph.node;

import it.liuc.stgraph.util.STTools;

import org.nfunk.jep.type.Tensor;


/** Data class. */
public final class STData {
	/** The Constant VALUETYPE_UNKNOWN. */
	public static final int VALUETYPE_UNKNOWN = -1;
	/** The Constant VALUETYPE_SCALAR. */
	public static final int VALUETYPE_SCALAR = 0;
	/** The Constant VALUETYPE_VECTOR. */
	public static final int VALUETYPE_VECTOR = 1;
	/** The Constant VALUETYPE_MATRIX. */
	public static final int VALUETYPE_MATRIX = 2;
	/** The Constant VALUETYPE_TENSOR. */
	public static final int VALUETYPE_TENSOR = 3;

	/** The Constant FORMAT_SHORT. */
	public static final int FORMAT_SHORT = 0;
	/** The Constant FORMAT_ALTERNATE. */
	public static final int FORMAT_ALTERNATE = 1;
	/** The Constant FORMAT_SHORT. */
	public static final int FORMAT_LONG = 2;

	/** Class constructor. */
	private STData() { ; }


	/** Get the type of the given data.
	 * @param data the data
	 * @return the type */
	public static int getType(final Object data) {
		if(data == null) { return VALUETYPE_UNKNOWN; }
		if(data instanceof Tensor) {
			Tensor t = (Tensor)data;
			if(t.isScalar()) { return VALUETYPE_SCALAR; }
			if(t.isVector()) { return VALUETYPE_VECTOR; }
			if(t.isMatrix()) { return VALUETYPE_MATRIX; }
			return VALUETYPE_TENSOR;
		}
		return VALUETYPE_UNKNOWN;
	}


	/** Get the type of the given data as string.
	 * @param data the data
	 * @return the type */
	public static String getTypeAsString(final Object data) {
		if(data == null) { return "null"; }
		if(data instanceof Tensor) {
			Tensor t = (Tensor)data;
			if(t.isScalar()) { return "S"; }
			if(t.isVector()) { return "V"; }
			if(t.isMatrix()) { return "M"; }
			return "T";
		}
		return "null";
	}


	/** Get whether the given data is a number, and not infinite or NaN.
	 * @param data the data
	 * @return result */
	public static boolean isNumber(final double data) { return (data == data && !(data == Double.POSITIVE_INFINITY) || (data == Double.NEGATIVE_INFINITY)); }


	/** Get whether the given data is of scalar, typically Double, type.
	 * @param data the data
	 * @return result */
	public static boolean isScalar(final Object data) { return data != null && data instanceof Tensor && ((Tensor)data).isScalar(); }


	/** Get whether the given data is of vector type.
	 * @param data the data
	 * @return result */
	public static boolean isVector(final Object data) { return data != null && data instanceof Tensor && ((Tensor)data).isVector(); }


	/** Get whether the given data is of matrix type.
	 * @param data the data
	 * @return result */
	public static boolean isMatrix(final Object data) { return data != null && data instanceof Tensor && ((Tensor)data).isMatrix(); }


	/** Parse the given string to the data of specified type.
	 * @param string the string
	 * @param type the data type
	 * @return the resulting data */
	public static Object parseData(final String string, final int type) {
		switch(type) {
		case VALUETYPE_SCALAR:
			return Tensor.newScalar(Double.parseDouble(string));
		case VALUETYPE_VECTOR:
			return new Tensor(string);
		case VALUETYPE_MATRIX:
			return new Tensor(string);
		case VALUETYPE_TENSOR:
			return new Tensor(string);
		default:
			return null;
		}
	}


	/** Round the given value to the specified number of decimal places.
	 * <br>Adapted from Jakarta Commons Math.
	 * @param x the value to round
	 * @param scale the number of digits to the right of the decimal point
	 * @return the rounded value */
	public static double round(final double x, final int scale) {
		if(scale == 0) { return (int)(x + 0.5); }
		double sign = Double.isNaN(x) ? Double.NaN : ((x >= 0.0) ? 1.0 : -1.0);
		double factor = Math.pow(10.0, scale) * sign;
		double unscaled = x * factor;
		double fraction = Math.abs(unscaled - Math.floor(unscaled));
		unscaled = (fraction >= 0.5) ? Math.ceil(unscaled) : Math.floor(unscaled);
		return unscaled / factor;
	}


	/** Get a HTML-formatted string from the given expression.
	 * @param expression the expression
	 * @return formatted expression */
	public static String toHTML(final String expression) {
		String result = expression;
		if(result == null) { return STTools.BLANK; }
		return result.replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Get a formatted string from the given data.
	 * <br>See Tensor.getValueAsString().
	 * @param data the data
	 * @param format the format
	 * @return value */
	public static String toString(final Object data, final int format) {
		if(data == null) { return STTools.BLANK; }
		if(data instanceof Tensor) { return ((Tensor)data).getValueAsString(format); }
		return STTools.BLANK;
	}


	/** Get a formatted string from the given data.
	 * <br>See Tensor.getValueAsString().
	 * @param data the data
	 * @param format the format
	 * @param numberFormat the numberFormat
	 * @return value */
	public static String toString(final Object data, final int format, final String numberFormat) {
		if(data == null) { return STTools.BLANK; }
		if(data instanceof Tensor) { return ((Tensor)data).getValueAsString(format, numberFormat); }
		return STTools.BLANK;
	}

}
