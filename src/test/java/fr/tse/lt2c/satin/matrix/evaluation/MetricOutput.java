package fr.tse.lt2c.satin.matrix.evaluation;

/**
 * This class stores the output of stats for a given method on a set of runs
 * 
 * 
 * @author Julien Subercaze
 * 
 */
public class MetricOutput {

	private double rectangleCountMean;
	private double rectangleCountCV;
	private double coverageMean;
	private double coverageCV;
	private double timeMean;// TODO
	private double timeCV;

	public MetricOutput(double rectangleCountMean, double rectangleCountCV,
			double coverageMean, double coverageCV) {
		super();
		this.rectangleCountMean = rectangleCountMean;
		this.rectangleCountCV = rectangleCountCV;
		this.coverageMean = coverageMean;
		this.coverageCV = coverageCV;
	}

	public MetricOutput(double rectangleCountMean, double rectangleCountCV,
			double coverageMean, double coverageCV, double timeMean, double timeCV) {
		super();
		this.rectangleCountMean = rectangleCountMean;
		this.rectangleCountCV = rectangleCountCV;
		this.coverageMean = coverageMean;
		this.coverageCV = coverageCV;
		this.timeMean = timeMean;
		this.timeCV = timeCV;
	}

	public double getRectangleCountMean() {
		return rectangleCountMean;
	}

	public double getRectangleCountCV() {
		return rectangleCountCV;
	}

	public double getCoverageMean() {
		return coverageMean;
	}

	public double getCoverageCV() {
		return coverageCV;
	}

	public double getTimeMean() {
		return timeMean;
	}

	public double getTimeCV() {
		return timeCV;
	}

}
