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
package it.liuc.stgraph.util;


/**
 * Handle complex numbers.
 */
public class STComplex {
	/** The re. */
	private double re;
	/** The im. */
	private double im;


	/**
	 * Class constructor.
	 *
	 * @param re the real part of this number
	 * @param im the imaginary part of this number
	 */
	public STComplex(final double re, final double im) {
		this.re = re;
		this.im = im;
	}


	/**
	 * Get the real part of this number
	 *
	 * @return number
	 */
	public double getRe() { return re; }


	/**
	 * Get the imaginary part of this number
	 *
	 * @return number
	 */
	public double getIm() { return im; }


	/**
	 * Conjugate function.
	 *
	 * @return n
	 */
	public final STComplex conjugate() { return new STComplex(this.re, -this.im); }


	/**
	 * Add function.
	 *
	 * @param x the x
	 *
	 * @return n
	 */
	public final STComplex plus(final STComplex x) { return new STComplex(this.re + x.re, this.im + x.im); }


	/**
	 * Minus function.
	 *
	 * @param x the x
	 *
	 * @return n
	 */
	public final STComplex minus(final STComplex x) { return new STComplex(this.re - x.re, this.im - x.im); }


	/**
	 * Times function.
	 *
	 * @param x the x
	 *
	 * @return n
	 */
	public final STComplex times(final STComplex x) { return new STComplex(this.re * x.re - this.im * x.im, this.re * x.im + this.im * x.re); }


	/**
	 * Times function.
	 *
	 * @param x the x
	 *
	 * @return n
	 */
	public final STComplex times(final double x) { return new STComplex(this.re * x, this.im * x); }


	/**
	 * Absolute value function.
	 *
	 * @return  n
	 */
	public final double abs() { return Math.sqrt(this.re * this.re + this.im * this.im); }


	/**
	 * To String method.
	 *
	 * @return n
	 */
	@Override
	public final String toString() { return "[" + this.re + "," + this.im + "]"; } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

}
