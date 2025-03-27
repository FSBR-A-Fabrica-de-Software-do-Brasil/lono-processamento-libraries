package espe.lono.db.negocios;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.dao.ClienteNotificacaoDAO;
import espe.lono.db.models.ClienteNotificacao;

import java.sql.SQLException;

public class NotificacaoNegocio {
    protected ClienteNotificacaoDAO clienteNotificacaoDAO = new ClienteNotificacaoDAO();

    public void escreverNotificacao(int idCliente, int idUsuario, String assunto, String mensagem, String actionUrl, boolean showMobile, DbConnection dbConnection) throws SQLException {
        // Alimentando classe de notificação
        ClienteNotificacao clienteNotificacao = new ClienteNotificacao();
        clienteNotificacao.setIdCliente(idCliente);
        clienteNotificacao.setIdUsuario(idUsuario);
        clienteNotificacao.setAssunto(assunto);
        clienteNotificacao.setMensagem(mensagem);
        clienteNotificacao.setActionUrl(actionUrl);
        clienteNotificacao.setShowMobile(showMobile);

        // Escrevendo a notificação
        clienteNotificacaoDAO.adicionarNovaNotificacao(clienteNotificacao, dbConnection);
    }

    public void escreverNotificacao(ClienteNotificacao clienteNotificacao, DbConnection dbConnection) throws SQLException {
        clienteNotificacaoDAO.adicionarNovaNotificacao(clienteNotificacao, dbConnection);
    }
}
