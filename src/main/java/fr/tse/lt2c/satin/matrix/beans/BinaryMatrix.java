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

	private double ratio = 0.7; // Ratio of 1 in the random matrix
	public boolean[][] matrix;
	int row;
	int col;
	private int numberOfOnes = -1;

	public BinaryMatrix(int row, int col, boolean random, double ratio) {
		super();
		this.row = row;
		this.col = col;
		this.ratio = ratio;
		matrix = new boolean[row][col];
		if (random) {
			generateRandom();
		}// Otherwise full of 0, bool default values
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
