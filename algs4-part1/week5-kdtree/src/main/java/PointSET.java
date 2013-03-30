/**
 * A mutable data type PointSET that represents a set of points in the unit square.
 * <p/>
 * The data type is based on a red-black BST and supports {@link #insert(Point2D)} and {@link #contains(Point2D)}
 * operations in time proportional to the logarithm of the number of points in the set in the worst case;
 * {@link #nearest(Point2D)} and {@link #range(RectHV)} in time proportional to the number of points in the set.
 */
public class PointSET {

    private SET<Point2D> points;

    /**
     * Construct an empty set of points.
     */
    public PointSET() {
        points = new SET<Point2D>();
    }

    /**
     * @return is the set empty?
     */
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /**
     * @return number of points in the set.
     */
    public int size() {
        return points.size();
    }

    /**
     * Add the point p to the set (if it is not already in the set).
     *
     * @param p the point to add.
     */
    public void insert(Point2D p) {
        if (!points.contains(p)) {
            points.add(p);
        }
    }

    /**
     * Does the set contain the point p?
     *
     * @param p the point to be checked.
     * @return <code>true</code> if the set contains the point p, <code>false</code> otherwise.
     */
    public boolean contains(Point2D p) {
        return points.contains(p);
    }

    /**
     * Draw all of the points to standard draw.
     */
    public void draw() {
        StdDraw.setScale(0, 1);

        // FAQ: How should I set the size and color of the points when drawing?
        // http://coursera.cs.princeton.edu/algs4/checklists/kdtree.html
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);

        for (Point2D p : points) {
            p.draw();
        }
    }

    /**
     * All points in the set that are inside the rectangle.
     *
     * @param rect the query rectangle.
     * @return all points in the set that are inside the rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> pointsInRect = new Queue<Point2D>();
        for (Point2D p : points) {
            if (rect.contains(p)) {
                pointsInRect.enqueue(p);
            }
        }

        return pointsInRect;
    }

    /**
     * A nearest neighbor in the set to p; null if set is empty.
     *
     * @param p the point to find a nearest neighbor to.
     * @return a nearest neighbor in the set to p; null if set is empty.
     */
    public Point2D nearest(Point2D p) {
        Point2D nearestPoint = null;
        double nearestDist = Double.MAX_VALUE;

        for (Point2D q : points) {
            double dist = p.distanceTo(q);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearestPoint = q;
            }
        }

        return nearestPoint;
    }

}
