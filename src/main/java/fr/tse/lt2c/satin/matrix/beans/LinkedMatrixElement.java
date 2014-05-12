package fr.tse.lt2c.satin.matrix.beans;

/**
 * Element of the linked matrix
 * 
 * @author Julien Subercaze
 * 
 */
public class LinkedMatrixElement {

	int x, y; // Position of the element in the matrix
	LinkedMatrixElement previousLeft, nextRight, previousUp, nextDown; // Pointers
																		// to
																		// the
	// next elements
	boolean value; // Value of the element

	public LinkedMatrixElement(int x, int y, boolean value) {
		super();
		this.x = x;
		this.y = y;
		this.value = value;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public LinkedMatrixElement getNextRight() {
		return nextRight;
	}

	public void setNextRight(LinkedMatrixElement nextRight) {
		this.nextRight = nextRight;
	}

	public LinkedMatrixElement getNextDown() {
		return nextDown;
	}

	public void setNextDown(LinkedMatrixElement nextDown) {
		this.nextDown = nextDown;
	}

	public LinkedMatrixElement getPreviousUp() {
		return previousUp;
	}

	public void setPreviousUp(LinkedMatrixElement previousUp) {
		this.previousUp = previousUp;
	}

	public LinkedMatrixElement getPreviousLeft() {
		return previousLeft;
	}

	public void setPreviousLeft(LinkedMatrixElement previousLeft) {
		this.previousLeft = previousLeft;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String positionAsString() {
		return "[x=" + x + ", y=" + y + "]";
	}

	@Override
	public String toString() {
		String left = previousLeft == null ? "null" : previousLeft
				.positionAsString();
		String right = nextRight == null ? "null" : nextRight
				.positionAsString();
		String up = previousUp == null ? "null" : previousUp.positionAsString();
		String down = nextDown == null ? "null" : nextDown.positionAsString();
		return "element [x=" + x + ", y=" + y + ", left=" + left + ", right="
				+ right + ", up=" + up + ", down=" + down + ", value=" + value
				+ "]";
	}

	/**
	 * 
	 * @return true if the next down element is the expected one i.e. if current
	 *         is x,y, next down should be x+1,y
	 */
	public boolean isNextElementDownConsecutive() {
		if (this.nextDown == null)
			return false;
		if (this.getNextDown().isValue()
				&& this.getNextDown().getX() == (this.x + 1)
				&& this.getNextDown().getY() == y)
			return true;
		return false;
	}

	/**
	 * 
	 * @return true if the next right element is the expected one i.e. if
	 *         current is x,y, next right should be x,y+1
	 */
	public boolean isNextElementRightConsecutive() {
		if (this.nextRight == null)
			return false;
		if (this.getNextRight().isValue()
				&& this.getNextRight().getX() == this.x
				&& this.getNextRight().getY() == (y + 1))
			return true;
		return false;
	}

	/**
	 * 
	 * @param moves
	 * @return the element that is at 'moves' from the current one to the right,
	 *         return null if there is no such element
	 */
	public LinkedMatrixElement getRightElement(int moves) {
		LinkedMatrixElement result = this;
		while (result != null && moves > 0) {
			result = result.getNextRight();
			moves--;
		}
		return result;
	}

	/**
	 * 
	 * @param moves
	 * @return the element that is at 'moves' from the current one towards
	 *         bottom, return null if there is no such element
	 */
	public LinkedMatrixElement getDownElement(int moves) {
		LinkedMatrixElement result = this;
		while (result != null && moves > 0) {
			result = result.getNextDown();
			moves--;
		}
		return result;
	}

	/**
	 * 
	 * @param moves
	 * @return the element that is at 'moves' from the current towards left
	 *         direction, return null if there is no such element
	 */
	public LinkedMatrixElement getLeftElement(int moves) {
		LinkedMatrixElement result = this;
		while (result != null && moves > 0) {
			result = result.getPreviousLeft();
			moves--;
		}
		return result;
	}

	/**
	 * 
	 * @param moves
	 * @return the element that is at 'moves' from the current towards the top,
	 *         return null if there is no such element
	 */
	public LinkedMatrixElement getUpElement(int moves) {
		LinkedMatrixElement result = this;
		while (result != null && moves > 0) {
			result = result.getPreviousUp();
			moves--;
		}
		return result;
	}

	public boolean isNextElementDownConsecutiveNoValue() {
		if (this.nextDown == null)
			return false;
		if (this.getNextDown().getX() == (this.x + 1)
				&& this.getNextDown().getY() == y)
			return true;
		return false;
	}

	/**
	 * 
	 * @return true if the next right element is the expected one i.e. if
	 *         current is x,y, next right should be x,y+1
	 */
	public boolean isNextElementRightConsecutiveNoValue() {
		if (this.nextRight == null)
			return false;
		if (this.getNextRight().getX() == this.x
				&& this.getNextRight().getY() == (y + 1))
			return true;
		return false;
	}

	public boolean isNull() {
		return (nextDown == null) && (nextRight == null)
				&& (previousLeft == null) && (previousUp == null);
	}

	public String viewPosition() {
		return "element [x=" + x + ", y=" + y + "]";
	}

	public boolean isNextElementUpConsecutive() {
		if (this.previousUp == null)
			return false;
		if (this.getPreviousUp().getX() == (this.x - 1)
				&& this.getPreviousUp().getY() == y)
			return true;
		return false;
	}

	public boolean isPreviousElementLeftConsecutive() {
		if (this.previousLeft == null)
			return false;
		if (this.getPreviousLeft().getX() == this.x
				&& this.getPreviousLeft().getY() == (y - 1))
			return true;
		return false;
	}
}
