package espe.lono.textsearcher.textsearcher.core;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.MateriaPublicacao;
import espe.lono.db.models.PautaPublicacao;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Petrus Augusto - Espe
 * @since 11/10/2016
 */

public class Colisao 
{
    
    Map<Integer, Map<Long, List<Long> >> colisao_lista;

    public Colisao()
    {
        this.colisao_lista = new HashMap<>();
    }
    
    public void adicionaMateria(int client_id, long doc_inicio, long doc_fim )
    {        
        // Checando se existe este cliente na lista
        // se nao existir, adiciona-o
        if ( !this.colisao_lista.containsKey( client_id ) )
        {
            Map<Long, List<Long> > cliente_lista = new HashMap<>();
            this.colisao_lista.put(client_id, cliente_lista);
        }
        
        // Adicionando dados da materia
        Map<Long, List<Long> > cliente_lista = this.colisao_lista.get(client_id);
        if ( !cliente_lista.containsKey(doc_inicio) )
        {
            List<Long> doc_array = new ArrayList<>();
            doc_array.add(doc_fim);
            cliente_lista.put(doc_inicio, doc_array);
        }
        else
        {
            List<Long> doc_array = cliente_lista.get(doc_inicio);
            cliente_lista.remove(doc_inicio);
            
            doc_array.add(doc_fim);
            cliente_lista.put(doc_inicio, doc_array);
        }
    }
    
    public int obterPautaID(PautaPublicacao pautaPub, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        
        // Checando se existe texto de pauta
        if ( pautaPub.getPauta().length() <= 0 )
            return 0; // Não há necessidade de pesquisar, ja que não tem texto.
        
        // Gerando hash...
        final String hash = DigestUtils.sha1Hex( "" + pautaPub.getNumDocLucene() + ":" + pautaPub.getPauta());
        pautaPub.setPautaHash(hash);
        
        // Montando SQL
        final int idPub = pautaPub.getIdPublicacao();
        final StringBuilder sqlSB = new StringBuilder();
        sqlSB.append("SELECT id_pauta FROM pauta_publicacao ");
        sqlSB.append("WHERE pauta_hash = '" + pautaPub.getPautaHash() + "' ");
        if ( idPub > 0 ) sqlSB.append("AND id_publicacao=" + idPub);

        // Realizando consulta
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlSB.toString());
        if ( !resultado.next() ) return 0;
        
        int id_pauta = resultado.getInt("id_pauta");
        
        stm.close();
        resultado.close();
        
        return id_pauta;
    }
    
    public int obterMateriaID(MateriaPublicacao materiaPub, long docInicio, long docFim, DbConnection dbconn) throws SQLException
    {
        // Gerando hash...
        final String hash = DigestUtils.sha1Hex( "" + docInicio + ":" + materiaPub.getMateria() + ":" + docFim);
        materiaPub.setMateriaHash(hash);
        
        // Montando SQL
        final int idPub = (materiaPub.getIdPublicacao() != null) ? materiaPub.getIdPublicacao() : 0;
        final StringBuilder sqlSB = new StringBuilder();
        sqlSB.append("SELECT id_materia FROM materia_publicacao ");
        sqlSB.append("WHERE materia_hash='" + hash + "'");
        if ( idPub > 0 ) sqlSB.append(" AND id_publicacao=" + idPub);

        // Realizando consulta
        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlSB.toString());
        if ( !resultado.next() ) return 0;
        
        int id_materia = resultado.getInt("id_materia");
        
        stm.close();
        resultado.close();
        
        return id_materia;
    }
    
    public boolean checarColisao(MateriaPublicacao materiaPub)
    {
        int client_id = materiaPub.getIdCliente();
        long doc_inicio = materiaPub.getLinhaInicialMateria();
        long doc_fim = materiaPub.getLinhaFinalMateria();
        
        // Checando se o cliente esta na lista
        if ( !this.colisao_lista.containsKey( client_id ) )
        {
            // Cliente nao esta na lista, retornando falso
            return false;
        }
        
        // Obtend lista do cliente e procurando o documento (lucene) de inicio/fim
        Map<Long, List<Long> > cliente_lista = this.colisao_lista.get(client_id);
        if ( !cliente_lista.containsKey( doc_inicio ) )
        {
            return false;
        }
        
        // Obtendo o array de inteiros que indica os documentos de fim da linha
        List<Long> client_doc_fim_lista = cliente_lista.get( doc_inicio );
        for ( long doc: client_doc_fim_lista )
        {
            if ( doc == doc_fim ) return true;
        }
        
        // Materia/Documentos nao processado/indexado p/ este cliente
        return false;
    }
}
