import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

public class WhitespaceLowerCaseAnalyzer extends Analyzer {
    
    // Lucene v4.0+ offers a nice speed increase over v3.6.1 in
    // terms of fuzzy search
    private final static Version matchVersion = Version.LUCENE_5_0_0 ;
    
    /**
     * Simple default constructor for
     * {@link WhitespaceLowerCaseAnalyzer}.
     * 
     */
    public WhitespaceLowerCaseAnalyzer() {}
    
    /**
     * Provides tokenizer access for the analyzer.
     * 
     * @param fieldName     field to be tokenized
     * @param reader
     */
    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        return new TokenStreamComponents(new WhitespaceLowerCaseTokenizer());
    }

}