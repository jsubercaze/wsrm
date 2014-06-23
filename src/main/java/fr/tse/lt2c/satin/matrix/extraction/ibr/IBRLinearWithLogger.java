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
 * Linear time implementation of IBR
 * 
 * @author Julien Subercaze
 * 
 */
public class IBRLinearWithLogger implements RectangleExtractor {

	Logger logger = Logger.getLogger("IBRLinear");
	public final static Level DEBUG_LEVEL = Level.OFF;

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

	public IBRLinearWithLogger(BinaryMatrix matrix) {
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
		if (logger.isDebugEnabled()) {
			logger.debug("First Line");
			logger.debug("Intervals " + Arrays.toString(intervals1));
		}
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
		for (int j = 1; j < binaryMatrix.getRow(); j++) {
			if (logger.isDebugEnabled()) {
				logger.debug("Line " + j);

			}
			previousIntervalInList = 0;
			newIntervalInList = 0;
			xmin = intervalsPrevious[0];
			xmax = intervalsPrevious[1];
			for (int i = 0; i < binaryMatrix.getCol(); i++) {
				if (logger.isDebugEnabled()) {
					logger.debug("(" + j + "," + i + ")");
				}

				if (mat[j][i]) {
					if (i != binaryMatrix.getCol() - 1) {
						if (!inInterval) {
							inInterval = true;
							startX = i;
							// Start a new interval, checks if matches with
							// previous
							if (i == xmin) {
								extendCurrent = true;
							} else if (xmin < i
									&& previousIntervalInList < maxInterval - 1) {
								// Move to the index to match current X
								while (xmin < i
										&& previousIntervalInList < maxInterval) {
									previousIntervalInList++;
									xmin = intervalsPrevious[previousIntervalInList * 3];
									//Add the previous as rectangle
									logger.debug("there");
									
								}
								// Once at the end
								xmax = intervalsPrevious[previousIntervalInList * 3 + 1];
								// Check again
								if (i == xmin) {
									extendCurrent = true;
								}
							}else{
								//Add the previous rectangle if any
								if(maxInterval!=0){
									rectangles
									.add(new SortableRectangle(
											intervalsPrevious[previousIntervalInList * 3],
											binaryMatrix.getRow()
													- intervalsPrevious[previousIntervalInList * 3 + 2],
											intervalsPrevious[previousIntervalInList * 3 + 1]
													- intervalsPrevious[previousIntervalInList * 3],
											intervalsPrevious[previousIntervalInList * 3 + 2]));
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
						logger.debug("Check EOL interval");
						checkInterval(i, j,true);
					}

				} else {
					// Did it grew larger than the max of previous interval
					// ?
					checkInterval(i, j,false);

				}

			}// Switch

			maxInterval = newIntervalInList;
			buffer = intervalsPrevious;
			intervalsPrevious = intervalsNew;
			Arrays.fill(buffer, 0);
			intervalsNew = buffer;
			if (logger.isDebugEnabled()) {
				logger.debug("Rectangles " + rectangles);
				logger.debug("Intervals " + Arrays.toString(intervalsPrevious));
			}

		}
		// Add the list of new rectangles
		for (int i = 0; i < maxInterval; i++) {
			rectangles
					.add(new SortableRectangle(
							intervalsPrevious[previousIntervalInList * 3],
							binaryMatrix.getRow()
									- intervalsPrevious[previousIntervalInList * 3 + 2],
							intervalsPrevious[previousIntervalInList * 3 + 1]
									- intervalsPrevious[previousIntervalInList * 3],
							intervalsPrevious[previousIntervalInList * 3 + 2]));
		}
		return new DecompositionResult(System.nanoTime() - t1,
				rectangles, -1);
	}

	private void checkInterval(int i, int j,boolean eol) {
		if (inInterval) {
			if (extendCurrent) {
				if (i+(eol?1:0) != xmax) {
					// Add the previous rectangle to the list of
					// rectangle
					SortableRectangle rect = new SortableRectangle(
							intervalsPrevious[previousIntervalInList * 3],
							j
									- intervalsPrevious[previousIntervalInList * 3 + 2],
							intervalsPrevious[previousIntervalInList * 3 + 1]
									- intervalsPrevious[previousIntervalInList * 3] +(eol?1:0),
							intervalsPrevious[previousIntervalInList * 3 + 2]);
					if (rect.getArea() != 0)
						rectangles.add(rect);
					// Create new interval
					intervalsNew[newIntervalInList * 3] = startX;
					intervalsNew[newIntervalInList * 3 + 1] = i;
					intervalsNew[newIntervalInList * 3 + 2] = 1;
					// Move
					newIntervalInList++;
					previousIntervalInList++;
					if (logger.isDebugEnabled()) {
						logger.debug("i!=xmax");
						logger.debug("rectangles " + rectangles);
						logger.debug("intervals new "
								+ Arrays.toString(intervalsNew));
					}

				} else {
					// Add the previous internal to the new one,
					// with increased height
					intervalsNew[newIntervalInList * 3] = intervalsPrevious[previousIntervalInList * 3];
					intervalsNew[newIntervalInList * 3 + 1] = intervalsPrevious[previousIntervalInList * 3 + 1];
					intervalsNew[newIntervalInList * 3 + 2] = intervalsPrevious[previousIntervalInList * 3 + 2] + 1;
					// Move indexs
					newIntervalInList++;
					previousIntervalInList++;
					if (logger.isDebugEnabled()) {
						logger.debug("Extended previous rectangle");
						logger.debug("intervals new "
								+ Arrays.toString(intervalsNew));

					}
				}
			} else {
				// Add new interval to the party
				intervalsNew[newIntervalInList * 3] = startX;
				intervalsNew[newIntervalInList * 3 + 1] = i+(eol?1:0);
				intervalsNew[newIntervalInList * 3 + 2] = 1;
				// Move
				newIntervalInList++;
				previousIntervalInList++;
				if (logger.isDebugEnabled()) {
					logger.debug("Added new interval");
					logger.debug("intervals new "
							+ Arrays.toString(intervalsNew));

				}

			}
			extendCurrent = false;
			inInterval = false;
		}

	}

}
