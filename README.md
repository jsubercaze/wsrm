# WSRM Algorithm 

The WSRM algorithm is an heuristic for the decomposition of binary matrix into non overlapping rectangles. It is a linear time approach
that produces good decomposition, especially for rectilinear polygons. It is a suitable alternative to the standard IBR algorithm.


![Matrix decomposition](http://satin-ppl.telecom-st-etienne.fr/jsubercaze/img/sea16.png)

# Getting started

Create a binary matrix from a picture :
```java
BinaryMatrix matrix = MatrixParser.binaryMatrixFromImage(new File(
				"mypicture.png"));

```
or from a text file :
```java
BinaryMatrix matrix = MatrixParser.binaryMatrixFromFile(new File(
					"matrix.txt"));
```

or from a boolean[][] :
```java
int row = ...;
int col = ...;
boolean[][] matrix= ...;
BinaryMatrix matrix = new BinaryMatrix(matrix,row,col);
```

or a random matrix, with a given percentage of 1-entries :
```java
BinaryMatrix matrix = new BinaryMatrix(matrix,row,col,percentage);
```


Then decompose the matrix into rectangles, 1 is the size of the smallest rectangle autorised :
```java
WSMRDecomposer extractor = new WSMRDecomposer(matrix);
DecompositionResult extractionResult = extractor.maximumDisjointRectangles(1);
List<SortableRectangle> rectangles = extractionResult.getRectangles();
```


## Testing the algorithm

To run the test, use the following classes :

* TestRectangleExtraction : verify the algorithm by deconstructing and rebuilding a binary matrix.
* TestSavedImage : show samples of extraction on images. Sample are given in the pictures directory.

### Finding bugs

If you happen to modify the source code, use the FindBugsOptim to generate random matrices and test the validity of the decomposition. The method halts if a problem is encountered, runs forever otherwise !

## Citation

```latex
@inproceedings{DBLP:conf/wea/SubercazeGR16,
  author    = {Julien Subercaze and
               Christophe Gravier and
               Pierre{-}Olivier Rocher},
  editor    = {Andrew V. Goldberg and
               Alexander S. Kulikov},
  title     = {A Merging Heuristic for the Rectangle Decomposition of Binary Matrices},
  booktitle = {Experimental Algorithms - 15th International Symposium, {SEA} 2016,
               St. Petersburg, Russia, June 5-8, 2016, Proceedings},
  series    = {Lecture Notes in Computer Science},
  volume    = {9685},
  pages     = {310--325},
  publisher = {Springer},
  year      = {2016},
  url       = {https://doi.org/10.1007/978-3-319-38851-9\_21},
  doi       = {10.1007/978-3-319-38851-9\_21},
  timestamp = {Tue, 14 May 2019 10:00:42 +0200},
  biburl    = {https://dblp.org/rec/conf/wea/SubercazeGR16.bib},
  bibsource = {dblp computer science bibliography, https://dblp.org}
}
