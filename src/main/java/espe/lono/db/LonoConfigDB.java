package espe.lono.db;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.enums.LonoConfigDB_Codes;
import espe.lono.db.models.Configuracao;
import espe.lono.db.negocios.ConfigNegocio;

import java.sql.SQLException;

public class LonoConfigDB {
    /**
     * Obtém uma configuração do banco de dados
     * @param config -> Configuração desejada
     * @param dbConnection -> Conexão com o banco Lono
     * @return -> Valor da configuração (NULL em erro ou desativada)
     */
    public static Configuracao GetConfig(LonoConfigDB_Codes config, DbConnection dbConnection) {
        ConfigNegocio configNegocio = new ConfigNegocio();
        try {
            return configNegocio.obterConfiguracao(config.getValue(), dbConnection);
        } catch (SQLException e) {
            return null;
        }
    }

    public static Configuracao GetConfig(String chave, DbConnection dbConnection) {
        ConfigNegocio configNegocio = new ConfigNegocio();
        try {
            return configNegocio.obterConfiguracao(chave, dbConnection);
        } catch (SQLException e) {
            return null;
        }
    }
}
