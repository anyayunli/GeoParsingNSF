import org.apache.lucene.search.similarities.DefaultSimilarity;


public class BinarySimilarity extends DefaultSimilarity {
    
    /**
     * Simple default constructor for {@link BinarySimilarity}.
     */
    public BinarySimilarity() {}
    
    /**
     * Ignores multiple appearance of the query term in the index
     * document field, effectively making TF (term frequency) a
     * yes/no proposition (i.e., zero is still zero, but you don't
     * get extra points for a query term being found multiple times in
     * an index document field).
     * 
     * @param freq      floating-point number being converted to 1.0 or 0.0
     */
    @Override
    public float tf(float freq) {
        if (freq > 0)
            return 1.0f;
        else return 0.0f;
    }

}