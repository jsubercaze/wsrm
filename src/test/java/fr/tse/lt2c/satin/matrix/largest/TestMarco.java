package fr.tse.lt2c.satin.matrix.largest;

import java.io.File;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.LargestArea;
import fr.tse.lt2c.satin.matrix.largestrectangle.LinearLargest;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;
import fr.tse.lt2c.satin.matrix.utils.MatrixToLatex;

public class TestMarco {
	public static void main(String[] args) {
		BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File(
				"matrices/matrix11.txt"));
		System.out.println(matrix);
		System.out.println(MatrixToLatex.matrixToBlock(matrix.getMatrix()));
		LinearLargest linear = new LinearLargest(matrix, new LargestArea[matrix.getRow()][matrix.getCol()]);
		linear.compute();
	}
}
