package fr.tse.lt2c.satin.matrix.extraction.recursive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;

/**
 * Recursive deletion method. Is used to be compared against the greedy method
 * 
 * Complexity is high (see paper)
 * 
 * @author Julien Subercaze
 * 
 */
public class RecursiveRectangleExtraction {

	BinaryMatrix m;
	int MIN_AREA;

	public RecursiveRectangleExtraction(BinaryMatrix m, int mIN_AREA) {
		super();
		this.m = m;
		MIN_AREA = mIN_AREA;
	}

	public DecompositionResult extractDisjointRectangles() {
		long t1 = System.currentTimeMillis();
		WSMRDecomposer r = new WSMRDecomposer(m);
		List<SortableRectangle> rectangles = new ArrayList<>();
		SortableRectangle rect = null;
		do {
			rect = r.findLargestRectangle();
			rectangles.add(rect);
			// Update the matrix, removes the rectangle previously found
			for (int i = rect.x; i < rect.x + rect.height; i++) {
				Arrays.fill(m.matrix[i], rect.y, rect.y + rect.width, false);
			}
		} while (rect.width * rect.height >= MIN_AREA);

		return new DecompositionResult(System.currentTimeMillis() - t1, rectangles,
				m.getNumberOfOnes());
	}

}
