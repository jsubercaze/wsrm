package fr.tse.lt2c.satin.matrix.largestrectangle;

import java.util.Arrays;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.LargestArea;
import fr.tse.lt2c.satin.matrix.beans.LinkedMatrix;

public class ComputeLargestRectangle {

	private BinaryMatrix binaryMatrix;
	private LargestArea[][] result;
	private LinkedMatrix linkedMatrix;

	public ComputeLargestRectangle(BinaryMatrix m) {
		super();
		this.binaryMatrix = m;
		result = new LargestArea[m.getRow()][m.getCol()];
	}

	public void compute(boolean topleft, boolean topright, boolean bottomleft,
			boolean bottomright) {
		
		if (!(topleft || topright || bottomleft || bottomright))
			System.exit(-1);
		if (topleft && bottomright) {
			
			FromTopLeft tl = new FromTopLeft(binaryMatrix, result);
			tl.compute();
			// System.out.println(matrixToString(result));
			FromBottomRight br = new FromBottomRight(binaryMatrix, result);
			br.setBuildMatrix(true);
			linkedMatrix = br.getBuiltMatrix();
			// System.out.println(matrixToString(result));
		} else if (topleft) {
			
			FromTopLeft tl = new FromTopLeft(binaryMatrix, result);
			tl.setBuildMatrix(true);
			linkedMatrix = tl.getBuiltMatrix();
			
		} else if (bottomright) {
			FromBottomRight br = new FromBottomRight(binaryMatrix, result);
			br.setBuildMatrix(true);
			linkedMatrix = br.getBuiltMatrix();
		}
	}

	public LinkedMatrix getLinkedMatrix() {
		return linkedMatrix;
	}

	public LargestArea[][] getResult() {
		return result;
	}

	private String matrixToString(Object[][] temp) {
		// Better output than deepToString
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < temp.length; i++) {
			s.append(Arrays.toString(temp[i]) + "\n");
		}
		return s.toString();
	}

}
