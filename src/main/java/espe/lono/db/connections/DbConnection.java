package espe.lono.db.connections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @version 1.1
 * @author Petrus Augusto - Espe
 * @since 16/09/2016
 */

abstract public class DbConnection 
{
    abstract public void conectar() throws SQLException;
    abstract public void desconectar() throws SQLException;
    
    abstract public int executeSqlLID(String sqlCommand) throws SQLException;
    abstract public boolean executarSql(String sqlCommand) throws SQLException;
    abstract public boolean executarSql(PreparedStatement pm) throws SQLException;
    abstract public ResultSet executarSql(PreparedStatement pm, boolean returnQuery) throws SQLException;
    abstract public ResultSet abrirConsultaSql(Statement stmt, String sqlCommand) throws SQLException;
    abstract public ResultSet abrirConsultaSql(String sqlCommand) throws SQLException;
    abstract public Statement obterStatement() throws SQLException;
    abstract public PreparedStatement obterPreparedStatement(String sql) throws SQLException;
    
    // Comandos relacionados a 'TRANSACTION'
    abstract public void iniciarTransaction() throws SQLException;
    abstract public void finalizarTransaction_COMMIT() throws SQLException;
    abstract public void finalizarTransaction_ROLLBACK() throws SQLException;
}
