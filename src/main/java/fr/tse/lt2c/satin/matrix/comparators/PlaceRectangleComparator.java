package fr.tse.lt2c.satin.matrix.comparators;

import java.util.Comparator;

import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;

/**
 * Rectangle comparator, compare upper left corners in lexicographic order
 * 
 * @author Julien
 * 
 */
public class PlaceRectangleComparator implements Comparator<SortableRectangle> {

	@Override
	public int compare(SortableRectangle o1, SortableRectangle o2) {
		if (o1.getX() < o2.getX())
			return -1;
		else if (o1.getX() == o2.getX()) {
			if (o1.getY() < o2.getY())
				return -1;
			else if (o1.getY() < o2.getY())
				return 0;
			else
				return 1;
		} else {
			return 1;
		}
	}
}
