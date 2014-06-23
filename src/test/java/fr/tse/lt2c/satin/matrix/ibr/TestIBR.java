package fr.tse.lt2c.satin.matrix.ibr;

import java.io.File;
import java.io.IOException;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.ibr.IBRLinear;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;

public class TestIBR {
	public static void main(String[] args) throws Exception {
		for (int i = 1; i <= 5; i++) {
			// BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File(
			// "matrices/matrix7.txt"));
			BinaryMatrix matrix = MatrixParser.binaryMatrixFromImage(new File(
					"c:/libs/rectangles/pic/test" + i + ".png"));
			IBRLinear ibr = new IBRLinear(matrix);
			DecompositionResult extractDisjointRectangles = ibr
					.extractDisjointRectangles();
			System.out.println(extractDisjointRectangles.getTime());
		}
	}
}
