package fr.tse.lt2c.satin.perf;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;

public class TestIterativeRemoval {
	private static final int MIN_AREA = 2;

	public static void main(String[] args) {
		BinaryMatrix m = new BinaryMatrix(200, 200, .7);
		WSMRDecomposer extractor = new WSMRDecomposer(m);

		Rectangle rect = null;
		long t1 = System.currentTimeMillis();
		do {
			rect = extractor.findLargestRectangle();
			// Update the matrix by removing the rectangle
			for (int i = rect.x; i < rect.x + rect.height; i++) {
				Arrays.fill(m.matrix[i], rect.y, rect.y + rect.width, false);
				// litUneLigne();
			}
		} while (rect.width * rect.height >= MIN_AREA);
		System.out.println(System.currentTimeMillis() - t1);
	}

	/**
	 * 
	 * @return une ligne entrée au clavier
	 */
	public static String litUneLigne() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			return reader.readLine();
		} catch (IOException e) {
			System.err.println("Erreur de lecture I/O, fermeture du programme");
			// A éviter, mais vous verrez les exceptions dans un prochain TP
			return null;
		}
	}
}
