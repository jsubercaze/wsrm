package fr.tse.lt2c.satin.matrix.largest;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.largestrectangle.ComputeLargestRectangle;

public class TestComputeRight {
	public static void main(String[] args) {
		BinaryMatrix m = new BinaryMatrix(7, 8, 0.7);
		System.out.println(m);
		ComputeLargestRectangle computor = new ComputeLargestRectangle(m);
		computor.compute(false,false,false,true);
	}
}
