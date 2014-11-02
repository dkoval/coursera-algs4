/**
 * Measures the semantic relatedness of given nouns.
 */
public class Outcast {

    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDistance = -1;
        String maxNoun = null;
        for (String noun : nouns) {
            int distance = 0;
            for (String otherNoun : nouns) {
                distance += wordNet.distance(noun, otherNoun);
            }
            if (distance > maxDistance) {
                maxDistance = distance;
                maxNoun = noun;
            }
        }
        return maxNoun;
    }
}
