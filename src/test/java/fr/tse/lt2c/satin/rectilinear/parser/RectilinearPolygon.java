package fr.tse.lt2c.satin.rectilinear.parser;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class RectilinearPolygon {

	List<Point> thelist;
	private int ymax;
	private int xmax;

	public RectilinearPolygon() {
		thelist = new ArrayList<>();
	}

	public void addPoint(Point p) {
		thelist.add(p);
	}

	public Point getPoint(int i) {
		return thelist.get(i);
	}

	public void setXmax(int xmax) {
		this.xmax = xmax;

	}

	public void setYmax(int ymax) {
		this.ymax = ymax;

	}

	public int getYmax() {
		return ymax;
	}

	public int getXmax() {
		return xmax;
	}

	public int getSize() {
		return thelist.size();
	}

	public RectilinearPolygon upscale(int factor) {
		RectilinearPolygon res = new RectilinearPolygon();
		res.setXmax((xmax) * factor);
		res.setYmax((ymax) * factor);

		for (Point p : thelist) {
			int transformX = upscale(p.x, factor);
			int transformY = upscale(p.y, factor);
			res.addPoint(new Point(transformX, transformY));

		}
		return res;
	}

	private int upscale(int x, int factor) {
		if (x % 2 == 0)
			return x * factor;
		else
			return x * factor + 1;

	}

	@Override
	public String toString() {
		return "RectilinearPolygon [thelist=" + thelist + "]";
	}

}
