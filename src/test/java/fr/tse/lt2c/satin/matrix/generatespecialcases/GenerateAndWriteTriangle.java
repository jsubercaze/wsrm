package fr.tse.lt2c.satin.matrix.generatespecialcases;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GenerateAndWriteTriangle {
	private static int size = 10000;

	public static void main(String[] args) throws IOException {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < size; i++) {
			for (int j = i % 2 == 1 ? 1 : 0; j < size; j += 2) {
				img.setRGB(i, j, 0xFFFFFF);
			}
		}
		ImageIO.write(img, "png", new File("chessboard" + size + ".png"));
	}
}
