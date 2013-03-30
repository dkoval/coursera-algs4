/**
 * A data type modelling a percolation system.
 */
public class Percolation {

    /**
     * The N-by-N grid of sites. Each site is either open (true) or blocked (false).
     */
    private final boolean open[][];

    /**
     * The N-by-N grid dimension.
     */
    private final int N;

    /**
     * The find-union object implementation used
     * for the "percolation" problem purposes.
     */
    private WeightedQuickUnionUF percolationHandle;

    /**
     * This additional find-union object aims to solve the so-called "backwash" problem.
     */
    private WeightedQuickUnionUF backwashHandle;

    /**
     * Create N-by-N grid, with all sites blocked.
     *
     * @param N the N-by-N grid dimension.
     */
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("The N-by-N grid dimension must be a positive number");
        }

        this.open = new boolean[N][N];
        this.N = N;

        // the total number of sites to be processed by the algorithm is:
        // N-by-N grid + virtual-top site + virtual-bottom site = N^2 + 2
        this.percolationHandle = new WeightedQuickUnionUF(N * N + 2);
        this.backwashHandle = new WeightedQuickUnionUF(N * N + 2);
    }

    private int virtualTopSiteIndex() {
        return 0;
    }

    private int virtualBottomSiteIndex() {
        return N * N + 1;
    }

    /**
     * Ensure that that valid row and column indices are integers between 1 and N.
     *
     * @param i the row index.
     * @param j the column index.
     * @throws IndexOutOfBoundsException if either i or j is outside [1; N] range.
     */
    private void validateIndices(int i, int j) throws IndexOutOfBoundsException {
        if (!isValidIndex(i) || !isValidIndex(j)) {
            throw new IndexOutOfBoundsException("The API specifies that valid row and column indices are between 1 and N. " +
                    "But got row index i = " + i + ", column index j = " + j);
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 1 && index <= N;
    }

    /**
     * A scheme for uniquely mapping 2D coordinates to 1D coordinates.
     * Note that both i and j indices must greater than or equal to 1.
     *
     * @param i the row index.
     * @param j the column index.
     * @return the 1D representation of the 2D site (i, j) coordinates.
     */
    private int convertTo1DIndex(int i, int j) {
        return (i - 1) * N + j;
    }

    /**
     * Open site (row i, column j) if it is not already.
     *
     * @param i the row index.
     * @param j the column index.
     */
    public void open(int i, int j) {
        validateIndices(i, j);
        if (isOpen(i, j)) {
            return;
        }

        // mark the site as open & link the site in question
        // to its open neighbors (up to 4 calls to percolationHandle.union())
        open[i - 1][j - 1] = true;
        int siteIndex = convertTo1DIndex(i, j);

        // connect sites from the top row to the virtual top-site
        if (i == 1) {
            percolationHandle.union(virtualTopSiteIndex(), siteIndex);
            backwashHandle.union(virtualTopSiteIndex(), siteIndex);
        }

        // site (i, j) "up" open neighbor
        if (i > 1 && isOpen(i - 1, j)) {
            percolationHandle.union(siteIndex, convertTo1DIndex(i - 1, j));
            backwashHandle.union(siteIndex, convertTo1DIndex(i - 1, j));
        }

        // site (i, j) "down" open neighbor
        if ((i < N) && isOpen(i + 1, j)) {
            percolationHandle.union(siteIndex, convertTo1DIndex(i + 1, j));
            backwashHandle.union(siteIndex, convertTo1DIndex(i + 1, j));
        }

        // connect sites from the bottom row to the virtual bottom-site.
        if (i == N) {
            percolationHandle.union(virtualBottomSiteIndex(), siteIndex);
        }

        // site (i, j) "left" open neighbor
        if (j > 1 && isOpen(i, j - 1)) {
            percolationHandle.union(siteIndex, convertTo1DIndex(i, j - 1));
            backwashHandle.union(siteIndex, convertTo1DIndex(i, j - 1));
        }

        // site (i, j) "right" open neighbor
        if (j < N && isOpen(i, j + 1)) {
            percolationHandle.union(siteIndex, convertTo1DIndex(i, j + 1));
            backwashHandle.union(siteIndex, convertTo1DIndex(i, j + 1));
        }
    }

    /**
     * Is site (row i, column j) open?
     *
     * @param i the row index.
     * @param j the column index.
     * @return whether the site (i, j) is open or not.
     */
    public boolean isOpen(int i, int j) {
        validateIndices(i, j);
        return open[i - 1][j - 1];
    }

    /**
     * Is site (row i, column j) full?
     * <p/>
     * A full site is an open site that can be connected to an open site in the top row via a chain of
     * neighboring (left, right, up, down) open sites.
     *
     * @param i the row index.
     * @param j the column index.
     * @return whether the site (i, j) is open or not.
     */
    public boolean isFull(int i, int j) {
        validateIndices(i, j);
        return backwashHandle.connected(virtualTopSiteIndex(), convertTo1DIndex(i, j));
    }

    /**
     * Does the system percolate?
     * <p/>
     * We say the system percolates if there is a full site in the bottom row.
     * In other words, a system percolates if we fill all open sites connected to
     * the top row and that process fills some open site on the bottom row.
     *
     * @return true if the system percolates.
     */
    public boolean percolates() {
        return percolationHandle.connected(virtualTopSiteIndex(), virtualBottomSiteIndex());
    }

}