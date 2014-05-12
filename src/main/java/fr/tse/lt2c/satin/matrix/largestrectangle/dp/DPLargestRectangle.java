package fr.tse.lt2c.satin.matrix.largestrectangle.dp;

public class DPLargestRectangle {

	boolean[][] matrix;
	int height, width;

	public DPLargestRectangle(boolean[][] matrix, int height, int width) {
		super();
		this.matrix = matrix;
		this.height = height;
		this.width = width;
	}

	public int dp(int x, int y) {
		if (x == 0 || y == 0)
			return 0;
		return dp(x - 1, y) + dp(x, y - 1) + (matrix[x][y] ? 0 : 1);
	}
}
