package fr.tse.lt2c.satin.matrix.beans;

/**
 * Bean that stores the largest rectangle for each point, considered the current
 * point is the top left of that rectangle
 * 
 * @author Julien Subercaze
 * 
 *         06/03/2013
 * 
 */
public class LargestArea {
	int largestWidth;
	int largestHeight;
	int largestArea;

	public LargestArea(int largestWidth, int largestHeight, int largestArea) {
		super();
		this.largestWidth = largestWidth;
		this.largestHeight = largestHeight;
		this.largestArea = largestArea;

	}

	public void setDimensions(int largestWidth, int largestHeight,
			int largestArea) {
		this.largestWidth = largestWidth;
		this.largestHeight = largestHeight;
		this.largestArea = largestArea;
	}

	public int getLargestArea() {
		return largestArea;
	}

	private void setLargestArea(int largestArea) {
		this.largestArea = largestArea;
	}

	public void setWidth(int i) {
		this.largestWidth = i;
		this.setLargestArea(largestWidth * largestHeight);

	}

	public int getLargestHeight() {
		return largestHeight;
	}

	public int getLargestWidth() {
		return largestWidth;
	}

	@Override
	public String toString() {

		return "" + largestArea;
	}

}