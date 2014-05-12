package fr.tse.lt2c.satin.matrix.testsegmentation;

import java.io.File;
import java.io.IOException;

import fr.tse.lt2c.satin.matrix.extraction.imagesegmentation.SegmentImage;

public class TestSegmentation {

	public static void main(String[] args) throws IOException {
		File f= new File("imageDataset/rlf1.jpg");
		SegmentImage segment = new SegmentImage(f);
		segment.segmentIntoBlocksAndSaveToFile(new File("segmented/thetest.jpg"));
	}
}
