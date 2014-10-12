/**
 * A mutable data type KdTree uses a 2d-tree to implement the same API as the {@link PointSET}.
 * <p/>
 * A 2d-tree is a generalization of a BST to two-dimensional keys.
 * The idea is to build a BST with points in the nodes, using the x- and y-coordinates of the points as keys
 * in strictly alternating sequence.
 * <p/>
 * The prime advantage of a 2d-tree over a BST is that it supports efficient implementation of range search and
 * nearest neighbor search. Each node corresponds to an axis-aligned rectangle in the unit square, which encloses
 * all of the points in its subtree. The root corresponds to the unit square; the left and right children of the root
 * corresponds to the two rectangles split by the x-coordinate of the point at the root; and so forth.
 */
public class KdTree {

    private static final RectHV UNIT_SQUARE = new RectHV(0.0, 0.0, 1.0, 1.0);

    /**
     * The root of the 2d-tree.
     */
    private Node root;

    /**
     * The number of points in the tree.
     */
    private int size;

    /**
     * Represent a node in a 2d-tree.
     */
    private static class Node {

        /**
         * The point.
         */
        private Point2D p;

        /**
         * The left/bottom subtree.
         */
        private Node left;

        /**
         * The right/top subtree.
         */
        private Node right;

        /**
         * The orientation of the rectangle corresponding to this node.
         */
        private boolean vertical = true;

        public Node(Point2D p, Node left, Node right, boolean vertical) {
            this.p = p;
            this.left = left;
            this.right = right;
            this.vertical = vertical;
        }
    }

    /**
     * Construct an empty 2d-tree.
     */
    public KdTree() {
        root = null;
        size = 0;
    }

    /**
     * @return is the 2d-tree empty?
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * @return number of points in the 2d-tree.
     */
    public int size() {
        return size;
    }

    /**
     * Add the point p to the 2d-tree (if it is not already in the 2d-tree).
     *
     * @param p the point to add.
     */
    public void insert(Point2D p) {
        root = doInsert(root, p, true);
    }

    private Node doInsert(Node node, Point2D p, boolean vertical) {
        if (node == null) {
            size++;
            return new Node(p, null, null, vertical);
        }

        // do not insert the point if it is already in the 2d-tree;
        // return the existing node instead.
        if (p.equals(node.p)) {
            return node;
        }

        // at the root we use the x-coordinate (if the point to be inserted has a smaller x-coordinate
        // than the point at the root, go left; otherwise go right);
        // then at the next level, we use the y-coordinate (if the point to be inserted has a smaller y-coordinate
        // than the point in the node, go left; otherwise go right);
        // then at the next level the x-coordinate, and so forth.
        if (isSmallerThanPointInNode(p, node)) {
            node.left = doInsert(node.left, p, !node.vertical);
        } else {
            node.right = doInsert(node.right, p, !node.vertical);
        }

        return node;
    }

    private boolean isSmallerThanPointInNode(Point2D p, Node node) {
        return node.vertical ? p.x() < node.p.x() : p.y() < node.p.y();
    }

    /**
     * Does the 2d-tree contain the point p?
     *
     * @param p the point to be checked.
     * @return <code>true</code> if the 2d-tree contains the point p, <code>false</code> otherwise.
     */
    public boolean contains(Point2D p) {
        Node node = root;
        while (node != null) {
            if (p.equals(node.p)) {
                return true;
            }

            node = isSmallerThanPointInNode(p, node) ? node.left : node.right;
        }

        return false;
    }

    /**
     * Draw all of the points along with the splitting lines to standard draw.
     */
    public void draw() {
        StdDraw.setScale(0.0, 1.0);
        doDraw(root, UNIT_SQUARE);
    }

    private void doDraw(Node node, RectHV nodeRect) {
        if (node == null) {
            return;
        }

        // FAQ: How should I set the size and color of the points and rectangles when drawing?
        // http://coursera.cs.princeton.edu/algs4/checklists/kdtree.html
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);

        // draw the point
        node.p.draw();

        // draw the splitting lines
        Point2D fromPoint, toPoint;
        if (node.vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            fromPoint = new Point2D(node.p.x(), nodeRect.ymin());
            toPoint = new Point2D(node.p.x(), nodeRect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            fromPoint = new Point2D(nodeRect.xmin(), node.p.y());
            toPoint = new Point2D(nodeRect.xmax(), node.p.y());
        }
        fromPoint.drawTo(toPoint);

        doDraw(node.left, leftNodeRect(node, nodeRect));
        doDraw(node.right, rightNodeRect(node, nodeRect));

    }

    private RectHV leftNodeRect(Node node, RectHV nodeRect) {
        return node.vertical
                ? new RectHV(nodeRect.xmin(), nodeRect.ymin(), node.p.x(), nodeRect.ymax())
                : new RectHV(nodeRect.xmin(), nodeRect.ymin(), nodeRect.xmax(), node.p.y());
    }

    private RectHV rightNodeRect(Node node, RectHV nodeRect) {
        return node.vertical
                ? new RectHV(node.p.x(), nodeRect.ymin(), nodeRect.xmax(), nodeRect.ymax())
                : new RectHV(nodeRect.xmin(), node.p.y(), nodeRect.xmax(), nodeRect.ymax());
    }

    /**
     * All points in the 2d-tree that are inside the rectangle.
     *
     * @param rect the query rectangle.
     * @return all points in the set that are inside the rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> pointsInRect = new Queue<Point2D>();
        doRange(root, UNIT_SQUARE, rect, pointsInRect);
        return pointsInRect;
    }

    private void doRange(Node node, RectHV nodeRect, RectHV queryRect, Queue<Point2D> pointsInRect) {
        // To find all points contained in a given query rectangle, start at the root and recursively search for
        // points in both subtrees using the following pruning rule:
        // if the query rectangle does not intersect the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees).
        // A subtree is searched only if it might contain a point contained in the query rectangle.
        if (node == null) {
            return;
        }

        if (queryRect.intersects(nodeRect)) {
            if (queryRect.contains(node.p)) {
                pointsInRect.enqueue(node.p);
            }

            doRange(node.left, leftNodeRect(node, nodeRect), queryRect, pointsInRect);
            doRange(node.right, rightNodeRect(node, nodeRect), queryRect, pointsInRect);
        }
    }

    /**
     * A nearest neighbor in the 2d-tree to p; null if 2d-tree is empty.
     *
     * @param p the point to find a nearest neighbor to.
     * @return a nearest neighbor in the 2d-tree to p; null if 2d-tree is empty.
     */
    public Point2D nearest(Point2D p) {
        return doNearest(root, UNIT_SQUARE, p, null);
    }

    private Point2D doNearest(Node node, RectHV nodeRect, Point2D queryPoint, Point2D nearestPoint) {
        // To find a closest point to a given query point, start at the root and recursively search in both subtrees
        // using the following pruning rule:
        // if the closest point discovered so far is closer than the distance between the query point and the rectangle
        // corresponding to a node, there is no need to explore that node (or its subtrees).
        // That is, a node is searched only if it might contain a point that is closer than the best one found so far.
        // The effectiveness of the pruning rule depends on quickly finding a nearby point.
        // To do this, organize your recursive method so that when there are two possible subtrees to go down,
        // you always choose the subtree that is on the same side of the splitting line as the query point as the first
        // subtree to explore — the closest point found while exploring the first subtree may enable pruning of the
        // second subtree.
        if (node == null) {
            return nearestPoint;
        }

        Point2D nearestPointCandidate = nearestPoint;
        double nearestDist = (nearestPointCandidate != null)
                ? queryPoint.distanceSquaredTo(nearestPointCandidate)
                : Double.MAX_VALUE;

        if (nearestDist > nodeRect.distanceSquaredTo(queryPoint)) {
            double dist = queryPoint.distanceSquaredTo(node.p);
            if (dist < nearestDist) {
                nearestPointCandidate = node.p;
            }

            RectHV leftNodeRect = leftNodeRect(node, nodeRect);
            RectHV rightNodeRect = rightNodeRect(node, nodeRect);

            if (isSmallerThanPointInNode(queryPoint, node)) {
                // explore left subtree first
                nearestPointCandidate = doNearest(node.left, leftNodeRect, queryPoint, nearestPointCandidate);
                nearestPointCandidate = doNearest(node.right, rightNodeRect, queryPoint, nearestPointCandidate);
            } else {
                // explore right subtree first
                nearestPointCandidate = doNearest(node.right, rightNodeRect, queryPoint, nearestPointCandidate);
                nearestPointCandidate = doNearest(node.left, leftNodeRect, queryPoint, nearestPointCandidate);
            }
        }

        return nearestPointCandidate;
    }

}
