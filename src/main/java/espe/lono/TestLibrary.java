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
//        EngineAction.LONO_BACKEND_URL = "http://localhost:8080";
//        EngineAction engineAction = new EngineAction();
//        engineAction.notifyWebRelevantesUpdate(60, "job-teste");

        // Definindo conexao com o banco de dados de homo
        LonoDatabaseConfigs.DBLONO_DBNAME = "lono_homo";
        LonoDatabaseConfigs.DBLONO_HOSTNAME = "52.67.3.92";
        LonoDatabaseConfigs.DBLONO_USERNAME = "postgres";
        LonoDatabaseConfigs.DBLONO_PASSWORD = "fsbr@postgres";
        LonoDatabaseConfigs.DBLONO_PORT = 5432;

        DbConnection dbConnection = new DbPostgres();

        // Listando nomes-pesquisa
        NomePesquisaCliente nomePesquisaCliente = new ClienteDAO().listarNomePesquisaPorIdJornalSituacao(361, 63, "A", dbConnection);


        List<BackserviceActions> response = BackServiceDAO.ObterRequisicaoProcessamento_Veiculos_GroupByCliente(dbConnection);
        BackServiceDAO.LiberarBackServiceActionsList(response, dbConnection);

        response = BackServiceDAO.ObterRequisicaoProcessamento_Jornal(dbConnection, 2);
        BackServiceDAO.LiberarBackServiceActionsList(response, dbConnection);

        response = BackServiceDAO.ObterRequisicaoProcessamento_Jornal(dbConnection, "PJE", 2);
        BackServiceDAO.LiberarBackServiceActionsList(response, dbConnection);
    }
}
