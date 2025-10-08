package espe.lono.util;

import espe.lono.db.LonoDatabaseConfigs;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.connections.drivers.DbPostgres;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DatabaseUtilTest {

    public static DbConnection getTestDbConnection() throws SQLException {
        // Configurações para o ambiente de dessenvolvimento
        LonoDatabaseConfigs.DBLONO_DBNAME = "lono";
        LonoDatabaseConfigs.DBLONO_HOSTNAME = "52.67.3.92";
        LonoDatabaseConfigs.DBLONO_USERNAME = "postgres";
        LonoDatabaseConfigs.DBLONO_PASSWORD = "fsbr@postgres";
        LonoDatabaseConfigs.DBLONO_PORT = 5431;

        return new DbPostgres();
    }

    @Test
    void testConexaoNaoLancaExcecao() {
        assertDoesNotThrow(() -> {
            DbConnection conn = getTestDbConnection();
            conn.conectar();
        }, "A conexão com o banco de dados deveria funcionar sem lançar exceção.");
    }
}
