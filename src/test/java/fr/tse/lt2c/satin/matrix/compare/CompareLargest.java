package fr.tse.lt2c.satin.matrix.compare;

import java.io.File;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;

public class CompareLargest {

	public static void main(String[] args) {
		BinaryMatrix m = MatrixParser.binaryMatrixFromFile(new File(
				"matrices/matrix15.txt"));
		WSMRDecomposer extractor = new WSMRDecomposer(
				m);
		System.out.println(extractor.computeDecomposition(1)
				.getRectangle());
		extractor = new WSMRDecomposer(m, true,
				false, false);
		System.out.println(extractor.computeDecomposition(1)
				.getRectangle());
	}
}
