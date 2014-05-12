package fr.tse.lt2c.satin.rectilinear.parser;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;

/**
 * Converters for rectilinear polygon produced by the Cut-Inflate implementation
 * in C
 * 
 * @author Julien
 * 
 */
public class Rectilinear2BinaryMatrix {

	static int[] resolutions = { 100, 250, 500, 750, 1000, 1500, 2000, 2500,
			3000, 3500, 4000 };
	// static int[] resolutions = { 100, 250 };
	static int MAXUPSCALED = 0;
	private static boolean upscale = false;

	/**
	 * Takes a file in input, read the different polygons in the file and
	 * convert them into binary matrices
	 * 
	 * @param f
	 * @return
	 */
	public static List<BinaryMatrix> loadRectilinearFromFile(File f,
			int nbvertices) {
		List<BinaryMatrix> matrices = new ArrayList<>();
		Scanner scanner = null;
		int nbmatrix = 0;
		upscale = false;
		try {
			scanner = new Scanner(f);
			RectilinearPolygon pol = null;
			int xmax = -1, ymax = -1, currentx, currenty;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("---")) {
					if (pol == null)
						pol = new RectilinearPolygon();
					else {
						nbmatrix++;
						upscale = nbmatrix <= MAXUPSCALED ? true : false;
						pol.setXmax(xmax);
						pol.setYmax(ymax);

						// System.out.println(pol);
						BinaryMatrix tmp = convertPolygonToMatrix(pol);
						// System.out.println(tmp);
						writeMatrixToImage(tmp, new File("polygons/full/"
								+ nbvertices + "-" + nbmatrix + ".png"),
								nbmatrix, nbvertices);
						// System.out.println(tmp);
						// matrices.add(tmp);
						pol = new RectilinearPolygon();
						xmax = -1;
						ymax = -1;
					}
				} else {
					// Parse the point
					String[] values = line.split("\\s");
					currentx = Integer.parseInt(values[0]);
					currenty = Integer.parseInt(values[1]);
					if (currentx > xmax)
						xmax = currentx;
					if (currenty > ymax) {
						ymax = currenty;
					}
					pol.addPoint(new Point(currentx, currenty));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}

		return null;
	}

	public static BinaryMatrix Polygon2Matrix(Polygon2D rectilinear,
			int dimension) {
		RectilinearPolygon pol = new RectilinearPolygon();
		pol.setXmax(dimension);
		pol.setYmax(dimension);
		Collection<Point2D> vertices = rectilinear.vertices();
		for (Point2D point : vertices) {
			pol.addPoint(new Point((int) point.getX(), (int) point.getY()));
		}
		System.out.println(vertices);
		System.out.println(pol);
		return convertPolygonToMatrix(pol);
	}

	/**
	 * Convert rectilinear polygon to binary matrix
	 * 
	 * @param pol
	 * @return
	 */
	private static BinaryMatrix convertPolygonToMatrix(RectilinearPolygon pol) {
		boolean[][] matrix = new boolean[pol.getXmax() + 1][pol.getYmax() + 1];
		for (int i = 0; i < pol.getSize() - 1; i++) {
			Point begin = pol.getPoint(i);
			Point end = pol.getPoint(i + 1);
			if (sameLine(begin, end)) {
				updateline(matrix, begin, end);
			} else {
				updatecolumn(matrix, begin, end);
			}
		}
		BinaryMatrix res = new BinaryMatrix(matrix, pol.getXmax() + 1,
				pol.getYmax() + 1);
		fillPolygons(res);
		return res;
	}

	private static void updatecolumn(boolean[][] matrix, Point begin, Point end) {
		int beginX = Math.min(begin.x, end.x);
		int endX = Math.max(begin.x, end.x);
		for (int i = beginX; i < endX; i++) {
			try{
			matrix[i][begin.y] = true;
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(i);
				System.out.println(end.x);
				System.out.println(begin.x);
				System.exit(-1);
			}
		}

	}

	private static void updateline(boolean[][] matrix, Point begin, Point end) {
		int beginY = Math.min(begin.y, end.y);
		int endY = Math.max(begin.y, end.y);
		for (int i = beginY; i < endY; i++) {
			matrix[begin.x][i] = true;
		}

	}

	private static boolean sameLine(Point begin, Point end) {
		return begin.x == end.x;
	}

	/**
	 * Previous method just draws the vertices of the polygons, this method will
	 * the polygons with 1s
	 * 
	 * @param matrix
	 */
	private static void fillPolygons(BinaryMatrix matrix) {
		boolean[][] theMatrix = matrix.matrix;
		for (int i = 0; i < matrix.getCol(); i++) {
			fillLine(theMatrix[i]);
		}
	}

	public static void writeMatrixToImage(BinaryMatrix m, File f, int nbmatrix,
			int nbvertices) {
		BufferedImage before = new BufferedImage(m.getCol(), m.getRow(),
				BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < m.getCol(); i++) {
			for (int j = 0; j < m.getRow(); j++) {
				if (m.getMatrix()[i][j])
					before.setRGB(j, i, 0x000000);
				else
					before.setRGB(j, i, 0xFFFFFF);
			}
		}
		try {
			ImageIO.write(before, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (upscale) {
			for (int resolution : resolutions) {
				int upscalingFactor = resolution / m.getCol();
				int w = before.getWidth();
				int h = before.getHeight();
				BufferedImage after = new BufferedImage(upscalingFactor * w,
						upscalingFactor * h, BufferedImage.TYPE_INT_ARGB);
				AffineTransform at = new AffineTransform();
				at.scale(upscalingFactor, upscalingFactor);
				AffineTransformOp scaleOp = new AffineTransformOp(at,
						AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				after = scaleOp.filter(before, after);
				File upscaled = new File("polygons/upscaled/" + resolution
						+ "/" + nbvertices + "-" + nbmatrix + ".png");
				try {
					ImageIO.write(after, "png", upscaled);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static void fillLine(boolean[] bs) {
		boolean hasOne = false;
		int currentBegin = 0, currentEnd;
		for (int i = 0; i < bs.length; i++) {
			if (bs[i]) {
				if (!hasOne) {// begins the interval
					hasOne = true;
					currentBegin = i;
				} else {
					hasOne = false;
					currentEnd = i;

					Arrays.fill(bs, currentBegin, currentEnd, true);
				}
			}
		}
	}

	public static void main(String[] args) {
		// Create directories
		for (int res : resolutions) {
			File f = new File("polygons/upscaled/" + res + "/");
			f.mkdirs();
		}
		File f = new File("polygons/full/");
		f.mkdirs();
		for (int nbvertices = 50; nbvertices <= 240; nbvertices++) {
			// Create directory to store pictures
			File rep = new File("polygons/regular/" + nbvertices + "/");
			rep.mkdirs();
			// Load polygons from CUT INFLATE output
			f = new File("inputPolygons/output" + nbvertices + ".txt");
			loadRectilinearFromFile(f, nbvertices);
		}

	}

}
