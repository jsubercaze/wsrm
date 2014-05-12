package fr.tse.lt2c.satin.matrix.utils;

public class MatrixToLatex {

	/**
	 * Returns a Latex String that will print the matrix as tikzpicture with
	 * blocks
	 * 
	 * @return
	 */
	public static String matrixToBlock(boolean[][] matrix) {
		int cols = matrix[0].length;
		int rows = matrix.length;

		StringBuilder sb = new StringBuilder(3 * cols * rows);
		sb.append("\\begin{tikzpicture}\n");
		sb.append("\\matrix[square matrix,ampersand replacement=\\&]\n");
		sb.append("{\n");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (j != 0)
					sb.append("\\&");
				if (matrix[i][j]) {
					sb.append(" \\1 ");
				} else {
					sb.append(" ~ ");
				}
			}
			sb.append("\\\\ \n");// Line return
		}
		sb.append("};\n");
		return sb.toString();
	}

	/**
	 * Returns a Latex String that will print the matrix as tikzpicture with
	 * numbers
	 * 
	 * @return
	 */
	public static String matrixToNumbers(int[][] matrix) {
		int cols = matrix[0].length;
		int rows = matrix.length;
		StringBuilder sb = new StringBuilder(3 * cols * rows);
		sb.append("\\begin{tikzpicture}\n");
		sb.append("\\matrix [matrix of math nodes,left delimiter=(,right delimiter=), ampersand replacement=\\&] (m)\n");
		sb.append("{\n");
		for (int i = 0; i < rows; i++) {

			for (int j = 0; j < cols; j++) {
				sb.append(matrix[i][j] + " \\& ");
			}
			sb.append("\\\\ \n");// Line return
		}
		sb.append("};\n");
		return sb.toString();
	}
}
