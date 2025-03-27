package espe.lono.db.dao;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.PalavrasSubstituicao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Petrus Augusto - Espe
 * @since 13/12/2016
 * @hidden 
 */

public class PalavrasDAO 
{
    public PalavrasSubstituicao[] listarPalavrasSubstituicao(DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<PalavrasSubstituicao> listaPalavras = new ArrayList();
        final String sql = "SELECT id_palavra, palavra_nova, palavra_original "
                         + "FROM palavra_substituicao "
                         + "WHERE sit_cad='A'";
        
        final ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        while (resultado.next()) 
        {
            PalavrasSubstituicao palavra = new PalavrasSubstituicao();
            palavra.setIdPalavra( resultado.getInt("id_palavra"));
            palavra.setPalavraNova( resultado.getString("palavra_nova").trim());
            palavra.setPalavraOriginal(resultado.getString("palavra_original").trim());
            
            listaPalavras.add(palavra);
        }
        
        resultado.close();
        stm.close();
        
        return listaPalavras.toArray( new PalavrasSubstituicao[0] );
    }
}
