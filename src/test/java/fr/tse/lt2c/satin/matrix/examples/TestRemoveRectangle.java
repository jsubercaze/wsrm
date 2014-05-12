package fr.tse.lt2c.satin.matrix.examples;

import fr.tse.lt2c.satin.matrix.beans.LinkedMatrix;
import fr.tse.lt2c.satin.matrix.beans.LinkedMatrixElement;

public class TestRemoveRectangle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean[][] matrixArray = new boolean[4][5];
		matrixArray[0][0] = true;
		matrixArray[1][0] = true;
		LinkedMatrix matrix = new LinkedMatrix(matrixArray, -1, -1);
		System.out.println(matrix);
		System.out.println(matrix.getFirstElement().isNextElementDownConsecutive());
		LinkedMatrixElement upperLeftCorner = matrix.getFirstElement();
		matrix.removeRectangle(upperLeftCorner, 2, 1);
		System.out.println(matrix);
		System.out.println(matrix.getFirstElement().isNextElementDownConsecutive());

	}

}
