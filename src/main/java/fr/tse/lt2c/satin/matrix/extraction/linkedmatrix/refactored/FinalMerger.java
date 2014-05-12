package fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored;

import java.util.List;

import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;
import fr.tse.lt2c.satin.matrix.beans.TemporaryRectangle;
import fr.tse.lt2c.satin.matrix.utils.RectangleDeque;

public class FinalMerger {

	private List<SortableRectangle> rectangles;
	private RectangleDeque rectanglePoint;

	public FinalMerger(RectangleDeque rectanglePoint, List<SortableRectangle> rectangles) {
		this.rectanglePoint = rectanglePoint;
		this.rectangles = rectangles;

	}

	public void mergeOrRemove() {
		while (rectanglePoint.size() > 0) {
			while (hasMerged()) {
			}
			addRectangle();
		}

	}

	private void addRectangle() {
		TemporaryRectangle tmp = rectanglePoint.poll();
		rectangles.add(new SortableRectangle(tmp.getUpperLeftCorner().getX(), tmp.getUpperLeftCorner().getY(), tmp.getWidth(), tmp.getHeight()));
	}

	private boolean hasMerged() {
		if (rectanglePoint.isSimplyHorizontallyMergeable()) {
			mergeHorizontal();
			return true;
		} else if (rectanglePoint.isSimplyVerticallyMergeable()) {
			mergeVertical();
			return true;
		}
		return false;
	}

	private void mergeVertical() {
		TemporaryRectangle oldlast = rectanglePoint.poll();
		TemporaryRectangle newLast = rectanglePoint.peek();
		int maxheightOld = oldlast.getUpperLeftCorner().getX() + oldlast.getHeight() - 1;
		int maxheightNew = newLast.getUpperLeftCorner().getX() + newLast.getHeight() - 1;
		if (maxheightOld > maxheightNew) {// New greater rectangle
			newLast.setHeight(maxheightOld - newLast.getUpperLeftCorner().getX() + 1);
		}

	}

	private void mergeHorizontal() {
		TemporaryRectangle oldlast = rectanglePoint.poll();
		TemporaryRectangle newLast = rectanglePoint.peek();
		int maxWidthOld = oldlast.getUpperLeftCorner().getY() + oldlast.getWidth() - 1;
		int maxWidthNew = newLast.getUpperLeftCorner().getY() + newLast.getWidth() - 1;
		if (maxWidthOld > maxWidthNew) {
			newLast.setWidth(maxWidthOld - newLast.getUpperLeftCorner().getY() + 1);
		}
	}

}
