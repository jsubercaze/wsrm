package fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.LinkedMatrix;
import fr.tse.lt2c.satin.matrix.beans.LinkedMatrixElement;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;
import fr.tse.lt2c.satin.matrix.beans.TemporaryRectangle;
import fr.tse.lt2c.satin.matrix.movie.CreateMovieFromFiles;
import fr.tse.lt2c.satin.matrix.utils.RectangleDeque;

/**
 * Find the largest rectangle. Complexity : time O(n), space O(n).
 * 
 * Add the optimisation if the removed rectangle in destacking is a point, leave
 * it to its parent
 * 
 * @author Julien Subercaze
 * 
 *         22/02/13
 */
public class WSMRDecomposer {
	Logger logger = Logger.getLogger("LinkedMatrixExtraction");
	public final static Level DEBUG_LEVEL = Level.DEBUG;
	/**
	 * Minimum area for a rectangle to be kept in the final list
	 */
	private int MIN_AREA = 1;

	/**
	 * The linked matrix
	 */
	BinaryMatrix binaryMatrix;
	/**
	 * H1 in the paper (until fourth step, replaced by max)
	 */
	int[][] temp1;
	/**
	 * W1 in the paper (until fourth step, replaced by max)
	 */
	int[][] temp2;
	/**
	 * H2 in the paper
	 */
	int[][] temp3;
	/**
	 * W2 in the paper
	 */
	int[][] temp4;
	// Matrix matrix;
	/**
	 * Wether the matrix has been preprocessed
	 */
	boolean preprocessed;
	/**
	 * Current point of the walker in the linked matrix
	 */
	private LinkedMatrixElement currentPoint;
	/**
	 * Generate images to produce a movie of the decomposition (need to be
	 * retested due to large modifications)
	 */
	private boolean generateImages;
	/**
	 * Generate directly the movie
	 */
	private boolean movie;
	/**
	 * Internal structure for generateing movie
	 */
	private ArrayList<BufferedImage> listImages;
	/**
	 * Largest rectangle after {@link #fourthStep()}
	 */
	private SortableRectangle largestOne;
	/**
	 * Heuristic : remove the largest rectangle prior to any other
	 */
	private boolean removeLargestFirst = false;
	/**
	 * Various internal counters
	 */
	/**
	 * Current area
	 */
	private int area = -1;
	/**
	 * How many 1 to check on the columns
	 */
	private int xToCheck = -1;
	/**
	 * How many 1 to check on the rows
	 */
	private int yToCheck = -1;
	/**
	 * Number of 1 in this column
	 */
	private int xTotal = -1;
	/**
	 * Number of 1 in this row
	 */
	private int yTotal = -1;
	// private LinkedMatrixElement currentBeginningLine;
	/**
	 * Current step
	 */
	private int step = 0;
	/**
	 * The linked matrix
	 */
	private LinkedMatrix linkedMatrix;
	/**
	 * Storage for temporary area
	 */
	private int areaTmp;
	/**
	 * Stack for rectangles
	 */
	private RectangleDeque stack;
	
	/**
	 * List of extracted rectangles
	 */
	private List<SortableRectangle> rectangles;
	/**
	 * Storage for previous point
	 */
	private LinkedMatrixElement lastpoint;

	/**
	 * Create a decomposer for the given matrix
	 * 
	 * @param m
	 */
	public WSMRDecomposer(BinaryMatrix m) {
		super();
		this.binaryMatrix = m;
		temp1 = new int[m.getRow()][m.getCol()];
		temp2 = new int[m.getRow()][m.getCol()];
	}

	/**
	 * Create a decomposer for the given matrix
	 * 
	 * @param m
	 *            the matrix
	 * @param images
	 *            create images at each step
	 * @param movie
	 *            create a movie from the decomposition (experimental)
	 */
	public WSMRDecomposer(BinaryMatrix m, boolean images, boolean movie) {
		super();
		this.binaryMatrix = m;
		temp1 = new int[m.getRow()][m.getCol()];
		temp2 = new int[m.getRow()][m.getCol()];
		this.generateImages = images;
		this.movie = movie;
		if (generateImages || movie)
			cleanfolder();
		if (movie)
			listImages = new ArrayList<>();
	}

	/**
	 * Create a decomposer for the given matrix
	 * 
	 * @param m
	 *            the matrix
	 * @param images
	 *            create images at each step
	 * @param movie
	 *            create a movie from the decomposition (experimental)
	 * @param remove
	 *            the largest (approximated) rectangle first then process the
	 *            heuristic
	 */
	public WSMRDecomposer(BinaryMatrix m, boolean images, boolean movie,
			boolean removelargest) {
		super();
		this.binaryMatrix = m;
		temp1 = new int[m.getRow()][m.getCol()];
		temp2 = new int[m.getRow()][m.getCol()];
		this.generateImages = images;
		this.movie = movie;
		if (generateImages || movie)
			cleanfolder();
		if (movie)
			listImages = new ArrayList<>();
		this.removeLargestFirst = removelargest;
	}

	/**
	 * 
	 * @return the largest rectangle in this matrix (approximated, see paper)
	 */
	public SortableRectangle findLargestRectangle() {
		firstStep();
		secondStep();
		thirdStep();
		fourthStep();
		if (logger.isDebugEnabled()) {
			logger.debug(matrixToString(temp1));
			logger.debug(matrixToString(temp2));
			logger.debug(matrixToString(temp3));
			logger.debug(matrixToString(temp4));
		}
		return firstLargestRectangle();
	}

	/**
	 * Compute for each column, i.e compute H1 matrix
	 */
	private void firstStep() {
		int current;
		for (int j = 0; j < binaryMatrix.getCol(); j++) {
			current = 1;
			for (int i = binaryMatrix.getRow() - 1; i >= 0; i--) {
				if (binaryMatrix.getMatrix()[i][j]) {
					temp1[i][j] = current;
					current++;
				} else {
					current = 1;
				}

			}
		}
	}

	/**
	 * Compute for each row, W1 matrix
	 */
	private void secondStep() {
		int current;
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			current = 1;
			for (int j = binaryMatrix.getCol() - 1; j >= 0; j--) {
				if (binaryMatrix.getMatrix()[i][j]) {
					temp2[i][j] = current;
					current++;
				} else {
					current = 1;
				}

			}
		}
	}

	/**
	 * The trick ! Compute both H2 and W2
	 */
	private void thirdStep() {
		temp3 = new int[temp1.length][];
		for (int i = 0; i < temp1.length; i++)
			temp3[i] = temp1[i].clone();
		temp4 = new int[temp2.length][];
		for (int i = 0; i < temp2.length; i++)
			temp4[i] = temp2[i].clone();
		int previous;
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			previous = 0;
			for (int j = binaryMatrix.getCol() - 1; j >= 0; j--) {
				if (previous == 0) {
					// Leave unchanged
					previous = temp3[i][j];
				} else if (temp3[i][j] <= previous) {
					previous = temp3[i][j];
				} else {
					temp3[i][j] = previous;
				}

			}
		}
		// Et en colomnes
		for (int j = 0; j < binaryMatrix.getCol(); j++) {
			previous = 0;
			for (int i = binaryMatrix.getRow() - 1; i >= 0; i--) {
				if (previous == 0)
					previous = temp2[i][j];
				else if (temp4[i][j] <= previous) {
					previous = temp2[i][j];
				} else {
					temp4[i][j] = previous;
				}

			}
		}
	}

	/**
	 * Replace temp1 and temp2 values with width and height for the largest
	 * rectangle starting (top left) at the current point updated with removal
	 * of zero in the matrix
	 */
	private void fourthStep() {
		int[] area_tmp = new int[2];
		int largestArea = -1, x = 0, y = 0, width = 0, height = 0;
		linkedMatrix = new LinkedMatrix(binaryMatrix);
		LinkedMatrixElement current = linkedMatrix.getCurrentRoot();
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			for (int j = 0; j < binaryMatrix.getCol(); j++) {
				current = current.getNextRight();
				if (binaryMatrix.getMatrix()[i][j]) {
					area_tmp[0] = temp3[i][j] * temp2[i][j];
					area_tmp[1] = temp1[i][j] * temp4[i][j];
					int max = Math.max(area_tmp[0], area_tmp[1]);
					// logger.debug(i+","+j+" | "+area_tmp[0]+" "+area_tmp[1]+" "+max);
					if (max == area_tmp[0]) {
						temp2[i][j] = temp2[i][j];
						temp1[i][j] = temp3[i][j];
					} else {
						temp2[i][j] = temp4[i][j];
						temp1[i][j] = temp1[i][j];
					}
					// Update largest rectangle
					if (max > largestArea) {
						largestArea = max;
						linkedMatrix.setLargestRectangle(current);
						x = i;
						y = j;
						if (max == area_tmp[0]) {
							width = temp2[i][j];
							height = temp3[i][j];
						} else {
							width = temp4[i][j];
							height = temp1[i][j];
						}
					}
				} else {
					current = linkedMatrix.deleteZero(current);
				}
			}
		}
		if (largestArea != -1)
			largestOne = new SortableRectangle(x, y, width, height);
	}

	/**
	 * 
	 * @return the largest rectangle
	 */
	private SortableRectangle firstLargestRectangle() {
		int area = 0;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		int[] area_tmp = new int[2];
		for (int i = 0; i < binaryMatrix.getRow(); i++) {
			for (int j = 0; j < binaryMatrix.getCol(); j++) {
				if (binaryMatrix.getMatrix()[i][j]) {
					area_tmp[0] = temp3[i][j] * temp2[i][j];
					area_tmp[1] = temp1[i][j] * temp4[i][j];
					int max = Math.max(area_tmp[0], area_tmp[1]);
					// logger.debug(i+","+j+" | "+area_tmp[0]+" "+area_tmp[1]+" "+max);
					if (max > area) {
						x = i;
						y = j;
						if (max == area_tmp[0]) {
							width = temp2[i][j];
							height = temp3[i][j];
						} else {
							width = temp4[i][j];
							height = temp1[i][j];
						}
						area = max;
					}
				}
			}
		}
		return new SortableRectangle(x, y, width, height);
	}

	/**
	 * Compute the DPs for the largest rectangle approximation
	 */
	public void preProcess() {
		firstStep();
		if (logger.isDebugEnabled())
			logger.debug(matrixToString(temp1));
		secondStep();
		if (logger.isDebugEnabled())
			logger.debug(matrixToString(temp2));
		thirdStep();
		if (logger.isDebugEnabled()) {
			logger.debug(matrixToString(temp3));
			logger.debug(matrixToString(temp4));
		}
		fourthStep();
		preprocessed = true;
		if (logger.isDebugEnabled()) {
			logger.debug("--------------------");
			logger.debug("----LARGEST------" + largestOne);
		}
	}

	/**
	 * Returns the list of maximum disjoint rectangles Sort criterium is the
	 * area
	 * 
	 * @return
	 */
	public DecompositionResult computeDecomposition(int min) {
		MIN_AREA = min;
		if (!preprocessed)
			preProcess();
		// System.out.println(linkedMatrix);
		long t1 = System.nanoTime();
		if (logger.isDebugEnabled()) {
			logger.debug("-------------------------------------");
			logger.debug(matrixToString(temp1));
			logger.debug(matrixToString(temp2));
			// logger.debug(matrixToString(temp3));
			// logger.debug(matrixToString(temp4));
		}
		rectangles = new ArrayList<>();
		stack = new RectangleDeque(linkedMatrix.getHeight(),
				linkedMatrix.getWidth());
		if (removeLargestFirst) {
			LinkedMatrixElement largestUpperLeft = linkedMatrix
					.getLargestRectangle();
			addRectangle(largestUpperLeft, largestOne.height, largestOne.width);

		}
		// matrix = new FastListMatrix(this.binaryMatrix.getRow(),
		// this.binaryMatrix.getCol());
		// currentBeginningLine = null;

		currentPoint = linkedMatrix.getFirstElement();

		step = 0;

		do {
			step++;
			if (currentPoint == null || currentPoint.isNull()) {
				// Move to the first element of the matrix
				currentPoint = linkedMatrix.getFirstElement();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("-----------------------------------------------------");
				logger.debug("-----------------------------------------------------");
				logger.debug("Entering Stage, current point " + currentPoint);
				logger.debug("Stack size " + stack.size());
				logger.debug("Stack  " + stack);
				logger.debug("XtoCheck " + xToCheck);
				logger.debug("XTotal " + xTotal);
				logger.debug("YtoCheck " + yToCheck);
				logger.debug("YTotal " + yTotal);
				logger.debug(rectangles);
				logger.debug("Current AREA " + getArea(currentPoint));
			}
			if (this.generateImages || movie) {
				generateImage();
			}
			process();
			if (logger.isDebugEnabled()) {
				logger.debug(linkedMatrix);
				logger.debug("YtoCheck " + yToCheck);
				logger.debug("Area " + area);
				logger.debug("XtoCheck " + xToCheck);
				logger.debug("CurrentPoint " + currentPoint);
				logger.debug("The stack" + stack);
				logger.debug(rectangles.size());
				logger.debug(rectangles);
			}

		} while (!linkedMatrix.isEmpty());
		if (generateImages) {
			CreateMovieFromFiles.createMovieFromImages(listImages);
		}
		int ones = binaryMatrix.getNumberOfOnes();
		temp1 = null;
		temp2 = null;
		temp3 = null;
		temp4 = null;
		binaryMatrix = null;
		return new DecompositionResult(System.nanoTime() - t1, rectangles, ones);
	}

	/**
	 * Process a step in the matrix decomposition. Walk, stack, merge or remove.
	 * 
	 */
	private void process() {

		// if (lastpoint == currentPoint)
		// System.exit(-1);
		step++;
		if (logger.isDebugEnabled())
			logger.debug("step " + step + ": " + currentPoint);
		if (stack.size() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("Stack empty");
			// Add new rectangle
			if (stackNewRectangle()) {
				setCounters();
				updateCounters();
				goNextElementEmptyStack();
			} else {
				lastpoint = currentPoint;
				currentPoint = linkedMatrix.getFirstElement();
			}
		} else {
			areaTmp = getArea(currentPoint);
			if (areaTmp > area) {
				if (logger.isDebugEnabled())
					logger.debug("Larger area " + areaTmp);
				// Take care of previous rectangle
				checkUpdateOldRectangle();
				// Start new rectangle
				if (stackNewRectangle()) {
					setCounters();
					updateCounters();
					// Try to merge
					tryMerge();
					goNextElementStack();
				} else
					currentPoint = linkedMatrix.getFirstElement();

			} else {
				if (logger.isDebugEnabled())
					logger.debug("Go On");
				// Update counters
				updateCounters();
				if (!foundAndMerge())
					goNextElementStack();
			}

		}

	}

	/**
	 * First setting of counter, when new rectangle is stacked
	 */
	private void setCounters() {
		xToCheck = xTotal;
		yToCheck = yTotal;
		if (logger.isDebugEnabled()) {
			logger.debug("After set counters");
			logger.debug("XtoCheck " + xToCheck);
			logger.debug("XTotal " + xTotal);
			logger.debug("YtoCheck " + yToCheck);
			logger.debug("YTotal " + yTotal);
		}

	}

	/**
	 * Check wether a rectangle has been found, if it could merge with previous
	 * one and removes it
	 */
	private boolean foundAndMerge() {
		if (stack.size() == 0)
			return true;// can happen at the end
		if (logger.isDebugEnabled()) {
			logger.debug("Found and merge");
			logger.debug("XtoCheck " + xToCheck);
			logger.debug("XTotal " + xTotal);
			logger.debug("YtoCheck " + yToCheck);
			logger.debug("YTotal " + yTotal);
		}
		if (!(xToCheck == 0))
			return false;
		if (stack.size() == 1) {
			if (logger.isDebugEnabled())
				logger.debug("Remove lonely rectangle");
			if (stack.peek().isValidated()) {
				removeCurrentRectangle();

				return true;
			} else
				return false;
		}

		// Rectangle found
		if (!tryMerge()) {
			if (logger.isDebugEnabled())
				logger.debug("Rectangle found, emptystack");
			stack.peek().setValidated(true);
			stack.peek().setPotentialSecondArea(0);
			emptyStack();

		}
		return true;

	}

	/**
	 * 
	 * @return <code>true</code> if merging occurred, whether horizontal or
	 *         vertical
	 */
	private boolean tryMerge() {
		// if (DEBUG && stack.size()>0)
		// logger.debug("TryMerge " + stack);

		if (logger.isDebugEnabled())
			logger.debug("Horizontal");
		if (!mergeHorizontal()) {
			if (logger.isDebugEnabled())
				logger.debug("didn't work");
			if (mergeVertical()) {
				if (logger.isDebugEnabled())
					logger.debug("worked vertical");
				if (stack.peek().isValidated()) {
					return false;
				}
				return true;
			}
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Worked horizontal " + stack);
			return true;
		}
		if (logger.isDebugEnabled())
			logger.debug("Merge return false");
		return false;

	}

	/**
	 * Remove the current rectangle from the matrix
	 * 
	 * @return the upper left corner of the removed rectangle
	 */
	private LinkedMatrixElement removeCurrentRectangle() {

		TemporaryRectangle temp = stack.poll();
		if (temp == null)
			return null;
		if (!temp.isValidated()) {
			potentialVSvalidated(-1, -1);
		}
		addRectangle(temp.getUpperLeftCorner(), temp.getHeight(),
				temp.getWidth());
		// if (stack.size() > 0)
		// stack.peek().setRestartingPoint(temp.getUpperLeftCorner());
		if (stack.size() > 0) {
			if (!stack.peek().isValidated()) {
				area = stack.peek().getArea();
			} else
				area = 0;
		}
		return temp.getUpperLeftCorner();
	}

	/**
	 * Update the internal counters
	 */
	private void updateCounters() {
		stack.peek().setCurrentPosition(currentPoint);
		if (yToCheck == yTotal)
			stack.peek().setLastLineFirstElement(currentPoint);
		if (yToCheck > 0) {
			if (logger.isDebugEnabled())
				logger.debug("Entering here");
			--yToCheck;
		}
		if (yToCheck == 0) {
			xToCheck--;
			if (xToCheck != 0)
				yToCheck = yTotal;
			else {
				if (stack.size() > 0)
					stack.peek().setValidated(true);
			}

		}
		if (logger.isDebugEnabled()) {
			logger.debug("After update counters");
			logger.debug("XtoCheck " + xToCheck);
			logger.debug("XTotal " + xTotal);
			logger.debug("YtoCheck " + yToCheck);
			logger.debug("YTotal " + yTotal);
		}
	}

	/**
	 * After validation, check if rectangle finished, line finished. Update the
	 * values of the counters accordingly
	 */
	private void checkUpdateOldRectangle() {
		// If current point is in the first line of the old rectangle, update
		// its width (case 1)
		if (stack.peek().isValidated())
			return;
		if (stack.peek().getHeight() == 1) {
			if (logger.isDebugEnabled())
				logger.debug("Set potential area to 0-0");
			stack.peek().setWidth(
					Math.max(currentPoint.getY()
							- stack.peek().getUpperLeftCorner().getY(), 1));

			stack.peek().setValidated(true);
		} else if (xToCheck == xTotal) {// First line
			// Update width of the rectangle
			if (logger.isDebugEnabled())
				logger.debug("Updating width");
			stack.peek().setWidth(
					currentPoint.getY()
							- stack.peek().getUpperLeftCorner().getY());
			int potentialArea = (currentPoint.getY() - stack.peek()
					.getLastLineFirstElement().getY())
					* (xToCheck - 1);
			if (logger.isDebugEnabled())
				logger.debug("Set potential area to " + potentialArea);

		} else if (stack.peek().getLastLineFirstElement()
				.isNextElementDownConsecutive()
				&& stack.peek().getLastLineFirstElement().getNextDown() == currentPoint) {
			// Case 2 in the paper

			// Update the height
			stack.peek().setHeight(xTotal - xToCheck);
			if (logger.isDebugEnabled()) {
				logger.debug("Set potential area to 0-1 height"
						+ (xTotal - xToCheck));
				logger.debug(stack);
			}
			stack.peek().setValidated(true);
			// nothing under
		} else {
			// Case 3 in the paper - compute potential size and cut if necessary
			int height = xToCheck - 1;
			height = height == 0 ? 1 : height;
			// int potentialArea = (currentPoint.getY() -
			// stack.peek().getLastLineFirstElement().getY()) * (height);

			// It will be checked after destacking if it is possible to continue
			// down on a new rectangle, with a new height and a reduced width
		}
		// Merge the rectangles.
		tryMerge();
		// stack.peek().setRestartingPoint(currentPoint);

	}

	/**
	 * Empty the stack, called whenever a rectangle is finished. Try to remove
	 * other completed rectangles from the stack. If not, set the values for
	 * next move
	 */
	private void emptyStack() {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing current rectangle " + stack);
		}
		removeCurrentRectangle();
		while (stack.size() > 0 && stack.peek().isValidated()) {
			if (logger.isDebugEnabled())
				logger.debug("Removing rectangle with no child " + stack);
			removeCurrentRectangle();
		}
		if (stack.size() > 0) {
			if (logger.isDebugEnabled())
				logger.debug("Removing last cutted rectangle " + stack);
			int potentialAreaVertical = (currentPoint.getY()
					- stack.peek().getLastLineFirstElement().getY() + 1)
					* (stack.peek().getHeight());
			// Validated area
			int validatedArea = stack.peek().getWidth()
					* (currentPoint.getX() - stack.peek().getUpperLeftCorner()
							.getX());
			// Choose the best cut
			potentialVSvalidated(validatedArea, potentialAreaVertical);
		}

	}

	/**
	 * If the stack is empty, move to the next element of the linkedmatrix
	 */
	private void goNextElementEmptyStack() {
		if (yToCheck == yTotal) {
			if (logger.isDebugEnabled()) {
				logger.debug("yToCheck==0");
				logger.debug("Try to go at (" + (currentPoint.getX() + 1) + ","
						+ currentPoint.getY() + ")");
			}
			if (!currentPoint.isNextElementDownConsecutive()) {
				if (logger.isDebugEnabled())
					logger.debug("Do not match remove current point "
							+ currentPoint);
				stack.peek().setHeight(1);
				stack.peek().setWidth(1);
				stack.peek().setValidated(true);
				stack.peek().setPotentialSecondArea(0);
				// stack.peek().setUpperLeftCorner(oldCurrentPoint);
				if (!currentPoint.isNextElementRightConsecutive()) {
					// Remove
					removeCurrentRectangle();
					currentPoint = linkedMatrix.getFirstElement();

				} else {
					area = 0;
					mergeOrRemove(false);
				}

			} else {
				// if (currentPoint != null)
				lastpoint = currentPoint;
				currentPoint = currentPoint.getNextDown();
			}
		} else {
			tryNextRight();
		}
		if (logger.isDebugEnabled())
			logger.debug("Point courant " + currentPoint);
	}

	/**
	 * Try to reach the point on the east
	 */
	private void tryNextRight() {
		if (currentPoint.isNextElementRightConsecutive()) {
			lastpoint = currentPoint;
			currentPoint = currentPoint.getNextRight();
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Next right not good");

			// Compute the potential second area
			if (stack.size() > 0) {
				if (logger.isDebugEnabled())
					logger.debug("remove or merge");

				mergeOrRemove(true);
			} else {
				lastpoint = currentPoint;
				currentPoint = linkedMatrix.getFirstElement();
			}
		}
	}

	/**
	 * 
	 * @param isRight
	 *            last move was on the eastern direction
	 */
	private void mergeOrRemove(boolean isRight) {
		if (isRight) { // case where we wanted to go right
			if (stack.size() == 1 && xToCheck == xTotal) {// First line of empty
				if (logger.isDebugEnabled())
					logger.debug("Thefirst line of empty stack");// stack
				TemporaryRectangle tempRectangle = stack.peek();
				stack.peek()
						.setWidth(
								tempRectangle.getCurrentPosition().getY()
										- tempRectangle.getUpperLeftCorner()
												.getY() + 1);
				stack.peek().setHeight(1);
				stack.peek().setValidated(true);
				yTotal = stack.peek().getWidth();
				yToCheck = yTotal;
				xToCheck--;// EOL
				area = stack.peek().getArea();
				stack.peek().setPotentialSecondArea(0);
				if (tempRectangle.getLastLineFirstElement()
						.isNextElementDownConsecutive()) {
					if (logger.isDebugEnabled())
						logger.debug("Go down");// stack
					stack.peek().setHeight(2);
					xTotal++;
					xToCheck++;
					area = stack.peek().getArea();
					stack.peek().setValidated(false);
					stack.peek().setPotentialSecondArea(-1);
					currentPoint = tempRectangle.getLastLineFirstElement()
							.getNextDown();
				} else {
					if (logger.isDebugEnabled())
						logger.debug("Remove");// stack
					removeCurrentRectangle();
					currentPoint = linkedMatrix.getFirstElement();

				}
			} else if (stack.size() > 1) {
				if (logger.isDebugEnabled())
					logger.debug("Merge or remove stack >1");
				// try merge
				if (xToCheck == xTotal) {// on the first line
					if (logger.isDebugEnabled())
						logger.debug("On the line");
					// update width
					TemporaryRectangle tempRectangle = stack.peek();
					stack.peek().setWidth(
							tempRectangle.getCurrentPosition().getY()
									- tempRectangle.getUpperLeftCorner().getY()
									+ 1);
					// stack.peek().setHeight(1);

					int oldsize = stack.size();
					// Update counters
					yTotal = stack.peek().getWidth();
					yToCheck = yTotal;
					xToCheck--;// EOL
					if (xToCheck == 0) {
						stack.peek().setValidated(true);
						stack.peek().setPotentialSecondArea(0);
					}
					area = stack.peek().getArea();
					if (!tryMerge()) {
						// Merge didn't work go next element
						if (stack.size() > 0) {
							if (oldsize != stack.size())
								potentialVSvalidated(-1, -1);
							else
								goNextElementStack();
						} else
							currentPoint = linkedMatrix.getFirstElement();
					} else {
						// if (oldsize != stack.size())
						// potentialVSvalidated(-1, -1);
						// else
						goNextElementStack();
					}

				} else {
					potentialVSvalidated(-1, -1);
				}
			} else {
				if (logger.isDebugEnabled())
					logger.debug("Cut rectangle");
				potentialVSvalidated(-1, -1);

			}
		} else {// case where we wanted to go down - stack point
			if (logger.isDebugEnabled())
				logger.debug("Stack point");
			stack.peek().setHeight(1);
			tryMerge();
			lastpoint = currentPoint;
			currentPoint = currentPoint.getNextRight();
			if (currentPoint == null)
				potentialVSvalidated(-1, -1);
		}
	}

	/**
	 * Merge or remove a depoped rectangle
	 * 
	 * @param isRight
	 *            last move was in the eastern direction
	 */
	private void mergeOrRemoveDepop(boolean isRight) {
		if (isRight) { // case where we wanted to go right
			// try merge
			if (xToCheck == xTotal) {// on the first line
				// update width
				TemporaryRectangle tempRectangle = stack.peek();
				stack.peek().setWidth(
						tempRectangle.getCurrentPosition().getY()
								- tempRectangle.getUpperLeftCorner().getY());
				stack.peek().setHeight(1);
				stack.peek().setValidated(true);
				stack.peek().setPotentialSecondArea(0);
			}
			if (stack.size() > 1)
				tryMerge();

		} else {// case where we wanted to go down - stack point
			tryMerge();
			lastpoint = currentPoint;
			currentPoint = currentPoint.getNextRight();
			if (currentPoint == null)
				potentialVSvalidated(-1, -1);
		}
	}

	/**
	 * Determine the best cut between the validated part and the potential
	 * rectangle. Heuristic favors the surebet.
	 * 
	 * @param validatedArea
	 * @param potentialAreaVertical
	 */
	private void potentialVSvalidated(int validatedArea,
			int potentialAreaVertical) {
		if (stack.size() == 0) {
			currentPoint = linkedMatrix.getFirstElement();
			return;
		}
		// Remove the validated part
		TemporaryRectangle tempRectangle = stack.peek();
		if (logger.isDebugEnabled()) {
			logger.debug("Restart or delete and go down " + tempRectangle);
		}
		if (tempRectangle.isValidated()
				&& tempRectangle.getPotentialSecondArea() == 0) {
			removeCurrentRectangle();
			if (stack.size() > 0)
				potentialVSvalidated(-1, -1);
			return;
		}
		// Case where cutpoint is current point
		if (tempRectangle.getCurrentPosition() == tempRectangle
				.getUpperLeftCorner()) {

			if (tempRectangle.getHeight() > 1 && tempRectangle.getWidth() == 1) {
				if (tempRectangle.getCurrentPosition()
						.isNextElementDownConsecutive()) {
					updateCounterToCurrentRectangle();
					xToCheck--;
					tryNextDown();
				} else {
					tempRectangle.setHeight(1);
					if (!tryMerge())
						removeCurrentRectangle();

				}

			} else {
				updateCounterToCurrentRectangle();
				goNextElementStack();

			}

		} else {
			LinkedMatrixElement cutPoint = tempRectangle.getCurrentPosition();
			int height = cutPoint.getX()
					- tempRectangle.getUpperLeftCorner().getX();
			if (logger.isDebugEnabled())
				logger.debug("Cutpoint " + cutPoint);
			// Case where first element is point
			if (height == 0 && (xToCheck == xTotal) && stack.size() == 1) {
				if (logger.isDebugEnabled())
					logger.debug("First point");
				tempRectangle.setWidth(1);
				tempRectangle.setHeight(1);
				if (!tryMerge())
					removeCurrentRectangle();
				else {
					logger.debug("MERGED !!");
				}
			} else if (height != 0) {
				if (logger.isDebugEnabled())
					logger.debug("Not a line " + height);
				// Delete the rectangle
				stack.poll();
				tempRectangle.setHeight(height);
				addRectangle(tempRectangle.getUpperLeftCorner(), height,
						tempRectangle.getWidth());
				temp2[tempRectangle.getLastLineFirstElement().getX()][tempRectangle
						.getLastLineFirstElement().getY()] = tempRectangle
						.getCurrentPosition().getY()
						- tempRectangle.getUpperLeftCorner().getY() + 1;
				lastpoint = currentPoint;
				LinkedMatrixElement oldcurrent = currentPoint;
				if (logger.isDebugEnabled()) {
					logger.debug("Last line element "
							+ tempRectangle.getLastLineFirstElement());
					logger.debug("Area "
							+ getArea(tempRectangle.getLastLineFirstElement()));
				}
				currentPoint = tempRectangle.getLastLineFirstElement();
				stackNewRectangle();
				currentPoint = oldcurrent;
				setCounters();
				// Set the end of line
				xToCheck--;
				if (stack.size() == 0) {
					if (logger.isDebugEnabled())
						logger.debug("Move out");
					return;
				}
				// Want to go to the right
				mergeOrRemoveDepop(true);
			} else {
				if (stack.size() == 0)
					return;
				if (logger.isDebugEnabled())
					logger.debug("Was a line");
				// Change the width of the rectangle
				stack.peek()
						.setWidth(
								tempRectangle.getCurrentPosition().getY()
										- tempRectangle.getUpperLeftCorner()
												.getY() + 1);
				// Update the counters
				xToCheck = stack.peek().getHeight() - 1;
				xTotal = stack.peek().getHeight();
				yTotal = stack.peek().getWidth();
				yToCheck = stack.peek().getWidth();
				tempRectangle.setLastLineFirstElement(tempRectangle
						.getUpperLeftCorner());
				tryMerge();

			}
		}
		if (!foundAndMerge() && stack.size() > 0) {
			tryNextDown();
		}

	}

	/**
	 * Try to acces the next point in the South direction
	 */
	private void tryNextDown() {
		if (logger.isDebugEnabled())
			logger.debug("Current beginning line "
					+ stack.peek().getLastLineFirstElement());
		if (!stack.peek().getLastLineFirstElement()
				.isNextElementDownConsecutive()) {
			if (logger.isDebugEnabled())
				logger.debug("New line is not continuous");
			// Update rectangle height
			int newHeight = stack.peek().getLastLineFirstElement().getX()
					- stack.peek().getUpperLeftCorner().getX() + 1;
			stack.peek().setHeight(newHeight);
			stack.peek().setValidated(true);
			stack.peek().setPotentialSecondArea(0);
			mergeVertical();
			emptyStack();

		} else {
			if (logger.isDebugEnabled())
				logger.debug("New line is continuous");
			lastpoint = currentPoint;
			currentPoint = stack.peek().getLastLineFirstElement().getNextDown();

			if (logger.isDebugEnabled())
				logger.debug("Current point " + currentPoint);
		}
	}

	/**
	 * Move to the next element
	 */
	private void goNextElementStack() {
		if (stack.size() > 0) {
			if (yToCheck == yTotal) {
				tryNextDown();
			} else {
				tryNextRight();
			}
		} else {
			currentPoint = linkedMatrix.getFirstElement();
		}
	}

	/**
	 * 
	 * @return <code>true</code> if a new rectangle is stacked
	 */
	private boolean stackNewRectangle() {
		if (logger.isDebugEnabled())
			logger.debug("Stacking new rectangle");
		int currentHeight = currentPoint.getX();
		int currentWidth = currentPoint.getY();
		int height = temp1[currentHeight][currentWidth];
		int width = temp2[currentHeight][currentWidth];
		LinkedMatrixElement restart;
		xTotal = height;
		yTotal = width;
		area = width * height;
		// If point remove and go
		if (area == 1) {
			stack.add(new TemporaryRectangle(height, width, area, currentPoint,
					currentPoint, currentPoint));

			if (stack.size() > 1) {
				if (logger.isDebugEnabled())
					logger.debug("Try merge " + stack);
				if (!tryMerge())
					emptyStack();

			} else {
				if (logger.isDebugEnabled())
					logger.debug("Removing point " + stack);
				removeCurrentRectangle();
			}
			return false;
		} else {
			if (logger.isDebugEnabled())
				logger.debug("yToCheck " + yToCheck);
			if (yTotal == 1) {// Point de rédemérrage en fonction de //
								// largeur>1 ou
								// non
				restart = currentPoint.getNextDown();
				// xToCheck--;
				// yToCheck=yTotal;
			} else {
				if (currentPoint.isNextElementRightConsecutive())
					restart = currentPoint.getNextRight();
				else
					restart = currentPoint;
			}
			stack.add(new TemporaryRectangle(height, width, area, currentPoint,
					currentPoint, restart));
			stack.peek().setLastLineFirstElement(currentPoint);
			if (logger.isDebugEnabled()) {
				if (stack.size() > 0)
					logger.debug("Stacked " + stack.peek());
			}

		}
		return true;
	}

	/**
	 * Return the area for a given point
	 * 
	 * @param point
	 * @return
	 */
	private int getArea(LinkedMatrixElement point) {
		int currentHeight = point.getX();
		int currentWidth = point.getY();
		return (temp1[currentHeight][currentWidth] * temp2[currentHeight][currentWidth]);
	}

	/**
	 * Remove rectangle and add to the list
	 * 
	 * @param origin
	 * @param height
	 * @param width
	 */
	private void addRectangle(LinkedMatrixElement origin, int height, int width) {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing rectangle " + origin + " width " + width
					+ " height " + height);
		}
		if (width * height >= MIN_AREA)
			rectangles.add(new SortableRectangle(origin.getX(), origin.getY(),
					width, height));
		linkedMatrix.removeRectangle(origin, height, width);

		if (logger.isDebugEnabled()) {
			logger.debug("After delete \n" + linkedMatrix);
			logger.debug("Stack " + stack.size() + " : " + stack);
		}

	}

	/**
	 * 
	 * @return <code>true</code> if the vertical merging occured,
	 *         <code>false</code> otherwise
	 */
	private boolean mergeVertical() {
		if (stack.isVerticallyMergeable()) {
			if (logger.isDebugEnabled())
				logger.debug("Merge vertical");
			// Two becomes one
			TemporaryRectangle oldlast = stack.poll();
			TemporaryRectangle newLast = stack.peek();
			int maxheightOld = oldlast.getUpperLeftCorner().getX()
					+ oldlast.getHeight() - 1;
			int maxheightNew = newLast.getUpperLeftCorner().getX()
					+ newLast.getHeight() - 1;
			if (maxheightOld > maxheightNew) {// New greater rectangle
				newLast.setHeight(maxheightOld
						- newLast.getUpperLeftCorner().getX() + 1);
			}
			stack.peek().setLastLineFirstElement(
					oldlast.getLastLineFirstElement());

			area = newLast.getArea();
			// Update counters
			xTotal = newLast.getHeight();
			// Set xTocheck from the position
			xToCheck = Math.max(maxheightOld, maxheightNew)
					- oldlast.getLastLineFirstElement().getX();
			yTotal = newLast.getWidth();
			yToCheck = yTotal;
			stack.peek().setValidated(false);
			newLast.setCurrentPosition(oldlast.getCurrentPosition());
			int expectedX = newLast.getUpperLeftCorner().getX()
					+ newLast.getHeight() - 1;
			int expectedY = newLast.getUpperLeftCorner().getY()
					+ newLast.getWidth() - 1;
			if (logger.isDebugEnabled())
				logger.debug("XY :" + expectedX + "  " + expectedY + " "
						+ newLast.getCurrentPosition());
			if (xToCheck == 0
					|| (expectedX == oldlast.getCurrentPosition().getX() && expectedY == oldlast
							.getCurrentPosition().getY())) {
				stack.peek().setValidated(true);
				stack.peek().setPotentialSecondArea(0);
				if (!newLast.getLastLineFirstElement()
						.isNextElementDownConsecutive()
						&& !oldlast.getCurrentPosition()
								.isNextElementRightConsecutive()) {
					if (logger.isDebugEnabled())
						logger.debug("Remove merged rectangle");
					removeCurrentRectangle();
					return false;
				}
			}
			if (logger.isDebugEnabled())
				logger.debug("After vertical merge " + stack);
			return true;
		} else
			return false;
	}

	/**
	 * 
	 * @return <code>true</code> if the horizontal merging occured
	 */
	private boolean mergeHorizontal() {
		if (logger.isDebugEnabled())
			logger.debug("Merge horizontal");
		if (stack.isHorizontallyMergeable()) {
			// Two becomes one
			TemporaryRectangle oldlast = stack.poll();
			TemporaryRectangle newLast = stack.peek();
			int maxWidthOld = oldlast.getUpperLeftCorner().getY()
					+ oldlast.getWidth() - 1;
			int maxWidthNew = newLast.getUpperLeftCorner().getY()
					+ newLast.getWidth() - 1;
			if (maxWidthOld > maxWidthNew) {
				newLast.setWidth(maxWidthOld
						- newLast.getUpperLeftCorner().getY() + 1);
			}
			newLast.setValidated(false);
			area = newLast.getArea();
			// Update counters

			if (newLast.getHeight() > 1) {
				if (oldlast.getCurrentPosition().getX() > newLast
						.getCurrentPosition().getX()) {// Some
														// parts
														// of
														// the
														// old
														// have
														// not
														// been
														// validated
					newLast.setCurrentPosition(oldlast.getUpperLeftCorner());
				} else
					newLast.setCurrentPosition(oldlast.getCurrentPosition());
			} else
				newLast.setCurrentPosition(oldlast.getCurrentPosition());

			updateCounterToCurrentRectangle();
			if (yToCheck == 0) {
				xToCheck--;
				if (xToCheck == 0) {
					newLast.setPotentialSecondArea(0);
					newLast.setValidated(true);
					// Check if rectangle is to be removed
					if (!newLast.getLastLineFirstElement()
							.isNextElementDownConsecutive()
							&& !oldlast.getCurrentPosition()
									.isNextElementRightConsecutive()) {
						if (logger.isDebugEnabled())
							logger.debug("Remove merged rectangle");
						removeCurrentRectangle();
						return false;
					}

				}

			}
			return true;
		}
		return false;
	}

	/**
	 * Update the counter after deletion
	 */
	private void updateCounterToCurrentRectangle() {
		TemporaryRectangle current = stack.peek();
		yTotal = current.getWidth();
		yToCheck = current.getWidth()
				- (current.getCurrentPosition().getY()
						- current.getUpperLeftCorner().getY() + 1);
		xTotal = current.getHeight();
		xToCheck = xTotal
				- (current.getCurrentPosition().getX()
						- current.getUpperLeftCorner().getX() + 1) + 1;
		if (logger.isDebugEnabled()) {
			logger.debug("After refresh counters");
			logger.debug("XtoCheck " + xToCheck);
			logger.debug("XTotal " + xTotal);
			logger.debug("YtoCheck " + yToCheck);
			logger.debug("YTotal " + yTotal);
		}
	}

	/**
	 * 
	 * @param temp
	 * @return String representation of the matrix
	 */
	private String matrixToString(int[][] temp) {
		// Better output than deepToString
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < temp.length; i++) {
			s.append(Arrays.toString(temp[i]) + "\n");
		}

		return s.toString();
	}

	/**
	 * Generate a picture of the current state
	 * 
	 * @param linkedMatrix
	 * @param rectangles
	 * @param stack
	 * @param currentPoint2
	 * @param step
	 */
	private void generateImage() {
		// Clean the folder

		int pixelsize = 1;
		if (linkedMatrix.getHeight() < 400 && linkedMatrix.getWidth() < 400) {
			// Compute size of pixels
			pixelsize = 400 / linkedMatrix.getHeight();
		}
		BufferedImage img = new BufferedImage(pixelsize
				* linkedMatrix.getWidth(),
				pixelsize * linkedMatrix.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);
		// Create background matrix
		setImageBackground(img, linkedMatrix, pixelsize);
		// Set rectangles in stack and current one
		setRectangleStack(img, stack, pixelsize);
		// Set current Point
		setCurrentPoint(img, currentPoint, pixelsize);
		// Save image
		if (!movie) {
			try {
				ImageIO.write(img, "jpg", new File("images/image" + step
						+ ".jpg"));
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else {
			listImages.add(img);
		}

	}

	private void cleanfolder() {
		File folder = new File("images/");
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (!f.isDirectory()) {
					f.delete();
				}
			}
		}

	}

	private void setImageBackground(BufferedImage img,
			LinkedMatrix linkedMatrix, int pixelsize) {
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				img.setRGB(j, i, 8421504);
			}
		}
		// Set the matrix values
		LinkedMatrixElement current = linkedMatrix.getFirstElement();
		while (current != null) {
			if (current.isValue())
				setPoint(img, current, pixelsize, 0xA329B);
			current = current.getNextRight();
		}

	}

	private void setRectangleStack(BufferedImage img, RectangleDeque stack2,
			int pixelsize) {
		TemporaryRectangle rect = stack2.peek();
		if (rect != null) {
			LinkedMatrixElement point = rect.getUpperLeftCorner();
			int xstart = point.getX();
			int ystart = point.getY();
			int color = 0xFFD700;
			for (int i = 0; i < rect.getHeight(); i++) {
				for (int j = 0; j < rect.getWidth(); j++) {
					setPoint(img, xstart + i, ystart + j, pixelsize, color);
				}
			}
		}

	}

	private void setCurrentPoint(BufferedImage img,
			LinkedMatrixElement currentPoint2, int pixelsize) {
		setPoint(img, currentPoint2, pixelsize, 0xCD1414);
	}

	private void setPoint(BufferedImage img, LinkedMatrixElement point,
			int pixelsize, int color) {
		int xstart = point.getX() * pixelsize;
		int ystart = point.getY() * pixelsize;
		for (int i = 0; i < pixelsize; i++) {
			for (int j = 0; j < pixelsize; j++) {
				img.setRGB(ystart + j, xstart + i, color);
			}
		}
	}

	private void setPoint(BufferedImage img, int x, int y, int pixelsize,
			int color) {
		int xstart = x * pixelsize;
		int ystart = y * pixelsize;
		for (int i = 0; i < pixelsize; i++) {
			for (int j = 0; j < pixelsize; j++) {
				img.setRGB(ystart + j, xstart + i, color);
			}
		}
	}

}
