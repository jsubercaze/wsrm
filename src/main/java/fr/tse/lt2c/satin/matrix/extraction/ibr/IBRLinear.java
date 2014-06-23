package fr.tse.lt2c.satin.matrix.extraction.ibr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;
import fr.tse.lt2c.satin.matrix.extraction.behaviors.RectangleExtractor;

/**
 * Linear time implementation of IBR, aka GDM, Generalized Delta Method
 * 
 * @author Julien Subercaze
 * 
 */
public class IBRLinear implements RectangleExtractor {

	Logger logger = Logger.getLogger(IBRLinear.class);
	BinaryMatrix binaryMatrix = null;
	int MIN_AREA;
	/**
	 * Intervals are encoded as triple : x_start,x_end,height
	 */
	int[] intervals1;
	int[] intervals2;
	List<SortableRectangle> rectangles;
	int intervalIndex = 0;
	private int maxInterval;
	private boolean extendCurrent;
	private int previousIntervalInList;
	private int newIntervalInList;
	private int startX;
	private int[] intervalsPrevious;
	private int[] intervalsNew;
	private int[] buffer;
	private int xmin;
	private int xmax;
	private boolean inInterval;

	public IBRLinear(BinaryMatrix matrix) {
		this.binaryMatrix = matrix;
	}

	@Override
	public DecompositionResult extractDisjointRectangles() {
		long t1 = System.nanoTime();
		rectangles = new ArrayList<>();
		// First line
		boolean[][] mat = this.binaryMatrix.matrix;
		inInterval = false;
		intervals1 = new int[binaryMatrix.getCol() / 2 * 3];
		intervals2 = new int[binaryMatrix.getCol() / 2 * 3];
		// First line
		for (int i = 0; i < binaryMatrix.getCol(); i++) {
			if (mat[0][i]) {
				if (!inInterval) {
					// Create new interval
					intervals1[intervalIndex * 3] = i;
					inInterval = true;
				}
			} else {
				if (inInterval) {
					inInterval = false;
					intervals1[intervalIndex * 3 + 1] = i;
					intervals1[intervalIndex * 3 + 2] = 1;
					intervalIndex++;
				}
			}
		}
		// logger.debug("Intervals " + Arrays.toString(intervals1));
		// Steady
		maxInterval = intervalIndex;
		inInterval = false;
		extendCurrent = false;
		// Index in list of previous intervals
		previousIntervalInList = 0;
		// New list
		newIntervalInList = 0;
		// Start of current interval
		startX = 0;
		intervalsPrevious = intervals1;
		intervalsNew = intervals2;
		// Use to remove previous intervals if the current line wall filled with
		// zeros
		boolean foundInterval = false;
		for (int j = 1; j < binaryMatrix.getRow(); j++) {
			// logger.debug("New row");
			// logger.debug("Intervals Previous"
			// + Arrays.toString(intervalsPrevious));
			// logger.debug("Rectangles " + rectangles);
			previousIntervalInList = 0;
			newIntervalInList = 0;
			xmin = intervalsPrevious[0];
			xmax = intervalsPrevious[1];
			foundInterval = false;
			for (int i = 0; i < binaryMatrix.getCol(); i++) {
				// logger.debug("(" + i + "," + j + ")");
				if (mat[j][i]) {
					if (i != binaryMatrix.getCol() - 1) {
						if (!inInterval) {
							// logger.debug("Not already in interval");
							inInterval = true;
							foundInterval = true;
							startX = i;
							// Start a new interval, checks if matches with
							// previous
							if (i == xmin) {
								// logger.debug("Extending previous rectangle starting at x="
								// + xmin);
								extendCurrent = true;
							} else if (xmin < i
									&& previousIntervalInList < maxInterval - 1) {
								// Move to the index to match current X
								while (xmin < i
										&& previousIntervalInList < maxInterval) {
									// Add the previous as rectangle
									SortableRectangle rect = new SortableRectangle(
											intervalsPrevious[previousIntervalInList * 3],
											j
													- intervalsPrevious[previousIntervalInList * 3 + 2],
											intervalsPrevious[previousIntervalInList * 3 + 1]
													- intervalsPrevious[previousIntervalInList * 3],
											intervalsPrevious[previousIntervalInList * 3 + 2]);
									// logger.debug("Advancing and adding rectangle "
									// + rect);
									rectangles.add(rect);
									previousIntervalInList++;
									xmin = intervalsPrevious[previousIntervalInList * 3];
									xmax = intervalsPrevious[previousIntervalInList * 3 + 1];
								}
								// Once at the end
								xmax = intervalsPrevious[previousIntervalInList * 3 + 1];
								// Check again
								if (i == xmin) {
									extendCurrent = true;
								}
							} else {
								// logger.debug("In interval");
								// Add the previous rectangle if any
								if (maxInterval != 0
										&& previousIntervalInList < maxInterval) {
									SortableRectangle rect = new SortableRectangle(
											intervalsPrevious[previousIntervalInList * 3],
											j
													- intervalsPrevious[previousIntervalInList * 3 + 2],
											intervalsPrevious[previousIntervalInList * 3 + 1]
													- intervalsPrevious[previousIntervalInList * 3],
											intervalsPrevious[previousIntervalInList * 3 + 2]);
									// logger.debug("Adding rectangle " + rect);
									rectangles.add(rect);
								}

							}
						}
						/**
						 * We could check here in an {else},if it grows larger
						 * than previous line interval here, however it would
						 * lead to unnecessary comparisons, we rather check at
						 * the end
						 */
					} else {
						// End of line, check for interval
						checkInterval(i, j, true);
					}

				} else {
					if (inInterval) {
						// logger.debug("Finished interval");
					}
					// Did it grew larger than the max of previous interval
					// ?
					checkInterval(i, j, false);

				}

			}// Switch
			if (!foundInterval) {
				// logger.debug("Line full with 0s");
				for (int i = 0; i < maxInterval; i++) {
					SortableRectangle rect = new SortableRectangle(
							intervalsPrevious[i * 3], j
									- intervalsPrevious[i * 3 + 2],
							intervalsPrevious[i * 3 + 1]
									- intervalsPrevious[i * 3],
							intervalsPrevious[i * 3 + 2]);

					// logger.debug("Adding new rectangle " + rect);
					rectangles.add(rect);
				}
				// Clear previous
				Arrays.fill(intervalsPrevious, 0);
				newIntervalInList = 0;
			}
			maxInterval = newIntervalInList;
			buffer = intervalsPrevious;
			intervalsPrevious = intervalsNew;
			Arrays.fill(buffer, 0);
			intervalsNew = buffer;

		}
		// Add the list of new rectangles from last line
		// logger.debug("Last line, maxInterval " + maxInterval +
		// ", intervals = "
		// + Arrays.toString(intervalsPrevious));
		previousIntervalInList = 0;
		for (int i = 0; i < maxInterval; i++) {
			SortableRectangle rect = new SortableRectangle(
					intervalsPrevious[i * 3], binaryMatrix.getRow()
							- intervalsPrevious[i * 3 + 2],
					intervalsPrevious[i * 3 + 1] - intervalsPrevious[i * 3],
					intervalsPrevious[i * 3 + 2]);

			// logger.debug("Adding new rectangle " + rect);
			rectangles.add(rect);
		}
		return new DecompositionResult(System.nanoTime() - t1, rectangles, -1);
	}

	private void checkInterval(int i, int j, boolean eol) {
		if (inInterval) {
			if (extendCurrent) {
				if (i + (eol ? 1 : 0) != xmax) {
					// logger.debug("Current rectangle is larger than the previous one, xmax ="
					// + xmax + " here :" + (i + (eol ? 1 : 0)));
					// Add the previous rectangle to the list of
					// rectangle
					SortableRectangle rect = new SortableRectangle(
							intervalsPrevious[previousIntervalInList * 3],
							j
									- intervalsPrevious[previousIntervalInList * 3 + 2],
							intervalsPrevious[previousIntervalInList * 3 + 1]
									- intervalsPrevious[previousIntervalInList * 3],
							intervalsPrevious[previousIntervalInList * 3 + 2]);
					if (rect.getArea() != 0) {
						// logger.debug("Adding rectangle " + rect);
						rectangles.add(rect);
					}
					// Create new interval
					intervalsNew[newIntervalInList * 3] = startX;
					intervalsNew[newIntervalInList * 3 + 1] = i + (eol ? 1 : 0);
					intervalsNew[newIntervalInList * 3 + 2] = 1;
					moveNextInterval();
				} else {
					// Add the previous internal to the new one,
					// with increased height
					// logger.debug("Adding 1 to previous intervals, before "
					// + Arrays.toString(intervalsNew));
					intervalsNew[newIntervalInList * 3] = intervalsPrevious[previousIntervalInList * 3];
					intervalsNew[newIntervalInList * 3 + 1] = intervalsPrevious[previousIntervalInList * 3 + 1];
					intervalsNew[newIntervalInList * 3 + 2] = intervalsPrevious[previousIntervalInList * 3 + 2] + 1;
					moveNextInterval();

				}
			} else {
				// logger.debug("Adding new interval, before "
				// + Arrays.toString(intervalsNew));
				// Add new interval to the party
				intervalsNew[newIntervalInList * 3] = startX;
				intervalsNew[newIntervalInList * 3 + 1] = i + (eol ? 1 : 0);
				intervalsNew[newIntervalInList * 3 + 2] = 1;
				// Move
				moveNextInterval();
				// logger.debug("Adding new interval, after "
				// + Arrays.toString(intervalsNew));
			}
		}
		extendCurrent = false;
		inInterval = false;

	}

	private void moveNextInterval() {
		newIntervalInList++;
		previousIntervalInList++;
		if (previousIntervalInList < (intervalsPrevious.length - 3) / 3) {
			xmin = intervalsPrevious[previousIntervalInList * 3];
			xmax = intervalsPrevious[previousIntervalInList * 3 + 1];
		} else {
			xmin = -1;
			xmax = -1;
		}
	}

}
