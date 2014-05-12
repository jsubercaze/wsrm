package fr.tse.lt2c.satin.testdecompo;

import java.util.Random;

import math.geom2d.polygon.Polygon2D;
import fr.tse.lt2c.satin.rectilinear.parser.Rectilinear2BinaryMatrix;


public class TestHullGeneration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random r = new Random();
		int size = 20 + r.nextInt(30);
		int point = 6 + r.nextInt(50);
		Polygon2D rect = RandomRectilinearFactory.generateRectilinearPolygon(
				point, size);
		System.out.println(Rectilinear2BinaryMatrix.Polygon2Matrix(rect, size));
	}

}
