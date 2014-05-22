package fr.tse.lt2c.satin.matrix.extraction.rowcolsegmentation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;
import fr.tse.lt2c.satin.matrix.extraction.behaviors.RectangleExtractor;

/**
 * Decompose a binary matrix <code>B : [n x m]</code> into a set of rectangles using row segmentation. <br />
 * In this approach, a rectangle is 1 x k, k < m.<br />
 * In other words, rectangle are looked in a line, but cannot be stacked bnetween lines. This is obviously a poor qualitative decomposition, yet running in O(nxm) time.
 * 
 * @author Christophe Gravier
 */
public class RowSegmenationMerging implements RectangleExtractor {

	BinaryMatrix B = null;
	int MIN_AREA;

	public RowSegmenationMerging(BinaryMatrix binMatrix, int minSize) {
		super();
		this.B = binMatrix;
		MIN_AREA = minSize;
	}

	@Override
	public DecompositionResult extractDisjointRectangles() {

		long t1 = System.nanoTime();
		List<SortableRectangle> rectangles = new ArrayList<>();

		// row-wise walk of the matrix from top left to bottom right.
		for (int i = 0; i < B.getRow(); i++) {
			int x = 0;
			int y = 0;
			boolean rectangleInProgess = false;
			for (int j = 0; j < B.getCol(); j++) {
				if (!rectangleInProgess && B.matrix[i][j]) { // start of a new rectangle
					if (j == (B.getCol() - 1)) { // but if it is the end this is a sole entry rectangle
						addRectangle(new SortableRectangle(i, j + 1, 1, 1));
						
					} else { // the walker start a new rectangle at this point.
						rectangleInProgess = true;
						x = j;
						y = j;
					}
				} else if (rectangleInProgess && (!(B.matrix[i][j]) || (j == (B.getCol() - 1)))) { // end of a rectangle (B[i][j] is zero or a right-size boundary) => add it to the collection
					int lastCount = (B.matrix[i][j]) ? 1 : 0;
					addRectangle(new SortableRectangle(i, x, (y - x + 1 + lastCount), 1));
					rectangleInProgess = false;
				} else if (rectangleInProgess && B.matrix[i][j]) { // same rectangle continues, increment width
					y++;
				}
			}
		}
		
		return new DecompositionResult(System.nanoTime() - t1, rectangles, B.getNumberOfOnes());
	}
	/** Add the current rectangle, try to merge when possible
	 * 
	 * @param sortableRectangle
	 */
	private void addRectangle(SortableRectangle sortableRectangle) {
		
		
	}

	public static void main(String[] args) {

		/********************* CONFIGURATION **************************/
		int minArea = 2;
		double oneMixture = 0.7;
		final int n = 1000;
		final int m = 1000;
		/************************** END *******************************/

		BinaryMatrix B = new BinaryMatrix(n, m,  oneMixture);
		System.out.println(B);

		RowSegmenationMerging extractor = new RowSegmenationMerging(B, minArea);
		DecompositionResult result = extractor.extractDisjointRectangles();
		System.out.println("Elapsed time : " + TimeUnit.NANOSECONDS.toMillis(result.getTime()) + " ms.\n");

		int nbOnes = 0;
		for (SortableRectangle rect : result.getRectangle()) {
			nbOnes += rect.getWidth();
			System.out.println("Found " + rect.toString());
		}

		int numberOfOnes = 0;
		for (int i = 0; i < B.getRow(); i++) {
			for (int j = 0; j < B.getCol(); j++) {
				numberOfOnes += B.matrix[i][j] ? 1 : 0;
			}
		}
		System.out.println("Rectangles cover " + nbOnes + " 1-entries");
		System.out.println("Matrix  contains " + numberOfOnes + " 1-entries");
	}
}
