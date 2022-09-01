/*****************************************************************************

JEP 2.4.0, Extensions 1.1.0
      June 13 2006
      (c) Copyright 2006, Nathan Funk and Richard Morris
      See LICENSE-*.txt for license information.

*****************************************************************************/

package org.nfunk.jep.type;

import org.nfunk.jep.ParseException;

/**
 * Default class for creating number objects.
 * Modified by lm.
 */
public class DoubleNumberFactory implements NumberFactory {
	public static Tensor ZERO = Tensor.newScalar(0.0);
	public static Tensor ONE = Tensor.newScalar(1.0);
	public static Tensor TWO = Tensor.newScalar(2.0);
	public static Tensor MINUSONE = Tensor.newScalar(-1.0);
	
	/**
	 * Creates a Double object initialized to the value of the parameter.
	 *
	 * @param value The initialization value for the returned object.
	 */
	public Object createNumber(String value) { return Tensor.newScalar(Double.valueOf(value).doubleValue()); }
	public Object createNumber(double value) { return Tensor.newScalar(value); }
	public Object createNumber(Number value) { return Tensor.newScalar(value.doubleValue()); }
	public Object createNumber(boolean value) { return (value?ONE:ZERO); }
	public Object createNumber(float value) { return Tensor.newScalar(value); }
	public Object createNumber(int value) { return Tensor.newScalar((double)value); }
	public Object createNumber(short value) { return Tensor.newScalar((double)value); }
	public Object createNumber(Complex value) throws ParseException {
		throw new ParseException("Cannot create a number from a Complex value");	
	}
	public Object getMinusOne() { return MINUSONE; }
	public Object getOne() { return ONE; }
	public Object getTwo() { return TWO; }
	public Object getZero() { return ZERO; }
}
