package fr.tse.lt2c.satin.testdecompo;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import au.com.bytecode.opencsv.CSVWriter;
import fr.tse.lt2c.satin.matrix.beans.BinaryMatrix;
import fr.tse.lt2c.satin.matrix.beans.DecompositionResult;
import fr.tse.lt2c.satin.matrix.extraction.ibr.IBRNonLinear;
import fr.tse.lt2c.satin.matrix.extraction.linkedmatrix.refactored.WSMRDecomposer;
import fr.tse.lt2c.satin.rectilinear.parser.RectilinearPolygon;

/**
 * Converters for rectilinear polygon produced by the Cut-Inflate implementation
 * in C
 * 
 * @author Julien
 * 
 */
public class CompareDecompoNewVersion {

	private static boolean DEBUG = false;
	private static boolean LARGEST = true;

	/**
	 * Takes a file in input, read the different polygons in the file and
	 * convert them into binary matrices
	 * 
	 * @param f
	 * @return
	 */
	public static DecompoResult loadRectilinearFromFile(File f, int nbvertices) {

		Scanner scanner = null;

		int better = 0;
		int equals = 0;
		int total = 0;
		DescriptiveStatistics statsMeanIBR = new DescriptiveStatistics();
		DescriptiveStatistics statsSTDIBR = new DescriptiveStatistics();
		DescriptiveStatistics statsMeanWASM = new DescriptiveStatistics();
		DescriptiveStatistics statsSTDWASM = new DescriptiveStatistics();
		DescriptiveStatistics statsAreaIBR = new DescriptiveStatistics();
		DescriptiveStatistics statsAreaWASM = new DescriptiveStatistics();
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

						pol.setXmax(xmax);
						pol.setYmax(ymax);
						// System.out.println(pol);
						BinaryMatrix tmp = convertPolygonToMatrix(pol);
						// System.out.println(tmp);
						// if(total>10)
						// DEBUG=true;
						IBRNonLinear ibr = new IBRNonLinear(tmp);
						DecompositionResult iBResult = ibr.extractDisjointRectangles();
						iBResult.initStats();
						int ibrectangles = iBResult.getRectangle().size();
						statsMeanIBR.addValue(iBResult.getRectangleRatioMean());
						statsSTDIBR.addValue(iBResult.getRectangleRatioStd());
						statsAreaIBR.addValue(iBResult.getRectanglesAreaStd());
						WSMRDecomposer optim = new WSMRDecomposer(
								tmp);
						DecompositionResult wASMResult = optim.computeDecomposition(1);
						wASMResult.initStats();
						statsMeanWASM.addValue(wASMResult
								.getRectangleRatioMean());
						statsSTDWASM
								.addValue(wASMResult.getRectangleRatioStd());
						statsAreaWASM.addValue(wASMResult
								.getRectanglesAreaStd());
						int wasm = wASMResult.getRectangle().size();
						++total;
						// System.out.println(total + " " + ibrectangles + " "
						// + wasm);
						// litUneLigne();
						// if (total % 100 == 0)
						// System.out.println(total);
						if (wasm < ibrectangles)
							++better;
						if (wasm == ibrectangles)
							++equals;
						xmax = -1;
						ymax = -1;
						pol = new RectilinearPolygon();
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
		DecompoResult decompo = new DecompoResult(
				((double) better / (double) total),
				((double) equals / (double) total), statsMeanIBR.getMean(),
				statsSTDIBR.getMean(), statsMeanWASM.getMean(),
				statsSTDWASM.getMean(), statsAreaIBR.getMean(),
				statsAreaWASM.getMean());
		return decompo;
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
			if (DEBUG) {
				System.out.println(begin + " " + end);
				System.out.println(matrixToString(matrix));
				litUneLigne();
			}
		}
		BinaryMatrix res = new BinaryMatrix(matrix, pol.getXmax() + 1,
				pol.getYmax() + 1);
		// if (LARGEST)
		fillPolygons(res);
		return res;
	}

	private static void updatecolumn(boolean[][] matrix, Point begin, Point end) {
		int beginX = Math.min(begin.x, end.x);
		int endX = Math.max(begin.x, end.x);
		for (int i = beginX; i <= endX; i++) {
			matrix[i][begin.y] = true;
		}

	}

	private static void updateline(boolean[][] matrix, Point begin, Point end) {
		int beginY = Math.min(begin.y, end.y);
		int endY = Math.max(begin.y, end.y);
		for (int i = beginY; i <= endY; i++) {
			matrix[begin.x][i] = true;
		}

	}

	private static boolean sameLine(Point begin, Point end) {
		return begin.x == end.x;
	}

	public static String litUneLigne() {
		System.out.println("Appuyez sur entrée pour continuer");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			return reader.readLine();
		} catch (IOException e) {
			System.err.println("Erreur de lecture I/O, fermeture du programme");
			// A Ã©viter, mais vous verrez les exceptions dans un prochain TP
			return null;
		}
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

	public static String matrixToString(boolean[][] matrix) {
		// Better output than deepToString
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {
			s.append(Arrays.toString(matrix[i]) + "\n");
		}
		return s.toString().replaceAll("false", "0").replaceAll("true", "1");
	}

	public static void main(String[] args) throws ExecuteException, IOException {
		// Prepare CSV

		CSVWriter writerFill = new CSVWriter(
				new FileWriter("resultLargest.csv"), ';');
		String[] entries = new String[10];
		entries[0] = "Vertices";
		entries[1] = "Worst";
		entries[2] = "Equals";
		entries[3] = "Better";
		entries[4] = "RatioMeanIBR";
		entries[5] = "RatioStdIBR";
		entries[6] = "RatioMeanWASM";
		entries[7] = "RatioStdWASM";
		entries[8] = "AreaSTDIBR";
		entries[9] = "AreaSTDWASM";
		writerFill.writeNext(entries);
		CSVWriter writerNoFill = new CSVWriter(new FileWriter(
				"resultNoLargest.csv"), ';');
		writerNoFill.writeNext(entries);
		// Start the project
		int nbVerticesBegin = Integer.parseInt(args[0]);
		int nbVerticesEnd = Integer.parseInt(args[1]);
		int nbPoly = Integer.parseInt(args[2]);
		for (int nbvertices = nbVerticesBegin; nbvertices <= nbVerticesEnd; nbvertices++) {
			String line;
			if (isUnix(System.getProperty("os.name").toLowerCase())) {
				line = "./Polygon";
			} else {
				line = "Polygon";
			}
			CommandLine cmdLine = CommandLine.parse(line);
			cmdLine.addArgument("" + nbvertices);
			cmdLine.addArgument("" + nbPoly);
			DefaultExecutor executor = new DefaultExecutor();
			int exitValue = executor.execute(cmdLine);
			// System.out.println("Nbvertices " + nbvertices);
			File f = new File("output" + nbvertices + ".txt");
			LARGEST = true;
			DecompoResult res = loadRectilinearFromFile(f, nbvertices);
			double percBetter = res.getBetter() * 100;
			double percEquals = res.getEquals() * 100;
			double percWorst = 100 - percBetter - percEquals;
			entries = new String[10];
			entries[0] = "" + nbvertices;
			entries[1] = String.format(Locale.FRANCE, "%f", percWorst);
			entries[2] = String.format(Locale.FRANCE, "%f", percEquals);
			entries[3] = String.format(Locale.FRANCE, "%fx", percBetter);
			entries[4] = String.format(Locale.FRANCE, "%f",
					res.getRatioAverageIBR());
			entries[5] = String.format(Locale.FRANCE, "%f",
					res.getRatioStdIBR());
			entries[6] = String.format(Locale.FRANCE, "%f",
					res.getRatioAverageWASM());
			entries[7] = String.format(Locale.FRANCE, "%f",
					res.getRatioStdWASM());
			entries[8] = String
					.format(Locale.FRANCE, "%f", res.getAreaStdIBR());
			entries[9] = String.format(Locale.FRANCE, "%f",
					res.getAreaStdWASM());
			writerFill.writeNext(entries);
			LARGEST = false;
			res = loadRectilinearFromFile(f, nbvertices);
			percBetter = res.getBetter() * 100;
			percEquals = res.getEquals() * 100;
			percWorst = 100 - percBetter - percEquals;
			entries = new String[10];
			entries[0] = "" + nbvertices;
			entries[1] = String.format(Locale.FRANCE, "%f", percWorst);
			entries[2] = String.format(Locale.FRANCE, "%f", percEquals);
			entries[3] = String.format(Locale.FRANCE, "%fx", percBetter);
			entries[4] = String.format(Locale.FRANCE, "%f",
					res.getRatioAverageIBR());
			entries[5] = String.format(Locale.FRANCE, "%f",
					res.getRatioStdIBR());
			entries[6] = String.format(Locale.FRANCE, "%f",
					res.getRatioAverageWASM());
			entries[7] = String.format(Locale.FRANCE, "%f",
					res.getRatioStdWASM());
			entries[8] = String
					.format(Locale.FRANCE, "%f", res.getAreaStdIBR());
			entries[9] = String.format(Locale.FRANCE, "%f",
					res.getAreaStdWASM());
			writerNoFill.writeNext(entries);
			f.delete();
		}
		writerFill.close();
		writerNoFill.close();

	}

	public static boolean isUnix(String OS) {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS
				.indexOf("aix") > 0);

	}
}
