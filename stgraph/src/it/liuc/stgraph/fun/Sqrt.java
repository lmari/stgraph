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

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;

import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;


/** Sqrt function. */
public class Sqrt extends STMonadicFun {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Exec method.
	 * @param x the x
	 * @return the object
	 * @throws ParseException the parse exception */
	@Override final Object exec(final Tensor x) throws ParseException { 
		double xx = x.getValue();
		if(xx < 0.0) { return STInterpreter.handleException(getException("ERR.FUN.NEGATIVE_VALUE_NOTALLOWED"), STData.VALUETYPE_SCALAR); } //$NON-NLS-1$
		return Tensor.newScalar(Math.sqrt(xx));
	}

}
