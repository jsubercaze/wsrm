package fr.tse.lt2c.satin.matrix.examples;

import java.io.IOException;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;


public class TestLargeReal {
	private static final int MIN_AREA = 2;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		for (int i = 0; i < 100; i++) {
			BinaryMatrix matrix = new BinaryMatrix(465, 465,  .7);
			// System.out.println(matrix);
			// FileUtils.writeStringToFile(new File("matrix.txt"),
			// matrix.toString());
			WSMRDecomposer extractor = new WSMRDecomposer(matrix);
		
			long t1 = System.nanoTime();

			DecompositionResult result = extractor.computeDecomposition(MIN_AREA);
			System.out.println((System.nanoTime() - t1)/(1000*1000) + " ms");
			// Collections.sort(result.getRectangle());
			// System.out.println(result.getRectangle());
		}

	}

}
