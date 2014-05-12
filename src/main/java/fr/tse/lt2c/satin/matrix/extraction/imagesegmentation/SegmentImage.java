package fr.tse.lt2c.satin.matrix.extraction.imagesegmentation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;
import fr.tse.lt2c.satin.matrix.comparators.PlaceRectangleComparator;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;

/**
 * Segment Binary B&W images
 * 
 * @author Julien
 * 
 */
public class SegmentImage {

	BufferedImage image;
	int[] colorscheme = { 0xE9003AFF, 0xFF5300FF, 0x00AB6FFF, 0xFF58E000,
			0xFFFF5600, 0xFFFF9900, 0xFF0D58A6, 0xFF00AA72, 0xFF4F10AD,
			0xFF9C02A7, 0xFFD2F700, 0xFFFFE500 };
	boolean useOptimization = false;

	public SegmentImage(BufferedImage image) {
		super();
		this.image = image;
	}

	public SegmentImage(File f) throws IOException {
		this(ImageIO.read(f));
	}

	public SegmentImage(String filename) throws IOException {
		this(new File(filename));
	}

	public BufferedImage segmentIntoBlock() {
		segmentAndUpdateImage();
		return image;
	}

	public long segmentSpeed() {
		BinaryMatrix m = convertImage();
		long t1 = System.currentTimeMillis();
		WSMRDecomposer extractor = new WSMRDecomposer(
				m, true, false, false);
		return extractor.computeDecomposition(1).getRectangle().size();
	}

	public void segmentIntoBlocksAndSaveToFile(File f) {
		segmentAndUpdateImage();
		System.out.println("opk");
		saveImage(f);
	}

	public boolean isUseOptimization() {
		return useOptimization;
	}

	public void setUseOptimization(boolean useOptimization) {
		this.useOptimization = useOptimization;
	}

	private void segmentAndUpdateImage() {
		BinaryMatrix m = convertImage();
		try {
			FileUtils.writeStringToFile(new File("matrxi.txt"), m.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<SortableRectangle> blocks = null;
		for (int i = 0; i < 10; i++) {
			long t1 = System.currentTimeMillis();

			if (!useOptimization) {
				WSMRDecomposer extractor = new WSMRDecomposer(
						m, true, false, false);
				blocks = extractor.computeDecomposition(1).getRectangle();
			} else {
				WSMRDecomposer extractor = new WSMRDecomposer(
						m, true, false, false);
				blocks = extractor.computeDecomposition(1).getRectangle();
			}
			System.out.println("Extraction "
					+ (System.currentTimeMillis() - t1));
		}

		Collections.sort(blocks, new PlaceRectangleComparator());
		System.out.println(blocks.size() + " rectangles");
		int i = 0;
		// Update colors in the image
		for (SortableRectangle rectangle : blocks) {
			colorRectangle(rectangle, colorscheme[i % colorscheme.length]);
			i++;
		}
	}

	private void colorRectangle(SortableRectangle rectangle, int color) {
		int startX = (int) rectangle.getX();
		int startY = (int) rectangle.getY();
		for (int i = 0; i < rectangle.getHeight(); i++) {
			for (int j = 0; j < rectangle.getWidth(); j++) {
				image.setRGB(startY + j, startX + i, color);
			}
		}

	}

	private void saveImage(File f) {
		try {
			ImageIO.write(image, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BinaryMatrix convertImage() {
		int height = image.getHeight();
		int width = image.getWidth();
		boolean[][] matrix = new boolean[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (image.getRGB(j, i) == 0xFFFFFFFF)
					matrix[i][j] = true;
			}
		}
		return new BinaryMatrix(matrix, height, width);
	}
}
