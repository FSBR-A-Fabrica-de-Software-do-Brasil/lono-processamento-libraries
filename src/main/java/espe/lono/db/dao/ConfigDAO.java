package espe.lono.db.dao;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.Configuracao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConfigDAO {
    public Configuracao obterConfiguracao(String key, DbConnection dbconn) throws SQLException {
        final Statement stm = dbconn.obterStatement();
        final String sqlcmd = "SElECT value_config as valor_primario, '' as valor_secundario, '' as valor_terciario " +
                "FROM lono_configs WHERE cod_config='" + key + "' " +
                "AND sit_cad='A' " +
                "union all " +
                "select valor_primario, valor_secundario, valor_terciario " +
                "from configuracoes " +
                "where nome_interno = '" + key + "'";

        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        if ( resultado.next() ) {
            return new Configuracao(
                    resultado.getString("valor_primario"),
                    resultado.getString("valor_secundario"),
                    resultado.getString("valor_terciario")
            );
        } else {
            return new Configuracao(null, null, null);
        }
    }
}
