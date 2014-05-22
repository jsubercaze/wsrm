package fr.tse.lt2c.satin.matrix.evaluation.run;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;
import fr.tse.lt2c.satin.matrix.evaluation.ExtractionResult;
import fr.tse.lt2c.satin.matrix.evaluation.MetricOutput;


public class RandomGrowing {

	final static int RUN_BY_SIZE = 1;
	final static int MIN_SIZE = 1000;
	final static int MAX_SIZE = 2000;
	final static int STEP = 500;
	private static final int MIN_AREA = 2;
	private final static List<ExtractionResult> global = new ArrayList<>(
			(MAX_SIZE - MIN_SIZE) / STEP + 1);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// RecursiveRectangleExtraction recursive;
		// ArrayMatrixRectangleExtraction linear;
		// ExtractionResult tmp;
		// for (int i = MIN_SIZE; i <= MAX_SIZE; i += STEP) {
		// System.out.println("Matrix side " + i);
		// tmp = new ExtractionResult(i, i);
		// for (int j = 0; j < RUN_BY_SIZE; j++) {
		// BinaryMatrix m = new BinaryMatrix(i, i, true, 0.7);
		// // recursive = new RecursiveRectangleExtraction(m, MIN_AREA);
		// linear = new ArrayMatrixRectangleExtraction(m);
		// tmp.addResult(linear.maximumDisjointRectangles(MIN_AREA), 0);
		// // tmp.addResult(recursive.extractDisjointRectangles(), 1);
		// linear = null;
		// }
		// tmp.computeStats();
		// global.add(tmp);
		// }
		// outputCSVResults2D();
		// outputCSVResults3D();
	}

	/**
	 * Create output for the 2D x = area
	 */
	private static void outputCSVResults2D() {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter("result2d.csv"),
					';');
			int area;
			String[] entries = new String[8];
			entries[0] = "Area";
			entries[1] = "Method";
			entries[2] = "Avg #rectangles";
			entries[3] = "CV #rectangles";
			entries[4] = "Avg #coverage";
			entries[5] = "CV #coverage";
			entries[6] = "Avg runtime (ms)";
			entries[7] = "CV runtime (ms)";
			writer.writeNext(entries);

			for (ExtractionResult r : global) {
				area = (r.getCol() * r.getRow());
				Map<Integer, MetricOutput> res = r.getComputedResults();
				Set<Integer> keys = res.keySet();
				for (Integer key : keys) {
					MetricOutput tmp = res.get(key);
					entries[0] = "" + area;// area
					entries[1] = "" + key;
					entries[2] = "" + tmp.getRectangleCountMean();
					entries[3] = "" + tmp.getCoverageCV();
					entries[4] = "" + tmp.getCoverageMean();
					entries[5] = "" + tmp.getCoverageCV();
					entries[6] = "" + tmp.getTimeMean();
					entries[7] = "" + tmp.getTimeCV();
					writer.writeNext(entries);
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create output for the 3D (x,y)= (row,col)
	 */
	private static void outputCSVResults3D() {

	}
}
