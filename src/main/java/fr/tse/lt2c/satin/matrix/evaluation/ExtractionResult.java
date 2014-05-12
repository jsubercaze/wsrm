package fr.tse.lt2c.satin.matrix.evaluation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.beans.SortableRectangle;

/**
 * This class computes statistics for the rectangle extraction quality for
 * several runs of different methods on a given matrix size
 * 
 * @author Julien Subercaze
 * 
 */
public class ExtractionResult {

	int row, col;
	// Results from the rectangle extraction, by method
	Multimap<Integer, DecompositionResult> results = HashMultimap.create();
	// Ouput results, computed in this class
	Map<Integer, MetricOutput> computedResults = new HashMap<>();

	public ExtractionResult(int row, int col) {
		super();
		this.row = row;
		this.col = col;
	}

	public void addResult(DecompositionResult result, int method) {
		results.put(method, result);
	}

	public void computeStats() {
		Set<Integer> keys = results.keySet();
		for (Integer key : keys) {
			Collection<DecompositionResult> res = results.get(key);
			Mean coverageMean = new Mean();
			StandardDeviation coverageStd = new StandardDeviation();
			Mean rectangleCountMean = new Mean();
			StandardDeviation rectangleCountStd = new StandardDeviation();
			Mean timeMean = new Mean();
			StandardDeviation timeStd = new StandardDeviation();
			double covered = 0;
			for (DecompositionResult r : res) {
				for (SortableRectangle rect : r.getRectangle()) {
					covered += rect.getArea();
				}
				rectangleCountMean.increment(r.getRectangle().size());
				rectangleCountStd.increment(r.getRectangle().size());
				covered = ((double) covered) / ((double) r.getNumberOfOnes());
				coverageMean.increment(covered);
				coverageStd.increment(covered);
				timeMean.increment(r.getTime());
				timeStd.increment(r.getTime());
			}
			computedResults.put(
					key,
					new MetricOutput(rectangleCountMean.getResult(),
							rectangleCountStd.getResult()
									/ rectangleCountMean.getResult(),
							coverageMean.getResult(), coverageStd.getResult()
									/ coverageMean.getResult(), timeMean
									.getResult(), timeStd.getResult()
									/ timeMean.getResult()));
		}
		results = null;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Map<Integer, MetricOutput> getComputedResults() {
		return computedResults;
	}

}
