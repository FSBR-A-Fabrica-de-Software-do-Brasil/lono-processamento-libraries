package espe.lono.db.negocios;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.dao.ConfigDAO;
import espe.lono.db.models.Configuracao;

import java.sql.SQLException;

public class ConfigNegocio {
    protected ConfigDAO configDAO = new ConfigDAO();
    public Configuracao obterConfiguracao(String key, DbConnection dbconn) throws SQLException {
        return configDAO.obterConfiguracao(key, dbconn);
    }
}
