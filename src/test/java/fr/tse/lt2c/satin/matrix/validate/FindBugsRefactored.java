package fr.tse.lt2c.satin.matrix.validate;

import java.io.IOException;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;

public class FindBugsRefactored {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		int resarea = 0;
		int matrixarea = 0;
		do {
			BinaryMatrix matrix = new BinaryMatrix(512, 512, .6);
			matrixarea = matrix.getNumberOfOnes();
			// System.out.println(matrixarea);
			// FileUtils.writeStringToFile(new File("matrix.txt"),
			// matrix.toString());
			WSMRDecomposer optim1 = new WSMRDecomposer(
					matrix, false, false, false);
			long t1 = System.currentTimeMillis();
			DecompositionResult result = optim1.computeDecomposition(1);
			resarea = result.totalArea();
			// System.out.println(res);
			System.out.println("" + result.getRectangle().size()
					+ " rectangles in " + (System.currentTimeMillis() - t1)
					+ " ms " + resarea);
		} while (resarea == matrixarea);
	}
}
