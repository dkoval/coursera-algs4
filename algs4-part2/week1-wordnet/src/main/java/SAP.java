import static java.util.Arrays.asList;

/**
 * Represents a shortest ancestral path.
 * <p/>
 * An ancestral path between two vertices v and w in a digraph is a directed path from v to a common ancestor x,
 * together with a directed path from w to the same ancestor x.
 * <p/>
 * A shortest ancestral path is an ancestral path of minimum total length.
 */
public class SAP {

    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        checkNotNull(G, "Digraph must not be null");
        // SAP is supossed to be an immutable data type
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        checkRange(v);
        checkRange(w);
        return length(asList(v), asList(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        checkRange(v);
        checkRange(w);
        return ancestor(asList(v), asList(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkRange(v);
        checkRange(w);
        return new SAP_BFS(G, v, w).length();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkRange(v);
        checkRange(w);
        return new SAP_BFS(G, v, w).ancestor();
    }

    private <T> T checkNotNull(T arg, String message) {
        if (arg == null) {
            throw (message != null) ? new NullPointerException(message) : new NullPointerException();
        }
        return arg;
    }

    private int checkRange(int v) {
        if (v < 0 || v > G.V() - 1) {
            throw new IndexOutOfBoundsException("Vertex must be between 0 and G.V() - 1, but got: " + v);
        }
        return v;
    }

    private Iterable<Integer> checkRange(Iterable<Integer> vs) {
        checkNotNull(vs, "Iterable of vertices must not be null");
        for (Integer v : vs) {
            checkRange(v);
        }
        return vs;
    }

    /**
     * Re-implements breadth-first search for the use in SAP class.
     */
    private static class SAP_BFS {

        private final Digraph G;
        private final BreadthFirstDirectedPaths v, w;
        private int ancestor = -1;
        private int length = -1;

        public SAP_BFS(Digraph G, Iterable<Integer> v, Iterable<Integer> w) {
            this.G = G;
            this.v = new BreadthFirstDirectedPaths(G, v);
            this.w = new BreadthFirstDirectedPaths(G, w);
            evaluateAncestor();
        }

        private void evaluateAncestor() {
            for (int a = 0; a < G.V(); a++) {
                if (isAncestor(a)) {
                    int l = totalDistTo(a);
                    if (this.length == -1 || this.length > l) {
                        this.length = l;
                        this.ancestor = a;
                    }
                }
            }
        }

        private boolean isAncestor(int x) {
            return v.hasPathTo(x) && w.hasPathTo(x);
        }

        private int totalDistTo(int x) {
            return v.distTo(x) + w.distTo(x);
        }

        public int ancestor() {
            return ancestor;
        }

        public int length() {
            return length;
        }
    }
}
