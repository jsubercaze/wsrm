package fr.tse.lt2c.satin.matrix.beans;

import java.util.Arrays;
import java.util.Random;

/**
 * Bean for binary matrix Generates random matrix for a given ratio
 * 
 * 
 * @author Julien Subercaze
 * 
 */
public class BinaryMatrix {
	/**
	 * Percentage of 1 in the matrix
	 */
	private double ratio = 0.7;
	/**
	 * Backing matrix
	 */
	public boolean[][] matrix;
	/**
	 * Rows
	 */
	int row;
	/**
	 * Columns
	 */
	int col;
	/**
	 * Number of ones
	 */
	private int numberOfOnes = -1;

	/**
	 * 
	 * @param row
	 *            number of rows
	 * @param col
	 *            number of columns
	 * @param random
	 *            if <code>true</code> fills the matrix with random values
	 * @param ratio
	 *            if <code>random</code> is set to <code>true</code>, percentage
	 *            of 1-entries in the random matrix
	 */
	public BinaryMatrix(int row, int col, double ratio) {
		super();
		this.row = row;
		this.col = col;
		this.ratio = ratio;
		matrix = new boolean[row][col];

		generateRandom();

	}

	private void generateRandom() {
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				matrix[i][j] = r.nextDouble() < ratio;
			}
		}
		countNumberOfOnes();
	}

	public BinaryMatrix(boolean[][] matrix, int row, int col) {
		super();
		this.matrix = matrix;
		this.row = row;
		this.col = col;
		countNumberOfOnes();
	}

	public boolean[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(boolean[][] matrix) {
		this.matrix = matrix;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	@Override
	public String toString() {
		// Better output than deepToString
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < this.row; i++) {
			s.append(Arrays.toString(matrix[i]) + "\n");
		}
		return s.toString().replaceAll("false", "0").replaceAll("true", "1");
	}

	/**
	 * Return a copy of the matrix
	 * 
	 * @return
	 */
	public BinaryMatrix copy() {
		boolean[][] myCopy = new boolean[this.row][];
		for (int i = 0; i < this.row; i++) {
			myCopy[i] = new boolean[this.col];
			System.arraycopy(this.matrix[i], 0, myCopy[i], 0, this.col);
		}
		return new BinaryMatrix(myCopy, row, col);
	}

	public void countNumberOfOnes() {
		numberOfOnes = 0;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				numberOfOnes += matrix[i][j] ? 1 : 0;
			}
		}
	}

	/**
	 * 
	 * @return the number of 1-entries in the matrix
	 */
	public int getNumberOfOnes() {
		if (numberOfOnes == -1)
			countNumberOfOnes();
		return numberOfOnes;
	}

	/**
	 * return a new matrix that is the logical AND of the two matrixes The
	 * current matrix and the parameters must have similar dimensions (no check
	 * done here)
	 * 
	 * @param m
	 * @return
	 */
	public BinaryMatrix difference(BinaryMatrix m) {
		boolean[][] newmatrix = new boolean[this.row][this.col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				newmatrix[i][j] = (this.matrix[i][j] != m.matrix[i][j]);
			}
		}
		return new BinaryMatrix(newmatrix, row, col);
	}

}
