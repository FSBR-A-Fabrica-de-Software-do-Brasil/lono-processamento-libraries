package espe.lono.db.connections.drivers;
import espe.lono.db.connections.DbConnectionMarcacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

/**
 * @version 1.1
 * @author Petrus Augusto - Espe
 * @since 16/09/2016
 */

public class DbSQLite extends DbConnectionMarcacao
{
    protected Connection conn;
    protected Statement stm;
    protected String db_dir;
    protected boolean autoCommit = true;
    protected boolean connectionIsAlive = false;

    public DbSQLite(final String pub_dir, boolean readonly) throws ClassNotFoundException, SQLException
    {
        this.initializeSQLite(pub_dir, readonly);
    }
    
    public DbSQLite(final String pub_dir) throws ClassNotFoundException, SQLException
    {
        this.initializeSQLite(pub_dir, false);
    }
    
    private void initializeSQLite(final String pub_dir, boolean readonly) throws ClassNotFoundException, SQLException
    {
        // Criando diretorio onde sera armazenado o banco de dados
        File db_fd = new File( pub_dir + "/db" );
        db_fd.mkdirs(); // Criando diretorio;
        this.db_dir = db_fd.getAbsolutePath();
        
        //Inicializando SQLite
        Class.forName("org.sqlite.JDBC");
        this.abrirConexao();
        
        // Criado/Limpando tabela (se nao for aberto como leitura)
        if ( readonly == false ) this.createTables();
    }
    
    protected void createTables() throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();

        // Inicializando DB (criando tabela)
        this.stm.executeUpdate("DROP TABLE IF EXISTS marcacao_publicacao");
        this.stm.executeUpdate("CREATE TABLE marcacao_publicacao ("
                + "id_marcacao INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "id_tipo_padrao INT NOT NULL,"
                + "num_doc_lucene BIGINT,"
                + "marcacao TEXT,"
                + "marcacao_original TEXT,"
                + "pagina INT,"
                + "linha_pagina INT,"
                + "linha_publicacao INT,"
                + "dat_cad DATETIME,"
                + "sit_cad CHARACTER(1),"
                + "usu_cad INT,"
                + "id_tipo_padrao_jornal INT,"
                + "complex BOOLEAN"
                + ");"
        );
        
        // Criando indexes
        this.stm.executeUpdate("CREATE INDEX 'idx0' ON 'marcacao_publicacao' (num_doc_lucene)");
        this.stm.executeUpdate("CREATE INDEX 'idx1' ON 'marcacao_publicacao' (id_tipo_padrao)");
        if ( !autoCommit ) this.conn.commit();
    }
    
    @Override
    public void fecharConexao() throws SQLException
    {
        this.desconectar();
    }
    
    protected void desconectar() throws SQLException
    {
        this.stm.close();
        this.conn.close();
    }
    
    @Override
    public PreparedStatement obterPreparedStatement(final String sql) throws SQLException
    {
        final String sql2 = sql.replace("NOW()", "datetime('now')");
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        return this.conn.prepareStatement(sql2);
    }
    
    @Override
    public boolean executarPreparedStatement(PreparedStatement stm) throws SQLException
    {
        final String sql1 = stm.toString().replace("NOW()", "datetime('now')");
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        return stm.execute();
    }
    
    @Override
    public ResultSet executarQueryPreparedStatement(PreparedStatement stm) throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        return stm.executeQuery();
    }

    @Override
    public int executarUpdate(PreparedStatement stm) throws SQLException {
        if ( !this.connectionIsAlive )
            this.abrirConexao();

        return stm.executeUpdate();
    }

    @Override
    public boolean executarSql(PreparedStatement stm) throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
       return stm.execute();
    }
    
    @Override
    public boolean executarSql(final String sql) throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
       return this.stm.execute(sql);
    }
    
    
    
    @Override
    public ResultSet abrirConsultaSql(final PreparedStatement stm) throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        ResultSet st = stm.executeQuery();
        return st;
    }
    
    @Override
    public ResultSet abrirConsultaSql(final String sql) throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        ResultSet st = stm.executeQuery(sql);
        return st;
    }

    @Override
    public String obterNomeTabela()
    {
        return "marcacao_publicacao";
    }

    @Override
    public String obterDatetimeNowFunction()
    {
        return "DATETIME('now')";
    }

    @Override
    public void destruirTabela() throws SQLException
    { /* Do Nothing */ }
    
    @Override
    public void iniciarTransaction() throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        if ( !autoCommit ) return;
        
        this.conn.setAutoCommit(false);
        this.autoCommit = false;
    }

    @Override
    public void finalizarTransaction_COMMIT() throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        if ( autoCommit ) return;
        
        this.conn.setAutoCommit(true);
        this.autoCommit = true;
    }

    @Override
    public void finalizarTransaction_ROLLBACK() throws SQLException
    {
        if ( this.connectionIsAlive == false ) this.abrirConexao();
        if ( autoCommit ) return;
        
        this.conn.rollback();
        this.conn.setAutoCommit(true);
    }

    @Override
    public void destruirTabela( int id_publicacao ) throws SQLException
    {
        /* Nothing to do */
    }
    
    protected void abrirConexao() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.db_dir + "/marcacoes.sqlite");
        this.stm = conn.createStatement();
        this.connectionIsAlive = true;
    }

    @Override
    public String getNowSQLCommand() {
        return "datetime('now')";
    }

    @Override
    public boolean exportTable(String ouputCSVFName, boolean deleteAfterExport) {
        return false;
    }

    @Override
    public boolean importTable(String inputCSVFName) {
        return false;
    }
}
