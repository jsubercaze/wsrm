package fr.tse.lt2c.satin.matrix.validate;

import java.io.File;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;

public class ValidateAll {

	private static final int MINRECTANGLESIZE = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		for (int i = 1; i <= 11; i++) {
			System.out.println("----------------------------------------");
			System.out.println("------------    "+i+" ------------------");
			System.out.println("----------------------------------------");
			BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File("matrices/matrix"+i+".txt"));
			System.out.println(matrix.getNumberOfOnes());
			WSMRDecomposer.DEBUG = true;
			WSMRDecomposer largestCompute = new WSMRDecomposer(matrix);
			System.out.println(matrix);
			DecompositionResult res = largestCompute.computeDecomposition(MINRECTANGLESIZE);
			System.out.println(res.totalArea());
			System.out.println(res.reconstructMatrix(matrix.getRow(), matrix.getCol()));
			System.out.println(matrix.difference(res.reconstructMatrix(matrix.getRow(), matrix.getCol())));
			// Collections.sort(res.getRectangle());
			System.out.println(res);
			System.out.println(res.overlappingRectangles());
		}

	}

}
