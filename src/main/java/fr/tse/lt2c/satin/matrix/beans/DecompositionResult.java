package fr.tse.lt2c.satin.matrix.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import fr.tse.lt2c.satin.matrix.comparators.PlaceRectangleComparator;

public class DecompositionResult {

	long time;
	List<SortableRectangle> rectangle;
	int numberOfOnes;
	DescriptiveStatistics statsRatio = new DescriptiveStatistics();
	DescriptiveStatistics statsArea = new DescriptiveStatistics();

	public DecompositionResult(long time, List<SortableRectangle> rectangle, int numberOfOnes) {
		super();
		this.time = time;
		this.rectangle = rectangle;
		this.numberOfOnes = numberOfOnes;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<SortableRectangle> getRectangle() {
		return rectangle;
	}

	public void setRectangle(List<SortableRectangle> rectangle) {
		this.rectangle = rectangle;
	}

	public int getNumberOfOnes() {
		return numberOfOnes;
	}

	public void setNumberOfOne(int numberOfOne) {
		this.numberOfOnes = numberOfOne;
	}

	@Override
	public String toString() {
		Collections.sort(rectangle, new PlaceRectangleComparator());
		return "Result " + rectangle.size() + " rectangles : [time=" + time
				+ ", rectangle=" + rectangle + ", numberOfOnes=" + numberOfOnes
				+ "]";
	}

	/**
	 * Metric for the quality of extraction : average rectangle size
	 * 
	 * @return
	 */
	public double quality() {
		int total = 0;
		for (SortableRectangle rect : rectangle) {
			total += rect.getArea();
		}
		return ((double) total / ((double) rectangle.size()));
	}

	public int totalArea() {
		int total = 0;
		for (SortableRectangle rect : rectangle) {
			total += rect.getArea();
		}
		return total;
	}

	public BinaryMatrix reconstructMatrix(int height, int width) {
		boolean[][] matrix = new boolean[height][width];
		for (SortableRectangle rect : rectangle) {
			addRectangle(rect, matrix);
		}
		return new BinaryMatrix(matrix, height, width);
	}

	private void addRectangle(SortableRectangle rect, boolean[][] matrix) {
		int startX = rect.x;
		int startY = rect.y;
		int width = rect.width;
		int height = rect.height;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				matrix[startX + i][startY + j] = true;
			}
		}
	}

	public List<SortableRectangle> overlappingRectangles() {
		List<SortableRectangle> result = new ArrayList<>();
		SortableRectangle[] array1 = rectangle
				.toArray(new SortableRectangle[rectangle.size()]);
		for (int i = 0; i < array1.length; i++) {
			for (int j = i + 1; j < array1.length; j++) {
				if (array1[i].intersects(array1[j])) {
					result.add(array1[i]);
					result.add(array1[j]);
				}
			}
		}
		return result;
	}

	public void initStats() {
		for (SortableRectangle rect : rectangle) {
			statsRatio.addValue(rectangleRatio(rect));
			statsArea.addValue(rect.height * rect.width);
		}
	}

	public double getRectangleRatioMean() {
		return statsRatio.getMean();
	}

	public double getRectangleRatioStd() {
		return statsRatio.getStandardDeviation();
	}

	public double getRectanglesAreaStd() {
		return statsArea.getMean();
	}

	private double rectangleRatio(SortableRectangle rect) {
		double ratio = rect.getHeight() / rect.getWidth();
		if (ratio < 1)
			ratio = 1 / ratio;
		return ratio;
	}
}
