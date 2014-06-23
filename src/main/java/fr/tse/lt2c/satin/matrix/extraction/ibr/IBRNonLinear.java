package fr.tse.lt2c.satin.matrix.extraction.ibr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;
import fr.tse.lt2c.satin.matrix.extraction.behaviors.RectangleExtractor;
import fr.tse.lt2c.satin.matrix.extraction.rowcolsegmentation.RowSegmentation;

/**
 * Post extraction merging, non linear implementation, for decomposition quality
 * only. Straightforward/stupid implementation. For test purpose. Values in the
 * papers are computed with the linear Version. Zodop implementation is even
 * worst than this one.
 * 
 * @author Julien Subercaze
 * 
 */
public class IBRNonLinear implements RectangleExtractor {

	BinaryMatrix B = null;
	int MIN_AREA;

	public IBRNonLinear(BinaryMatrix matrix) {
		this.B = matrix;
	}

	@Override
	public DecompositionResult extractDisjointRectangles() {
		RowSegmentation rowResult = new RowSegmentation(B, 1);
		DecompositionResult temp = rowResult.extractDisjointRectangles();
		// System.out.println(temp);
		List<SortableRectangle> res = merge(temp);
		temp.setRectangle(res);
		return temp;
	}

	private List<SortableRectangle> merge(DecompositionResult result) {
		List<Integer> toremove = new ArrayList<Integer>();
		List<SortableRectangle> liste = result.getRectangle();
		List<SortableRectangle>[] indexedByLine = new List[B.getRow()];
		for (SortableRectangle rect : liste) {
			if (indexedByLine[rect.x] == null)
				indexedByLine[rect.x] = new ArrayList<>();
			indexedByLine[rect.x].add(rect);
		}
		// System.out.println(Arrays.toString(indexedByLine));
		// MergeEm
		for (int i = 0; i < indexedByLine.length - 1; i++) {
			if (toremove.size() > 0) {
				Collections.reverse(toremove);
				for (int k : toremove) {
					indexedByLine[i - 1].remove(k);
				}
				toremove = new ArrayList<Integer>();
			}
			List<SortableRectangle> up = indexedByLine[i];
			List<SortableRectangle> down = indexedByLine[i + 1];
			if (up == null || up.size() == 0 || down == null
					|| down.size() == 0)
				continue;
			for (int j = 0; j < up.size(); j++) {
				SortableRectangle rect = up.get(j);
				SortableRectangle tmp = getStartingAt(rect.y, down);
				if (tmp != null && (tmp.y + tmp.width) == (rect.y + rect.width)) {
					// System.out.println("here " + rect.y);
					tmp.x = i;
					tmp.height = rect.height + 1;
					toremove.add(j);
				}
			}

		}
		if (toremove.size() > 0) {
			Collections.reverse(toremove);
			for (int k : toremove) {
				indexedByLine[indexedByLine.length - 2].remove(k);
			}
		}
		List<SortableRectangle> resultat = new ArrayList<>();
		for (int i = 0; i < indexedByLine.length; i++) {
			if (indexedByLine[i] != null)
				resultat.addAll(indexedByLine[i]);
		}
		return resultat;
	}

	private SortableRectangle getStartingAt(int y, List<SortableRectangle> list) {
		for (SortableRectangle rect : list) {
			if (rect.y > y)
				break;
			if (rect.y == y) {
				return rect;
			}

		}
		return null;
	}
}
