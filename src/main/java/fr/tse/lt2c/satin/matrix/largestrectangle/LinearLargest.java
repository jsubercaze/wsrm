package fr.tse.lt2c.satin.matrix.largestrectangle;

import java.util.Arrays;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.LargestArea;
import fr.tse.lt2c.satin.matrix.beans.LinkedMatrix;

/**
 * Implementation of the Marco.za solution
 * 
 * @author Julien Subercaze
 * 
 */
public class LinearLargest {
	private int[][] height;
	private int[][] left;
	private int[][] right;
	private int[][] area;
	private int[][] areaPoint;

	private LargestArea[][] result;
	private boolean buildMatrix;
	private LinkedMatrix linkedMatrix;
	private BinaryMatrix binaryMatrix;

	public LinearLargest(BinaryMatrix binaryMatrix, LargestArea[][] result) {
		this.binaryMatrix = binaryMatrix;
		this.result = result;
		height = new int[binaryMatrix.getRow()][binaryMatrix.getCol()];
		right = new int[binaryMatrix.getRow()][binaryMatrix.getCol()];
		left = new int[binaryMatrix.getRow()][binaryMatrix.getCol()];
		area = new int[binaryMatrix.getRow()][binaryMatrix.getCol()];

	}

	public void compute() {
		computeRightDP();
		computeLeftDP();
		computeHeightDP();
		System.out.println(binaryMatrix);
		// System.out.println(MatrixToLatex.matrixToNumbers(height));
		// System.out.println(MatrixToLatex.matrixToNumbers(left));
		// System.out.println(MatrixToLatex.matrixToNumbers(right));
		// System.out.println(MatrixToLatex.matrixToNumbers(area));
		//System.out.println(matrixToString(height));
		//System.out.println(matrixToString(left));
		//System.out.println(matrixToString(right));
		System.out.println(matrixToString(area));
		computeLargestArea();
		System.out.println(matrixToString(areaPoint));
	}

	private void computeRightDP() {
		int lastColumn;
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			lastColumn = binaryMatrix.getCol();
			for (int j = binaryMatrix.getCol() - 1; j >= 0; j--) {// R to L
				if (!binaryMatrix.getMatrix()[i][j]) { // Has a 0
					// right[i][j] = 0; Default value
					lastColumn = j;
				} else {
					if (i > 0) {
						if (!binaryMatrix.getMatrix()[i - 1][j]) {
							right[i][j] = lastColumn - j;
						} else {
							right[i][j] = Math.min(right[i - 1][j], lastColumn
									- j);
						}
					} else {
						right[i][j] = lastColumn - j;
					}

				}
			}
		}

	}

	private void computeLeftDP() {
		int lastColumn; // p in the doc
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			lastColumn = -1;
			for (int j = 0; j < binaryMatrix.getCol(); j++) {
				if (!binaryMatrix.getMatrix()[i][j]) { // Has a 0
					// left[i][j] = 0;
					lastColumn = j;
				} else {// Has a 1
					if (i > 0) {
						if (!binaryMatrix.getMatrix()[i - 1][j]) {
							left[i][j] = j - lastColumn;
						} else {
							left[i][j] = Math.min(left[i - 1][j], j
									- lastColumn);
						}
					} else {
						left[i][j] = j - lastColumn;
					}
				}
			}
		}
	}

	/**
	 * Same as for UpperLeftCorner
	 */
	private void computeHeightDP() {

		for (int j = 0; j < binaryMatrix.getCol(); j++) {
			for (int i = 0; i < binaryMatrix.getRow(); i++) {
				if (binaryMatrix.getMatrix()[i][j]) {
					if (i == 0) {
						height[i][j] = 1;
						area[i][j] = height[i][j]
								* ((left[i][j]) + right[i][j] - 1);
					} else {
						height[i][j] = height[i - 1][j] + 1;
						area[i][j] = height[i][j]
								* ((left[i][j]) + (right[i][j]) - 1);
					}
				}
			}
		}

	}

	private void computeLargestArea() {
		areaPoint = new int[binaryMatrix.getRow()][binaryMatrix.getCol()];
		for (int i = 0; i < binaryMatrix.getCol(); i++) {
			for (int j = 0; j < binaryMatrix.getRow(); j++) {
				if (area[i][j] > 0) {
					int pointx = i - height[i][j] + 1;
					int pointy = j - left[i][j]+1;
					if (area[i][j] > areaPoint[pointx][pointy])
						areaPoint[pointx][pointy] = area[i][j];
				}
			}
		}
	}

	private String matrixToString(int[][] temp) {
		// Better output than deepToString
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < temp.length; i++) {
			s.append(Arrays.toString(temp[i]) + "\n");
		}
		return s.toString();
	}

}
