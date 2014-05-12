package fr.tse.lt2c.satin.testdecompo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import au.com.bytecode.opencsv.CSVWriter;
import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;

public class TestDecompo {
	public static void main(String[] args) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("reportJava.csv"), ';',
				CSVWriter.NO_QUOTE_CHARACTER);
		File f = new File("polygons/full/");
		for (File tmp : f.listFiles()) {
			if (tmp.isFile() && tmp.getName().endsWith("png")) {
				System.out.println(tmp.getAbsolutePath());
				BufferedImage image = ImageIO.read(tmp);
				BinaryMatrix m = convertImage(image);
				WSMRDecomposer extractor = new WSMRDecomposer(
						m, true, false, false);
				DecompositionResult res = extractor.computeDecomposition(1);
				String[] entries = new String[6];
				entries[0] = tmp.getAbsolutePath().toLowerCase()
						.replaceAll("\"", "");
				entries[1] = "" + image.getWidth();
				entries[2] = "" + image.getHeight();
				entries[3] = "'WSMR'";
				entries[4] = "" + res.getTime();
				entries[5] = "" + res.getRectangle().size();
				writer.writeNext(entries);
			}
		}
		writer.close();
	}

	/**
	 * Previous method just draws the vertices of the polygons, this method will
	 * the polygons with 1s
	 * 
	 * @param matrix
	 */
	private static void fillPolygons(BinaryMatrix matrix) {
		boolean[][] theMatrix = matrix.matrix;
		for (int i = 0; i < matrix.getCol(); i++) {
			fillLine(theMatrix[i]);
		}
	}

	private static void fillLine(boolean[] bs) {
		boolean hasOne = false;
		int currentBegin = 0, currentEnd;
		for (int i = 0; i < bs.length; i++) {
			if (bs[i]) {
				if (!hasOne) {// begins the interval
					hasOne = true;
					currentBegin = i;
				} else {
					hasOne = false;
					currentEnd = i;

					Arrays.fill(bs, currentBegin, currentEnd, true);
				}
			}
		}
	}

	private static BinaryMatrix convertImage(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		boolean[][] matrix = new boolean[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (image.getRGB(j, i) == 0xFFFFFFFF)
					matrix[i][j] = true;
			}
		}
		BinaryMatrix mat = new BinaryMatrix(matrix, height, width);
		fillPolygons(mat);
		return mat;
	}
}
