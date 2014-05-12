package fr.tse.lt2c.satin.matrix.utils;

import java.io.File;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.LargestArea;
import fr.tse.lt2c.satin.matrix.largestrectangle.FromTopLeft;

public class GenerateLatex {
	public static void main(String[] args) {
		BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File("matrices/matrixpaper.txt"));
		LargestArea[][] tmp = new LargestArea[matrix.getCol()][matrix.getRow()];
		FromTopLeft rect = new FromTopLeft(matrix, tmp);
		rect.compute();
	}
}
