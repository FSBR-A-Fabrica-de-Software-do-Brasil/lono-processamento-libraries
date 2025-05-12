package espe.lono.db.connections.drivers;

import espe.lono.db.LonoDatabaseConfigs;
import espe.lono.db.connections.DbConnectionMarcacao;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DbPostgresMarcacao  extends DbConnectionMarcacao  {
    protected Connection conn;
    protected Statement stm;
    protected int idPublicacao;
    protected boolean autoCommit = true;
    protected boolean connectionIsAlive = false;

    private static final Object connectionPoolMutext = new Object();
    private static BasicDataSource connectionPool = null;

    static {
        connectionPool = new BasicDataSource();
        connectionPool.setValidationQuery("SELECT id FROM pool_validation");
        connectionPool.setUsername(LonoDatabaseConfigs.DBLONO_USERNAME);
        connectionPool.setPassword(LonoDatabaseConfigs.DBLONO_PASSWORD);
        connectionPool.setMaxTotal(12);
        connectionPool.setUrl(String.format("jdbc:postgresql://%s:%d/%s?currentSchema=marcacoes", LonoDatabaseConfigs.DBMARCACOES_HOSTNAME, LonoDatabaseConfigs.DBMARCACOES_PORT, LonoDatabaseConfigs.DBMARCACOES_DATABASE));
        connectionPool.setDriverClassName("org.postgresql.Driver");
        connectionPool.setInitialSize(1);
    }

    private static synchronized Connection GetConnectionFromPooling() throws SQLException {
        synchronized (connectionPoolMutext) {
            if ( connectionPool == null ) {
                connectionPool = new BasicDataSource();
                connectionPool.setValidationQuery("SELECT id FROM pool_validation");
                connectionPool.setUsername(LonoDatabaseConfigs.DBLONO_USERNAME);
                connectionPool.setPassword(LonoDatabaseConfigs.DBLONO_PASSWORD);
                connectionPool.setMaxTotal(12);
                connectionPool.setUrl(String.format("jdbc:postgresql://%s:%d/%s?currentSchema=marcacoes", LonoDatabaseConfigs.DBMARCACOES_HOSTNAME, LonoDatabaseConfigs.DBMARCACOES_PORT, LonoDatabaseConfigs.DBMARCACOES_DATABASE));
                connectionPool.setDriverClassName("org.postgresql.Driver");
                connectionPool.setInitialSize(1);
            }

            return connectionPool.getConnection();
        }
    }

    public DbPostgresMarcacao(int idPub) throws ClassNotFoundException, SQLException
    {
        // Iniciando conexao...
        this.abrirConexao();

        // Criando a tabela
        this.idPublicacao = idPub;
        this.createTable();
    }

    public DbPostgresMarcacao(int idPub, boolean createTable) throws ClassNotFoundException, SQLException
    {
        // Iniciando conexao...
        Class.forName("org.postgresql.Driver");
        this.abrirConexao();

        // Criando a tabela
        this.idPublicacao = idPub;
        if (createTable) this.createTable();
    }

    protected void createTable() throws SQLException
    {
        if (!this.connectionIsAlive)
            this.abrirConexao();

        final String table_name = this.obterNomeTabela();
        this.stm.executeUpdate("DROP TABLE IF EXISTS " + table_name);
        this.stm.executeUpdate("CREATE TABLE IF NOT EXISTS " + table_name + " ("
                + "id_marcacao serial8 NOT NULL,"
                + "id_tipo_padrao int4 NOT NULL,"
                + "num_doc_lucene int8,"
                + "marcacao text,"
                + "marcacao_original text,"
                + "pagina int4,"
                + "linha_pagina int4,"
                + "linha_publicacao int4,"
                + "dat_cad timestamp,"
                + "sit_cad CHAR(1),"
                + "usu_cad int4,"
                + "id_tipo_padrao_jornal int4,"
                + "complex BOOLEAN"
                + ");"
        );

        // Criando indexes
        this.stm.executeUpdate("CREATE INDEX " + table_name + "_idx0 ON " + table_name + "(id_tipo_padrao)");
        this.stm.executeUpdate("CREATE INDEX " + table_name + "_idx1 ON " + table_name + "(num_doc_lucene)");
    }

    @Override
    public void fecharConexao() throws SQLException
    {
        this.desconectar();
    }

    protected void desconectar() throws SQLException
    {
        if ( this.stm != null && !this.stm.isClosed())
            this.stm.close();

        this.conn.close();
        this.connectionIsAlive = false;
    }

    @Override
    public PreparedStatement obterPreparedStatement(final String sql) throws SQLException
    {
        if (!this.connectionIsAlive)
            this.abrirConexao();

        return this.conn.prepareStatement(sql);
    }

    @Override
    public boolean executarPreparedStatement(PreparedStatement stm) throws SQLException
    {
        if (!this.connectionIsAlive)
            this.abrirConexao();

        return stm.execute();
    }

    @Override
    public ResultSet executarQueryPreparedStatement(PreparedStatement stm) throws SQLException
    {
        if ( !this.connectionIsAlive )
            this.abrirConexao();

        return stm.executeQuery();
    }

    @Override
    public boolean executarSql(PreparedStatement stm) throws SQLException
    {
        if ( !this.connectionIsAlive )
            this.abrirConexao();

        return stm.execute();
    }

    @Override
    public boolean executarSql(final String sql) throws SQLException
    {
        if ( !this.connectionIsAlive )
            this.abrirConexao();

        return this.stm.execute(sql);
    }


    @Override
    public ResultSet abrirConsultaSql(final PreparedStatement stm) throws SQLException
    {
        if ( !this.connectionIsAlive )
            this.abrirConexao();

        return stm.executeQuery();
    }

    @Override
    public ResultSet abrirConsultaSql(final String sql) throws SQLException
    {
        if ( !this.connectionIsAlive )
            this.abrirConexao();

        return stm.executeQuery(sql);
    }

    @Override
    public String obterNomeTabela()
    {
        return "marcacao_publicacao_" + this.idPublicacao;
    }

    @Override
    public String obterDatetimeNowFunction()
    {
        return "NOW()";
    }

    @Override
    public void destruirTabela() throws SQLException
    {
        if ( !this.connectionIsAlive ) {
            abrirConexao();
        }

        if ( this.stm == null || this.stm.isClosed() ) {
            this.stm = conn.createStatement();
        }
        final String table_name = "marcacao_publicacao_" + this.idPublicacao;
        this.stm.executeUpdate("DROP TABLE IF EXISTS " + table_name);
    }

    @Override
    public void iniciarTransaction() throws SQLException
    {
        if ( !this.connectionIsAlive ) this.abrirConexao();
        if ( !autoCommit ) return;

        this.conn.setAutoCommit(false);
        this.autoCommit = false;
    }

    @Override
    public void finalizarTransaction_COMMIT() throws SQLException
    {
        if ( !this.connectionIsAlive ) this.abrirConexao();
        if ( autoCommit ) return;

        this.conn.setAutoCommit(true);
        this.autoCommit = true;
    }

    @Override
    public void finalizarTransaction_ROLLBACK() throws SQLException
    {
        if ( !this.connectionIsAlive ) this.abrirConexao();
        if ( autoCommit ) return;

        this.conn.rollback();
        this.conn.setAutoCommit(true);
    }

    @Override
    public void destruirTabela( int id_publicacao ) throws SQLException
    {
        if ( !this.connectionIsAlive ) {
            abrirConexao();
        }

        if ( this.stm == null || this.stm.isClosed() ) {
            this.stm = conn.createStatement();
        }

        final String table_name = "marcacao_publicacao_" + id_publicacao;
        this.stm.executeUpdate("DROP TABLE IF EXISTS " + table_name);
    }

    protected void abrirConexao() throws SQLException {
        for ( int contador = 0; contador <= 3; contador++ )
        {
            try
            {
                // Inicializando conexao com o banco de dados
                if ( this.conn == null || this.conn.isClosed() )
                {
                    this.conn = DbPostgresMarcacao.GetConnectionFromPooling();
                    this.stm = this.conn.createStatement();
                    this.connectionIsAlive = true;
                }

                // Saindo do loop
                break;
            }
            catch ( SQLException ex )
            {
                if ( contador == 3 ) throw ex; // Limite de tentativas excedidas
                else
                {
                    // Espera 5 segundos e tenta conectar novamente ao BD
                    try
                    {
                        this.TimeWaiter(5);
                    }
                    catch ( Exception ex1 )
                    { }
                }
            }
        }
    }

    @Override
    public String getNowSQLCommand() {
        return "NOW()";
    }

    @Override
    public boolean exportTable(String outputCSVFName, boolean deleteAfterExport) {
        final String table_name = this.obterNomeTabela();
        String sqlcmd = "SELECT id_tipo_padrao, num_doc_lucene, marcacao, marcacao_original, pagina, linha_pagina, linha_publicacao, dat_cad, sit_cad, usu_cad, id_tipo_padrao_jornal, complex ";
        sqlcmd += "FROM " + table_name;

        try {
            final File outputFile = new File(outputCSVFName);
            if ( outputFile.exists() ) outputFile.delete();

            final FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos));

            ResultSet resultados = this.abrirConsultaSql(sqlcmd);
            ResultSetMetaData metaData = resultados.getMetaData();
            while ( resultados.next() ) {
                StringBuilder sb = new StringBuilder();
                for (int col = 1; col <= metaData.getColumnCount(); col++ ) {
                    if ( col > 1 ) sb.append(",@,");
                    sb.append("\"").append(resultados.getString(col)).append("\"");
                }
                bufferedWriter.write(sb.toString());
                bufferedWriter.newLine();
            }

            // Fechando arquivo
            bufferedWriter.close();
            fos.close();

            // Fechando o resultset
            resultados.close();

            if ( deleteAfterExport ) {
                // Deletando a tabela
                this.executarSql("DROP TABLE IF EXISTS " + table_name);
            }

            // Checando se o arquivo Existe
            return new File(outputCSVFName).exists();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean importTable(String inputCSVFName) {
        String line;
        final String tableName = this.obterNomeTabela();
        final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        FileReader fileReader = null;
        BufferedReader reader = null;

        try {
            PreparedStatement ps = this.obterPreparedStatement(
                    "INSERT INTO " + tableName + " (id_tipo_padrao, num_doc_lucene, marcacao, marcacao_original, pagina, linha_pagina, linha_publicacao, dat_cad, sit_cad, usu_cad, id_tipo_padrao_jornal, complex) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );

            // Validando o arquivo CSV
            File csvFD = new File(inputCSVFName);
            if ( !csvFD.exists() )
                throw new IOException("CSV File not located!");

            // Lendo line-by-line o arquivo CSV
            fileReader = new FileReader(csvFD);
            reader = new BufferedReader(fileReader);
            int lineNum = 1;
            while ((line = reader.readLine()) != null ) {
                // Removendo 'newline'
                line = line.replace("\n", "").replace("\r","");

                // Verificando se a linha esta em branca
                if ( line.trim().length() <= 0 )
                    continue;

                String[] explodedLine = line.split("\",@,\"");
                if( explodedLine.length != 12 )
                    throw new Exception("Invalid CSV format -> " + lineNum + " | " + line + " | " + explodedLine.length);

                explodedLine[0] = explodedLine[0].replaceAll("\"", "");
                explodedLine[11] = explodedLine[11].replaceAll("\"", "");

                // Alimentando o PreparedStatment para a escrita
                ps.setInt(1, Integer.parseInt(explodedLine[0].replaceAll("[^\\d.]", "")));
                ps.setLong(2, Long.parseLong(explodedLine[1].replaceAll("[^\\d.]", "")));
                ps.setString(3, explodedLine[2].replaceAll("\"", ""));
                ps.setString(4, explodedLine[3].replaceAll("\"", ""));
                ps.setInt(5, Integer.parseInt(explodedLine[4].replaceAll("[^\\d.]", "")));
                ps.setInt(6, Integer.parseInt(explodedLine[5].replaceAll("[^\\d.]", "")));
                ps.setInt(7, Integer.parseInt(explodedLine[6].replaceAll("[^\\d.]", "")));
                ps.setDate(8, new Date( sdf.parse( explodedLine[7].replaceAll("\"", "")).getTime() ));
                ps.setString(9, explodedLine[8].replaceAll("\"", ""));
                ps.setInt(10, Integer.parseInt(explodedLine[9].replaceAll("[^\\d.]", "")));
                ps.setInt(11, Integer.parseInt(explodedLine[10].replaceAll("[^\\d.]", "")));
                ps.setBoolean(12, (explodedLine[11].replaceAll("\"", "").toUpperCase().equals("TRUE") || explodedLine[11].replaceAll("\"", "").equals("1")));

                this.executarPreparedStatement(ps);
                lineNum += 1;
            }

            // Retornando sucesso!
            return true;
        } catch (Exception ex ) {
            System.out.println("ImportError -> " + ex.getMessage());
            return false;
        } finally {
            // Fechando o arquivo
            try {
                if (reader != null) reader.close();
                if (fileReader != null) fileReader.close();
            } catch ( Exception ex ) { /*Nothing to do */ }
        }
    }


    private void TimeWaiter(int seconds) throws Exception
    {
        for ( int idx = 0; idx < (seconds * 2); idx++ )
        {
            Thread.sleep(500);
        }
    }
}
