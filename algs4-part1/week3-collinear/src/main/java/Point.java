import java.util.Comparator;

/**
 * An immutable data type for points in the plane.
 */
public class Point implements Comparable<Point> {

    /**
     * Compare points by slope to this point.
     */
    public final Comparator<Point> SLOPE_ORDER = new SlopeOrder();

    /**
     * x-coordinate.
     */
    private final int x;

    /**
     * y-coordinate.
     */
    private final int y;

    /**
     * Construct the point (x, y).
     *
     * @param x the x-coordinate of this point.
     * @param y the y-coordinate of this point.
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Plot this point to standard drawing.
     */
    public void draw() {
        StdDraw.point(x, y);
    }

    /**
     * Draw line between this point and that point to standard drawing.
     *
     * @param that the other point to draw the line segment to.
     */
    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * @return string representation of this point.
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Is this point lexicographically smaller than that point?
     * <p/>
     * The method compares points by their y-coordinates, breaking ties by their x-coordinates.
     * Formally, the invoking point (x0, y0) is less than the argument point (x1, y1) if and only if
     * either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that the Point object to be compared.
     * @return a negative integer, zero, or a positive integer as this Point object
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Point that) {
        return (this.y == that.y) ? (this.x - that.x) : (this.y - that.y);
    }

    /**
     * The slope between this point (x0, y0) and that point (x1, y1),
     * which is given by the formula (y1 − y0) / (x1 − x0).
     * <p/>
     * The slope of a horizontal line segment is treated as positive zero;
     * the slope of a vertical line segment is treated as positive infinity;
     * the slope of a degenerate line segment (between a point and itself) is treated as negative infinity.
     *
     * @param that the other Point object.
     * @return the slope between this point and that point.
     */
    public double slopeTo(Point that) {
        if (this.y == that.y) {
            if (this.x == that.x) {
                // the slope of a degenerate line segment is treated as negative infinity
                return Double.NEGATIVE_INFINITY;
            }
            // the slope of a horizontal line segment is treated as positive zero
            return 0.0;
        }

        if (this.x == that.x) {
            // the slope of a vertical line segment is treated as positive infinity
            return Double.POSITIVE_INFINITY;
        }

        return ((double) that.y - this.y) / (that.x - this.x);
    }

    /**
     * Compare points by the slopes they make with the invoking point (x0, y0).
     * <p/>
     * Formally, the point (x1, y1) is less than the point (x2, y2) if and only if
     * the slope (y1 − y0) / (x1 − x0) is less than the slope (y2 − y0) / (x2 − x0).
     */
    private final class SlopeOrder implements Comparator<Point> {

        @Override
        public int compare(Point point1, Point point2) {
            double slopeToPoint1 = slopeTo(point1);
            double slopeToPoint2 = slopeTo(point2);
            return Double.compare(slopeToPoint1, slopeToPoint2);
        }

    }

}
