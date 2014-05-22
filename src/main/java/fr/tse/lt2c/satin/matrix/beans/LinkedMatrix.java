package fr.tse.lt2c.satin.matrix.beans;

/**
 * Binary Matrix as elements simply linked towards right and towards bottom
 * 
 * This matrix can be instantiated using a boolean[][]
 * 
 * @author Julien Subercaze
 * 
 *         Feb. 2013
 * 
 */
public class LinkedMatrix {

	LinkedMatrixElement root;
	LinkedMatrixElement currentPoint;
	LinkedMatrixElement previousPoint;
	private int height;
	private int width;
	LinkedMatrixElement largestRectangle;

	public LinkedMatrix(BinaryMatrix m) {
		this(m.matrix, -1, -1);
	}

	public LinkedMatrix(BinaryMatrix m, int x, int y) {
		this(m.matrix, x, y);
	}

	public LinkedMatrixElement getLargestRectangle() {
		return largestRectangle;
	}

	public void setLargestRectangle(LinkedMatrixElement largestRectangle) {
		this.largestRectangle = largestRectangle;
	}

	/**
	 * Convert a boolean matrix representation into a linked matrix
	 * 
	 * @param matrix
	 */
	public LinkedMatrix(boolean[][] matrix, int x, int y) {

		root = new LinkedMatrixElement(-1, -1, false);
		setHeight(matrix.length);
		setWidth(matrix[getHeight() - 1].length);

		LinkedMatrixElement current = null, previous = null, currentRequiringNextDown = null;
		boolean startedSettingNextDownUp = false; // Used to saved void hits,
													// since setting is
													// guaranteed
													// to be done after
													// insertion of
													// the first line
		for (int i = 0; i < getHeight(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				previous = current;
				current = new LinkedMatrixElement(i, j, matrix[i][j]);
				if (i == 0 && j == 0) {
					if (x == i && j == y)
						largestRectangle = current;
					root.setNextRight(current);
					current.setPreviousLeft(root);
					currentRequiringNextDown = current;
				} else {
					if (x == i && j == y)
						largestRectangle = current;
					previous.setNextRight(current);
					current.setPreviousLeft(previous);
					// Check for setting the next down Pointer of the current
					// element
					if (!startedSettingNextDownUp) {
						if (currentRequiringNextDown.getX() == i - 1
								&& currentRequiringNextDown.getY() == j) {
							startedSettingNextDownUp = true;
						}
					}
					if (startedSettingNextDownUp) {
						currentRequiringNextDown.setNextDown(current);
						current.setPreviousUp(currentRequiringNextDown);
						currentRequiringNextDown = currentRequiringNextDown
								.getNextRight();
					}
				}
			}
		}
	}

	/**
	 * Remove a rectangle from the matrix This method does not guarantee
	 * integrity, beware to check the existence of the rectangle before calling
	 * this method
	 * 
	 * @param origin
	 *            upper left corner of the rectangle that will be removed
	 * @param rectangleWidth
	 * @param rectangleHeight
	 */
	public void removeRectangle(LinkedMatrixElement origin,
			int rectangleHeight, int rectangleWidth) {
		if (rectangleHeight == 1 && rectangleWidth == 1) {
			deleteElement(origin);
		} else {
			LinkedMatrixElement current = origin;
			// Remove length elements for each line
			for (int i = 0; i < rectangleHeight; i++) {
				LinkedMatrixElement next = current;
				if (!current.isNextElementDownConsecutiveNoValue()
						&& !(i == (rectangleHeight - 1))) {
					System.out.println(this);
					System.out.println("oRigin " + origin);

					System.out.println("Down " + current);
					System.exit(0);
				}
				current = current.getNextDown();
				for (int j = 0; j < rectangleWidth; j++) {
					if (!next.isNextElementRightConsecutiveNoValue()
							&& !(j == (rectangleWidth - 1))) {
						System.out.println(this);
						System.out.println("oRigin " + origin);
						System.out.println("Width " + rectangleWidth + "; i="
								+ i + " ;j=" + j);
						System.out.println("Right " + next);
						System.exit(0);
					}
					next = deleteElement(next);
				}
				// Go down a line
			}
		}

	}

	/**
	 * Delete the current element from the matrix
	 * 
	 * @param current
	 * @return the next element in the eastern direction
	 */
	private LinkedMatrixElement deleteElement(LinkedMatrixElement current) {
		// Update the pointers
		if (current.nextDown != null)
			current.getNextDown().setPreviousUp(current.previousUp);
		if (current.previousLeft != null)
			current.getPreviousLeft().setNextRight(current.getNextRight());
		if (current.previousUp != null)
			current.getPreviousUp().setNextDown(current.getNextDown());
		if (current.nextRight != null) {
			current.getNextRight().setPreviousLeft(current.getPreviousLeft());
		}
		LinkedMatrixElement res = current.getNextRight();
		current.nextDown = null;
		current.nextRight = null;
		current.previousLeft = null;
		current.previousUp = null;
		current = null;
		return res;
	}

	public LinkedMatrixElement deleteZero(LinkedMatrixElement current) {
		// Update the pointers
		if (current.nextDown != null)
			current.getNextDown().setPreviousUp(current.previousUp);
		if (current.previousLeft != null)
			current.getPreviousLeft().setNextRight(current.getNextRight());
		if (current.previousUp != null)
			current.getPreviousUp().setNextDown(current.getNextDown());
		if (current.nextRight != null) {
			current.getNextRight().setPreviousLeft(current.getPreviousLeft());
		}
		LinkedMatrixElement res = current.getPreviousLeft();
		current.nextDown = null;
		current.nextRight = null;
		current.previousLeft = null;
		current.previousUp = null;
		current = null;
		return res;
	}

	@Override
	public String toString() {
		boolean[][] matrix = new boolean[getHeight()][getWidth()];
		LinkedMatrixElement current = root;
		StringBuffer sb2 = new StringBuffer();
		while (current != null) {
			sb2.append(current.positionAsString() + ";");
			if (current.isValue()) {
				matrix[current.getX()][current.getY()] = true;
			}
			current = current.getNextRight();

		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < getHeight(); i++) {
			sb.append(lineToString(matrix[i]) + "\n");
		}

		return sb.toString() + "\n" + sb2.toString();
	}

	private String lineToString(boolean[] line) {
		StringBuffer sb = new StringBuffer(line.length * 3 + 2);
		sb.append("[");
		for (int i = 0; i < line.length - 1; i++) {
			sb.append(line[i] ? "1" : "0");
			sb.append(", ");
		}
		sb.append(line[line.length - 1] ? "1" : "0");
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Return the current root of the matrix. Use with caution
	 * 
	 * @return
	 */
	public LinkedMatrixElement getCurrentRoot() {
		return this.root;
	}

	/**
	 * Return the first element of the matrix (upper left corner), null if empty
	 * 
	 * @return
	 */
	public LinkedMatrixElement getFirstElement() {
		return this.root.nextRight;
	}

	/**
	 * 
	 * @return true if the matrix is empty
	 */
	public boolean isEmpty() {
		return root.nextRight == null;
	}

	public int countElements() {
		LinkedMatrixElement _tmp = this.getFirstElement();
		if (_tmp == null)
			return 0;
		int count = 1;
		while (_tmp != null) {
			count++;
			_tmp = _tmp.getNextRight();
		}
		return count;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
