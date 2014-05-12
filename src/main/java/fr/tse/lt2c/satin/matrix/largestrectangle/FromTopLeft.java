package fr.tse.lt2c.satin.matrix.largestrectangle;

import java.util.Arrays;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.LargestArea;
import fr.tse.lt2c.satin.matrix.beans.LinkedMatrix;
import fr.tse.lt2c.satin.matrix.beans.LinkedMatrixElement;

public class FromTopLeft {
	private int[][] temp1;
	private int[][] temp2;
	private int[][] temp3;
	private int[][] temp4;
	private BinaryMatrix binaryMatrix;
	private LargestArea[][] result;
	private boolean buildMatrix;
	private LinkedMatrix linkedMatrix;

	public FromTopLeft(BinaryMatrix binaryMatrix, LargestArea[][] result) {
		super();
		this.binaryMatrix = binaryMatrix;
		this.result = result;
		temp1 = new int[binaryMatrix.getRow()][binaryMatrix.getCol()];
		temp2 = new int[binaryMatrix.getRow()][binaryMatrix.getCol()];

	}

	public boolean isBuildMatrix() {
		return buildMatrix;
	}

	public void setBuildMatrix(boolean buildMatrix) {
		this.buildMatrix = buildMatrix;
	}

	public void compute() {
		firstStep();
		secondStep();
		thirdStep();
		
		fourthStep();
		
	}

	public LinkedMatrix getBuiltMatrix() {
		if (!isBuildMatrix())
			return null;

		firstStep();

		secondStep();

		thirdStep();
		System.out.println(matrixToString(temp1));
		System.out.println(matrixToString(temp2));
		System.out.println(matrixToString(temp3));
		System.out.println(matrixToString(temp4));
		fourthStep();
		System.out.println("Apres fourth step");
		System.out.println(matrixToString(temp1));
		System.out.println(matrixToString(temp2));
		// System.out.println(matrixToString(temp1));
		// System.out.println(matrixToString(temp4));
		// System.out.println(matrixToString(temp2));
		// System.out.println(matrixToString(temp3));
		return linkedMatrix;// TODO
	}

	/**
	 * Compute for each column the max
	 */
	private void firstStep() {
		int current;
		for (int j = 0; j < binaryMatrix.getCol(); j++) {
			current = 1;
			for (int i = binaryMatrix.getRow() - 1; i >= 0; i--) {
				if (binaryMatrix.getMatrix()[i][j]) {
					temp1[i][j] = current;
					current++;
				} else {
					current = 1;
				}

			}
		}
	}

	/**
	 * Compute for each row
	 */
	private void secondStep() {
		int current;
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			current = 1;
			for (int j = binaryMatrix.getCol() - 1; j >= 0; j--) {
				if (binaryMatrix.getMatrix()[i][j]) {
					temp2[i][j] = current;
					current++;
				} else {
					current = 1;
				}

			}
		}
	}

	/**
	 * The trick !
	 */
	private void thirdStep() {
		temp3 = new int[temp1.length][];
		for (int i = 0; i < temp1.length; i++)
			temp3[i] = temp1[i].clone();
		temp4 = new int[temp2.length][];
		for (int i = 0; i < temp2.length; i++)
			temp4[i] = temp2[i].clone();
		int previous;
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			previous = 0;
			for (int j = binaryMatrix.getCol() - 1; j >= 0; j--) {
				if (previous == 0)
					previous = temp3[i][j];
				else if (temp3[i][j] <= previous) {
					previous = temp3[i][j];
				} else {
					temp3[i][j] = previous;
				}

			}
		}
		// Et en colomnes
		for (int j = 0; j < binaryMatrix.getCol(); j++) {
			previous = 0;
			for (int i = binaryMatrix.getRow() - 1; i >= 0; i--) {
				if (previous == 0)
					previous = temp2[i][j];
				else if (temp4[i][j] <= previous) {
					previous = temp2[i][j];
				} else {
					temp4[i][j] = previous;
				}

			}
		}
	}

	private void fourthStep() {
		int[] area_tmp = new int[2];
		LinkedMatrixElement current = null;
		int largestArea = -1;
		if (buildMatrix) {
			linkedMatrix = new LinkedMatrix(binaryMatrix);
			current = linkedMatrix.getCurrentRoot();
		}
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			for (int j = 0; j < binaryMatrix.getCol(); j++) {
				if (buildMatrix)
					current = current.getNextRight();
				if (binaryMatrix.getMatrix()[i][j]) {
					area_tmp[0] = temp3[i][j] * temp2[i][j];
					area_tmp[1] = temp1[i][j] * temp4[i][j];
					int max = Math.max(area_tmp[0], area_tmp[1]);
					System.out.println((i+","+j+" | "+area_tmp[0]+" "+area_tmp[1]+" "+max));
					if (max == area_tmp[0]) {
						temp2[i][j] = temp2[i][j];
						temp1[i][j] = temp3[i][j];
						updateResult(i, j, temp2[i][j], temp3[i][j], max);
					} else {
						updateResult(i, j, temp4[i][j], temp1[i][j], max);
					}
					// Update largest rectangle
					if (max > largestArea) {
						largestArea = max;
						if (buildMatrix)
							linkedMatrix.setLargestRectangle(current);
					}
				} else {
					if (buildMatrix)
						current = linkedMatrix.deleteZero(current);
				}
			}
		}

	}

	private void updateResult(int i, int j, int width, int height, int area) {
		if (result[i][j] == null) {
			result[i][j] = new LargestArea(width, height, area);
		} else {
			if (area > result[i][j].getLargestArea())
				result[i][j].setDimensions(width, height, area);
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
