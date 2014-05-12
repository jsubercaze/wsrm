package fr.tse.lt2c.satin.matrix.ibr;

import java.io.File;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.ibr.IBRNonLinear;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;

public class TestIBR {
	public static void main(String[] args) {
		BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File("matrices/matrixAmphore.txt"));
		IBRNonLinear ibr = new IBRNonLinear(matrix);
		System.out.println(matrix);
		DecompositionResult extractDisjointRectangles = ibr.extractDisjointRectangles();
		System.out.println(extractDisjointRectangles.getRectangle().size());
	}
}
