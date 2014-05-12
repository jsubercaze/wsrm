package fr.tse.lt2c.satin.matrix.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;

public class MatrixParser {

	public static BinaryMatrix binaryMatrixFromImage(File file)
			throws IOException {
		BufferedImage img = ImageIO.read(file);
		int width = img.getWidth();
		int height = img.getHeight();
		boolean[][] matrix = new boolean[height][width];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				
				if (img.getRGB(i, j) >= -1)
					matrix[j][i] = true;
			}
		}
		return new BinaryMatrix(matrix, height, width);
	}

	public static BinaryMatrix binaryMatrixFromFile(File file) {
		try {
			String content = FileUtils.readFileToString(file);
			String[] lines = content.split("\n");
			// Parse the size
			int height = lines.length;
			if (lines[height - 1].length() == 0)
				height--;
			int width = lines[0].replaceAll("[\\s\\p{Punct}]", "").length();
			// System.out.println(height + " " + width);
			boolean[][] matrix = new boolean[height][width];
			int i = 0;
			for (String line : lines) {

				int j = 0;
				Scanner in = new Scanner(line);
				in.useDelimiter("[\\[\\p{Punct}\\s]+");
				while (in.hasNext()) {

					if (in.hasNextInt()) {

						boolean _tmp = (in.nextInt() == 1 ? true : false);
						// System.out.println(_tmp + " " + i + " " + j);
						matrix[i][j] = _tmp;
						j++;
					} else
						in.next();
				}
				i++;
			}

			return new BinaryMatrix(matrix, height, width);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		File file = new File("testMatrixes/matrix0.txt");
		System.out.println(binaryMatrixFromFile(file));
	}
}
