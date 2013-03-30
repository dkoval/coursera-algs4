import java.util.Arrays;

/**
 * An immutable data type taking part in 8-puzzle problem solving.
 */
public class Board {

    /**
     * The N-by-N board with (N^2 - 1) square blocks labeled 1 through (N^2 - 1)
     * and a blank square.
     */
    private final int[][] blocks;

    /**
     * The board dimension.
     */
    private final int N;

    /**
     * Construct a board from an N-by-N array of blocks,
     * where blocks[i][j] = block in row i, column j).
     *
     * @param blocks the N-by-N array of blocks.
     */
    public Board(int[][] blocks) {
        this.blocks = copyOf(blocks);
        this.N = blocks.length;
    }

    private int[][] copyOf(int[][] blocks) {
        if (blocks == null) {
            return null;
        }

        int N = blocks.length;
        int[][] copy = new int[N][];
        for (int i = 0; i < N; i++) {
            copy[i] = Arrays.copyOf(blocks[i], N);
        }
        return copy;
    }

    private int[][] copyOfBlocks() {
        return copyOf(this.blocks);
    }

    /**
     * @return board dimension N.
     */
    public int dimension() {
        return N;
    }

    /**
     * @return number of blocks out of place.
     */
    public int hamming() {
        int wrongPosCounter = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] != goalPositionOf(i, j)) {
                    wrongPosCounter++;
                }
            }
        }
        // subtract 1 for the blank square
        return wrongPosCounter - 1;
    }

    private int goalPositionOf(int i, int j) {
        assert (i >= 0 && i <= N - 1) && (j >= 0 && j <= N - 1);
        return i * N + j + 1;
    }

    /**
     * @return sum of Manhattan distances between blocks and goal.
     */
    public int manhattan() {
        int sum = 0;
        int goalRow, goalCol;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int val = blocks[i][j];

                if (val == 0) {
                    // skip the blank square
                    continue;
                }

                goalRow = (val - 1) / N;
                goalCol = (val - 1) % N;
                sum += Math.abs(i - goalRow) + Math.abs(j - goalCol);
            }
        }
        return sum;
    }

    /**
     * @return is this board the goal board?
     */
    public boolean isGoal() {
        if (blocks[N - 1][N - 1] != 0) {
            return false;
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // the only valid situation where blocks[i][j] might not equal to goalPositionOf(i, j) is
                // if i = j = N - 1
                if ((blocks[i][j] != goalPositionOf(i, j)) && (i != N - 1 || j != N - 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return a board obtained by exchanging two adjacent blocks in the same row (the blank does not count).
     */
    public Board twin() {
        int[][] copy = copyOfBlocks();
        // if N is either 0 or 1
        if (N <= 1) {
            return new Board(copy);
        }

        for (int i = 0; i < N; i++) {
            int lastVal = blocks[i][0];

            for (int j = 1; j < N; j++) {
                int currentVal = blocks[i][j];

                // if neither lastVal nor val is the blank square
                if (currentVal != 0 && lastVal != 0) {
                    copy[i][j] = lastVal;
                    copy[i][j - 1] = currentVal;
                    return new Board(copy);
                }

                lastVal = currentVal;
            }
        }
        // that is hardly possible
        throw unexpectedError();
    }

    private IllegalStateException unexpectedError() {
        return new IllegalStateException("No blank square found given the board: \n" + this);
    }

    /**
     * Does this board equal y?
     *
     * @param y the board with which to compare.
     * @return <code>true</code> if this board equals to y.
     */
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }

        if (y == null) {
            return false;
        }

        if (y.getClass() != this.getClass()) {
            return false;
        }

        Board that = (Board) y;
        if (this.N != that.N) {
            return false;
        }

        for (int i = 0; i < N; i++) {
            if (!Arrays.equals(this.blocks[i], that.blocks[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return all neighboring boards.
     */
    public Iterable<Board> neighbors() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] == 0) {
                    return createNeighborsOf(i, j);
                }
            }
        }
        // that is hardly possible
        throw unexpectedError();
    }

    private Iterable<Board> createNeighborsOf(int i, int j) {
        Queue<Board> q = new Queue<Board>();

        if (i > 0) {
            q.enqueue(new Board(swap(i, j, i - 1, j)));
        }

        if (i < N - 1) {
            q.enqueue(new Board(swap(i, j, i + 1, j)));
        }

        if (j > 0) {
            q.enqueue(new Board(swap(i, j, i, j - 1)));
        }

        if (j < N - 1) {
            q.enqueue(new Board(swap(i, j, i, j + 1)));
        }

        return q;
    }

    private int[][] swap(int fromRow, int fromCol, int toRow, int toCol) {
        int[][] copy = copyOfBlocks();
        int tmp = copy[toRow][toCol];
        copy[toRow][toCol] = copy[fromRow][fromCol];
        copy[fromRow][fromCol] = tmp;
        return copy;
    }

    /**
     * @return string representation of the board (in the output format specified below).
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N).append("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

}