/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.node.STData;

import java.util.Stack;

import jxl.NumberCell;
import jxl.Workbook;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.type.Tensor;


/** Read from XLS function. */
@SuppressWarnings("serial")
public class ReadFromXLS extends STFunction {


	/** Class constructor. */
	public ReadFromXLS() { numberOfParameters = -1; }


	/** Run method.
	 * @param stack the stack
	 * @throws ParseException the parse exception */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void run(final Stack stack) throws ParseException {
		checkStack(stack);
		if(curNumberOfParameters == 4) {
			Object param4 = stack.pop();
			Object param3 = stack.pop();
			Object param2 = stack.pop();
			Object param1 = stack.pop();
			if(!(param1 instanceof String) || !Tensor.isScalar(param2) || !Tensor.isScalar(param3) || !Tensor.isScalar(param4)) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR));
				return;
			}
			STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
			String fileName = graph.contextName + System.getProperty("file.separator") + (String)param1;
			Workbook workbook = (Workbook)graph.handleResource(fileName, STGraphImpl.RES_TYPE_WORKBOOK, true);
			if(workbook == null) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.FILE_NOT_FOUND"), STData.VALUETYPE_SCALAR));
				return;
			}
			int sheet = (int)((Tensor)param2).getValue() - 1;
			int row = (int)((Tensor)param3).getValue() - 1;
			int col = (int)((Tensor)param4).getValue() - 1;
			stack.push(Tensor.newScalar(getValue(workbook, sheet, col, row)));
			return;
		}
		if(curNumberOfParameters == 6) {
			Object param6 = stack.pop();
			Object param5 = stack.pop();
			Object param4 = stack.pop();
			Object param3 = stack.pop();
			Object param2 = stack.pop();
			Object param1 = stack.pop();
			if(!(param1 instanceof String) || !Tensor.isScalar(param2) || !Tensor.isScalar(param3) || !Tensor.isScalar(param4) || !Tensor.isScalar(param5) || !Tensor.isScalar(param6)) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_TYPE"), STData.VALUETYPE_SCALAR));
				return;
			}
			STGraphExec graph = STGraph.getSTC().getCurrentlyComputedGraph();
			String fileName = graph.contextName + System.getProperty("file.separator") + (String)param1;
			Workbook workbook = (Workbook)graph.handleResource(fileName, STGraphImpl.RES_TYPE_WORKBOOK, true);
			if(workbook == null) {
				stack.push(STInterpreter.handleException(getException("ERR.FUN.FILE_NOT_FOUND"), STData.VALUETYPE_SCALAR));
				return;
			}
			int sheet = (int)((Tensor)param2).getValue() - 1;
			int row1 = (int)((Tensor)param3).getValue() - 1;
			int col1 = (int)((Tensor)param4).getValue() - 1;
			int row2 = (int)((Tensor)param5).getValue() - 1;
			int col2 = (int)((Tensor)param6).getValue() - 1;
			if(row1 > row2 || col1 > col2) {
				stack.push(new Tensor());
				return;
			}
			if(row1 == row2) {
				Tensor result = Tensor.newVector(col2 - col1 + 1);
				int j = 0;
				for(int i = col1; i <= col2; i++) { result.setScalar(getValue(workbook, sheet, i, row1), j++); }
				stack.push(result);
				return;
			}
			if(col1 == col2) {
				Tensor result = Tensor.newVector(row2 - row1 + 1);
				int j = 0;
				for(int i = row1; i <= row2; i++) { result.setScalar(getValue(workbook, sheet, col1, i), j++); }
				stack.push(result);
				return;
			}
			Tensor result = Tensor.newMatrix(row2 - row1 + 1, col2 - col1 + 1);
			int k = 0;
			for(int i = 0; i <= row2 - row1; i++) {
				for(int j = 0; j <= col2 - col1; j++) { result.setScalar(getValue(workbook, sheet, col1 + j, row1 + i), k++); }
			}
			stack.push(result);
			return;
		}
		stack.push(STInterpreter.handleException(getException("ERR.FUN.INVALID_PARAMETER_NUMBER"), STData.VALUETYPE_SCALAR));
	}


	/** Get the value.
	 * @param w the workbook
	 * @param s the sheet
	 * @param c the column
	 * @param r the row
	 * @return result */
	private double getValue(final Workbook w, final int s, final int c, final int r) {
		try {
			return ((NumberCell)w.getSheet(s).getCell(c, r)).getValue();
		} catch (final Exception e) {
			return 0.0;
		}
	}

}
