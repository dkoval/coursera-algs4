import java.util.Arrays;

/**
 * A program Fast.java implements a faster, sorting-based solution compared to that of {@link Brute}.
 * The order of growth of the running time of is  N^2 * log N in the worst case and uses space
 * proportional to N.
 * <p/>
 * Execution:
 * <pre>
 *     % java Fast input.txt
 * </pre>
 *
 * @see Brute
 */
public class Fast {

    private static final int MIN_NUM_OF_COLLINEAR_POINTS = 3;

    public static void main(String[] args) {
        // setup drawing
        setupPlot();

        // read in the input
        In filename = new In(args[0]);
        // total number of points
        int N = filename.readInt();
        // actual points
        Point[] points = readAndDrawPoints(filename, N);

        // not entirely necessary;
        // the only reason is to make the resulting output
        // much more readable
        Arrays.sort(points);

        // run the algorithm
        doFast(points);

        // display to screen all at once
        StdDraw.show(0);
    }

    private static void setupPlot() {
        // rescale coordinates and turn on animation mode
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.show(0);
    }

    private static Point[] readAndDrawPoints(In in, int N) {
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            Point point = new Point(x, y);
            points[i] = point;
            point.draw();
        }
        return points;
    }

    private static void doFast(Point[] points) {
        int N = points.length;
        Point[] slopeOrderedPoints = new Point[N];
        System.arraycopy(points, 0, slopeOrderedPoints, 0, N);

        for (Point p : points) {
            // think of p as the origin
            // for each other point q, determine the slope it makes with p;
            // sort the points according to the slopes they makes with p
            Arrays.sort(slopeOrderedPoints, p.SLOPE_ORDER);

            // check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p.
            // If so, these points, together with p, are collinear.

            // line segment boundaries
            int lineSegmentStartIndex = 0, lineSegmentLength = 0;
            // last slope slope with respect to p
            double lastSlope = p.slopeTo(slopeOrderedPoints[0]);

            // look for line segments
            for (int k = 1; k < N; k++) {
                double currentSlope = p.slopeTo(slopeOrderedPoints[k]);

                if (Double.compare(currentSlope, lastSlope) == 0) {
                    // same line segment
                    lineSegmentLength++;
                } else {
                    // detected a new line segment
                    if (lineSegmentLength >= MIN_NUM_OF_COLLINEAR_POINTS) {
                        handleLineSegment(slopeOrderedPoints, p, lineSegmentStartIndex, lineSegmentLength);
                    }

                    lineSegmentStartIndex = k;
                    lineSegmentLength = 1;
                    lastSlope = currentSlope;
                }
            }

            // detected a continual line segment which had not yet been handled within the previous 'for' loop
            if (lineSegmentLength >= MIN_NUM_OF_COLLINEAR_POINTS) {
                handleLineSegment(slopeOrderedPoints, p, lineSegmentStartIndex, lineSegmentLength);
            }
        }
    }

    private static void handleLineSegment(Point[] slopeOrderedPoints, Point p,
                                          int lineSegmentStartIndex, int lineSegmentLength) {

        Point[] lineSegmentPoints = new Point[lineSegmentLength + 1];
        System.arraycopy(slopeOrderedPoints, lineSegmentStartIndex, lineSegmentPoints, 0, lineSegmentLength);
        lineSegmentPoints[lineSegmentLength] = p;

        // To draw the line segment, we need to know the endpoints.
        // One approach is to print out a line segment only if the 4 or more points are
        // in ascending order  (say, relative to the natural order),
        // in which case, the endpoints are the first and last points.
        Arrays.sort(lineSegmentPoints);
        // The check below aims at handling line subsegments.
        // It ensures that after sorting the lineSegmentPoints array
        // in natural order the first element is still the given origin point p.
        if (p.compareTo(lineSegmentPoints[0]) == 0) {
            printAndDrawLineSegment(lineSegmentPoints);
        }
    }

    private static void printAndDrawLineSegment(Point[] points) {
        int N = points.length;

        // the first element is always the origin
        Point p = points[0];

        // print the line segment
        StdOut.print(p);
        for (int i = 1; i < N; i++) {
            StdOut.printf(" -> %s", points[i]);
        }
        StdOut.println();

        // draw the line segment
        p.drawTo(points[N - 1]);
    }

}