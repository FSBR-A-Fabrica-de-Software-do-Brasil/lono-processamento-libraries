package espe.lono;

import espe.lono.config.LonoConfigLoader;
import espe.lono.db.Fachada;
import espe.lono.db.LonoDatabaseConfigs;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.connections.drivers.DbPostgres;
import espe.lono.db.dao.BackServiceDAO;
import espe.lono.db.dao.ClienteDAO;
import espe.lono.db.models.BackserviceActions;
import espe.lono.db.models.NomePesquisaCliente;
import espe.lono.db.models.PublicacaoJornal;
import espe.lono.engine.EngineAction;
import espe.lono.indexercore.LonoIndexerConfigs;
import espe.lono.indexercore.LonoIndexerCore;
import espe.lono.indexercore.data.LonoIndexData;
import espe.lono.textsearcher.textsearcher.LonoTextSearcher;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class TestLibrary {
    public static void main(String[] args) throws Exception {
        // Definindo conexao com o banco de dados de homo
        LonoDatabaseConfigs.DBLONO_DBNAME = "lono";
        LonoDatabaseConfigs.DBLONO_HOSTNAME = "52.67.3.92";
        LonoDatabaseConfigs.DBLONO_USERNAME = "postgres";
        LonoDatabaseConfigs.DBLONO_PASSWORD = "fsbr@postgres";
        LonoDatabaseConfigs.DBLONO_PORT = 5431;

        LonoIndexerConfigs.INDEXER_DIRETORIO_PUBLICACAO = "C:/Projetos/FSBR/lono/arquivos/publico/";
        LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS = "C:/Projetos/FSBR/lono/arquivos/documentos/";

//        final String caminhoDirPublicacao = "C:/Projetos/FSBR/Lono/lono-processamento/lono-processamento-libraries/teste-indices";
//        TestarPesquisa("Nome", "Nome exttra", caminhoDirPublicacao);

        DbConnection dbConnection = new DbPostgres();
        final Fachada fachada = new Fachada();

        TestarPesquisa("Maria Jose",    "", "C:/Projetos/FSBR/lono/arquivos/documentos/2025");

//        PublicacaoJornal publicacaoJornal = fachada.listarPublicacoesPorID(1030, dbConnection);
//        TestarIndexacao(dbConnection, publicacaoJornal);
    }


    public static void TestarIndexacao(DbConnection dbconn, PublicacaoJornal publicacao) throws Exception {
        //....
        final String diretorio_indice = publicacao.getCaminhoDirPublicacaoIndice(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS);
        final String diretorio_origem = LonoIndexerConfigs.INDEXER_DIRETORIO_PUBLICACAO;
        criarDiretorio(diretorio_indice);
        if (!moverArquivo(diretorio_origem + publicacao.getArqPublicacao(), publicacao.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS))) {
            throw new Exception("Erro ao mover o arquivo de publicação para o diretório de processamento");
        }

        final Fachada fachada = new Fachada();
        fachada.alterarSituacaoPublicacao(publicacao.getIdPublicacao(), PublicacaoJornal.Status.SIT_ARQ_MOV_AGUARDANDO_PROCESSAMENTO, dbconn);

        LonoIndexerCore indexerCore = new LonoIndexerCore(dbconn, new Object());
        LonoIndexData indexerResponseData = indexerCore.ExecutarIndexacao();
    }


    public static void TestarPesquisa(String pesquisaNome, String pesquisaNomeExt, String caminhoDirPublicacao) throws Exception {
        final String caminhoDirPublicacaoIndiceMarcacao = caminhoDirPublicacao + "/indice";
        final String caminhoDirPublicacaoIndicePesquisa = caminhoDirPublicacao + "/indice_pesquisa";

        Directory luceneDirMarcacao = null;
        Directory luceneDirPesquisa = null;
        luceneDirMarcacao = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndiceMarcacao));
        luceneDirPesquisa = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndicePesquisa));

        final DirectoryReader readerMarcacao = DirectoryReader.open(luceneDirMarcacao);
        final DirectoryReader readerPesquisa = DirectoryReader.open(luceneDirPesquisa);
        Object[][] results = LonoTextSearcher.pesquisarTermo_Normal(pesquisaNome, pesquisaNomeExt, readerPesquisa, readerMarcacao, "contents", true, false);

        System.out.println("Resultados encontrados: " + results.length);

        readerPesquisa.close();
        readerMarcacao.close();
        luceneDirPesquisa.close();
        luceneDirMarcacao.close();
    }




    private static boolean criarDiretorio(String caminho)
    {
        // Diratorio raiz
        File dir = new File(caminho);
        if ( dir.exists() ) {
            try { FileUtils.deleteDirectory(dir); }
            catch (Exception ignore ) {}
        }
        dir.mkdirs();

        // Diretorio onde contem os dados convertidos em HTML/PDF
        final String html_path = dir.getParent();
        dir = new File(html_path + "/html");
        if ( dir.exists() ) {
            try { FileUtils.deleteDirectory(dir); }
            catch (Exception ignore ) {}
        }
        dir.mkdirs();

        // Diretorio onde seram armazenados os dados de pesquisa...
        dir = new File(html_path + "/indice_pesquisa");
        if ( dir.exists() ) {
            try { FileUtils.deleteDirectory(dir); }
            catch (Exception ignore ) {}
        }
        dir.mkdirs();

        return true;
    }

    private static boolean moverArquivo(String caminhoArquivo, String diretorioDestino)
    {
        // Checando se existe o arquivo de destino
        File arquivo = new File(caminhoArquivo);
        File desArquivo = new File(diretorioDestino, arquivo.getName());
        if ( desArquivo.exists() ) desArquivo.delete();

        // Movendo o arquivo
        return arquivo.renameTo(new File(diretorioDestino, arquivo.getName()));
    }
}
