package fr.tse.lt2c.satin.matrix.generatespecialcases;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GenerateAndWriteChessBoard {
	private static int size = 100;

	public static void main(String[] args) throws IOException {
		for (int k = 500; k < 3000; k += 500) {
			size = k;
			BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
			for (int i = size - 1; i > 0; i--) {
				for (int j = 0; j < i; j++) {
					img.setRGB(i, j, 0xFFFFFF);
				}
			}
			ImageIO.write(img, "png", new File("triangle" + size + ".png"));
		}
	}
}
