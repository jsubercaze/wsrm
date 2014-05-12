package fr.tse.lt2c.satin.matrix.evaluation;

import java.io.File;
import java.io.IOException;

import fr.tse.lt2c.satin.matrix.extraction.imagesegmentation.SegmentImage;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;

public class LeavesEvaluation {
	public static void main(String[] args) throws IOException {
		int total = 0;
		long time = System.currentTimeMillis();
		File folder = new File("imageDataset/leaves");
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String files = listOfFiles[i].getName();
				if (files.endsWith(".png")) {
					WSMRDecomposer.DEBUG = false;
					SegmentImage segment = new SegmentImage(listOfFiles[i]);
					segment.setUseOptimization(true);
					total += segment.segmentSpeed();
				}
			}
		}
		System.out.println("Total blocks " + total);
		System.out.println("Elapsed time "
				+ (System.currentTimeMillis() - time));
	}
}
