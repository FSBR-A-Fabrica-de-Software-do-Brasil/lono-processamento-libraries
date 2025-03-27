package espe.lono.textsearcher.database;

import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.models.MateriaPublicacao;
import espe.lono.textsearcher.database.negocios.LuceneNegocio;
import org.apache.lucene.index.DirectoryReader;

import java.sql.SQLException;

public class Fachada extends espe.lono.db.Fachada {
    LuceneNegocio luceneNegocio = new LuceneNegocio();

    /**************************************************************************
     * Metodos  ligados ao Lucene
     **************************************************************************/
    /*

    /**
     * Obtém o conteudo FIXO do INICIO (se houver) das materias
     * @param reader DirectoryReader (Lucene) para obter os dados
     * @param materiaPublicacao Objeto MateriaPublicacao pré alimentado com os dados sobre a matéria
     * @param sqlite Conexão com o banco de marcações (processamento/engine)
     * @return String com o texto Fixo do início da máteria (null se não houver)
     * @throws SQLException Excessões relacioados ao SQL
     */
    public String obterConteudoFixoInicioMateria(DirectoryReader reader, MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return luceneNegocio.obterConteudoFixoInicioMateria(reader, materiaPublicacao, sqlite);
    }

    /**
     * Obtém o contéudo FIXO do FIM (se houver) das máterias
     * @param reader DirectoryReader (Lucene) para obter os dados
     * @param materiaPublicacao Objeto MateriaPublicacao pré alimentado com os dados sobre a matéria
     * @param sqlite Conexão com o banco de marcações (processamento/engine)
     * @return String com o texto Fixo do fim da máteria (null se não houver)
     * @throws SQLException Excessões relacioados ao SQL
     */
    public String obterConteudoFixoFimMateria(DirectoryReader reader, MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return luceneNegocio.obterConteudoFixoFimMateria(reader, materiaPublicacao, sqlite, 3000);
    }
}
