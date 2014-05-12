package fr.tse.lt2c.satin.matrix.examples;

import java.io.File;
import java.io.IOException;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;

public class TestSavedImage {

	private static final int MINRECTANGLESIZE = 1;

	public static void main(String[] args) throws IOException {
		BinaryMatrix matrix = MatrixParser.binaryMatrixFromImage(new File(
				"po/mask2.png"));
//		System.out.println(matrix);
		System.out.println(matrix.getNumberOfOnes());
		WSMRDecomposer.DEBUG=true;
		WSMRDecomposer optim = new WSMRDecomposer(
				matrix);
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
