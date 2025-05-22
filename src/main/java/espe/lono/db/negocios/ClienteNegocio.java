package espe.lono.db.negocios;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.dao.ClienteDAO;
import espe.lono.db.models.*;

import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author Luiz Diniz/Petrus Augusto - Espe
 * @since 10/09/2015
 * @hidden 
 */

public class ClienteNegocio {
    protected ClienteDAO clienteDAO = new ClienteDAO();

    public boolean inserirDadosPesquisa(int idCliente, int idNomePesquisa, int idPublicacao, int idJornal, int qtdLocatedTerms, int qtdPjeProcs, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.inserirDadosPesquisa(idCliente, idNomePesquisa, idPublicacao, idJornal, qtdLocatedTerms, qtdPjeProcs, dbconn);
    }

    public NomePesquisaCliente[] listarNumeroOABJornal(int idJornal, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.dadoslistarNumeroOABJornal(0, idJornal, dbconn);
    }

    public String dadosListarNomePesquisaConcatenado(int idTermoPai, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.dadosListarNomePesquisaConcatenado(idTermoPai, dbconn);
    }

    public NomePesquisaCliente[] listarNomesPesquisaJornal(int idJornal, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.dadosListarNomesPesquisaJornal(0, idJornal, dbconn);
    }
    
    public NomePesquisaCliente listarNomePesquisaPorID(int idNomePesquisaCliente, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.listarNomePesquisaPorID(idNomePesquisaCliente, dbconn);
    }

    public boolean atualizarStatusNomePesquisa(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.atualizarStatusNomePesquisa(idNomePesquisa, dbconn);
    }

    public boolean atualizarBlacklistNotifyDat(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.atualizarBlacklistNotifyDat(idNomePesquisa, dbconn);
    }
    
    public Usuario[] listarUsuariosCliente(int idCliente, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.dadosListarUsuarios(idCliente, dbconn);
    }

    public Cliente listarClientePorID(int idCliente, DbConnection dbconn) throws SQLException
    {
        return clienteDAO.listarClientePorID(idCliente, dbconn);
    }
}
