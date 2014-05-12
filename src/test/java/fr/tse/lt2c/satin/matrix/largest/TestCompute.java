package fr.tse.lt2c.satin.matrix.largest;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.largestrectangle.ComputeLargestRectangle;

public class TestCompute {
	public static void main(String[] args) {
		BinaryMatrix m = new BinaryMatrix(7, 8, true, 0.7);
		System.out.println(m);
		ComputeLargestRectangle computor = new ComputeLargestRectangle(m);
		computor.compute(true,false,false,false);
	}
}
