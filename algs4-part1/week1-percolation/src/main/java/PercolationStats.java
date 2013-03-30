/**
 * A data type to keep results of a series of computational experiments.
 */
public class PercolationStats {

    /**
     * results[t] is the fraction of open sites in computational experiment t.
     */
    private final double[] results;

    /**
     * Perform T independent computational experiments on an N-by-N grid.
     *
     * @param N the N-by-N grid dimension.
     * @param T the number of computational experiments.
     * @throws IllegalArgumentException if either N <= 0 or T <= 0.
     */
    public PercolationStats(int N, int T) throws IllegalArgumentException {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("Both N and T arguments must be positive integers. " +
                    "But got N = " + N + ", T = " + T);
        }

        this.results = new double[T];
        performMonteCarloSimulation(N, T);
    }

    /**
     * Perform Monte Carlo simulation T times on an N-by-N grid to estimate
     * the value of the percolation threshold.
     *
     * @param N the N-by-N grid dimension.
     * @param T the number of computational experiments.
     */
    private void performMonteCarloSimulation(int N, int T) {
        // perform T independent computational experiments
        for (int t = 0; t < T; t++) {
            int openSitesNum = singleMonteCarloSimulation(N);
            // increment the fraction of open sites in computational experiment t
            results[t] = (double) openSitesNum / (N * N);
        }
    }

    /**
     * Perform single Monte Carlo simulation on an N-by-N grid.
     *
     * @param N the N-by-N grid dimension.
     * @return the number of sites opened in this experiment.
     */
    private int singleMonteCarloSimulation(int N) {
        // initialize all sites to be blocked
        Percolation percolation = new Percolation(N);
        // no open sites yet
        int openSitesNum = 0;

        // repeat the following until the system percolates
        do {
            // choose a site (row i, column j) uniformly at random among all blocked sites
            // by using StdRandom to generate two integers between 1 and N and use this site
            // if it is blocked; if not, repeat
            int i, j;
            do {
                i = StdRandom.uniform(1, N + 1);
                j = StdRandom.uniform(1, N + 1);
            } while (percolation.isOpen(i, j));

            // open the site (row i, column j)
            percolation.open(i, j);
            openSitesNum++;
        } while (!percolation.percolates());

        return openSitesNum;
    }

    /**
     * @return Sample mean of percolation threshold.
     */
    public double mean() {
        return StdStats.mean(results);
    }

    /**
     * @return Sample standard deviation of percolation threshold.
     */
    public double stddev() {
        return StdStats.stddev(results);
    }

    /**
     * @return Lower bound of the 95% confidence interval.
     */
    public double confidenceLo() {
        return mean() - delta();
    }

    private double delta() {
        return 1.96 * stddev() / Math.sqrt(results.length);
    }

    /**
     * @return Upper bound of the 95% confidence interval.
     */
    public double confidenceHi() {
        return mean() + delta();
    }

    /**
     * Execution:
     * <pre>
     * % java PercolationStats 200 100
     * mean                    = 0.5929934999999997
     * stddev                  = 0.00876990421552567
     * 95% confidence interval = 0.5912745987737567, 0.5947124012262428
     * </pre>
     *
     * @param args args[0] is the N-by-N grid dimension, args[1] is the number of independent computational experiments.
     */
    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

        PercolationStats percolationStats = new PercolationStats(N, T);
        StdOut.printf("mean                    = %.16f\n", percolationStats.mean());
        StdOut.printf("stddev                  = %.16f\n", percolationStats.stddev());
        StdOut.printf("95%% confidence interval = %.16f, %.16f", percolationStats.confidenceLo(), percolationStats.confidenceHi());
    }

}