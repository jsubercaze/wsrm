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
	private int MIN_AREA = 1;
	public static boolean DEBUG;
	public static boolean QUICKDEBUG;
	BinaryMatrix binaryMatrix;
	int[][] temp1;
	int[][] temp2;
	int[][] temp3;
	int[][] temp4;
	// Matrix matrix;
	boolean preprocessed;
	private LinkedMatrixElement currentPoint;
	private boolean generateImages;
	private boolean movie;

	private ArrayList<BufferedImage> listImages;
	private SortableRectangle largestOne;
	private boolean removeLargestFirst = false;
	private int area = -1;
	private int xToCheck = -1;
	private int yToCheck = -1;
	private int xTotal = -1;
	private int yTotal = -1;
	// private LinkedMatrixElement currentBeginningLine;
	private boolean currentUpdated;
	private boolean previousUpdated;
	private int step = 0;
	private LinkedMatrix linkedMatrix;
	private int areaTmp;
	private RectangleDeque stack;
	private boolean match_expected;
	private List<SortableRectangle> rectangles;
	private LinkedMatrixElement lastpoint;

	public WSMRDecomposer(BinaryMatrix m) {
		super();
		this.binaryMatrix = m;
		temp1 = new int[m.getRow()][m.getCol()];
		temp2 = new int[m.getRow()][m.getCol()];
	}

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

	public SortableRectangle findLargestRectangle() {
		firstStep();
		secondStep();
		thirdStep();

		// if (DEBUG) {
		// logger.debug(matrixToString(temp1));
		// logger.debug(matrixToString(temp2));
		// logger.debug(matrixToString(temp3));
		// logger.debug(matrixToString(temp4));
		// }
		return firstLargestRectangle();
	}

	/**
	 * Compute for each column
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
	 * Compute for each row
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
	 * The trick !
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
				if (previous == 0)
					previous = temp3[i][j];
				else if (temp3[i][j] <= previous) {
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

	public void preProcess() {
		firstStep();
		if (DEBUG)
			logger.debug(matrixToString(temp1));
		secondStep();
		if (DEBUG)
			logger.debug(matrixToString(temp2));
		thirdStep();
		if (DEBUG) {
			logger.debug(matrixToString(temp3));
			logger.debug(matrixToString(temp4));
		}
		fourthStep();
		preprocessed = true;
		if (DEBUG) {
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
		long t1 = System.currentTimeMillis();
		if (DEBUG) {
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
			System.out.println(largestOne);
			LinkedMatrixElement largestUpperLeft = linkedMatrix
					.getLargestRectangle();
			System.out.println(largestUpperLeft);
			addRectangle(largestUpperLeft, largestOne.height, largestOne.width);

		}
		// matrix = new FastListMatrix(this.binaryMatrix.getRow(),
		// this.binaryMatrix.getCol());
		// currentBeginningLine = null;

		currentPoint = linkedMatrix.getFirstElement();

		step = 0;

		do {
			step++;
			if (currentPoint == null) {
				currentPoint = linkedMatrix.getFirstElement();
			}
			if (currentPoint.isNull())
				currentPoint = linkedMatrix.getFirstElement();
			if (DEBUG) {
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
			if (DEBUG) {
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
		return new DecompositionResult(System.currentTimeMillis() - t1,
				rectangles, ones);
	}

	/**
	 * Process a step in the matrix decomposition
	 * 
	 */
	private void process() {

		// if (lastpoint == currentPoint)
		// System.exit(-1);
		step++;
		if (QUICKDEBUG)
			logger.debug("step " + step + ": " + currentPoint);
		if (stack.size() == 0) {
			if (DEBUG)
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
				if (DEBUG)
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
				if (DEBUG)
					logger.debug("Go On");
				// Update counters
				updateCounters();
				if (!foundAndMerge())
					goNextElementStack();
			}

		}

	}

	private void setCounters() {
		xToCheck = xTotal;
		yToCheck = yTotal;
		if (DEBUG) {
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
		if (DEBUG) {
			logger.debug("Found and merge");
			logger.debug("XtoCheck " + xToCheck);
			logger.debug("XTotal " + xTotal);
			logger.debug("YtoCheck " + yToCheck);
			logger.debug("YTotal " + yTotal);
		}
		if (!(xToCheck == 0))
			return false;
		if (stack.size() == 1) {
			if (DEBUG)
				logger.debug("Remove lonely rectangle");
			if (stack.peek().isValidated()) {
				removeCurrentRectangle();

				return true;
			} else
				return false;
		}

		// Rectangle found
		if (!tryMerge()) {
			if (DEBUG)
				logger.debug("Rectangle found, emptystack");
			stack.peek().setValidated(true);
			stack.peek().setPotentialSecondArea(0);
			emptyStack();

		}
		return true;

	}

	private boolean tryMerge() {
		// if (DEBUG && stack.size()>0)
		// logger.debug("TryMerge " + stack);

		if (DEBUG)
			logger.debug("Horizontal");
		if (!mergeHorizontal()) {
			if (DEBUG)
				logger.debug("didn't work");
			if (mergeVertical()) {
				if (DEBUG)
					logger.debug("worked vertical");
				if (stack.peek().isValidated()) {
					return false;
				}
				return true;
			}
		} else {
			if (DEBUG)
				logger.debug("Worked horizontal " + stack);
			return true;
		}
		if (DEBUG)
			logger.debug("Merge return false");
		return false;

	}

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

	private void updateCounters() {
		stack.peek().setCurrentPosition(currentPoint);
		if (yToCheck == yTotal)
			stack.peek().setLastLineFirstElement(currentPoint);
		if (yToCheck > 0) {
			if (DEBUG)
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
		if (DEBUG) {
			logger.debug("After update counters");
			logger.debug("XtoCheck " + xToCheck);
			logger.debug("XTotal " + xTotal);
			logger.debug("YtoCheck " + yToCheck);
			logger.debug("YTotal " + yTotal);
		}
	}

	private void checkUpdateOldRectangle() {
		// If current point is in the first line of the old rectangle, update
		// its width (case 1)
		if (stack.peek().isValidated())
			return;
		if (stack.peek().getHeight() == 1) {
			if (DEBUG)
				logger.debug("Set potential area to 0-0");
			stack.peek().setWidth(
					Math.max(currentPoint.getY()
							- stack.peek().getUpperLeftCorner().getY(), 1));

			stack.peek().setValidated(true);
		} else if (xToCheck == xTotal) {// First line
			// Update width of the rectangle
			if (DEBUG)
				logger.debug("Updating width");
			stack.peek().setWidth(
					currentPoint.getY()
							- stack.peek().getUpperLeftCorner().getY());
			int potentialArea = (currentPoint.getY() - stack.peek()
					.getLastLineFirstElement().getY())
					* (xToCheck - 1);
			if (DEBUG)
				logger.debug("Set potential area to " + potentialArea);

		} else if (stack.peek().getLastLineFirstElement()
				.isNextElementDownConsecutive()
				&& stack.peek().getLastLineFirstElement().getNextDown() == currentPoint) {
			// Case 2 in the paper

			// Update the height
			stack.peek().setHeight(xTotal - xToCheck);
			if (DEBUG) {
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
		tryMerge();
		// stack.peek().setRestartingPoint(currentPoint);

	}

	private void emptyStack() {
		if (DEBUG) {
			logger.debug("Removing current rectangle " + stack);
		}
		removeCurrentRectangle();
		while (stack.size() > 0 && stack.peek().isValidated()) {
			if (DEBUG)
				logger.debug("Removing rectangle with no child " + stack);
			removeCurrentRectangle();
		}
		if (stack.size() > 0) {
			if (DEBUG)
				logger.debug("Removing last cutted rectangle " + stack);
			int potentialAreaVertical = (currentPoint.getY()
					- stack.peek().getLastLineFirstElement().getY() + 1)
					* (stack.peek().getHeight());
			// Validated area
			int validatedArea = stack.peek().getWidth()
					* (currentPoint.getX() - stack.peek().getUpperLeftCorner()
							.getX());
			potentialVSvalidated(validatedArea, potentialAreaVertical);
		}

	}

	private void goNextElementEmptyStack() {
		if (yToCheck == yTotal) {
			if (DEBUG) {
				logger.debug("yToCheck==0");
				logger.debug("Try to go at (" + (currentPoint.getX() + 1) + ","
						+ currentPoint.getY() + ")");
			}
			if (!currentPoint.isNextElementDownConsecutive()) {
				if (DEBUG)
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
		if (DEBUG)
			logger.debug("Point courant " + currentPoint);
	}

	private void tryNextRight() {
		if (currentPoint.isNextElementRightConsecutive()) {
			lastpoint = currentPoint;
			currentPoint = currentPoint.getNextRight();
		} else {
			if (DEBUG)
				logger.debug("Next right not good");

			// Compute the potential second area
			if (stack.size() > 0) {
				if (DEBUG)
					logger.debug("remove or merge");

				mergeOrRemove(true);
			} else {
				lastpoint = currentPoint;
				currentPoint = linkedMatrix.getFirstElement();
			}
		}
	}

	private void mergeOrRemove(boolean isRight) {
		if (isRight) { // case where we wanted to go right
			if (stack.size() == 1 && xToCheck == xTotal) {// First line of empty
				if (DEBUG)
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
					if (DEBUG)
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
					if (DEBUG)
						logger.debug("Remove");// stack
					removeCurrentRectangle();
					currentPoint = linkedMatrix.getFirstElement();

				}
			} else if (stack.size() > 1) {
				if (DEBUG)
					logger.debug("Merge or remove stack >1");
				// try merge
				if (xToCheck == xTotal) {// on the first line
					if (DEBUG)
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
				if (DEBUG)
					logger.debug("Cut rectangle");
				potentialVSvalidated(-1, -1);

			}
		} else {// case where we wanted to go down - stack point
			if (DEBUG)
				logger.debug("Stack point");
			stack.peek().setHeight(1);
			tryMerge();
			lastpoint = currentPoint;
			currentPoint = currentPoint.getNextRight();
			if (currentPoint == null)
				potentialVSvalidated(-1, -1);
		}
	}

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

	private void potentialVSvalidated(int validatedArea,
			int potentialAreaVertical) {
		// double ratio = (double) potentialAreaVertical / (double)
		// validatedArea;
		// if (ratio > MIN_RATIO) {// Introduce non linearity
		// Give a try
		// } else {

		if (stack.size() == 0) {
			currentPoint = linkedMatrix.getFirstElement();
			return;
		}
		// Play safe - remove the validated part
		TemporaryRectangle tempRectangle = stack.peek();
		if (DEBUG) {
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
			if (DEBUG)
				logger.debug("Cutpoint " + cutPoint);
			// Case where first element is point
			if (height == 0 && (xToCheck == xTotal) && stack.size() == 1) {
				if (DEBUG)
					logger.debug("First point");
				tempRectangle.setWidth(1);
				tempRectangle.setHeight(1);
				if (!tryMerge())
					removeCurrentRectangle();
				else {
					logger.debug("MERGED !!");
				}
			} else if (height != 0) {
				if (DEBUG)
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
				// temp1[tempRectangle.getLastLineFirstElement().getX()][tempRectangle.getLastLineFirstElement().getY()]
				// = tempRectangle.getHeight() -
				// tempRectangle.getLastLineFirstElement().getX() + 1;
				lastpoint = currentPoint;
				LinkedMatrixElement oldcurrent = currentPoint;
				if (DEBUG) {
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
					if (DEBUG)
						logger.debug("Move out");
					return;
				}

				// Want to go to the right
				mergeOrRemoveDepop(true);
			} else {
				if (stack.size() == 0)
					return;
				if (DEBUG)
					logger.debug("Was a line");
				// Change the width
				// if (stack.size() == 1) {// all alone
				// stack.peek().setWidth(tempRectangle.getRestartingPoint().getY()
				// -
				// tempRectangle.getUpperLeftCorner().getY() + 1);
				// } else
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
		if (!foundAndMerge() && stack.size() > 0)

			tryNextDown();
		// }

	}

	private void tryNextDown() {
		if (DEBUG)
			logger.debug("Currentbeginning line "
					+ stack.peek().getLastLineFirstElement());
		if (!stack.peek().getLastLineFirstElement()
				.isNextElementDownConsecutive()) {
			if (DEBUG)
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
			if (DEBUG)
				logger.debug("New line is continuous");
			lastpoint = currentPoint;
			currentPoint = stack.peek().getLastLineFirstElement().getNextDown();

			if (DEBUG)
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

	private boolean stackNewRectangle() {
		if (DEBUG)
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
				if (DEBUG)
					logger.debug("Try merge " + stack);
				if (!tryMerge())
					emptyStack();

			} else {
				if (DEBUG)
					logger.debug("Removing point " + stack);
				removeCurrentRectangle();
			}
			return false;
		} else {
			if (DEBUG)
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
			if (DEBUG) {
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
		if (DEBUG || QUICKDEBUG) {
			logger.debug("Removing rectangle " + origin + " width " + width
					+ " height " + height);
		}
		if (width * height >= MIN_AREA)
			rectangles.add(new SortableRectangle(origin.getX(), origin.getY(),
					width, height));
		linkedMatrix.removeRectangle(origin, height, width);

		if (DEBUG) {
			logger.debug("After delete \n" + linkedMatrix);
			logger.debug("Stack " + stack.size() + " : " + stack);
		}

	}

	public boolean mergeVertical() {
		if (stack.isVerticallyMergeable()) {
			if (DEBUG || QUICKDEBUG)
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
			if (DEBUG)
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
					if (DEBUG)
						logger.debug("Remove merged rectangle");
					removeCurrentRectangle();
					return false;
				}
			}
			if (DEBUG)
				logger.debug("After vertical merge " + stack);
			return true;
		} else
			return false;
	}

	public boolean mergeHorizontal() {
		if (DEBUG || QUICKDEBUG)
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
			// yTotal = newLast.getWidth();
			// yToCheck = newLast.getWidth() -
			// (oldlast.getCurrentPosition().getY() -
			// newLast.getUpperLeftCorner().getY() + 1);
			// xTotal = oldlast.getHeight();
			// xToCheck = xTotal - (oldlast.getCurrentPosition().getX() -
			// newLast.getUpperLeftCorner().getX() + 1) + 1;
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
						if (DEBUG)
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
		if (DEBUG) {
			logger.debug("After refresh counters");
			logger.debug("XtoCheck " + xToCheck);
			logger.debug("XTotal " + xTotal);
			logger.debug("YtoCheck " + yToCheck);
			logger.debug("YTotal " + yTotal);
		}
	}

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
