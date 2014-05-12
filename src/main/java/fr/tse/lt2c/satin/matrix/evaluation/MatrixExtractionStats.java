package fr.tse.lt2c.satin.matrix.evaluation;

/** Stats for a given extraction
 * 
 * @author Julien Subercaze
 * 
 */
public class MatrixExtractionStats {

	int col;
	int row;
	int method;
	int rectangleCount;
	double coverage;
	double averageSize;
	double stdSize;

	public MatrixExtractionStats(int col, int row, int method,
			int rectangleCount, double coverage, double averageSize,
			double stdSize) {
		super();
		this.col = col;
		this.row = row;
		this.method = method;
		this.rectangleCount = rectangleCount;
		this.coverage = coverage;
		this.averageSize = averageSize;
		this.stdSize = stdSize;
	}

}
