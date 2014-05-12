package fr.tse.lt2c.satin.matrix.testsegmentation;

import java.io.File;
import java.io.IOException;

import fr.tse.lt2c.satin.matrix.extraction.imagesegmentation.SegmentImage;

public class TestSegmentationSpeed {

	public static void main(String[] args) throws IOException {
		File f = new File("imageDataset/jar-12.gif");
		SegmentImage segment = new SegmentImage(f);
		for (int i = 0; i < 10; i++) {
			System.out.println(segment.segmentSpeed());
		}
	}
}
