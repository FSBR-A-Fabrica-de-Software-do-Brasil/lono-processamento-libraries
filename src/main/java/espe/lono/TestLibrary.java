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
import espe.lono.indexercore.LonoIndexerConfigs;
import espe.lono.textsearcher.textsearcher.LonoTextSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class TestLibrary {
    public static void main(String[] args) throws Exception {
        // Definindo conexao com o banco de dados de homo
        LonoDatabaseConfigs.DBLONO_DBNAME = "lono_homo";
        LonoDatabaseConfigs.DBLONO_HOSTNAME = "52.67.3.92";
        LonoDatabaseConfigs.DBLONO_USERNAME = "postgres";
        LonoDatabaseConfigs.DBLONO_PASSWORD = "fsbr@postgres";
        LonoDatabaseConfigs.DBLONO_PORT = 5432;

//        DbConnection dbConnection = new DbPostgres();

        Directory luceneDirMarcacao = null;
        Directory luceneDirPesquisa = null;
        final String caminhoDirPublicacao = "C:/Projetos/FSBR/Lono/lono-processamento/lono-processamento-libraries/teste-indices";
        final String caminhoDirPublicacaoIndiceMarcacao = caminhoDirPublicacao + "/indice";
        final String caminhoDirPublicacaoIndicePesquisa = caminhoDirPublicacao + "/indice_pesquisa";

        luceneDirMarcacao = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndiceMarcacao));
        luceneDirPesquisa = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndicePesquisa));

        final DirectoryReader readerMarcacao = DirectoryReader.open(luceneDirMarcacao);
        final DirectoryReader readerPesquisa = DirectoryReader.open(luceneDirPesquisa);
        String pesquisaNome = "LUCIANO";
        String pesquisaNomeExt = "ROSTIROLLA";
        Object[][] results = LonoTextSearcher.pesquisarTermo_Normal(pesquisaNome, pesquisaNomeExt, readerPesquisa, readerMarcacao, "contents", true, false);

        System.out.println("Resultados encontrados: " + results.length);

        readerPesquisa.close();
        readerMarcacao.close();
        luceneDirPesquisa.close();
        luceneDirMarcacao.close();
    }
}
