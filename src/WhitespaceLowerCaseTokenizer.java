
import org.apache.lucene.analysis.util.CharTokenizer;

public class WhitespaceLowerCaseTokenizer extends CharTokenizer {
    
    /**
     * Call the "super" constructor.
     * 
     * @param matchVersion      e.g., Version.LUCENE_4_9
     * @param in
     */
    public WhitespaceLowerCaseTokenizer() {
        super();
    }

    /** Collects only characters which do not satisfy
     * {@link Character#isWhitespace(int)}.
     * 
     * @param c     char being processed
     */
    @Override
    protected boolean isTokenChar(int c) {
        return !Character.isWhitespace(c);
    }

    /** Converts char to lower case
     * {@link Character#toLowerCase(int)}.
     * 
     * @param c     char being processed
     */
    @Override
    protected int normalize(int c) {
        return Character.toLowerCase(c);
    }
}