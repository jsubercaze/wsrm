package fr.tse.lt2c.satin.matrix.compare;

import java.io.File;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;
/**
 * 
 * @author Julien
 *
 */
public class DisplayMatrix {
	public static void main(String[] args) {
		BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File("matrices/matrix10.txt"));
		System.out.println(matrix.getNumberOfOnes());
		WSMRDecomposer largestCompute = new WSMRDecomposer(matrix, false, false, false);
		
		largestCompute.computeDecomposition(1);
		
	}
}
