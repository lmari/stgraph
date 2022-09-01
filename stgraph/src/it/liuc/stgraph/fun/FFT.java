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
import it.liuc.stgraph.util.STComplex;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** FFT and inverse FFT function. */
public class FFT extends STFunction {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class constructor. */
	public FFT() { numberOfParameters = 2; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		Tensor param1 = null;
		Tensor param2 = null;
		try {
			param2 = (Tensor)stack.pop();
			param1 = (Tensor)stack.pop();
		} catch (Exception e) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		if(param2.isNotScalar()) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.NONSCALAR_SELECTOR"))); //$NON-NLS-1$
			return;
		}
		double selector = param2.getValue();
		if(selector != 1 && selector != 2) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_VALUE"))); //$NON-NLS-1$
			return;
		}
		if(selector == 1) { // direct FFT
			if(param1.getOrder() != 1) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
				return;
			}
			int size = param1.getSize();
			if(size == 0) {
				stack.push(new Tensor(2));
				return;
			}
			if(!isPower2(size)) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_POWER2_VECTOR"))); //$NON-NLS-1$
				return;
			}
			STComplex[] x = new STComplex[size];
			for(int i = 0; i < size; i++) { x[i] = new STComplex(param1.getScalar(i).getValue(), 0); }
			STComplex[] y = fft(x);
			Tensor result = new Tensor(new int[]{size, 2});
			for(int i = 0; i < size; i++) {
				result.setSubTensor(Tensor.newScalar(y[i].getRe()), i, 0);
				result.setSubTensor(Tensor.newScalar(y[i].getIm()), i, 1);
			}
			stack.push(result);
			return;
		}
		// inverse FFT
		if(param1.getOrder() != 2) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"))); //$NON-NLS-1$
			return;
		}
		int size = param1.getDimensions()[0];
		if(size == 0) {
			stack.push(new Tensor(1));
			return;
		}
		if(!isPower2(size)) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_POWER2_MATRIX"))); //$NON-NLS-1$
			return;
		}
		if(param1.getDimensions()[1] != 2) {
			stack.push(STInterpreter.handleException(getException("ERR.FUN.REQUIRES_2COLS_MATRIX"))); //$NON-NLS-1$
			return;
		}
		STComplex[] x = new STComplex[size];
		for(int i = 0; i < size; i++) { x[i] = new STComplex(param1.getSubTensor(i, 0).getValue(), param1.getSubTensor(i, 1).getValue()); }
		STComplex[] y = ifft(x);
		Tensor result = Tensor.newVector(size);
		for(int i = 0; i < size; i++) { result.setScalar(y[i].getRe(), i); }
		stack.push(result);
		return;
	}


	private boolean isPower2(final int x) {
		double t = Math.log(x) / Math.log(2);
		return t == (int)t;
	}


	/** Compute the FFT of the argument, assuming its length is a power of 2.
	 * Radix 2 Cooley-Tukey FFT algorithm.
	 * @param x the complex vector of which the FFT is to be computed
	 * @return value */
	private static STComplex[] fft(final STComplex[] x) {
		int n = x.length;
		if(n == 1) { return x; } // base case
		STComplex[] y = new STComplex[n];
		STComplex[] even = new STComplex[n / 2];
		STComplex[] odd  = new STComplex[n / 2];
		for(int k = 0; k < n / 2; k++) { even[k] = x[2 * k]; }
		for(int k = 0; k < n / 2; k++) { odd[k]  = x[2 * k + 1]; }
		STComplex[] q = fft(even);
		STComplex[] r = fft(odd);
		double kth;
		STComplex wk;
		double c = -2 * Math.PI / n;
		for(int k = 0; k < n / 2; k++) {
			kth = c * k;
			wk = new STComplex(Math.cos(kth), Math.sin(kth));
			y[k] = q[k].plus(wk.times(r[k]));
			y[k + n / 2] = q[k].minus(wk.times(r[k]));
		}
		return y;
	}


	/** Compute the inverse FFT of the argument, assuming its length is a power of 2.
	 * @param x the complex vector of which the inverse FFT is to be computed
	 * @return value */
	private static STComplex[] ifft(STComplex[] x) {
		int n = x.length;
		for(int i = 0; i < n; i++) { x[i] = x[i].conjugate(); }
		STComplex[] y = fft(x);
		double nn = 1.0 / n;
		for(int i = 0; i < n; i++) { y[i] = y[i].times(nn); }
		return y;
	}

}
