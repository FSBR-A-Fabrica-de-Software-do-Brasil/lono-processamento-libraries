package espe.lono.textsearcher.query;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.util.BitSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DocIdFilterQuery extends Query {
    private final Query query;
    private final Set<Integer> allowedDocIds;

    public DocIdFilterQuery(Query query, Set<Integer> allowedDocIds) {
        this.query = query;
        this.allowedDocIds = allowedDocIds;
    }

    public DocIdFilterQuery(Query query, ScoreDoc[] scoreDocs) {
        this.allowedDocIds = Arrays.stream(scoreDocs)
                .map(scoreDoc -> scoreDoc.doc)
                .collect(Collectors.toSet());
        this.query = query;
    }

    @Override
    public Weight createWeight(IndexSearcher searcher, boolean needsScore, float boost) throws IOException {
        final Weight innerWeight = query.createWeight(searcher, needsScore, boost);

        return new Weight(this) {

            @Override
            public boolean isCacheable(LeafReaderContext ctx) {
                return false;
            }

            @Override
            public void extractTerms(Set<Term> terms) {
                innerWeight.extractTerms(terms);
            }

            @Override
            public Explanation explain(LeafReaderContext context, int doc) throws IOException {
                Scorer scorer = scorer(context);
                if (scorer != null) {
                    int targetDoc = scorer.iterator().advance(doc);
                    if (targetDoc == doc) {
                        float score = scorer.score();
                        return Explanation.match(score, "matched with score " + score);
                    }
                }
                return Explanation.noMatch("Not in allowed doc IDs or did not match query");
            }

            @Override
            public Scorer scorer(LeafReaderContext context) throws IOException {
                int docBase = context.docBase;
                int maxDoc = context.reader().maxDoc();

                // Cria bitset com os docs permitidos no contexto atual
                FixedBitSet bitSet = new FixedBitSet(maxDoc);
                for (int globalDocId : allowedDocIds) {
                    int localDocId = globalDocId - docBase;
                    if (localDocId >= 0 && localDocId < maxDoc) {
                        bitSet.set(localDocId);
                    }
                }

                Scorer innerScorer = innerWeight.scorer(context);
                if (innerScorer == null) return null;

                DocIdSetIterator filteredIterator = new FilteredDocIdSetIterator(innerScorer.iterator()) {
                    @Override
                    protected boolean match(int doc) {
                        return bitSet.get(doc);
                    }
                };

                return new Scorer(this) {
                    @Override
                    public DocIdSetIterator iterator() {
                        return filteredIterator;
                    }

                    @Override
                    public int docID() {
                        return filteredIterator.docID();
                    }

                    @Override
                    public float score() throws IOException {
                        return innerScorer.score();
                    }

                    @Override
                    public Collection<ChildScorer> getChildren() throws IOException {
                        return innerScorer.getChildren();
                    }
                };
            }
        };
    }

    @Override
    public String toString(String field) {
        return "DocIdFilterQuery(" + query.toString(field) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DocIdFilterQuery that = (DocIdFilterQuery) obj;
        return query.equals(that.query) && allowedDocIds.equals(that.allowedDocIds);
    }

    @Override
    public int hashCode() {
        return 31 * query.hashCode() + allowedDocIds.hashCode();
    }
}