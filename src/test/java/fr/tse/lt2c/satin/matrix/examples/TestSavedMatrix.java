package fr.tse.lt2c.satin.matrix.examples;

import java.io.File;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;

public class TestSavedMatrix {

	private static final int MINRECTANGLESIZE = 1;

	public static void main(String[] args) {
		BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File(
				"matrices/mask4.txt"));
		System.out.println(matrix.getNumberOfOnes());
		WSMRDecomposer optim = new WSMRDecomposer(matrix);
		System.out.println(matrix);
		DecompositionResult res = optim.computeDecomposition(MINRECTANGLESIZE);
		System.out.println(res);
		System.out.println(res.reconstructMatrix(matrix.getRow(),
				matrix.getCol()));
		System.out.println(matrix.difference(res.reconstructMatrix(
				matrix.getRow(), matrix.getCol())));
		// Collections.sort(res.getRectangle());
		System.out.println(res);
		System.out.println(res.overlappingRectangles());

	}

}
