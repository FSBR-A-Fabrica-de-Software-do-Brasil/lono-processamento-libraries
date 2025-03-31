package espe.lono.textsearcher.textsearcher.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class LuceneUtils {
    public static TopDocs GetTopDocByRealDocIdValue(long realDocId, IndexReader reader) {
        return LuceneUtils.GetTopDocByRealDocIdValue(String.valueOf(realDocId), reader);
    }

    public static TopDocs GetTopDocByRealDocIdValue(String realDocId, IndexReader reader) {
        final IndexSearcher searcherMarcacao = new IndexSearcher(reader);
        TermQuery query = new TermQuery(new Term("real_doc_id", realDocId));
        try {
            final TopDocs marcacaoResults = searcherMarcacao.search(query, 1);
            return ( marcacaoResults.totalHits <= 0 ) ? null : marcacaoResults;
        } catch (IOException e) {
            return null;
        }
    }

    public static Document GetDocumentByRealDocIdValue(long realDocId, IndexReader reader) {
        return LuceneUtils.GetDocumentByRealDocIdValue(String.valueOf(realDocId), reader);
    }

    public static Document GetDocumentByRealDocIdValue(String realDocId, IndexReader reader) {
        TopDocs topDocs = LuceneUtils.GetTopDocByRealDocIdValue(realDocId, reader);
        try {
            return (topDocs == null || topDocs.scoreDocs.length <= 0) ? null : reader.document(topDocs.scoreDocs[0].doc);
        } catch (IOException e) {
            return null;
        }
    }
}
