package espe.lono.textsearcher.textsearcher.database.dao;

import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.models.MateriaPublicacao;
import espe.lono.textsearcher.utils.LuceneUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LuceneDao {
    public String obterConteudoFixoFimMateria(DirectoryReader reader, MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite, int limiteDistanciaIncio) throws SQLException {
        final String table_name = sqlite.obterNomeTabela();
        String sql = "SELECT mp.num_doc_lucene,  mp.id_tipo_padrao "
                + "FROM " + table_name + " mp "
                + "WHERE mp.num_doc_lucene > " + materiaPublicacao.getLinhaInicialMateria() + " "
                + "AND id_tipo_padrao IN (1, 2, 3, 4, 25, 26) "
                + "ORDER BY mp.num_doc_lucene ASC LIMIT 1";

        ResultSet resultado = sqlite.abrirConsultaSql(sql);
        if ( !resultado.next() ) return "";

        // Checando se estrapola o limite de linhas do inicio da materia/processo
        final int idTipoPadao = resultado.getInt("id_tipo_padrao");
        final long inicialNumDocLucene = resultado.getLong("num_doc_lucene");
        resultado.close();

        // Checando se esta fora do range esperado
        final long distanceWeight = inicialNumDocLucene - materiaPublicacao.getLinhaInicialMateria();
        if ( idTipoPadao != 26 || distanceWeight > limiteDistanciaIncio) {
            return "";
        }

        // Obtendo o texto completo desta regiao (obtendo o proximo titulo/processe a partir deste conteudo)
        sql = "SELECT mp.num_doc_lucene "
                + "FROM " + table_name + " mp "
                + "WHERE mp.num_doc_lucene > " + inicialNumDocLucene + " "
                + "AND id_tipo_padrao IN (1, 2, 3, 4, 13, 23, 25, 26) "
                + "ORDER BY mp.num_doc_lucene ASC LIMIT 1";
        //System.out.println("SQL Fixo Pos Materia[2] -> " + sql);
        resultado = sqlite.abrirConsultaSql(sql);
        if ( !resultado.next() ) return "";
        final long finalNumDocLucene = resultado.getLong("num_doc_lucene");
        resultado.close();

        // Obtendo o texto diretamente do Reader
        String texto = "";
        try {
            // Obtendo os documentos dentro deste elemento
            texto = oberTextoReader(reader, sqlite, inicialNumDocLucene, (finalNumDocLucene - 1));
        } catch (Exception ex) {
            texto = "";
        }

        return texto;
    }

    public String obterConteudoFixoInicioMateria(DirectoryReader reader, MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException {
        // Obtendo o doc de inici do texto fixo da materia
        final String table_name = sqlite.obterNomeTabela();
        String sql = "SELECT mp.num_doc_lucene, id_tipo_padrao "
                + "FROM " + table_name + " mp "
                + "WHERE mp.num_doc_lucene < " + materiaPublicacao.getLinhaInicialMateria() + " "
                + "AND mp.num_doc_lucene >" + materiaPublicacao.getLinhaTitulo() + " "
                + "AND id_tipo_padrao IN (25, 1, 2) "
                + "ORDER BY mp.num_doc_lucene DESC LIMIT 1";

        ResultSet resultado = sqlite.abrirConsultaSql(sql);
        if ( !resultado.next() ) return "";

        final int idTipoPadrao = resultado.getInt("id_tipo_padrao");
        if ( idTipoPadrao != 25 ) return ""; // Não foi encontrado.

        final long numDocLuceneInicial = resultado.getLong("num_doc_lucene");
        resultado.close();

        // Obtendo o doc final do texto fixodo inicio da materia
        sql = "SELECT mp.num_doc_lucene "
                + "FROM " + table_name + " mp "
                + "WHERE mp.num_doc_lucene < " + materiaPublicacao.getLinhaInicialMateria() + " "
                + "AND mp.num_doc_lucene > " + numDocLuceneInicial + " "
                + "AND id_tipo_padrao IN (1, 2, 3, 4, 13, 23, 25, 26) "
                + "ORDER BY mp.num_doc_lucene ASC LIMIT 1";
        resultado = sqlite.abrirConsultaSql(sql);
        if ( !resultado.next() ) return "";

        final long numDocLuceneFinal = resultado.getLong("num_doc_lucene");
        resultado.close();

        String texto;
        try {
            texto = oberTextoReader(reader, sqlite, numDocLuceneInicial, (numDocLuceneFinal - 1));
        } catch (IOException ex) {
            texto = "";
        }

        return texto ;
    }

    private String oberTextoReader(DirectoryReader reader, DbConnectionMarcacao sqlite, long inicialDoc, long finalDoc) throws SQLException, IOException {
        Document doc = null;
        final String sql = "SELECT mp.marcacao "
                + "FROM " + sqlite.obterNomeTabela() + " mp "
                + "WHERE mp.num_doc_lucene = ? "
                + "AND id_tipo_padrao IN (6, 7, 8, 9, 10, 11, 12) ";
        final PreparedStatement pstm = sqlite.obterPreparedStatement(sql);
        Bits liveDocs = MultiFields.getLiveDocs(reader);
        final StringBuilder textoFinal = new StringBuilder();
        for ( long x = inicialDoc; x <= finalDoc; x++ ) {
            // Checando se o document foi removeido
            doc = LuceneUtils.GetDocumentByRealDocIdValue(x, reader);
            if ( doc == null ) {
                // Documento removido, ignorando esta linha..
                continue;
            }

            pstm.setLong(1, x);
            final ResultSet result = sqlite.abrirConsultaSql(pstm);
            if ( result.next() ) {
                continue;
            }

            // Obtendo o texto
            final String doc_line = doc.getField("textoLinhaLimpa").stringValue();
            if ( doc_line == null || doc_line.length() <= 0 )
                continue;

            textoFinal.append(doc_line).append(" ");
        }

        return textoFinal.toString().trim();
    }
}
