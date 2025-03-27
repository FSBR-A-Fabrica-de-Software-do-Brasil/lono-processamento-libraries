package espe.lono.textsearcher.database.negocios;

import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.models.MateriaPublicacao;
import espe.lono.textsearcher.database.dao.LuceneDao;
import org.apache.lucene.index.DirectoryReader;

import java.sql.SQLException;

public class LuceneNegocio {
    final LuceneDao luceneDao = new LuceneDao();

    public String obterConteudoFixoFimMateria(DirectoryReader reader, MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite, int limiteDistanciaIncio) throws SQLException {
        return luceneDao.obterConteudoFixoFimMateria(reader, materiaPublicacao, sqlite, limiteDistanciaIncio);
    }

    public String obterConteudoFixoInicioMateria(DirectoryReader reader, MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return luceneDao.obterConteudoFixoInicioMateria(reader, materiaPublicacao, sqlite);
    }

}
