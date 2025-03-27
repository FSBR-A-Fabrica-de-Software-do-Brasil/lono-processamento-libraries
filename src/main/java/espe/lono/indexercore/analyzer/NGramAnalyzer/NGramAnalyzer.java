package espe.lono.indexercore.analyzer.NGramAnalyzer;

import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

/**
 * @author ESPE
 */

public class NGramAnalyzer extends Analyzer {

  final public static int DEFAULT_MIN_TOKEN = 3;
  final public static int DEFAULT_MAX_TOKEN = 17;
  final private int minGram;
  final private int maxGram;
  final private Set stoptable;
  private TokenStream result;
  NGramTokenizer gramTokenizer;
  final private Version luc_version;
  
    public NGramAnalyzer(int minGram, int maxGram)
    {
        this.minGram = minGram;
        this.maxGram = maxGram;

        // Defini a lista de stop filter p/ PT-BR
        stoptable = BrazilianAnalyzer.getDefaultStopSet();
        stoptable.add("<br>");
        stoptable.add("&nbsp;");
        stoptable.add("\\");
        stoptable.add("/");
        stoptable.add("silva"); // Oq mais tem em nomes BR é o tal do Silva...
                                // Dito por: Petrus A. C. Silva! :P
        // Versao
        luc_version = Version.LUCENE_CURRENT;
    }
  
    public NGramAnalyzer()
    {
        this.minGram = DEFAULT_MIN_TOKEN;
        this.maxGram = DEFAULT_MAX_TOKEN;

        // Defini a lista de stop filter p/ PT-BR
        stoptable = BrazilianAnalyzer.getDefaultStopSet();
        stoptable.add("<br>");
        stoptable.add("&nbsp;");
        stoptable.add("\\");
        stoptable.add("/");
        stoptable.add("silva"); // Oq mais tem em nomes BR é o tal do Silva...
                                // Dito por: Petrus A. C. Silva! :P
        // Versao
        luc_version = Version.LUCENE_CURRENT;
    }

    @Override
    protected TokenStreamComponents createComponents(String string) {
        gramTokenizer = new NGramTokenizer(minGram, maxGram);
        result = new StandardFilter(gramTokenizer);
        result =  new StopFilter(result, (CharArraySet) stoptable);
        return new TokenStreamComponents(gramTokenizer, result);
    }
  
}

