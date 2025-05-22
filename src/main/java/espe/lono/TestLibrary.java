package espe.lono;

import espe.lono.config.LonoConfigLoader;
import espe.lono.db.LonoDatabaseConfigs;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.connections.drivers.DbPostgres;
import espe.lono.db.dao.BackServiceDAO;
import espe.lono.db.dao.ClienteDAO;
import espe.lono.db.models.BackserviceActions;
import espe.lono.db.models.NomePesquisaCliente;
import espe.lono.engine.EngineAction;

import java.sql.SQLException;
import java.util.List;

public class TestLibrary {
    public static void main(String[] args) throws Exception {
        // Testando envio de notificacao p/ o backend
        EngineAction.LONO_BACKEND_URL = "https://applications.fsbr.com.br/homolog/lono--backend";
        EngineAction engineAction = new EngineAction();
//        engineAction.notifyWebRelevantesUpdate();

        // Definindo conexao com o banco de dados de homo
        LonoDatabaseConfigs.DBLONO_DBNAME = "lono_homo";
        LonoDatabaseConfigs.DBLONO_HOSTNAME = "52.67.3.92";
        LonoDatabaseConfigs.DBLONO_USERNAME = "postgres";
        LonoDatabaseConfigs.DBLONO_PASSWORD = "fsbr@postgres";
        LonoDatabaseConfigs.DBLONO_PORT = 5432;

        DbConnection dbConnection = new DbPostgres();

        // Listando nomes-pesquisa
        NomePesquisaCliente nomePesquisaCliente = new ClienteDAO().listarNomePesquisaPorID(466, dbConnection);


        List<BackserviceActions> response = BackServiceDAO.ObterRequisicaoProcessamento_Veiculos(dbConnection, 2);
        BackServiceDAO.LiberarBackServiceActionsList(response, dbConnection);

        response = BackServiceDAO.ObterRequisicaoProcessamento_Jornal(dbConnection, 2);
        BackServiceDAO.LiberarBackServiceActionsList(response, dbConnection);

        response = BackServiceDAO.ObterRequisicaoProcessamento_Jornal(dbConnection, "PJE", 2);
        BackServiceDAO.LiberarBackServiceActionsList(response, dbConnection);
    }
}
