package org.nfunk.jep.type;

import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;


/**
 * Matrix basic type and related methods.
 */
public class Matrix implements Serializable {
	private int rowCount;
	private int columnCount;
	private Object[][] elementData;

	private static Tensor z = Tensor.newScalar(0.0);


	/**
	 * Default constructor: create an empty matrix.
	 */
	public Matrix() {
		elementData = null;
		rowCount = 0;
		columnCount = 0;
	}


	/**
	 * Create a matrix of specified size,
	 * with the specified value for the elements.
	 *
	 * @param _rowCount the row number
	 * @param _columnCount the column number
	 * @param object initial value for elements
	 */
	public Matrix(int _rowCount, int _columnCount, Object object) {
		elementData = new Object[_rowCount][_columnCount];
		for(int i = 0; i < _rowCount; i++) {
			for(int j = 0; j < _columnCount; j++) { elementData[i][j] = object; }
		}
		rowCount = _rowCount;
		columnCount = _columnCount;
	}


	/**
	 * Create a matrix of specified size,
	 * with 0.0 as the value for the elements.
	 *
	 * @param _rowCount the row number
	 * @param _columnCount the column number
	 */
	public Matrix(int _rowCount, int _columnCount) { this(_rowCount, _columnCount, z); }


	/**
	 * Create a matrix by cloning the specified matrix.
	 *
	 * @param matrix the matrix to be cloned
	 */
	public Matrix(Matrix matrix) {
		rowCount = matrix.rowCount;
		columnCount = matrix.columnCount;
		elementData = new Object[rowCount][columnCount];
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { elementData[i][j] = matrix.get(i, j); }
		}
	}


	/**
	 * Create a matrix from the specified vector.
	 *
	 * @param vector the vector to be transformed
	 */
	public Matrix(Vector vector) {
		rowCount = vector.size();
		columnCount = 1;
		elementData = new Object[rowCount][columnCount];
		for(int i = 0; i < rowCount; i++) { elementData[i][0] = vector.get(i); }
	}


	/**
	 * Create a matrix from the specified number.
	 *
	 * @param number the number to be transformed
	 */
	public Matrix(Number number) {
		rowCount = 1;
		columnCount = 1;
		elementData = new Object[rowCount][columnCount];
		elementData[0][0] = number;
	}


	/**
	 * Get the matrix as a 2D array
	 *
	 * @return the matrix
	 */
	public Object[][] getElementData() { return elementData; }


	/**
	 * Set the specified element to the object, suitably resizing the matrix if required.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @param object the object
	 */
	public void set(int rowIndex, int columnIndex, Object object) {
		if((rowIndex > (rowCount - 1)) || (columnIndex > (columnCount - 1))) {
			int rowC = Math.max(rowCount, rowIndex + 1);
			int columnC = Math.max(columnCount, columnIndex + 1);
			Object[][] buffer = new Object[rowC][columnC];
			for(int i = 0; i < rowC; i++) {
				for(int j = 0; j < columnC; j++) { buffer[i][j] = Matrix.z; }
			}
			for(int i = 0; i < rowCount; i++) {
				for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
			}
			elementData = buffer;
			rowCount = rowC;
			columnCount = columnC;
		}
		elementData[rowIndex][columnIndex] = object;
	}


	/**
	 * Set the matrix data from a formatted string.
	 *
	 * @param data the data
	 */
	/*
	public void setData(String data) {
		if(data == null) { return; }
		data = data.trim();
		if(data.startsWith("[[")) { // [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
			data = data.substring(1, data.length() - 1); // [1, 2, 3], [4, 5, 6], [7, 8, 9]
			String[] rows = data.split("\\]\\, \\["); // [1,2,3  4,5,6  7,8,9]
			rows[0] = rows[0].substring(1); // 1,2,3  4,5,6  7,8,9]
			rows[rows.length - 1] = rows[rows.length - 1].substring(0, rows[rows.length - 1].length() - 1); // 1,2,3  4,5,6  7,8,9
			elementData = new Object[rowCount = rows.length][columnCount = rows[0].split(",").length];
			for(int i = 0; i < rowCount; i++) {
				String[] cols = rows[i].split(", ");
				for(int j = 0; j < columnCount; j++) { elementData[i][j] = EDouble.valueOf(cols[j]); }
			}
			return;
		}
		if(data.startsWith("[")) { // [1, 2, 3]
			data = data.substring(1, data.length() - 1); // 1, 2, 3
			String[] rows = data.split("\\, ");
			elementData = new Object[rowCount = rows.length][columnCount = 1];
			for(int i = 0; i < rowCount; i++) { elementData[i][0] = EDouble.valueOf(rows[i]); }
			return;
		}
		elementData = new Object[rowCount = 1][columnCount = 1];
		elementData[0][0] = EDouble.valueOf(data);
	}
	*/


	/**
	 * Resize this matrix, by possibly removing the exceeding rows or columns and possibly inserting new rows or columns.
	 *
	 * @param newRowCount the new row count
	 * @param newColumnCount the new column count
	 */
	public void resize(int newRowCount, int newColumnCount) {
		Object[][] buffer = new Object[newRowCount][newColumnCount];
		if(newRowCount > rowCount || newColumnCount > columnCount) { // some zero padding is required
			for(int i = 0; i < newRowCount; i++) {
				for(int j = 0; j < newColumnCount; j++) { buffer[i][j] = z; }
			}
		}
		int r = Math.min(rowCount, newRowCount);
		int c = Math.min(columnCount, newColumnCount);
		for(int i = 0; i < r; i++) {
			for(int j = 0; j < c; j++) { buffer[i][j] = elementData[i][j]; }
		}
		elementData = buffer;
		rowCount = newRowCount;
		columnCount = newColumnCount;
	}


	/**
	 * Set the specified row to the matrix, suitably resizing the matrix if required,
	 * with the specified vector as the value for the row.
	 *
	 * @param rowIndex the row index
	 * @param objects the vector
	 */
	public void setRow(int rowIndex, Vector objects) {
		int columnIndex = objects.size();
		if((rowIndex > (rowCount - 1)) || (columnIndex > (columnCount - 1))) {
			int rowC = Math.max(rowCount, rowIndex + 1);
			int columnC = Math.max(columnCount, columnIndex);
			Object[][] buffer = new Object[rowC][columnC];
			for(int i = 0; i < rowC; i++) {
				for(int j = 0; j < columnC; j++) { buffer[i][j] = Matrix.z; }
			}
			for(int i = 0; i < rowCount; i++) {
				for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
			}
			elementData = buffer;
			rowCount = rowC;
			columnCount = columnC;
		}
		for(int i = 0; i < columnIndex; i++) { elementData[rowIndex][i] = objects.get(i); }
	}


	/**
	 * Set the specified column to the matrix, suitably resizing the matrix if required,
	 * with the specified vector as the value for the column.
	 *
	 * @param columnIndex the column index
	 * @param objects the vector
	 */
	public void setColumn(int columnIndex, Vector objects) {
		int rowIndex = objects.size();
		if((columnIndex > (columnCount - 1)) || (rowIndex > (rowCount - 1))) {
			int rowC = Math.max(rowCount, rowIndex);
			int columnC = Math.max(columnCount, columnIndex + 1);
			Object[][] buffer = new Object[rowC][columnC];
			for(int i = 0; i < rowC; i++) {
				for(int j = 0; j < columnC; j++) { buffer[i][j] = Matrix.z; }
			}
			for(int i = 0; i < rowCount; i++) {
				for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
			}
			elementData = buffer;
			rowCount = rowC;
			columnCount = columnC;
		}
		for(int i = 0; i < rowIndex; i++) { elementData[i][columnIndex] = objects.get(i); }
	}


	/**
	 * Add a row to the matrix with the given data, suitably resizing the matrix if required.
	 *
	 * @param objects the vector
	 */
	public void addRow(Vector objects) {
		int rowIndex = rowCount;
		int columnIndex = objects.size();
		int rowC = rowIndex + 1;
		int columnC = Math.max(columnCount, columnIndex);
		Object[][] buffer = new Object[rowC][columnC];
		for(int i = 0; i < rowC; i++) {
			for(int j = 0; j < columnC; j++) { buffer[i][j] = Matrix.z; }
		}
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
		}
		elementData = buffer;
		rowCount = rowC;
		columnCount = columnC;
		for(int j = 0; j < columnIndex; j++) { elementData[rowIndex][j] = objects.get(j); }
	}


	/**
	 * Add a column to the matrix with the given data, suitably resizing the matrix if required.
	 *
	 * @param objects the vector
	 */
	public void addColumn(Vector objects) {
		int rowIndex = objects.size();
		int columnIndex = columnCount;
		int rowC = Math.max(rowCount, rowIndex);
		int columnC = columnIndex + 1;
		Object[][] buffer = new Object[rowC][columnC];
		for(int i = 0; i < rowC; i++) {
			for(int j = 0; j < columnC; j++) { buffer[i][j] = Matrix.z; }
		}
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
		}
		elementData = buffer;
		rowCount = rowC;
		columnCount = columnC;
		for(int i = 0; i < rowIndex; i++) { elementData[i][columnIndex] = objects.get(i); }
	}


	/**
	 * Add a column with the specified index to the matrix, suitably resizing the matrix if required,
	 * with the specified vector as the value for the added column.
	 *
	 * @param columnIndex the column index
	 * @param objects the vector
	 */
	public void addColumn(int columnIndex, Vector objects) {
		int rowIndex = objects.size();
		int rowC = Math.max(rowCount, rowIndex);
		int columnC = columnIndex < columnCount ? columnCount + 1 : columnIndex + 1;
		Object[][] buffer = new Object[rowC][columnC];
		for(int i = 0; i < rowC; i++) {
			for(int j = 0; j < columnC; j++) { buffer[i][j] = Matrix.z; }
		}
		if(columnIndex < columnCount) {
			for(int i = 0; i < rowCount; i++) {
				for(int j = 0; j < columnIndex; j++) { buffer[i][j] = elementData[i][j]; }
				buffer[i][columnIndex] = objects.get(i);
				for(int j = columnIndex; j < columnCount; j++) { buffer[i][j + 1] = elementData[i][j]; }
			}
		} else {
			for(int i = 0; i < rowCount; i++) {
				for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
				buffer[i][columnIndex] = objects.get(i);
			}
		}
		elementData = buffer;
		rowCount = rowC;
		columnCount = columnC;
	}


	/**
	 * Remove the row with the specified index to the matrix.
	 *
	 * @param rowIndex the row index
	 */
	public void removeRow(int rowIndex) {
		if(rowIndex < 0 || rowIndex >= rowCount) { return; }
		Object[][] buffer = new Object[rowCount - 1][columnCount];
		for(int i = 0; i < rowIndex; i++) {
			for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
		}
		for(int i = rowIndex + 1; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { buffer[i - 1][j] = elementData[i][j]; }
		}
		rowCount--;
		elementData = buffer;
	}


	/**
	 * Remove the column with the specified index to the matrix.
	 *
	 * @param columnIndex the column index
	 */
	public void removeColumn(int columnIndex) {
		if(columnIndex < 0 || columnIndex >= columnCount) { return; }
		Object[][] buffer = new Object[rowCount][columnCount - 1];
		for(int j = 0; j < columnIndex; j++) {
			for(int i = 0; i < rowCount; i++) { buffer[i][j] = elementData[i][j]; }
		}
		for(int j = columnIndex + 1; j < columnCount; j++) {
			for(int i = 0; i < rowCount; i++) { buffer[i][j - 1] = elementData[i][j]; }
		}
		columnCount--;
		elementData = buffer;
	}


	/**
	 * Add a matrix to the matrix, suitably resizing the matrix if required.
	 *
	 * @param objects the matrix
	 */
	public void addMatrix(Matrix objects) {
		int rowCount2 = objects.getRowCount();
		int columnCount2 = objects.getColumnCount();
		int rowC = Math.max(rowCount, rowCount2);
		int columnC = columnCount + columnCount2;
		Object[][] buffer = new Object[rowC][columnC];
		for(int i = 0; i < rowC; i++) {
			for(int j = 0; j < columnC; j++) { buffer[i][j] = Matrix.z; }
		}
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { buffer[i][j] = elementData[i][j]; }
		}
		for(int i = 0; i < rowCount2; i++) {
			for(int j = 0; j < columnCount2; j++) { buffer[i][columnCount + j] = objects.get(i, j); }
		}
		elementData = buffer;
		rowCount = rowC;
		columnCount = columnC;
	}


	/**
	 * Get the specified element.
	 *
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 *
	 * @return the element
	 */
	public Object get(int rowIndex, int columnIndex) {
		if(rowIndex >= rowCount || columnIndex >= columnCount) { return null; }
		return elementData[rowIndex][columnIndex];
	}


	/**
	 * Get the collection of rows.
	 *
	 * @return the row collection
	 */
	public Vector<Vector> getRows() {
		if(rowCount == 0 || columnCount == 0) { return null; }
		Vector r = new Vector(rowCount);
		for(int i = 0; i < rowCount; i++) {
			Vector c = new Vector(columnCount);
			for(int j = 0; j < columnCount; j++) { c.add(elementData[i][j]); }
			r.add(c);
		}
		return r;
	}


	/**
	 * Get the collection of columns.
	 *
	 * @return the column collection
	 */
	public Vector<Vector> getColumns() {
		if(rowCount == 0 || columnCount == 0) { return null; }
		Vector c = new Vector(columnCount);
		for(int i = 0; i < columnCount; i++) {
			Vector r = new Vector(rowCount);
			for(int j = 0; j < rowCount; j++) { r.add(elementData[j][i]); }
			c.add(r);
		}
		return c;
	}


	/**
	 * Get the specified row.
	 *
	 * @param rowIndex the row index
	 *
	 * @return the row
	 */
	public Vector getRow(int rowIndex) {
		if(columnCount == 0 || rowIndex >= rowCount) { return null; }
		Vector r = new Vector(columnCount);
		for(int i = 0; i < columnCount; i++) { r.add(elementData[rowIndex][i]); }
		return r;
	}


	/**
	 * Get the specified column.
	 *
	 * @param columnIndex the column index
	 *
	 * @return the column
	 */
	public Vector getColumn(int columnIndex) {
		if(rowCount == 0 || columnIndex >= columnCount) { return null; }
		Vector r = new Vector(rowCount);
		for(int i = 0; i < rowCount; i++) { r.add(elementData[i][columnIndex]); }
		return r;
	}


	/**
	 * Get the number of defined rows in the matrix.
	 *
	 * @return the row count
	 */
	public int getRowCount() { return rowCount; }


	/**
	 * Get the number of defined columns in the matrix.
	 *
	 * @return the column count
	 */
	public int getColumnCount() { return columnCount; }


	/**
	 * Get the matrix size.
	 *
	 * @return the size
	 */
	public Point size() { return new Point(rowCount, columnCount); }


	/**
	 * Get whether the matrix is empty.
	 *
	 * @return <code>true</code> if the matrix is empty
	 */
	public boolean isEmpty() { return rowCount == 0 || columnCount == 0; }


	/**
	 * Transpose the matrix.
	 */
	public void transpose() {
		Object[][] buffer = new Object[columnCount][rowCount];
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { buffer[j][i] = elementData[i][j]; }
		}
		elementData = buffer;
		int t = rowCount;
		rowCount = columnCount;
		columnCount = t;
	}


	/**
	 * Multiply the matrix with the specified one.
	 *
	 * @param matrix the matrix to be multiplied
	 */
	/*
	public void product(Matrix matrix) {
		int mRowCount = matrix.rowCount;
		int mColumnCount = matrix.columnCount;
		if(columnCount != mRowCount) { return; }
		Object[][] buffer = new Object[rowCount][mColumnCount];
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < mColumnCount; j++) {
				double r = 0;
				for(int k = 0; k < columnCount; k++) { r += ((Number)elementData[i][k]).doubleValue() * ((Number)matrix.get(k, j)).doubleValue(); }
				buffer[i][j] = EDouble.valueOf(r);
			}
		}
		elementData = buffer;
		columnCount = mColumnCount;
	}
	*/


	/**
	 * Dump the matrix.
	 *
	 * @return the matrix
	 */
	public String dump() {
		if(rowCount == -1 || columnCount == -1) return "Empty matrix"; //$NON-NLS-1$
		String t = "\t", r = "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		StringBuffer s = new StringBuffer();
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { s.append(elementData[i][j] + t); }
			s.append(r);
		}
		return s.toString();
	}


	/**
	 * Clone the matrix
	 *
	 * @return the matrix
	 */
	@Override public Matrix clone() {
		Matrix result = new Matrix(rowCount, columnCount);
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < columnCount; j++) { result.elementData[i][j] = elementData[i][j]; }
		}
		return result;
	}


	/**
	 * Print out the matrix.
	 *
	 * @return the matrix
	 */
	@Override public String toString() {
		if(rowCount == -1 || columnCount == -1) { return "Empty matrix"; } //$NON-NLS-1$
		StringBuffer s = new StringBuffer("[");
		for(int i = 0; i < rowCount; i++) {
			s.append("["); //$NON-NLS-1$
			if(columnCount > 0) {
				for(int j = 0; j < columnCount - 1; j++) { s.append(elementData[i][j] + ", "); } //$NON-NLS-1$
				s.append(elementData[i][columnCount - 1] + "]"); //$NON-NLS-1$
				if(i < rowCount - 1) { s.append(", "); } //$NON-NLS-1$
			} else { s.append("]"); } //$NON-NLS-1$
		}
		s.append("]"); //$NON-NLS-1$
		return s.toString();
	}

}
