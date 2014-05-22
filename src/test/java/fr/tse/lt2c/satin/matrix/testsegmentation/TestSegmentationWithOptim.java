package fr.tse.lt2c.satin.matrix.testsegmentation;

import java.io.File;
import java.io.IOException;

import fr.tse.lt2c.satin.matrix.extraction.imagesegmentation.SegmentImage;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;

public class TestSegmentationWithOptim {

	public static void main(String[] args) throws IOException {
		File f = new File("imageDataset/leaves/Aronia melanocarpa 2.png");
		
		SegmentImage segment = new SegmentImage(f);
		segment.setUseOptimization(true);

		segment.segmentIntoBlocksAndSaveToFile(new File("images/tested.png"));
	}
}
