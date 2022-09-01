package org.nfunk.jep.type;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * Tensor type and related methods.
 * Tensors include as special case scalars, i.e., 0-order tensors.
 * While a n-order, n>0, tensor can be empty (e.g., [], [[]], ...),
 * empty scalars are meaningless.
 */
public class Tensor implements Serializable {
	/** The values stored in this tensor. */
	protected double[] value;
	/** The order (i.e., the number of dimensions) of this tensor. */
	protected int order;
	/** The array of dimensions of this tensor.
	 * Null in the case of scalars, it has 0 values for empty vectors, matrices, ... . */
	protected int[] dimensions;
	/** The pointers to the dimensions of this tensor. */
	public int[] pointers = null;
	/** The threshold to switch from LONG to SHORT format in string generation. */
	protected static int alternateThreshold = 5;

	/** The Constant FORMAT_SHORT. */
	public static final int FORMAT_SHORT = 0;
	/** The Constant FORMAT_ALTERNATE. */
	public static final int FORMAT_ALTERNATE = 1;
	/** The Constant FORMAT_SHORT. */
	public static final int FORMAT_LONG = 2;


	/**
	 * Default constructor: create a 0.0-valued scalar (0-order) tensor.
	 */
	public Tensor() {
		value = new double[] {0.0};
		order = 0;
		dimensions = null;
	}


	/**
	 * Create an empty tensor of given order.
	 *
	 * @param order the order
	 */
	public Tensor(final int order) {
		if(order <= 0) {
			value = new double[] {0.0};
			this.order = 0;
			dimensions = null;
		} else {
			value = null;
			this.order = order;
			dimensions = new int[order];
			for(int i = 0; i < order; i++) { dimensions[i] = 0; }
		}
	}


	/**
	 * Create and return a scalar (0-order) tensor.
	 *
	 * @return tensor
	 */
	public static Tensor newScalar(final double value) {
		Tensor tensor = new Tensor(0);
		tensor.setValue(value);
		return tensor;
	}


	/**
	 * Create and return a vector (1-order) empty tensor.
	 *
	 * @return tensor
	 */
	public static Tensor newVector() { return new Tensor(1); }


	/**
	 * Create and return a matrix (2-order) empty tensor.
	 *
	 * @return tensor
	 */
	public static Tensor newMatrix() { return new Tensor(2); }


	/**
	 * Create an empty tensor of given dimensions.
	 *
	 * @param dims the dimensions
	 */
	public Tensor(final int[] dims) {
		int s = Tensor.getSize(dims);
		if(s == 0) {
			value = new double[] {0.0};
			order = 0;
			dimensions = null;
		} else if(s == -1) {
			value = null;
			order = dims.length;
			dimensions = new int[order];
			for(int i = 0; i < order; i++) { dimensions[i] = 0; }
		} else {
			value = new double[s];
			order = dims.length;
			dimensions = new int[order];
			for(int i = 0; i < order; i++) { dimensions[i] = dims[i]; }
			setPointers();
		}
	}


	/**
	 * Create a vector (1-order) empty tensor.
	 *
	 * @param numElements number of elements
	 *
	 * @return tensor
	 */
	public static Tensor newVector(final int numElements) { return new Tensor(new int[]{numElements}); }


	/**
	 * Create a vector (1-order) tensor from the specified Vector.
	 *
	 * @param vector the vector
	 *
	 * @return tensor
	 */
	public static Tensor newVector(final Vector vector) {
		int size = vector.size();
		if(size == 0) { return new Tensor(1); }
		Tensor tensor = new Tensor(new int[]{size});
		for(int i = 0; i < size; i++) { tensor.setScalar(vector.get(i), i); }
		return tensor;
	}


	/**
	 * Create a matrix (2-order) empty tensor.
	 *
	 * @param numRows number of rows
	 * @param numColumns number of columns
	 *
	 * @return tensor
	 */
	public static Tensor newMatrix(final int numRows, final int numColumns) { return new Tensor(new int[]{numRows, numColumns}); }


	/**
	 * Create a tensor by an array of data and an array of dimensions.
	 *
	 * @param data the data
	 * @param dims the dimensions
	 */
	public Tensor(final double[] data, final int[] dims) {
		int s = Tensor.getSize(dims);
		if(s == 0) {
			value = new double[] {0.0};
			order = 0;
			dimensions = null;
		} else if(s == -1) {
			value = null;
			order = dims.length;
			dimensions = new int[order];
			for(int i = 0; i < order; i++) { dimensions[i] = 0; }
		} else {
			value = new double[s];
			for(int i = 0; i < s; i++) { value[i] = data[i]; }
			order = dims.length;
			dimensions = new int[order];
			for(int i = 0; i < order; i++) { dimensions[i] = dims[i]; }
			setPointers();
		}
	}


	/**
	 * Create a tensor by cloning the specified tensor.
	 *
	 * @param tensor the tensor to be cloned
	 */
	public Tensor(final Tensor tensor) { deepCopy(tensor); }


	/**
	 * Create a tensor by parsing a formatted string of data.
	 *
	 * @param string the string to be parsed
	 */
	public Tensor(final String string) {
		if(string == null || string.length() == 0) {
			value = new double[] {0.0};
			order = 0;
			dimensions = null;
		} else {
			boolean isNumber = true;
			double n = 0.0;
			try {
				n = Double.parseDouble(string);
			} catch (Exception e) {
				isNumber = false;
			}
			if(isNumber) {
				value = new double[] {n};
				order = 0;
				dimensions = null;
			} else {
				String s = string.trim();
				boolean found = true;
				order = 0;
				while(found) {
					if(s.substring(order, order + 1).equals("[")) {
						order++;
					} else {
						found = false;
					}
				}
				dimensions = new int[order];
				int runningdim = 1;
				String t1; String t2; String tt1; String tt2; 
				String[] ss = null;
				s = s.substring(1, s.length() - 1);
				for(int i = 0; i < order; i++) {
					t1 = ""; t2 = ""; tt1 = ""; tt2 = "";
					for(int j = 0; j < order - (i + 1); j++) { t1 += "\\]"; tt1 += "]"; t2 += "\\["; tt2 += "["; }
					ss = s.split(t1 + "," + t2);
					dimensions[i] = ss.length / runningdim;
					runningdim *= dimensions[i];
					if(ss.length > 1) { ss[0] += tt1; ss[ss.length - 1] = tt2 + ss[ss.length - 1]; }
					for(int j = 1; j < ss.length - 1; j++) { ss[j] = tt2 + ss[j] + tt1; }
					if(i < order - 1) {
						s = "";
						for(int j = 0; j < ss.length; j++) {
							s += ss[j].substring(1, ss[j].length() - 1) + ",";
						}
						s = s.substring(0, s.length() - 1);
					}

				}
				int l = ss.length;
				if(l == 1 && ss[0].trim().length() == 0) {
					value = null;
					for(int i = 0; i < order; i++) { dimensions[i] = 0; }
				} else {
					value = new double[ss.length];
					for(int i = 0; i < ss.length; i++) { try { value[i] = Double.parseDouble(ss[i]); } catch (Exception e) { ; }  }
				}
				setPointers();
			}
		}
	}


	/**
	 * Get the order, i.e., the number of dimensions, of this tensor.
	 * <br>Specific cases are 0=scalar, 1=vector, and 2=matrix.
	 *
	 * @return the number
	 */
	public int getOrder() { return order; }


	/**
	 * Get the array of dimensions of this tensor.
	 *
	 * @return the dimensions
	 */
	public int[] getDimensions() { return dimensions; }


	/**
	 * Get whether this is an empty tensor, i.e., it is not a scalar and 
	 * the total number of its elements is zero (equivalent to getSize() == -1).
	 *
	 * @return result
	 */
	public boolean isEmpty() { return getSize() == -1; }


	/**
	 * Get the total number of elements in this tensor.
	 * <br>-1: empty vector, matrix,...
	 * <br>0: scalar
	 * <br>>0: non-empty vector, matrix,...
	 *
	 * @return the number
	 */
	public int getSize() {
		if(order == 0) { return 0; }
		int ret = 1;
		for(int i = 0; i < order; i++) { ret *= dimensions[i]; }
		if(ret == 0) { return -1; }
		return ret;
	}


	/**
	 * Get the total number of elements in a tensor of specified dimensions.
	 * <br>See getSize().
	 *
	 * @param dims the dimensions
	 *
	 * @return the number
	 */
	public static int getSize(final int[] dims) {
		int l = dims.length;
		if(l == 0) { return 0; }
		int s = 1;
		int d;
		for(int i = 0; i < l; i++) {
			d = dims[i];
			if(d <= 0) { return -1; }
			s *= d;
		}
		return s;
	}


	/**
	 * Get whether this is a 0-order tensor, i.e., a scalar.
	 *
	 * @return is scalar?
	 */
	public boolean isScalar() { return order == 0; }


	/**
	 * Get whether the specified object is a 0-order tensor, i.e., a scalar.
	 *
	 * @return is scalar?
	 */
	public static boolean isScalar(final Object object) {
		return object != null && object instanceof Tensor
				&& ((Tensor) object).isScalar(); }


	/**
	 * Get whether this is a 1-order tensor, i.e., a vector.
	 *
	 * @return is vector?
	 */
	public boolean isVector() { return order == 1; }


	/**
	 * Get whether the specified object is a 1-order tensor, i.e., a vector.
	 *
	 * @return is vector?
	 */
	public static boolean isVector(final Object object) { return object != null && object instanceof Tensor && ((Tensor)object).isVector(); }


	/**
	 * Get whether this is a 2-order tensor, i.e., a matrix.
	 *
	 * @return is matrix?
	 */
	public boolean isMatrix() { return order == 2; }


	/**
	 * Get whether the specified object is a 2-order tensor, i.e., a matrix.
	 *
	 * @return is matrix?
	 */
	public static boolean isMatrix(final Object object) { return object != null && object instanceof Tensor && ((Tensor)object).isMatrix(); }


	/**
	 * Get whether this is a n>0-order tensor, i.e., a non-scalar.
	 *
	 * @return is matrix?
	 */
	public boolean isNotScalar() { return order > 0; }


	/**
	 * Get whether this is a n>0-order tensor, i.e., a non-scalar.
	 *
	 * @return result
	 */
	public static boolean isNotScalar(final Object object) { return object != null && object instanceof Tensor && ((Tensor)object).isNotScalar(); }


	/**
	 * Get the scalar subtensor identified by the specified index in the value list.
	 * If the index is invalid, conventionally return a 0 scalar.
	 *
	 * @param index the index of the scalar subtensor
	 *
	 * @return the subtensor
	 */
	public Tensor getScalar(final int index) {
		if(order == 0) {
			if(index == 0) { return this; }
			return new Tensor();
		}
		int ret = 1;
		for(int i = 0; i < order; i++) { ret *= dimensions[i]; }
		if(ret == 0 || index >= ret) { return new Tensor(); }
		Tensor tensor = new Tensor(0);
		tensor.setValue(value[index]);
		return tensor;
	}


	/**
	 * Set the scalar subtensor identified by the specified index in the value list
	 * with the specified tensor.
	 *
	 * @param object the object
	 * @param index the index of the scalar subtensor
	 *
	 * @return has operation been performed correctly?
	 */
	public boolean setScalar(final Object object, final int index) {
		if(!(object instanceof Tensor)) { return false; }
		Tensor tensor = (Tensor)object;
		if(tensor.order != 0) { return false; }
		int s = getSize();
		if(s == 0 && index == 0) {
			this.value[0] = tensor.value[0];
			return true;
		}
		if(s == -1 || index < 0 || index >= s) { return false; }
		this.value[index] = tensor.value[0];
		return true;
	}


	/**
	 * Set the value identified by the specified index in the value list
	 * with the specified value.
	 *
	 * @param value the value
	 * @param index the index of the scalar subtensor
	 *
	 * @return has operation been performed correctly?
	 */
	public boolean setScalar(final double value, final int index) {
		int s = getSize();
		if(s == -1 || index < 0 || index >= s) { return false; }
		this.value[index] = value;
		return true;
	}


	/**
	 * Get whether this tensor has the same dimensions as the specified one.
	 *
	 * @return has the same dimensions?
	 */
	public boolean hasSameDimensions(final Tensor tensor) {
		if(order != tensor.order) { return false; }
		for(int i = 0; i < order; i++) {
			if(dimensions[i] != tensor.dimensions[i]) { return false; }
		}
		return true;
	}


	/**
	 * Get a dimensionally minimized copy of this tensor.
	 *
	 * @return the tensor
	 */
	public Tensor dimensionMinimize() {
		if(dimensions == null || dimensions.length == 0 || dimensions[0] != 1) { return this; } //new Tensor(this); }
		if(value.length == 1) { return Tensor.newScalar(getValue()); }
		int j = 0;
		while(dimensions[j] == 1 && j < dimensions.length - 1) { j++; }
		int newOrder = dimensions.length - j;
		int[] newDims = new int[newOrder];
		for(int i = 0; i < newOrder; i++) { newDims[i] = dimensions[i + j]; }
		Tensor result = new Tensor(newDims);
		for(int i = 0; i < result.getSize(); i++) { result.value[i] = value[i]; }
		return result;
	}


	/**
	 * Set the pointers required to properly identify elements and subtensors by indexes
	 */
	private void setPointers() {
		pointers = new int[order];
		pointers[order - 1] = 1;
		int m = 1;
		for(int i = order - 2; i >= 0; i--) { pointers[i] = m *= dimensions[i + 1]; }
	}


	/**
	 * Get the subtensor identified by the specified indexes, orderly up to one for each dimension.
	 * <br>If the indexes are invalid, conventionally return an empty vector.
	 *
	 * @param indexes the indexes of the subtensor
	 *
	 * @return the subtensor
	 */
	public Tensor getSubTensor(final int... indexes) {
		int l = indexes.length;
		if(l > order) { return Tensor.newVector(); }
		if(l == 0) { return this; }
		for(int i = 0; i < l; i++) {
			if(indexes[i] < 0 || indexes[i] >= dimensions[i]) { return Tensor.newVector(); }
		}
		if(l == order) {
			int p = indexes[order - 1];
			for(int i = 0; i < order - 1; i++) { p += indexes[i] * pointers[i]; }
			return Tensor.newScalar(value[p]);
		}
		int[] newDims = new int[order - l];
		int p = 0;
		for(int i = 0; i < l; i++) { p += indexes[i] * pointers[i]; }
		int m = 1;
		for(int i = l; i < order; i++) {
			m *= dimensions[i];
			newDims[i - l] = dimensions[i];
		}
		double[] d = new double[m];
		for(int i = 0; i < m; i++) { d[i] = value[p + i]; }
		return new Tensor(d, newDims);
	}


	/**
	 * Set the subtensor identified by the specified indexes with the specified tensor.
	 *
	 * @param tensor the tensor
	 * @param indexes the indexes of the element
	 *
	 * @return has operation been performed correctly?
	 */
	public boolean setSubTensor(final Tensor tensor, final int... indexes) {
		int l = indexes.length;
		if(l > order) { return false; }
		if(l == 0) {
			deepCopy(tensor);
			return true;
		}
		for(int i = 0; i < l; i++) {
			if(indexes[i] < 0 || indexes[i] > dimensions[i]) { return false; }
		}
		if(l == order) {
			int p = indexes[order - 1];
			for(int i = 0; i < order - 1; i++) { p += indexes[i] * pointers[i]; }
			this.value[p] = tensor.value[0];
			return true;
		}
		if(order - l != tensor.order) { return false; }
		int[] subDims = new int[order - l];
		int p = 0;
		for(int i = 0; i < l; i++) { p += indexes[i] * pointers[i]; }
		int m = 1;
		for(int i = l; i < order; i++) {
			m *= dimensions[i];
			subDims[i - l] = dimensions[i];
		}
		/*
		for(int i = 0; i < order - l; i++) {
			if(subDims[i] != tensor.dimensions[i]) { return false; }
		}
		 */
		//modified (see OpList.run())
		if(order > 2) {
			for(int i = 0; i < order - l; i++) {
				if(subDims[i] != tensor.dimensions[i]) { return false; }
			}
		} else {
			for(int i = 0; i < order - l; i++) {
				if(subDims[i] < tensor.dimensions[i]) { return false; }
			}
		}
		int mm = tensor.getSize(); if(mm < m) { m = mm; }
		//end modified
		for(int i = 0; i < m; i++) { this.value[p + i] = tensor.value[i]; }
		return true;
	}


	/**
	 * Get the subtensor identified by the specified indexes, all related to the first, i.e., slowest, dimension.
	 * <br>A forgiving behavior is adopted for invalid indexes, i.e., they are simply skipped away.
	 * <br>If the indexes are invalid, conventionally return an empty vector.
	 *
	 * @param indexes the indexes of the subtensor
	 *
	 * @return the subtensor
	 */
	public Tensor getSubTensorByMajorDim(final int... indexes) {
		int l = indexes.length;
		if(l == 0) { return this; }
		int n = 0;
		for(int i = 0; i < l; i++) {
			if(indexes[i] >= 0 && indexes[i] < dimensions[0]) { n++; }
		}
		if(n == 0) { return Tensor.newVector(); } // all indexes are invalid		
		int d = 1;
		int[] newDims = new int[order];
		newDims[0] = n;
		for(int i = 1; i < order; i++) { d *= newDims[i] = dimensions[i]; }
		Tensor result = new Tensor(newDims);
		int k = 0;
		for(int i = 0; i < n; i++) {
			if(indexes[i] >= 0 && indexes[i] < dimensions[0]) {
				for(int j = 0; j < d; j++) { result.value[k++] = value[indexes[i] * d + j]; }
			}
		}
		return result;
	}


	/**
	 * Create and return the matrix of the indexes along the specified dimensions.
	 *
	 * @param dims the dimensions
	 * @param origin the dimension origin (typically either 0 or 1)
	 *
	 * @return the matrix
	 */
	public static int[][] getIndexes(final int[] dims, final int origin) {
		int l = dims.length;
		if(l == 0) { return null; }
		int t = 1;
		for(int i = 0; i < l; i++) {
			if(dims[i] <= 0) { return null; }
			t *= dims[i];
		}
		int[] ptrs = new int[l];
		ptrs[l - 1] = 1;
		int m = 1;
		for(int i = l - 2; i >= 0; i--) { ptrs[i] = m *= dims[i + 1]; }		
		int[][] indexes = new int[l][t];
		int[] nums = new int[l];
		for(int j = 0; j < l; j++) {
			indexes[j][0] = origin;
			nums[j] = 0;
		}
		for(int i = 1; i < t; i++) {
			for(int j = 0; j < l; j++) {
				nums[j]++;
				if(nums[j] < ptrs[j]) {
					indexes[j][i] = indexes[j][i - 1];
				} else {
					nums[j] = 0;
					indexes[j][i] = indexes[j][i - 1] + 1;
					if(indexes[j][i] == dims[j] + origin) { indexes[j][i] = origin; }
				}
			}
		}
		return indexes;
	}


	/**
	 * Get the list of the values of this tensor.
	 *
	 * @return the value list
	 */
	public double[] getValues() { return value; }


	/**
	 * Get the list of the values of this tensor as integer values.
	 *
	 * @return the value list
	 */
	public int[] getValuesAsInt() {
		int[] result = new int[value.length];
		for(int i = 0; i < value.length; i++) { result[i] = (int)value[i]; }
		return result;
	}


	/**
	 * Get the list of the values of this tensor as a list of Double values.
	 *
	 * @return the value list
	 */
	public List getValuesAsList() {
		int size = value.length;
		List result = new ArrayList<Double>(size);
		for(int i = 0; i < size; i++) { result.add(Double.valueOf(value[i])); }
		return result;
	}


	/**
	 * Set the list of the values of this tensor.
	 *
	 * @param the value list
	 */
	public void setValues(final double[] value) { this.value = value; }


	/**
	 * Set the list of the values of this tensor from a list of Double values.
	 *
	 * @param the value list
	 */
	public void setValues(final List value) {
		int size = value.size();
		this.value = new double[size];
		for(int i = 0; i < size; i++) { this.value[i] = ((Double)value.get(i)).doubleValue(); }
	}


	/**
	 * Get the values of this 1-order tensor as a vector.
	 * <br>If this is empty of non-1-order, conventionally return an empty vector.
	 *
	 * @return the vector
	 */
	public Vector getVector() {
		int s = getSize();
		if(s == -1 || order != 1) { return new Vector(); }
		Vector vector = new Vector(s);
		for(int i = 0; i < s; i++) { vector.add(Tensor.newScalar(value[i])); }
		return vector;
	}


	/** Get the number of 1-order tensors in the last dimension of this tensor.
	 * @return the number */
	public int getNumberOfLastDimTensors() {
		int result = 1;
		if(order <= 1) { return result; }
		for(int i = 0; i < order - 1; i++) { result *= dimensions[i]; }
		return result;
	}


	/** Get the number of 1-order tensors in the given dimension of this tensor.
	 * @param dim the dimension
	 * @return the number */
	public int getNumberOfGivenDimTensors(final int dim) {
		if(dim < 0 || dim >= order) { return 0; }
		int result = 1;
		if(order <= 1) { return result; }
		for(int i = 0; i < order; i++) { if(i != dim) { result *= dimensions[i]; } }
		return result;
	}


	/** Get the length of 1-order tensors in the last dimension of this tensor.
	 * @return the length */
	public int getLengthOfLastDimTensors() {
		int size = getSize();
		if(size == -1) { return 0; }
		if(size == 0) { return 1; }
		return dimensions[order - 1];
	}


	/** Get the length of 1-order tensors in the given dimension of this tensor.
	 * @param dim the dimension
	 * @return the length */
	public int getLengthOfGivenDimTensors(final int dim) {
		if(dim < 0 || dim >= order) { return 0; }
		int size = getSize();
		if(size == -1) { return 0; }
		if(size == 0) { return 1; }
		return dimensions[dim];
	}


	/** Set the 1-order tensor of the specified index in the last dimension of this tensor to the specified tensor.
	 * @param tensor the tensor to assign
	 * @param index the index
	 * @return has operation been performed correctly? */
	public boolean setLastDimTensor(final Tensor tensor, final int index) {
		if(index < 0 || index >= getNumberOfLastDimTensors()) { return false; }
		int size = getLengthOfLastDimTensors();
		if(size != tensor.getSize()) { return false; }
		int j = index * size;
		for(int i = 0; i < size; i++) { value[j + i] = tensor.getScalar(i).getValue(); }
		return true;
	}


	/** Set the 1-order tensor of the specified index in the last dimension of this tensor to the specified list of Double values.
	 * @param list the list of values to assign
	 * @param index the index
	 * @return has operation been performed correctly? */
	public boolean setLastDimTensor(final List list, final int index) {
		if(index < 0 || index >= getNumberOfLastDimTensors()) { return false; }
		int size = getLengthOfLastDimTensors();
		if(size != list.size()) { return false; }
		int j = index * size;
		for(int i = 0; i < size; i++) { value[j + i] = ((Double)list.get(i)).doubleValue(); }
		return true;
	}


	/** Get the 1-order tensor of the specified index in the last dimension of this tensor.
	 * @param index the index
	 * @return the tensor */
	public Tensor getLastDimTensor(final int index) {
		if(index < 0 || index >= getNumberOfLastDimTensors()) { return Tensor.newVector(); }
		int size = getLengthOfLastDimTensors();
		if(size == 0) { return Tensor.newVector(); }
		Tensor result = Tensor.newVector(size);
		int j = index * size;
		for(int i = 0; i < size; i++) { result.setScalar(value[j + i], i); }
		return result;
	}


	/** Get the 1-order tensor of the specified index in the given dimension of this tensor.
	 * <br>Currently if the requested dimension is not first or last one only the 3 dim case is handled. 
	 * @param index the index
	 * @param dim the dimension
	 * @return the tensor */
	public Tensor getGivenDimTensor(final int index, final int dim) {
		if(dim < 0 || dim >= order) { return Tensor.newVector(); }
		if(index < 0 || index >= getNumberOfGivenDimTensors(dim)) { return Tensor.newVector(); }
		int size = getLengthOfGivenDimTensors(dim);
		if(size == 0) { return Tensor.newVector(); }
		Tensor result = Tensor.newVector(size);
		if(dim == 0) {
			int delta = 1;
			for(int i = 1; i < order; i++) { delta *= getLengthOfGivenDimTensors(i); }
			for(int i = 0; i < size; i++) { result.setScalar(value[delta * i + index], i); }
		} else if(dim == order - 1) {
			int j = index * size;
			for(int i = 0; i < size; i++) { result.setScalar(value[j + i], i); }
		} else { // here assumed only for the 3-dim case, i.e. 1 in {0,1,2}
			int d1 = getLengthOfGivenDimTensors(1);
			int d2 = getLengthOfGivenDimTensors(2);
			for(int i = 0; i < size; i++) { result.setScalar(value[d2 * i + ((int)(index / d2)) * d2 * (d1 - 1) + index], i); }
		}
		return result;
	}


	/**
	 * Remove the subtensor identified by the specified index on first, i.e., slowest, dimension. 
	 *
	 * @param index the index
	 */
	public void removeOnFirstDim(final int index) {
		if(index < 0 || index >= dimensions[0]) { return; }
		int d = 1;
		for(int i = 1; i < order; i++) { d *= dimensions[i]; }
		int size = value.length;
		double temp[] = new double[size];
		for(int i = 0; i < size; i++) { temp[i] = value[i]; }
		value = new double[size - d];
		for(int i = 0; i < index; i++) {
			for(int j = 0; j < d; j++) { value[i * d + j] = temp[i * d + j]; }
		}
		for(int i = index + 1; i < dimensions[0]; i++) {
			for(int j = 0; j < d; j++) { value[(i - 1) * d + j] = temp[i * d + j]; }
		}
		dimensions[0]--;
		setPointers();
	}


	/**
	 * Deep copy from the specified tensor.
	 *
	 * @param tensor the tensor to be cloned
	 */
	public void deepCopy(final Tensor tensor) {
		if(tensor.getSize() == -1) { return; }
		if(tensor.order == 0) {
			value = new double[] {tensor.value[0]};
			order = 0;
			dimensions = null;
			return;
		}
		int s = tensor.getSize();
		value = new double[s];
		for(int i = 0; i < s; i++) { value[i] = tensor.value[i]; }
		order = tensor.order;
		dimensions = new int[order];
		for(int i = 0; i < order; i++) { dimensions[i] = tensor.dimensions[i]; }
		setPointers();
	}


	/**
	 * Get a resized copy of this tensor to the specified dimensions.
	 *
	 * @param dims the dimensions
	 * @param conservative should the sequence position be maintained for existing elements?
	 *
	 * @return the resized tensor
	 */
	public Tensor resize(final int[] dims, final boolean conservative) {
		int newOrder = dims.length;
		int newSize = Tensor.getSize(dims);
		if(newOrder == 0 || newSize <= 0) { return Tensor.newScalar(value != null ? value[0] : 0.0); }
		Tensor result = new Tensor(dims);
		result.setPointers();
		result.value = new double[newSize];
		int oldSize = getSize();
		if(oldSize == -1) { return result; }
		if(conservative) {
			double[] tempValue = new double[newSize];
			int[][] oldIndexes = Tensor.getIndexes(dimensions, 0);
			int[][] newIndexes = Tensor.getIndexes(dims, 0);
			int[] p = new int[order];
			Tensor tensor;
			for(int i = 0; i < newSize; i++) {
				for(int j = 0; j < order; j++) { p[j] = newIndexes[j][i]; }
				tensor = getSubTensor(p);
				if(tensor.getSize() != -1) {
					tempValue[i] = tensor.getValue();
				} else {
					tempValue[i] = 0.0;
				}
			}
			for(int i = 0; i < newSize; i++) { result.value[i] = tempValue[i]; }
		} else {
			if(oldSize >= newSize) {
				for(int i = 0; i < newSize; i++) { result.value[i] = value[i]; }
			} else {
				for(int i = 0; i < oldSize; i++) { result.value[i] = value[i]; }
			}
		}
		return result;
	}


	/**
	 * Get a transposed copy of this tensor.
	 *
	 * @param last2first set the last dimension as the first one (or vice versa)
	 *
	 * @return the transposed tensor
	 */
	public Tensor transpose(final boolean last2first) {
		Tensor result = this.clone();
		if(order == 0) { return result; }
		int size = getSize();
		if(size == -1 || size == 1) { return result; }
		if(order == 1) { // a specific case: transform the (column) vector into a 1-row matrix
			result.order = 2;
			result.dimensions = new int[]{1, size};
			result.setPointers();
			return result;
		}
		if(order == 2 && dimensions[0] == 1) { // a specific case: transform the a 1-row matrix into a (column) vector
			result.order = 1;
			result.dimensions = new int[]{size};
			result.setPointers();
			return result;
		}
		int[][] indexes = Tensor.getIndexes(dimensions, 0);
		int[][] newIndexes = new int[order][size];
		if(last2first) {
			for(int i = 1; i < order; i++) {
				result.dimensions[i] = dimensions[i - 1];
				for(int j = 0; j < size; j++) { newIndexes[i][j] = indexes[i - 1][j]; }
			}
			result.dimensions[0] = dimensions[order - 1];
			for(int j = 0; j < size; j++) { newIndexes[0][j] = indexes[order - 1][j]; }
		} else {
			for(int i = 0; i < order - 1; i++) {
				result.dimensions[i] = dimensions[i + 1];
				for(int j = 0; j < size; j++) { newIndexes[i][j] = indexes[i + 1][j]; }
			}
			result.dimensions[order - 1] = dimensions[0];
			for(int j = 0; j < size; j++) { newIndexes[order - 1][j] = indexes[0][j]; }
		}
		result.setPointers();
		for(int i = 0; i < size; i++) { 
			int p = 0;
			for(int j = 0; j < order; j++) { p += newIndexes[j][i] * result.pointers[j]; }
			result.value[p] = value[i];
		}
		return result;
	}


	/**
	 * Multiply this tensor by the specified one.
	 *
	 * @param tensor the tensor to be multiplied
	 *
	 * @return the multiplied tensor or an error object
	 */
	public Object product(final Tensor tensor) {
		if(order == 0 && tensor.order == 0) { // a specific case...
			return Tensor.newScalar(getValue() * tensor.getValue());
		}
		if(order == 0) {
			double x = getValue();
			Tensor result = tensor.clone();
			int s = tensor.getSize();
			for(int i = 0; i < s; i++) { result.value[i] = tensor.value[i] * x; }
			return result;
		}
		if(tensor.order == 0) {
			double x = tensor.getValue();
			Tensor result = clone();
			int s = getSize();
			for(int i = 0; i < s; i++) { result.value[i] = value[i] * x; }
			return result;
		}
		if(order > 2 || tensor.order > 2) {
			return Integer.valueOf(-2); // ERR: not implemented (yet)
		}
		if(order == 1 || dimensions[1] != tensor.dimensions[0]) {
			return Integer.valueOf(-1); // ERR: sizes not compatible
		}
		int rowCount = dimensions[0];
		int colCount = dimensions[1];
		int xcolCount = tensor.order == 1 ? 1 : tensor.dimensions[1];
		Tensor result = new Tensor(new int[]{rowCount, xcolCount});
		for(int i = 0; i < rowCount; i++) {
			for(int j = 0; j < xcolCount; j++) {
				double r = 0;
				for(int k = 0; k < colCount; k++) { r += getSubTensor(i, k).getValue() * (tensor.order == 1 ? tensor.getScalar(k).getValue() : tensor.getSubTensor(k, j).getValue()); }
				result.setSubTensor(Tensor.newScalar(r), i, j);
			}
		}
		return result;
	}


	/**
	 * Get a copy of this tensor concatenated with the specified tensor.
	 *
	 * @param tensor the tensor to be concatenated
	 *
	 * @return the concatenated tensor or an error object
	 */
	public Object concatenate(final Tensor tensor) {
		if(order == 0 && tensor.order == 0) { // a specific case...
			Tensor result = Tensor.newVector(2);
			result.setScalar(value[0], 0);
			result.setScalar(tensor.value[0], 1);
			return result;
		}
		int[] dim1 = null;
		int[] dim2 = null;
		int ord;
		if(order == tensor.order) {
			dim1 = new int[order];
			dim2 = new int[order];
			for(int i = 0; i < order; i++) {
				dim1[i] = dimensions[i];
				dim2[i] = tensor.dimensions[i];
			}
			ord = order;
		} else if(order == tensor.order - 1) {
			dim1 = new int[tensor.order];
			dim2 = new int[tensor.order];
			for(int i = 0; i < order; i++) {
				dim1[i] = dimensions[i];
				dim2[i] = tensor.dimensions[i];
			}
			dim1[order] = 1;
			dim2[order] = tensor.dimensions[order];
			ord = tensor.order;
		} else if(order == tensor.order + 1) {
			dim1 = new int[order];
			dim2 = new int[order];
			for(int i = 0; i < tensor.order; i++) {
				dim1[i] = dimensions[i];
				dim2[i] = tensor.dimensions[i];
			}
			dim1[tensor.order] = dimensions[tensor.order];
			dim2[tensor.order] = 1;
			ord = order;
		} else {
			return Integer.valueOf(-1); // ERR: orders not compatible
		}
		if(getSize() == -1 || tensor.getSize() == -1) { // one or both are empty
			int[] newDims = new int[ord];
			for(int i = 0; i < ord; i++) { newDims[i] = Math.max(dim1[i], dim2[i]); }
			Tensor result = new Tensor(newDims);
			if(value != null && value.length > 0) {
				for(int i = 0; i < value.length; i++) { result.value[i] = value[i]; }
			} else if(tensor.value != null && tensor.value.length > 0) {
				for(int i = 0; i < tensor.value.length; i++) { result.value[i] = tensor.value[i]; }
			}
			return result;
		}
		for(int i = 0; i < ord - 1; i++) {
			if(dim1[i] != dim2[i]) { return Integer.valueOf(-2); } // ERR: dimensions not compatible
		}
		int[] newDims = new int[ord];
		for(int i = 0; i < ord - 1; i++) { newDims[i] = dim1[i]; }
		newDims[ord - 1] = dim1[ord - 1] + dim2[ord - 1];
		Tensor result = new Tensor(newDims);
		int d1 = dim1[ord - 1];
		int d2 = dim2[ord - 1];
		int p = 1;
		for(int i = 0; i < ord - 1; i++) { p *= dim1[i]; }
		int k = 0;
		for(int i = 0; i < p; i++) {
			for(int j = 0; j < d1; j++) { result.setScalar(getScalar(i * d1 + j), k++); }
			for(int j = 0; j < d2; j++) { result.setScalar(tensor.getScalar(i * d2 + j), k++); }
		}
		return result;
	}


	/**
	 * Get a copy of this tensor concatenated with the specified tensor along the specified dimension.
	 *
	 * @param tensor the tensor to be concatenated
	 * @param dim the dimension along which the concatenation has to be done
	 *
	 * @return the concatenated tensor or an error object
	 */
	public Object concatenate(final Tensor tensor, final int dim) {
		if(dim < 0 || dim > 1) { return Integer.valueOf(-4); } // ERR: only up 2 dim concatenation is implemented
		if(order == 0 && tensor.order == 0) { // a specific case...
			Tensor result = Tensor.newVector(2);
			result.setScalar(value[0], 0);
			result.setScalar(tensor.value[0], 1);
			return result;
		}
		int[] dim1 = null;
		int[] dim2 = null;
		int ord;
		if(order == tensor.order) {
			dim1 = new int[order];
			dim2 = new int[order];
			for(int i = 0; i < order; i++) {
				dim1[i] = dimensions[i];
				dim2[i] = tensor.dimensions[i];
			}
			ord = order;
		} else if(order == tensor.order - 1) {
			dim1 = new int[tensor.order];
			dim2 = new int[tensor.order];
			for(int i = 0; i < order; i++) {
				dim1[i] = dimensions[i];
				dim2[i] = tensor.dimensions[i];
			}
			dim1[order] = 1;
			dim2[order] = tensor.dimensions[order];
			ord = tensor.order;
		} else if(order == tensor.order + 1) {
			dim1 = new int[order];
			dim2 = new int[order];
			for(int i = 0; i < tensor.order; i++) {
				dim1[i] = dimensions[i];
				dim2[i] = tensor.dimensions[i];
			}
			dim1[tensor.order] = dimensions[tensor.order];
			dim2[tensor.order] = 1;
			ord = order;
		} else { return Integer.valueOf(-1); } // ERR: orders not compatible
		if(dim < 0 || dim >= ord) {
			return Integer.valueOf(-3); // ERR: specified dimension not compatible with order
		}
		int s1 = 0;
		int s2 = 0;
		if((s1 = getSize()) == -1 || (s2 = tensor.getSize()) == -1) { // one or both are empty
			int[] newDims = new int[ord];
			for(int i = 0; i < ord; i++) { newDims[i] = Math.max(dim1[i], dim2[i]); }
			Tensor result = new Tensor(newDims);
			if(value != null && value.length > 0) {
				for(int i = 0; i < value.length; i++) { result.value[i] = value[i]; }
			} else if(tensor.value != null && tensor.value.length > 0) {
				for(int i = 0; i < tensor.value.length; i++) { result.value[i] = tensor.value[i]; }
			}
			return result;
		}
		for(int i = 0; i < ord; i++) {
			if(i != dim && dim1[i] != dim2[i]) { return Integer.valueOf(-2); } // ERR: dimensions not compatible
		}
		int[] newDims = new int[ord];
		for(int i = 0; i < ord; i++) { newDims[i] = dim1[i]; }
		newDims[dim] = dim1[dim] + dim2[dim];
		Tensor result = new Tensor(newDims);
		if(dim == 0) {
			for(int i = 0; i < s1; i++) { result.setScalar(getScalar(i), i); }
			for(int i = 0; i < s2; i++) { result.setScalar(tensor.getScalar(i), s1 + i); }
			return result;
		} else if(dim == 1) {
			int d1 = dim1[ord - 1];
			int d2 = dim2[ord - 1];
			int p = 1;
			for(int i = 0; i < ord - 1; i++) { p *= dim1[i]; }
			int k = 0;
			for(int i = 0; i < p; i++) {
				for(int j = 0; j < d1; j++) { result.setScalar(getScalar(i * d1 + j), k++); }
				for(int j = 0; j < d2; j++) { result.setScalar(tensor.getScalar(i * d2 + j), k++); }
			}
			return result;
		}
		return Integer.valueOf(-4); // ERR: only up 2 dim concatenation is implemented
	}


	/**
	 * Get a copy of this tensor to which the specified number of (n-1)-order tensors have been either head or tail removed ("decatenated").
	 *
	 * @param num the number of tensors to be removed
	 * @param head remove from head?
	 *
	 * @return the decatenated tensor or an error object
	 */
	public Object decatenate(final int num, final boolean head) {
		if(order == 0) { return Integer.valueOf(-1); } // ERR: scalar tensor
		if(getSize() == -1) { return this; }
		if(num <= 0) { return this; }
		if(num >= dimensions[order - 1]) { return new Tensor(order); }
		int[] newDims = new int[order];
		for(int i = 0; i < order - 1; i++) { newDims[i] = dimensions[i]; }
		int d = newDims[order - 1] = dimensions[order - 1] - num;
		Tensor result = new Tensor(newDims);
		int p = 1;
		for(int i = 0; i < order - 1; i++) { p *= newDims[i]; }
		int k = 0;
		int offset = head ? num : 0;
		for(int i = 0; i < p; i++) {
			for(int j = 0; j < d; j++) { result.setScalar(getScalar(offset + i * dimensions[order - 1] + j), k++); }
		}
		return result;
	}


	/**
	 * Get a copy of this tensor to which the specified number of (n-1)-order tensors have been either head or tail removed ("decatenated")
	 * along the specified dimension.
	 *
	 * @param num the number of tensors to be removed
	 * @param head remove from head?
	 * @param dim the dimension along which the decatenation has to be done
	 *
	 * @return the decatenated tensor or an error object
	 */
	public Object decatenate(final int num, final boolean head, final int dim) {
		if(dim < 0 || dim > 1) { return Integer.valueOf(-5); } // ERR: only up 2 dim decatenation is implemented
		if(order == 0) { return Integer.valueOf(-1); } // ERR: scalar tensor
		int s = getSize();
		if(s == -1) { return this; }
		if(num <= 0) { return this; }
		if(num >= dimensions[dim]) { return new Tensor(order); }
		int[] newDims = new int[order];
		if(dim == 0) {
			for(int i = 1; i < order; i++) { newDims[i] = dimensions[i]; }
			int d = newDims[0] = dimensions[0] - num;
			Tensor result = new Tensor(newDims);
			int skip = dimensions[1] * num;
			if(head) {
				for(int i = skip; i < s; i++) { result.setScalar(getScalar(i), i - skip); }
			} else {
				for(int i = 0; i < s - skip; i++) { result.setScalar(getScalar(i), i); }
			}
			return result;
		} else if(dim == 1) {
			for(int i = 0; i < order - 1; i++) { newDims[i] = dimensions[i]; }
			int d = newDims[order - 1] = dimensions[order - 1] - num;
			Tensor result = new Tensor(newDims);
			int p = 1;
			for(int i = 0; i < order - 1; i++) { p *= newDims[i]; }
			int k = 0;
			int offset = head ? num : 0;
			for(int i = 0; i < p; i++) {
				for(int j = 0; j < d; j++) { result.setScalar(getScalar(offset + i * dimensions[order - 1] + j), k++); }
			}
			return result;
		}
		return Integer.valueOf(-4); // ERR: only up 2 dim concatenation is implemented
	}


	/**
	 * Clone the tensor.
	 *
	 * @return the tensor
	 */
	@Override public Tensor clone() {
		Tensor tensor = new Tensor();
		if(order == 0) {
			tensor.value = new double[] {value[0]};
			tensor.order = 0;
			tensor.dimensions = null;
			return tensor;
		}
		int s = getSize();
		if(s != -1) {
			tensor.value = new double[s];
			for(int i = 0; i < s; i++) { tensor.value[i] = value[i]; }
		} else {
			tensor.value = null;
		}
		tensor.order = order;
		tensor.dimensions = new int[order];
		for(int i = 0; i < order; i++) { tensor.dimensions[i] = dimensions[i]; }
		tensor.setPointers();
		return tensor;
	}


	/** Get the value of this tensor as a formatted string.
	 * @param format format, either FORMAT_SHORT, e.g., @[2] for non-scalars,
	 * 		or FORMAT_ALTERNATE, e.g., [1,2,3] if the tensor size is less than ALTERNATE_THRESHOLD,
	 * 		or FORMAT_LONG, e.g., [1,2,3] in all cases
	 * @param numberFormat numberFormat
	 * @return the value */
	public String getValueAsString(final int format, final String numberFormat) {
		if(order == 0) { return (new DecimalFormat(numberFormat)).format(value[0]); }
		if(format == FORMAT_LONG || (format == FORMAT_ALTERNATE && getSize() <= alternateThreshold)) { return toString(); }
		StringBuilder s = new StringBuilder("@[");
		if(dimensions != null) {
			for(int i = 0; i < order; i++) { s.append(dimensions[i]).append(","); }
		} else {
			for(int i = 0; i < order; i++) { s.append("0,"); }
		}
		return s.substring(0, s.length() - 1) + "]";
	}


	/** Get the value of this tensor as a formatted string.
	 * @param format format, either FORMAT_SHORT, e.g., @[2] for non-scalars,
	 * 		or FORMAT_ALTERNATE, e.g., [1,2,3] if the tensor size is less than ALTERNATE_THRESHOLD,
	 * 		or FORMAT_LONG, e.g., [1,2,3] in all cases
	 * @return the value */
	public String getValueAsString(final int format) {
		if(order == 0) { return "" + value[0]; }
		if(format == FORMAT_LONG || (format == FORMAT_ALTERNATE && getSize() <= alternateThreshold)) { return toString(); }
		StringBuilder s = new StringBuilder("@[");
		if(dimensions != null) {
			for(int i = 0; i < order; i++) { s.append(dimensions[i]).append(","); }
		} else {
			for(int i = 0; i < order; i++) { s.append("0,"); }
		}
		return s.substring(0, s.length() - 1) + "]";
	}


	/** Set the threshold to switch from LONG to SHORT format in string generation.
	 * @param threshold the threshold */
	public static void setAlternateThreshold(final int threshold) { alternateThreshold = threshold; }


	/** Get the threshold to switch from LONG to SHORT format in string generation. */
	public static int getAlternateThreshold() { return alternateThreshold; }


	/** Print out the tensor.
	 * @return the printed string */
	@Override public String toString() {
		if(order == 0) { return "" + value[0]; }
		if(value == null || value.length == 0) {
			String s = "[]";
			for(int i = 1; i < order; i++) { s = "[" + s + "]"; }
			return s;
		}
		int n1 = dimensions[order - 1]; //number of elements per fastest dim
		int n2 = value.length / n1; //number of groups 
		String[] ss = new String[n2];
		int k = 0;
		for(int i = 0; i < n2; i++) {
			ss[i] = "";
			for(int j = 0; j < n1; j++) { ss[i] += value[k++] + ","; }
			ss[i] = "[" + ss[i].substring(0, ss[i].length() - 1) + "]";
		}
		for(int i = order - 2; i >= 0; i--) {
			ss = buildString(ss, dimensions[i]);
		}
		return ss[0];
	}


	/** Helper for toString().
	 * @param s s
	 * @param n1 n1
	 * @return string */
	private String[] buildString(String[] s, int n1) {
		int n2 = s.length / n1;
		String[] ss = new String[n2];
		int k = 0;
		for(int i = 0; i < n2; i++) {
			ss[i] = "";
			for(int j = 0; j < n1; j++) { ss[i] += s[k++] + ","; }
			ss[i] = "[" + ss[i].substring(0, ss[i].length() - 1) + "]";
		}
		return ss;
	}


	/** Get the value of this scalar.
	 * @return the value */
	public double getValue() { return value[0]; }


	/** Set the value of this scalar to the specified value.
	 * @param value the value */
	public void setValue(final double value) { this.value[0] = value;	}


	/** Get whether this tensor is a scalar and its value is a number, and not infinite or NaN.
	 * @return result */
	public boolean isNumber() {
		if(order != 0) { return false; }
		double v = value[0];
		return (v == v && !(v == Double.POSITIVE_INFINITY) || (v == Double.NEGATIVE_INFINITY));
	}


	/** Round the values of the specified tensor to the specified number of decimal places
	 * and return the resulting tensor.
	 * @param tensor the tensor whose values are to be rounded
	 * @param scale the number of digits to the right of the decimal point
	 * @return the tensor of rounded values */
	public static Tensor round(final Tensor x, final int scale) {
		Tensor res = x.clone();
		double[] v = res.value;
		for(int i = 0; i < v.length; i++) { v[i] = Tensor.round(v[i], scale); }
		return res;
	}


	/** Round the given value to the specified number of decimal places.
	 * <br>Adapted from Jakarta Commons Math.
	 * @param x the value to round
	 * @param scale the number of digits to the right of the decimal point
	 * @return the rounded value */
	private static double round(final double x, final int scale) {
		if(scale == 0) { return (int)(x + 0.5); }
		double sign = Double.isNaN(x) ? Double.NaN : ((x >= 0.0) ? 1.0 : -1.0);
		double factor = Math.pow(10.0, scale) * sign;
		double unscaled = x * factor;
		double fraction = Math.abs(unscaled - Math.floor(unscaled));
		unscaled = (fraction >= 0.5) ? Math.ceil(unscaled) : Math.floor(unscaled);
		return unscaled / factor;
	}


	//************************************************
	//*** Vector-specific (1-order tensor) methods ***
	//************************************************

	/** Vector-specific (1-order tensor) method: get the specified element.
	 * @param elementIndex the index of the element to be returned
	 * @return element */
	public Tensor getElement(final int elementIndex) {
		if(value == null) { return null; }
		if(order != 1) { return null; }
		if(elementIndex < 0 || elementIndex >= value.length) { return null; }
		return Tensor.newScalar(value[elementIndex]);
	}


	/** Vector-specific (1-order tensor) method: set the specified element.
	 * @param data the data to be inserted
	 * @param elementIndex the index of the element to be returned */
	public void setElement(final Tensor data, final int elementIndex) {
		if(value == null) { return; }
		if(order != 1) { return; }
		if(elementIndex < 0 || elementIndex >= value.length) { return; }
		if(data.order != 1 || data.value.length == 0) { return; }
		if(elementIndex < 0 || elementIndex >= value.length) { return; }
		value[elementIndex] = data.getValue();
	}


	/** Vector-specific (1-order tensor) method: append a new element,
	 * possibly to a still empty vector.
	 * @param data the data to be inserted */
	public void appendElement(final Tensor data) {
		if(order != 1) { return; }
		if(data.order != 0) { return; }
		if(value == null) {
			if(data.value == null) { return; }
			value = new double[1];
			value[0] = data.value[0];
			dimensions[0] = 1;
			setPointers();
			return;
		}
		int size = value.length;
		double temp[] = new double[size];
		for(int i = 0; i < size; i++) { temp[i] = value[i]; }
		value = new double[size + 1];
		for(int i = 0; i < size; i++) { value[i] = temp[i]; }
		value[size] = data.getValue();
		dimensions[0]++;
		setPointers();
	}


	//************************************************
	//*** Matrix-specific (2-order tensor) methods ***
	//************************************************

	/** Matrix-specific (2-order tensor) method: get the number of rows.
	 * @return number */
	public int getRowCount() {
		if(order != 2) { return 0; }
		return dimensions[0];
	}


	/** Matrix-specific (2-order tensor) method: get the number of columns.
	 * @return number */
	public int getColumnCount() {
		if(order != 2) { return 0; }
		return dimensions[1];
	}


	/** Matrix-specific (2-order tensor) method: get the specified row.
	 * @param rowIndex the index of the row to be returned
	 * @return row */
	public Tensor getRow(final int rowIndex) {
		if(value == null) { return null; }
		if(order != 2) { return null; }
		if(rowIndex < 0 || rowIndex >= dimensions[0]) { return null; }
		Tensor result = Tensor.newVector(dimensions[1]);
		int j = rowIndex * pointers[0];
		for(int i = 0; i < dimensions[1]; i++) { result.value[i] = value[j + i]; }
		return result;
	}


	/** Matrix-specific (2-order tensor) method: get the specified column.
	 * @param columnIndex the index of the column to be returned
	 * @return column */
	public Tensor getColumn(final int columnIndex) {
		if(value == null) { return null; }
		if(order != 2) { return null; }
		if(columnIndex < 0 || columnIndex >= dimensions[1]) { return null; }
		Tensor result = Tensor.newVector(dimensions[0]);
		for(int i = 0; i < dimensions[0]; i++) { result.value[i] = value[columnIndex + i * pointers[0]]; }
		return result;
	}


	/** Matrix-specific (2-order tensor) method: set the specified row.
	 * @param data the data to be inserted
	 * @param rowIndex the index of the row to be overwritten  */
	public void setRow(final Tensor data, final int rowIndex) {
		if(order != 2) { return; }
		if(rowIndex < 0 || rowIndex >= dimensions[0]) { return; }
		if(data.order != 1 || data.value == null) { return; }
		int j = rowIndex * pointers[0];
		for(int i = 0; i < Math.min(data.dimensions[0], dimensions[1]); i++) { value[j + i] = data.value[i]; }
	}


	/** Matrix-specific (2-order tensor) method: set the specified column.
	 * @param data the data to be inserted
	 * @param columnIndex the index of the column to be overwritten */
	public void setColumn(final Tensor data, final int columnIndex) {
		if(order != 2) { return; }
		if(columnIndex < 0 || columnIndex >= dimensions[1]) { return; }
		if(data.order != 1 || data.value == null) { return; }
		for(int i = 0; i < Math.min(data.dimensions[0], dimensions[0]); i++) { value[columnIndex + i * pointers[0]] = data.value[i]; }
	}


	/** Matrix-specific (2-order tensor) method: append a new row,
	 * possibly to a still empty matrix.
	 * @param data the data to be inserted */
	public void appendRow(final Tensor data) {
		if(order != 2) { return; }
		if(data.order != 1) { return; }
		if(value == null) {
			if(data.value == null) { return; }
			int l = data.value.length;
			value = new double[l];
			for(int i = 0; i < l; i++) { value[i] = data.value[i]; }
			dimensions[0] = 1;
			dimensions[1] = l;
			setPointers();
			return;
		}
		int size = value.length;
		double temp[] = new double[size];
		for(int i = 0; i < size; i++) { temp[i] = value[i]; }
		value = new double[size + dimensions[1]];
		for(int i = 0; i < size; i++) { value[i] = temp[i]; }
		if(data.dimensions[0] >= dimensions[1]) {
			for(int i = 0; i < dimensions[1]; i++) { value[size + i] = data.value[i]; }
		} else {
			for(int i = 0; i < data.dimensions[0]; i++) { value[size + i] = data.value[i]; }
			for(int i = data.dimensions[0]; i < dimensions[1]; i++) { value[size + i] = 0.0; }
		}
		dimensions[0]++;
		setPointers();
	}


	/** Matrix-specific (2-order tensor) method: append a new column,
	 * possibly to a still empty matrix.
	 * @param data the data to be inserted */
	public void appendColumn(final Tensor data) {
		if(order != 2) { return; }
		if(data.order != 1) { return; }
		if(value == null) {
			if(data.value == null) { return; }
			int l = data.value.length;
			value = new double[l];
			for(int i = 0; i < l; i++) { value[i] = data.value[i]; }
			dimensions[0] = l;
			dimensions[1] = 1;
			setPointers();
			return;
		}
		int size = value.length;
		double temp[] = new double[size];
		for(int i = 0; i < size; i++) { temp[i] = value[i]; }
		value = new double[size + dimensions[0]];
		int i = 0;
		int j = 0;
		for(int r = 0; r < dimensions[0]; r++) {
			for(int c = 0; c < dimensions[1]; c++) {
				value[i++] = temp[j++];
			}
			value[i++] = (r < data.value.length) ? data.value[r] : 0.0;
		}
		dimensions[1]++;
		setPointers();
	}


	/** Matrix-specific (2-order tensor) method: remove the first column. */
	public void removeFirstColumn() {
		if(order != 2) { return; }
		if(value == null) { return; }
		if(dimensions[1] == 1) {
			value = null;
			dimensions[0] = 0;
			dimensions[1] = 0;
			setPointers();
			return;
		}
		int size = value.length;
		double temp[] = new double[size];
		for(int i = 0; i < size; i++) { temp[i] = value[i]; }
		value = new double[size - dimensions[0]];
		int i = 0;
		int j = 0;
		for(int r = 0; r < dimensions[0]; r++) {
			j++;
			for(int c = 0; c < dimensions[1] - 1; c++) {
				value[i++] = temp[j++];
			}
		}
		dimensions[1]--;
		setPointers();
	}


	/** Matrix-specific (2-order tensor) method: remove the last column. */
	public void removeLastColumn() {
		if(order != 2) { return; }
		if(value == null) { return; }
		if(dimensions[1] == 1) {
			value = null;
			dimensions[0] = 0;
			dimensions[1] = 0;
			setPointers();
			return;
		}
		int size = value.length;
		double temp[] = new double[size];
		for(int i = 0; i < size; i++) { temp[i] = value[i]; }
		value = new double[size - dimensions[0]];
		int i = 0;
		int j = 0;
		for(int r = 0; r < dimensions[0]; r++) {
			for(int c = 0; c < dimensions[1] - 1; c++) {
				value[i++] = temp[j++];
			}
			j++;
		}
		dimensions[1]--;
		setPointers();
	}

}
