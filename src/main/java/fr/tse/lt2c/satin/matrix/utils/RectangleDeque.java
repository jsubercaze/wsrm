package fr.tse.lt2c.satin.matrix.utils;

import fr.tse.lt2c.satin.matrix.beans.TemporaryRectangle;

public class RectangleDeque {

	CustomDeque<TemporaryRectangle> queue;

	/**
	 * Constructor that automatically compute the required size of the queue
	 * 
	 * @param width
	 * @param height
	 */
	public RectangleDeque(int width, int height) {
		queue = new CustomDeque<>(width * height / 2);
	}

	public boolean isSimplyHorizontallyMergeable() {
		if (queue.size() >= 2) {

			TemporaryRectangle last = queue.peek();
			TemporaryRectangle secondLast = queue.peekSecondLast();

			if (last.getHeight() > 1 && last.getCurrentPosition().getX() > secondLast.getCurrentPosition().getX())
				return false;
			return (last.getHeight() == secondLast.getHeight() && (last.getUpperLeftCorner().getX() == secondLast.getUpperLeftCorner().getX()));
		} else
			return false;
	}

	/**
	 * Determine if the two last rectangles can be merged horizontally
	 * 
	 * @return
	 */
	public boolean isHorizontallyMergeable() {
		if (queue.size() >= 2) {

			TemporaryRectangle last = queue.peek();
			TemporaryRectangle secondLast = queue.peekSecondLast();

			if (last.getHeight() > 1 && last.getCurrentPosition().getX() > secondLast.getCurrentPosition().getX())
				return false;
			return (last.getHeight() == secondLast.getHeight() && (last.getUpperLeftCorner().getX() == secondLast.getUpperLeftCorner().getX())
					&& last.getUpperLeftCorner().isPreviousElementLeftConsecutive() && secondLast.getCurrentPosition().isNextElementRightConsecutive());
		} else
			return false;
	}

	public boolean isSimplyVerticallyMergeable() {
		if (queue.size() >= 2) {
			TemporaryRectangle last = queue.peek();
			TemporaryRectangle secondLast = queue.peekSecondLast();
			return ((last.getWidth() == secondLast.getWidth()) && (last.getUpperLeftCorner().getY() == secondLast.getUpperLeftCorner().getY()));
		} else {
			return false;
		}
	}

	/**
	 * Determine if the two last rectangles can be merged vertically
	 * 
	 * @return
	 */
	public boolean isVerticallyMergeable() {
		if (queue.size() >= 2) {
			TemporaryRectangle last = queue.peek();
			TemporaryRectangle secondLast = queue.peekSecondLast();
			return ((last.getWidth() == secondLast.getWidth()) && (last.getUpperLeftCorner().getY() == secondLast.getUpperLeftCorner().getY())
					&& last.getUpperLeftCorner().isNextElementUpConsecutive() && secondLast.getLastLineFirstElement().isNextElementDownConsecutive());
		} else {
			return false;
		}
	}

	public TemporaryRectangle secondPeek() {
		return queue.peekSecondLast();
	}

	public void add(TemporaryRectangle rect) {
		queue.add(rect);
	}

	public int size() {
		return queue.size();
	}

	public TemporaryRectangle peek() {
		return this.queue.peek();
	}

	public TemporaryRectangle poll() {
		return this.queue.poll();
	}

	@Override
	public String toString() {
		if (queue.size() > 0)
			return "RectangleDeque [queue=" + queue + "]";
		else
			return "Empty queue";
	}

}
