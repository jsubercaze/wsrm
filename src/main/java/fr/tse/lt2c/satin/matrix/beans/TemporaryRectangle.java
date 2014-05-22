package fr.tse.lt2c.satin.matrix.beans;

/**
 * Temporary rectangle, for storage during decomposition process
 * 
 * @author Julien
 * 
 */
public class TemporaryRectangle {

	int height, width, area; // rectangle values
	public LinkedMatrixElement upperLeftCorner;
	LinkedMatrixElement currentPosition;
	LinkedMatrixElement restartingPoint, lastLineFirstElement;
	boolean hasUpdatedWidth;
	int previousLargestArea;
	int potentialSecondArea = -1;// Potential area after cut
	private boolean validated;

	public TemporaryRectangle(int height, int width, int area,
			LinkedMatrixElement upperLeftCorner,
			LinkedMatrixElement currentPosition,
			LinkedMatrixElement restartingPoint) {
		super();
		this.height = height;
		this.width = width;
		this.area = area;
		this.upperLeftCorner = upperLeftCorner;
		this.currentPosition = currentPosition;
		this.restartingPoint = restartingPoint;
		this.lastLineFirstElement = upperLeftCorner;
	}

	public void updateWidth(int newWidth) {
		hasUpdatedWidth = true;
		previousLargestArea = area;
		this.width = newWidth;
		area = this.height * this.width;
	}

	public boolean isHasUpdatedWidth() {
		return hasUpdatedWidth;
	}

	public LinkedMatrixElement getLastLineFirstElement() {
		return lastLineFirstElement;
	}

	public void setLastLineFirstElement(LinkedMatrixElement lastLineFirstElement) {
		this.lastLineFirstElement = lastLineFirstElement;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		this.setArea(height * width);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.setArea(height * width);
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		if (area == 0)
			System.exit(-1);
		this.area = area;
	}

	public LinkedMatrixElement getUpperLeftCorner() {
		return upperLeftCorner;
	}

	public void setUpperLeftCorner(LinkedMatrixElement upperLeftCorner) {
		this.upperLeftCorner = upperLeftCorner;
	}

	public LinkedMatrixElement getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(LinkedMatrixElement currentPosition) {
		this.currentPosition = currentPosition;
	}

	public LinkedMatrixElement getRestartingPoint() {
		return restartingPoint;
	}

	public void setRestartingPoint(LinkedMatrixElement restartingPoint) {
		this.restartingPoint = restartingPoint;
	}

	@Override
	public String toString() {

		return "rectangle " + isValidated() + "[height=" + height + ", width="
				+ width + ", area=" + area + ", upperLeftCorner="
				+ upperLeftCorner.viewPosition() + ", currentPosition="
				+ currentPosition.viewPosition() + ", lineBegin "
				+ lastLineFirstElement.viewPosition() + "potentialArea"
				+ potentialSecondArea + " ]\n";

	}

	public void updateWithLastGoodPoint() {
		this.height = this.currentPosition.getX() - this.upperLeftCorner.getX()
				+ 1;
		this.width = this.currentPosition.getY() - this.upperLeftCorner.getY()
				+ 1;
		this.area = this.height * this.width;
	}

	public int getPotentialSecondArea() {
		return potentialSecondArea;
	}

	public void setPotentialSecondArea(int potentialSecondArea) {
		this.potentialSecondArea = potentialSecondArea;
	}

	public void setValidated(boolean b) {
		this.validated = b;

	}

	public boolean isValidated() {
		return validated;
	}

}
