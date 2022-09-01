package org.nfunk.jep.type;


public class TensorTest {

	public static void main(String[] args) {

		Tensor t = new Tensor("[[1,2,3,33],[4,5,6,66]]");
		Tensor t2 = new Tensor("[[7,8,9,99]]");
		t = (Tensor)t2.concatenate(t, 0);
		System.out.println(t);

		/*
		Tensor t = new Tensor("[1,2,3,4,5,6,10,20,30,40,50,60]");
		t.setScalar(99, 3);
		System.out.println(t);
		*/

		/*
		Tensor t = new Tensor("[1,2,3,4,5,6,10,20,30,40,50,60]");
		System.out.println(t);
		t.removeOnFirstDim(1);
		System.out.println(t);
		*/
		/*
		Tensor t = new Tensor("[[[1,2,3],[4,5,6]],[[10,20,30],[40,50,60]]]");
		System.out.println(t);
		Tensor t2 = t.getSubTensorByMajorDim(0,2);
		System.out.println(t2);
		Tensor t3 = t2.transpose(false).transpose(false).transpose(false);
		System.out.println(t3);
		*/
		
		/*
		Tensor t = new Tensor("[[[1,2,3],[4,5,6]],[[7,8,9],[10,11,12]]]");
		System.out.println(t);
		System.out.println(t.getSubTensor(0));
		*/
		
		/*
		Tensor t = new Tensor(new int[] {2, 2, 3});
		System.out.println(t);
		int[][] x = t.getIndexes(new int[] {2, 2, 3}, 1);
		for(int j = 0; j < 3; j++) {
			for(int i = 0; i < t.getSize(); i++) {
				System.out.print(x[j][i] + " ");
			}
			System.out.println("");
		}
		*/
		
		/*
		int[][] x = Tensor.getIndexes(new int[] {2, 2, 2}, 1);
		for(int j = 0; j < 3; j++) {
			for(int i = 0; i < 8; i++) {
				System.out.print(x[j][i] + " ");
			}
			System.out.println("");
		}
		*/
		
		/*
		double[] x = new double[12];
		for(int i = 0; i < 12; i++) { x[i] = i; }
		Tensor t = new Tensor(x, new int[] {2, 2, 3});
		System.out.println(t);
		System.out.println(t.getSubTensor(0, 0));
		System.out.println(t.setSubTensor(new Tensor("[[16.0,7.0,18.0],[9.0,20.0,11.0]]"), new int[]{1}));
		System.out.println(t);
		*/

		

		/*
		System.out.println(t.getElement(0, 0, 1));
		t.setElement(999, 1, 0, 1);
		System.out.println(t);
		
		String s = "[[[[0.0,1.0,2.0],[3.0,4.0,5.0]],[[6.0,7.0,8.0],[9.0,10.0,11.0]],[[12.0,13.0,14.0],[15.0,16.0,17.0]]],[[[18.0,19.0,20.0],[21.0,22.0,23.0]],[[24.0,25.0,26.0],[27.0,28.0,29.0]],[[30.0,31.0,32.0],[33.0,34.0,35.0]]]]";
		
		Tensor t2 = new Tensor("[[[12.0,11.0],[10.0,9.0],[8.0,7.0]],[[6.0,5.0],[4.0,3.0],[2.0,1.0]]]");
		System.out.println(t2);
		t2.setElement(999, 1, 1, 0);
		System.out.println(t2);
		*/
	}

}
