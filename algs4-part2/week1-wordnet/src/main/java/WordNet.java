import java.util.*;

/**
 * WordNet is a semantic lexicon for the English language.
 * WordNet groups words into sets of synonyms called synsets and describes semantic relationships between them.
 * One such relationship is the is-a relationship, which connects a hyponym (more specific synset) to a hypernym (more general synset).
 * For example, a plant organ is a hypernym of carrot and plant organ is a hypernym of plant root.
 */
public class WordNet {

    private Map<Integer, String> synsetIdToSynsetMap = new HashMap<Integer, String>();
    private Map<String, Set<Integer>> nounToSynsetIdsMap = new HashMap<String, Set<Integer>>();
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        checkNotNull(synsets, "synsets filename must not be null");
        checkNotNull(hypernyms, "hypernyms filename must not be null");

        // construct a directed graph out of synsets and hypernyms input files
        int numOfVertices = processSynsets(synsets);
        Digraph g = parseHypernyms(hypernyms, numOfVertices);

        // check for cycles
        checkNoCycles(g);
        // check whether g is a rooted DAG
        checkRootedDAG(g);

        // finally, construct a SAP object
        this.sap = new SAP(g);

    }

    private int processSynsets(String filename) {
        In in = null;
        try {
            in = new In(filename);
            return doProcessSynsets(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private int doProcessSynsets(In in) {
        int numOfVertices = 0;
        while (in.hasNextLine()) {
            // parse CSV format of synsets
            String line = in.readLine();
            String[] chunks = line.split(",");

            Integer synsetId = Integer.parseInt(chunks[0]);
            String synset = chunks[1];

            // synset id (1) -> synset (1) mapping
            synsetIdToSynsetMap.put(synsetId, synset);

            // noun (1) -> synset ids (1..*) mapping
            String[] nouns = synset.split(" ");
            for (String noun : nouns) {
                Set<Integer> synsetIds = nounToSynsetIdsMap.get(noun);
                if (synsetIds == null) {
                    synsetIds = new HashSet<Integer>();
                }
                synsetIds.add(synsetId);
                nounToSynsetIdsMap.put(noun, synsetIds);
            }
            numOfVertices++;
        }
        return numOfVertices;
    }

    private Digraph parseHypernyms(String hypernyms, int numOfVertices) {
        In in = null;
        try {
            in = new In(hypernyms);
            return doParseHypernyms(in, numOfVertices);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private Digraph doParseHypernyms(In in, int numOfVertices) {
        Digraph g = new Digraph(numOfVertices);
        while (in.hasNextLine()) {
            // parse CSV format of hypernyms
            String line = in.readLine();
            String[] chunks = line.split(",");

            int hyponymId = Integer.parseInt(chunks[0]);
            for (int i = 1; i < chunks.length; i++) {
                int hypernymId = Integer.parseInt(chunks[i]);
                g.addEdge(hyponymId, hypernymId);
            }
        }
        return g;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return Collections.unmodifiableSet(nounToSynsetIdsMap.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        checkNotNull(word, "word must not be null");
        return nounToSynsetIdsMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNoun(nounA);
        checkNoun(nounB);
        Set<Integer> synsetIdsOfNounA = nounToSynsetIdsMap.get(nounA);
        Set<Integer> synsetIdsOfNounB = nounToSynsetIdsMap.get(nounB);
        return sap.length(synsetIdsOfNounA, synsetIdsOfNounB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNoun(nounA);
        checkNoun(nounB);
        Set<Integer> synsetIdsOfNounA = nounToSynsetIdsMap.get(nounA);
        Set<Integer> synsetIdsOfNounB = nounToSynsetIdsMap.get(nounB);
        int ancestor = sap.ancestor(synsetIdsOfNounA, synsetIdsOfNounB);
        return synsetIdToSynsetMap.get(ancestor);
    }

    private <T> T checkNotNull(T arg, String message) {
        if (arg == null) {
            throw (message != null) ? new NullPointerException(message) : new NullPointerException();
        }
        return arg;
    }

    private void checkNoCycles(Digraph g) {
        DirectedCycle cycleFinder = new DirectedCycle(g);
        if (cycleFinder.hasCycle()) {
            throw new IllegalArgumentException("Not a valid DAG");
        }
    }

    private void checkRootedDAG(Digraph g) {
        int possibleRoots = 0;
        for (int i = 0; i < g.V(); i++) {
            if (!g.adj(i).iterator().hasNext()) {
                possibleRoots++;
            }
        }
        if (possibleRoots != 1) {
            throw new IllegalArgumentException("Not a rooted DAG");
        }
    }

    private String checkNoun(String noun) {
        if (!isNoun(noun)) {
            throw new IllegalArgumentException("Noun = " + noun + " is not a WordNet noun");
        }
        return noun;
    }
}
