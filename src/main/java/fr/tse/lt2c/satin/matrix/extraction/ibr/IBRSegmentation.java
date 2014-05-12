package fr.tse.lt2c.satin.matrix.extraction.ibr;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.behaviors.RectangleExtractor;

public class IBRSegmentation implements RectangleExtractor {

	BinaryMatrix B = null;
	int MIN_AREA;

	public IBRSegmentation(BinaryMatrix matrix) {
		this.B = matrix;
	}

	@Override
	public DecompositionResult extractDisjointRectangles() {
		boolean ch, c1, c2, c3, c4;
		int m, n, k1 = 0, k2, jx, jy, jxn, jyn, jx1 = 0, jx2, jy1 = 0, jy2, flgdist = 0, k = 0;
		boolean[][] matrix = B.getMatrix();
		n = B.getCol();
		m = B.getRow();
		System.out.println(n + " " + m);
		for (jx = 0; jx < m - 1; jx++) {
			for (jy = 0; jy < n - 1; jy++) {
				System.out.println("[" + jx + ";" + jy + "]");
				c1 = matrix[jx][jy];
				c2 = matrix[jx][jy+1];
				c3 = matrix[jx+1][jy];
				c4 = matrix[jx + 1][jy + 1];
				if (!c1 && c2) {
					jx1 = jx + 1;
					jy1 = jy;
					k1 = 1;
					while (!c3 && c4 && (jx + k1 + 1) < m) {
						k1++;
						c3 = matrix[jx+k1][jy];
						c4 = matrix[jx+k1][jy];
					}
				}
				if (c1 && c2) {
					k2 = 1;
					while (c3 && c4 && (jx + k2 + 1) < m && k2 < k1) {
						k2++;
						c3 = matrix[jx+k2][jy];
						c4 = matrix[jx+k2][jy];
					}
					if (k2 < k1)
						k2 = k1;
				}
				if (c1 && !c2) {
					k2 = 1;
					while (c3 && c4 && (jy + k2 + 1) < n && k2 < k1) {
						k2++;
						c3 = matrix[jx+k2][jy];
						c4 = matrix[jx+k2][jy];
					}
					if (k2 < k1)
						k2 = k1;
					jx2 = jx+k1-1;
					jy2 = jy;
					k++;
					System.out.println(k + " " + c1 + " " + c2 + " " + c3 + " "
							+ c4);
					System.out.println(jx1 + " " + jx2 + " " + jy1 + " " + jy2);
				}
			}
		}
		return null;
	}

}
