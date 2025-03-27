/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espe.lono.db.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.ClienteNotificacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ESPE
 */
public class ClienteNotificacaoDAO {
    public boolean adicionarNovaNotificacao(final ClienteNotificacao clienteNotificacao, final DbConnection dbconn) throws SQLException
    {
        // Escrevendo no banco de dados
        String sql = "INSERT INTO cliente_notificacoes "
                + "(id_cliente, id_usuario, mensagem, usu_cad, lida, assunto, action_url, dat_cad, push_sent) "
                + "VALUES("
                + "'" + clienteNotificacao.getIdCliente() + "', "
                + "'" + clienteNotificacao.getIdUsuario() + "', "
                + "'" + clienteNotificacao.getMensagem() + "', "
                + "'99', "
                + "FALSE, "
                + "'" + clienteNotificacao.getAssunto() + "', "
                + "'" + clienteNotificacao.getActionUrl() + "', "
                + "NOW(), "
                + "TRUE"  
                + ")";
        try {
            dbconn.executeSqlLID(sql);
        } catch (SQLException ex) {
            /* Nothing to do */
        }
        
        return true;
    }
    
    public boolean atualizarNotificacaoUsuario(ClienteNotificacao clienteNotificacao, DbConnection dbconn) throws SQLException
    {
        String sql = "UPDATE cliente_notificacoes SET "
                + "mensagem = '" + clienteNotificacao.getMensagem() + "', "
                + "dat_cad = NOW() "
                + "WHERE id_notificacao = " + clienteNotificacao.getIdNotificacao();
        
        return dbconn.executarSql(sql);
    }
    
    public ClienteNotificacao obterNotificacaoMateriaUsuario(int idUsuario, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();        
        String sqlcmd = "SELECT * FROM cliente_notificacoes "
                + "WHERE id_usuario = " + idUsuario + " "
                + "AND action_url = '/application/materia' "
                + "ORDER BY id_notificacao DESC "
                + "LIMIT 1";

        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        ClienteNotificacao usuarioNotificacaoLocated = null;
        
        while(resultado.next()) {
            usuarioNotificacaoLocated = new ClienteNotificacao();
            usuarioNotificacaoLocated.setIdNotificacao( resultado.getInt("id_notificacao"));
            usuarioNotificacaoLocated.setIdUsuario(resultado.getInt("id_usuario"));
            usuarioNotificacaoLocated.setIdCliente(resultado.getInt("id_cliente"));
            usuarioNotificacaoLocated.setAssunto(resultado.getString("assunto"));
            usuarioNotificacaoLocated.setMensagem(resultado.getString("mensagem"));
            usuarioNotificacaoLocated.setUsuCad(resultado.getInt("usu_cad"));
            usuarioNotificacaoLocated.setDatCad(resultado.getDate("dat_cad"));
            usuarioNotificacaoLocated.setActionUrl(resultado.getString("action_url"));
            usuarioNotificacaoLocated.setLida(resultado.getBoolean("lida"));
            usuarioNotificacaoLocated.setPushSent(resultado.getBoolean("push_sent"));
            break;
        }
        
        return usuarioNotificacaoLocated;
    }
}
