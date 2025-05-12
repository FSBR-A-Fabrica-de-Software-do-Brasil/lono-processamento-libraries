package espe.lono.db.connections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @version 1.1
 * @author Petrus Augusto - Espe
 * @since 16/09/2016
 */

abstract public class DbConnectionMarcacao 
{
    abstract public String obterNomeTabela();
    abstract public String obterDatetimeNowFunction();
    
    abstract public void destruirTabela() throws SQLException;
    abstract public void destruirTabela(final int id_publicacao) throws SQLException;
    abstract public void fecharConexao() throws SQLException;
    
    abstract public PreparedStatement obterPreparedStatement(final String sql) throws SQLException;
    
    abstract public boolean executarPreparedStatement(PreparedStatement stm) throws SQLException;
    abstract public ResultSet executarQueryPreparedStatement(PreparedStatement stm) throws SQLException;
    
    abstract public boolean executarSql(PreparedStatement stm) throws SQLException;
    abstract public boolean executarSql(final String sql) throws SQLException;
    
    abstract public ResultSet abrirConsultaSql(final PreparedStatement stm) throws SQLException;
    abstract public ResultSet abrirConsultaSql(final String sql) throws SQLException;
    
    abstract public void iniciarTransaction() throws SQLException;
    abstract public void finalizarTransaction_COMMIT() throws SQLException;
    abstract public void finalizarTransaction_ROLLBACK() throws SQLException;
    
    abstract public String getNowSQLCommand();

    abstract public boolean exportTable(String outputCSVFName, boolean deleteAfterExport);
    abstract public boolean importTable(String inputCSVFName);
}
