package fr.tse.lt2c.satin.matrix.compare;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.ibr.IBRLinear;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;
import fr.tse.lt2c.satin.matrix.utils.MatrixParser;
/**
 * 
 * @author Julien
 *
 */
public class CompareLargest {

	public static void main(String[] args) throws IOException {
		WSMRDecomposer.DEBUG_LEVEL = Level.OFF;
		// BinaryMatrix m = MatrixParser.binaryMatrixFromFile(new File(
		// "matrices/matrixAmphore.txt"));
		
		for (int i = 1; i <= 7; i++) {
			BinaryMatrix m = MatrixParser.binaryMatrixFromImage(new File(
					"x:/rectangles/test"+i+".png"));
			WSMRDecomposer extractor = new WSMRDecomposer(m);
			DecompositionResult decompo;
			IBRLinear ibrliner = new IBRLinear(m);
			decompo = ibrliner.extractDisjointRectangles();
			//System.out.println(decompo.getRectangle().size());
			System.out.println(decompo.getTime()/1_000_000);
			decompo = extractor.computeDecomposition(1);
			//System.out.println(decompo.getRectangle().size());
			System.out.println(decompo.getTime()/1_000_000);
			extractor = new WSMRDecomposer(m, false, false, true);
			decompo = extractor.computeDecomposition(1);
			//System.out.println(decompo.getRectangle().size());
			System.out.println(decompo.getTime()/1_000_000);
			System.out.println("----------------------------------");
		}
	}
}
