/**
 * A client program that takes a command-line integer k, reads in a sequence of N strings
 * from standard input using StdIn.readString(), and prints out exactly k of them, uniformly at random.
 * Each item from the sequence can be printed out at most once.
 * <p/>
 * You may assume that k ≥ 0 and no greater than the number of string on standard input.
 * <p/>
 * Execution:
 * <pre>
 *     % echo A B C D E F G H I | java Subset 3
 *     C
 *     G
 *     A
 * </pre>
 */
public class Subset {

    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();

        // read in a sequence of N strings from standard input
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            rq.enqueue(item);
        }

        // print out exactly k of them, uniformly at random;
        // each item from the sequence can be printed out at most once
        while (k > 0) {
            StdOut.println(rq.dequeue());
            k--;
        }
    }

}