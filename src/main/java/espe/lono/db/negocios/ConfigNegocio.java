package espe.lono.db.negocios;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.dao.ConfigDAO;

import java.sql.SQLException;

public class ConfigNegocio {
    protected ConfigDAO configDAO = new ConfigDAO();
    public String obterConfiguracao(String key, DbConnection dbconn) throws SQLException {
        return configDAO.obterConfiguracao(key, dbconn);
    }
}
