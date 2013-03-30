import java.util.Arrays;

/**
 * A program Brute.java examines 4 points at a time and checks
 * whether they all lie on the same line segment, printing out any such
 * line segments to standard output and drawing them using standard drawing.
 * To check whether the 4 points p, q, r, and s are collinear, it checks whether
 * the slopes between p and q, between p and r, and between p and s are all equal.
 * <p/>
 * The order of growth of the running time is  N^4 in the worst case and uses space
 * proportional to N.
 * <p/>
 * Execution:
 * <pre>
 *     % java Brute input.txt
 * </pre>
 *
 * @see Fast
 */
public class Brute {

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
        doBrute(points);

        // display to screen all at once
        StdDraw.show(0);
    }

    // iterate through all combinations of 4 points (N choose 4)
    // and check if the 4 points are collinear
    private static void doBrute(Point[] points) {
        int N = points.length;
        for (int pIdx = 0; pIdx < N; pIdx++) {
            // choose the 1st point
            Point p = points[pIdx];

            for (int qIdx = pIdx + 1; qIdx < N; qIdx++) {
                // choose the 2nd point
                Point q = points[qIdx];

                for (int rIdx = qIdx + 1; rIdx < N; rIdx++) {
                    // choose the 3rd point
                    Point r = points[rIdx];

                    double pqSlope = p.slopeTo(q);
                    double prSlope = p.slopeTo(r);
                    // don't need to consider whether 4 points are collinear if we already know
                    // that the first 3 are not collinear; this can save us a factor of N on typical inputs
                    if (Double.compare(pqSlope, prSlope) != 0) {
                        continue;
                    }

                    for (int sIdx = rIdx + 1; sIdx < N; sIdx++) {
                        // choose the 4th point
                        Point s = points[sIdx];

                        double psSlope = p.slopeTo(s);
                        // 4 points (p, q, r, s) are collinear
                        if (Double.compare(pqSlope, psSlope) == 0) {
                            printAndDrawLineSegment(p, q, r, s);
                        }
                    }
                }
            }
        }
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

    private static void printAndDrawLineSegment(Point... points) {
        // To draw the line segment, we need to know the endpoints.
        // One approach is to print out a line segment only if the 4 points are
        // in ascending order  (say, relative to the natural order),
        // in which case, the endpoints are the first and last points.
        Arrays.sort(points);
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
