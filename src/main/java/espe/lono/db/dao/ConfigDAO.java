package espe.lono.db.dao;

import espe.lono.db.connections.DbConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConfigDAO {
    public String obterConfiguracao(String key, DbConnection dbconn) throws SQLException {
        final Statement stm = dbconn.obterStatement();
        final String sqlcmd = "SElECT value_config FROM lono_configs WHERE cod_config='" + key + "' AND sit_cad='A'";

        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        if ( resultado.next() ) {
            return resultado.getString("value_config");
        } else {
            return null;
        }
    }
}
