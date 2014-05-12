package fr.tse.lt2c.satin.testdecompo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;
import math.geom2d.polygon.convhull.GrahamScan2D;

/**
 * Generates random rectilinear Polygons by generating random points, finding
 * their convex hull. Secondly, between each point of the convex hull, a point
 * is seletected randomly to construct right angles.
 * 
 * @author Julien Subercaze
 * 
 *         23/05/2013
 * 
 *         Released under the CRAPL License
 *         http://matt.might.net/articles/crapl/
 */
public class RandomRectilinearFactory {
	/**
	 * 
	 * @param points
	 *            Number of random points
	 * @param dimension
	 *            Width/Height of the matrix
	 * @return A rectilinear 2D polygon
	 */
	public static Polygon2D generateRectilinearPolygon(int points, int dimension) {

		List<Point2D> randomList = new ArrayList<Point2D>(points);
		for (int i = 0; i < points; i++) {
			randomList.add(randomPoint(dimension));
		}
		GrahamScan2D graham = new GrahamScan2D();
		Polygon2D randomConvexPolygon = graham.convexHull(randomList);

		return convex2Rectilinear(randomConvexPolygon);
	}

	

	/**
	 * Randomly convert a convex polygon into a rectilinear polygon
	 * 
	 * @param randomConvexPolygon
	 * @return
	 */
	private static Polygon2D convex2Rectilinear(Polygon2D randomConvexPolygon) {
		Iterator<? extends LineSegment2D> edges = randomConvexPolygon.edges()
				.iterator();
		SimplePolygon2D rectilinear = new SimplePolygon2D();
		System.out.println(randomConvexPolygon.vertices());
		while (edges.hasNext()) {
			LineSegment2D edge = edges.next();
			Point2D first = edge.firstPoint();
			Point2D last = edge.lastPoint();
			fillRectilinear(rectilinear, first, last);
		}
		return rectilinear;
	}

	/**
	 * Add a rectilinear path between first and last into the rectilinear
	 * polygon
	 * 
	 * @param rectilinear
	 * @param first
	 * @param last
	 */
	private static void fillRectilinear(SimplePolygon2D rectilinear,
			Point2D first, Point2D last) {
		if (first.getX() != last.getX() && first.getY() != last.getY()) {
			rectilinear.addVertex(first);
			Random r = new Random();
			// Height or width
			int delta;
			boolean inHeight = r.nextBoolean();
			if (inHeight) {
				delta = (int) Math.abs(first.getX() - last.getX());
				int add = Math.min(r.nextInt(delta) + 1,delta-1);
				add = Math.max(delta, 1);
				if (first.getX() < last.getX()) {
					rectilinear.addVertex(new Point2D(first.getX() + add, first
							.getY()));
					rectilinear.addVertex(new Point2D(first.getX() + add, last
							.getY()));
				} else {
					rectilinear.addVertex(new Point2D(first.getX() - add, first
							.getY()));
					rectilinear.addVertex(new Point2D(first.getX() - add, last
							.getY()));
				}
			} else {
				delta = (int) Math.abs(first.getY() - last.getY());
				int add = Math.min(r.nextInt(delta) + 1,delta-1);
				add = Math.max(delta, 1);
				if (first.getY() < last.getY()) {
					rectilinear.addVertex(new Point2D(first.getX(), first
							.getY() + add));
					rectilinear.addVertex(new Point2D(last.getX(), first
							.getY() + add));
				} else {
					rectilinear.addVertex(new Point2D(first.getX(), first
							.getY() - add));
					rectilinear.addVertex(new Point2D(last.getX(), first
							.getY() - add));
				}
			}
			//rectilinear.addVertex(last);
		} else {
			rectilinear.addVertex(first);
			//rectilinear.addVertex(last);
		}
	}

	/**
	 * Create a random 2D Point
	 * 
	 * @param dimension
	 * @return
	 */
	private static Point2D randomPoint(int dimension) {
		Random random = new Random();
		int x = random.nextInt(dimension);
		int y = random.nextInt(dimension);
		return new Point2D(x, y);
	}
}
