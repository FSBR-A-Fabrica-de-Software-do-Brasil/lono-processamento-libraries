package espe.lono.db.connections.drivers;

import espe.lono.db.LonoDatabaseConfigs;
import espe.lono.db.connections.DbConnection;
import java.sql.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.dbcp2.BasicDataSource;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

/**
 * @version 1.1
 * @author Petrus Augusto - Espe
 * @since 16/09/2016
 */

public class DbPostgres extends DbConnection
{
    // Static: Inicializando e entrega conexoes do Pool do Postgres
    private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private static BasicDataSource connectionPool = null;
    private boolean autoCommit = true;

    private static synchronized Connection GetConnectionFromPooling() throws SQLException {
        int loopCount = 0;
        while ( (loopCount++) <= 10 ) {
            if (atomicBoolean.compareAndSet(false, true)) {
                try {
                    if (connectionPool == null || connectionPool.isClosed()) {
                        connectionPool = new BasicDataSource();
                        connectionPool.setValidationQuery("SELECT id FROM usuario WHERE id=1");
                        connectionPool.setUsername(LonoDatabaseConfigs.DBLONO_USERNAME);
                        connectionPool.setPassword(LonoDatabaseConfigs.DBLONO_PASSWORD);
//                        connectionPool.setRemoveAbandonedOnBorrow(true);
//                        connectionPool.setRemoveAbandonedTimeout(90);
                        connectionPool.setLogAbandoned(true);
                        connectionPool.setMaxWaitMillis(30000);
                        connectionPool.setMaxTotal(12);
                        connectionPool.setUrl(String.format("jdbc:postgresql://%s:%d/%s", LonoDatabaseConfigs.DBLONO_HOSTNAME, LonoDatabaseConfigs.DBLONO_PORT, LonoDatabaseConfigs.DBLONO_DBNAME));
                        connectionPool.setDriverClassName("org.postgresql.Driver");
                        connectionPool.setInitialSize(1);
                    }

                    return connectionPool.getConnection();
                } finally {
                    atomicBoolean.set(false);
                }
            } else {
                try { Thread.sleep(1000); }
                catch (InterruptedException ignore) {}
            }
        }

        throw new PSQLException("Can not obtain a connection from the pool after 10 attempts.", PSQLState.CONNECTION_UNABLE_TO_CONNECT);
    }

    static {
        connectionPool = new BasicDataSource();
        connectionPool.setValidationQuery("SELECT id FROM usuario WHERE id=1");
        connectionPool.setUsername(LonoDatabaseConfigs.DBLONO_USERNAME);
        connectionPool.setPassword(LonoDatabaseConfigs.DBLONO_PASSWORD);
        connectionPool.setMaxTotal(12);
//        connectionPool.setRemoveAbandonedOnBorrow(true);
//        connectionPool.setRemoveAbandonedTimeout(90);
        connectionPool.setLogAbandoned(true);
        connectionPool.setMaxWaitMillis(30000);
        connectionPool.setUrl(String.format("jdbc:postgresql://%s:%d/%s", LonoDatabaseConfigs.DBLONO_HOSTNAME, LonoDatabaseConfigs.DBLONO_PORT, LonoDatabaseConfigs.DBLONO_DBNAME));
        connectionPool.setDriverClassName("org.postgresql.Driver");
        connectionPool.setInitialSize(1);
    }

    // Propriedades/Metodos da classe em uso Geral
    private Connection conn = null;
    private Statement stmt = null;

    public DbPostgres() throws SQLException
    {
        this.conectar();
    }

    @Override
    public final void conectar() throws SQLException
    {
        for ( int contador = 0; contador <= 3; contador++ )
        {
            try
            {
                // Inicializando conexao com o banco de dados
                if ( this.conn == null || this.conn.isClosed() )
                {
                    this.conn = DbPostgres.GetConnectionFromPooling();
                    this.stmt = this.conn.createStatement();
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
    public void desconectar() throws SQLException
    {
        if ( this.stmt != null && !this.stmt.isClosed())
            this.stmt.close();


        this.conn.close();
    }

    @Override
    public int executeSqlLID( String sql ) throws SQLException
    {
        PreparedStatement stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stm.executeUpdate();
        ResultSet set = stm.getGeneratedKeys();
        if ( !set.next() ) return 0;

        int gen_id = set.getInt(1);
        set.close();

        // Retornando ID
        return gen_id;
    }

    @Override
    public boolean executarSql( String sql ) throws SQLException
    {
        try {
            return this.stmt.execute(sql);
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        return this.stmt.execute(sql);
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public boolean executarSql( PreparedStatement stm ) throws SQLException
    {
        try {
            return stm.execute();
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        return stm.execute();
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public ResultSet abrirConsultaSql( Statement stm, String sqlCommand ) throws SQLException
    {
        try {
            return stm.executeQuery(sqlCommand);
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        return this.conn.createStatement().executeQuery(sqlCommand);
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public ResultSet executarSql( PreparedStatement stm, boolean returnQuery ) throws SQLException
    {
        ResultSet resultSet;
        try {
            resultSet = stm.executeQuery();
            if ( returnQuery ) return resultSet;
            else {
                resultSet.close();
                return null;
            }
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        resultSet = stm.executeQuery();
                        if ( returnQuery ) return resultSet;
                        else {
                            resultSet.close();
                            return null;
                        }
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public Statement obterStatement() throws SQLException
    {
        try {
            return this.conn.createStatement();
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        return this.conn.createStatement();
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public PreparedStatement obterPreparedStatement(String sql) throws SQLException {
        try {
            return this.conn.prepareStatement(sql);
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        return this.conn.prepareStatement(sql);
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public PreparedStatement obterPreparedStatement(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            return this.conn.prepareStatement(sql, autoGeneratedKeys);
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        return this.conn.prepareStatement(sql);
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public void iniciarTransaction() throws SQLException
    {
        if ( !autoCommit ) return;
        try {
            this.conn.setAutoCommit(false);
            this.autoCommit = false;
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        this.conn.setAutoCommit(false);
                        this.autoCommit = false;
                        break;
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public void finalizarTransaction_COMMIT() throws SQLException
    {
        if ( autoCommit ) return;
        try {
            this.conn.commit();
            this.conn.setAutoCommit(true);
            this.autoCommit = true;
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        this.conn.setAutoCommit(true);
                        this.autoCommit = true;
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public void finalizarTransaction_ROLLBACK() throws SQLException
    {
        if ( autoCommit ) return;
        try {
            this.conn.rollback();
            this.conn.setAutoCommit(true);
            this.autoCommit = true;
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        this.conn.setAutoCommit(true);
                        this.autoCommit = true;
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }

    @Override
    public ResultSet abrirConsultaSql( String sqlCommand ) throws SQLException
    {
        try {
            return this.stmt.executeQuery(sqlCommand);
        } catch ( PSQLException ex ) {
            // Tratando a excessão do banco de modo a solucionar o problema
            switch ( ex.getSQLState() ) {
                case "57P01": // Admin shutdown
                case "57P02": // Crash shutdown
                case "58030": // IO error
                case "08000": // Connection Exception
                case "08003": // Connection does not exists
                case "08006": // Connection Failure
                case "08001": // SQLCliente unbale establishe connection
                    // Tentando reconectar-se com o banco de dados
                    if ( this.reestablishDBConnection() ) {
                        return this.stmt.executeQuery(sqlCommand);
                    }
                default: // Ação padrão
                    throw ex;
            }
        }
    }


    private void TimeWaiter(int seconds) throws Exception
    {
        for ( int idx = 0; idx < (seconds * 2); idx++ )
        {
            Thread.sleep(500);
        }
    }

    public boolean reestablishDBConnection() {
        int sleepTime = 15000;
        boolean connectionIsOk = false;
        for ( int nTry = 0; nTry < LonoDatabaseConfigs.DBLONO_MAXRETRYCONN; nTry++ ) {
            try {
                // Aguardando o tempo p/ o banco se estabilizar
                System.err.println("Aguardando " + (sleepTime / 1000) + " segundos antes de iniciar a conexão com o banco");
                Thread.sleep(sleepTime);

                // Tentando dar lock e entrando ni trecho
                int loopCount = 0;
                while ( (connectionPool != null) && (loopCount++ <= 10) ) {
                    if (atomicBoolean.compareAndSet(false, true)) {
                        try {
                            if (connectionPool != null && !connectionPool.isClosed()) {
                                connectionPool.close();
                            }

                            connectionPool = null;
                            break;
                        } finally {
                            atomicBoolean.set(false);
                        }
                    } else {
                        try { Thread.sleep(1000); }
                        catch (InterruptedException ignore) {}
                    }
                }

                // Tentando se conectar ao banco
                System.err.println("Reiniciando a conexão com o banco de dados");
                this.conn = null;
                this.conectar();

                // Se chegar a esse ponto, é pq a conexão foi estabilizada,
                // continuando a execução normal
                connectionIsOk = true;
                System.err.println("A conexão com o banco estabilizada corretamente.");
                break;
            } catch ( Exception ex ) {
                System.err.println("A conexão com o banco não foi iniciada...");
                /* Connection is not alive */
            }
        }

        return connectionIsOk;
    }
}
