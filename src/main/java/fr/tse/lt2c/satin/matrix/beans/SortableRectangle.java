package fr.tse.lt2c.satin.matrix.beans;

import java.awt.Rectangle;

/**
 * Rectangle that are sortable by their areas.
 * 
 * @author Julien Subercaze
 * 
 */
public class SortableRectangle extends Rectangle implements
		Comparable<SortableRectangle> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2153413477134553950L;
	protected int area;

	public SortableRectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.area = width * height;
		// if (ArrayMatrixRectangleExtraction.DEBUG)
		// System.out.println("New Rectangle " + this.toString());
	}

	

	public int compareTo(SortableRectangle arg0) {
		return -Integer.compare(this.area, arg0.area);
	}

	@Override
	public String toString() {
		return "SortableRectangle [area=" + area + ", x=" + x + ", y=" + y
				+ ", width=" + width + ", height=" + height + "]\n";
	}

	public int getArea() {
		return area;
	}

	/**
	 * When possible, merge the two rectangles into the current one
	 * 
	 * @param sortableRectangle
	 * @return
	 */
	public boolean merged(SortableRectangle sortableRectangle) {
		if (isSimplyVerticallyMergeable(sortableRectangle)) {
			this.height += sortableRectangle.height;

			return true;
		} else if (isSimplyHorizontallyMergeable(sortableRectangle)) {
			this.width += sortableRectangle.width;
			return true;
		}
		return false;
	}

	public boolean isSimplyVerticallyMergeable(
			SortableRectangle sortableRectangle) {
		return ((this.width == sortableRectangle.width)
				&& (this.y) == sortableRectangle.y && (this.x + this.height) == sortableRectangle.x);

	}

	public boolean isSimplyHorizontallyMergeable(
			SortableRectangle sortableRectangle) {
		return ((this.height == sortableRectangle.height)
				&& (this.x) == sortableRectangle.x && (this.y + this.width) == sortableRectangle.y);
	}

}
