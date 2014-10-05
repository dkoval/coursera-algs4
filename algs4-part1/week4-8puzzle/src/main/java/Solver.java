/**
 * The '8-puzzle problem' solver that reads a puzzle from a file (specified as a command-line argument)
 * and prints the solution to standard output.
 * <p/>
 * <b>Input and output formats.</b> The input and output format for a board is the board dimension N
 * followed by the N-by-N initial board, using 0 to represent the blank square. As an example,
 * <pre>
 *     % more puzzle04.txt
 *     3
 *      0  1  3
 *      4  2  5
 *      7  8  6
 * </pre>
 * <p/>
 * Execution:
 * <pre>
 *     % java Solver puzzle04.txt
 * </pre>
 */
public class Solver {

    private SearchNode result;

    /**
     * A search node of the game.
     */
    private static class SearchNode implements Comparable<SearchNode> {

        /**
         * The board itself.
         */
        private final Board board;

        /**
         * The number of moves made to reach the board.
         */
        private final int moves;

        /**
         * The previous search node.
         */
        private final SearchNode previous;

        /**
         * Manhattan priority of this search node is
         * the sum of Manhattan distances between blocks and goal,
         * plus the number of moves made so far to get to the search node.
         */
        private final int priority;

        private SearchNode(Board board, SearchNode previous) {
            this.board = board;
            this.previous = previous;
            this.moves = (this.previous == null) ? 0 : this.previous.moves + 1;
            this.priority = this.board.manhattan() + this.moves;
        }

        @Override
        public int compareTo(SearchNode that) {
            return this.priority - that.priority;
        }
    }

    /**
     * Find a solution to the initial board (using the A* algorithm).
     *
     * @param initial the initial board.
     */
    public Solver(Board initial) {
        // run the A* algorithm simultaneously on two puzzle instances — one with the initial board
        // and one with the initial board modified by swapping a pair of adjacent blocks in the same row.
        // Exactly one of the two will lead to the goal board.
        result = initial.isGoal() ? new SearchNode(initial, null) : solve(initial, initial.twin());
    }

    // 'Best-first search' solution to the problem that illustrates a general artificial intelligence methodology
    // known as the A* search algorithm.
    private SearchNode solve(Board initial, Board twin) {
        MinPQ<SearchNode> mainPQ = new MinPQ<SearchNode>();
        // This PQ is used for detecting infeasible puzzles.
        // It keeps track of boards that lead to the goal board if we modify
        // the initial board by swapping any pair of adjacent (non-blank) blocks in the same row.
        MinPQ<SearchNode> twinPQ = new MinPQ<SearchNode>();

        // First, insert the initial search node (the initial board, 0 moves, and a null previous search node)
        // into a priority queue.
        mainPQ.insert(new SearchNode(initial, null));
        twinPQ.insert(new SearchNode(twin, null));

        // Repeat until the search node dequeued corresponds to a goal board.
        while (true) {
            SearchNode sn = step(mainPQ);
            if (sn.board.isGoal()) {
                return sn;
            }

            if (step(twinPQ).board.isGoal()) {
                return null;
            }
        }
    }

    private SearchNode step(MinPQ<SearchNode> pq) {
        // Then, delete from the priority queue the search node with the minimum priority,
        // and insert onto the priority queue all neighboring search nodes
        // (those that can be reached in one move from the dequeued search node).
        SearchNode least = pq.delMin();
        for (Board neighbor : least.board.neighbors()) {
            // A critical optimization.
            // Best-first search has one annoying feature: search nodes corresponding to the same board
            // are enqueued on the priority queue many times.
            // To reduce unnecessary exploration of useless search nodes, when considering the neighbors of
            // a search node, don't enqueue a neighbor if its board is the same as the board of the previous search node.
            if (least.previous == null || !neighbor.equals(least.previous.board)) {
                pq.insert(new SearchNode(neighbor, least));
            }
        }
        return least;
    }

    /**
     * @return is the initial board solvable?
     */
    public boolean isSolvable() {
        return (result != null);
    }

    /**
     * @return min number of moves to solve initial board; -1 if no solution.
     */
    public int moves() {
        return (result != null) ? result.moves : -1;
    }

    /**
     * @return sequence of boards in a shortest solution; null if no solution.
     */
    public Iterable<Board> solution() {
        if (result == null) {
            return null;
        }

        Stack<Board> solution = new Stack<Board>();
        for (SearchNode sn = result; sn != null; sn = sn.previous) {
            solution.push(sn.board);
        }
        return solution;
    }

    /**
     * Solve a slider puzzle (given below).
     *
     * @param args the filename containing board description.
     */
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }
}
