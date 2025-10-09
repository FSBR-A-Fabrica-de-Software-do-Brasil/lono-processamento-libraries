package espe.lono;

import espe.lono.config.LonoConfigLoader;
import espe.lono.db.Fachada;
import espe.lono.db.LonoDatabaseConfigs;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.connections.drivers.DbPostgres;
import espe.lono.db.connections.drivers.DbPostgresMarcacao;
import espe.lono.db.dao.BackServiceDAO;
import espe.lono.db.dao.ClienteDAO;
import espe.lono.db.models.*;
import espe.lono.engine.EngineAction;
import espe.lono.indexercore.LonoIndexerConfigs;
import espe.lono.indexercore.LonoIndexerCore;
import espe.lono.indexercore.data.LonoIndexData;
import espe.lono.textsearcher.LonoTextSearcherConfigs;
import espe.lono.textsearcher.core.Colisao;
import espe.lono.textsearcher.textsearcher.LonoTextSearcher;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestLibrary {
    public static void main(String[] args) throws Exception {
        // Definindo conexao com o banco de dados de homo
        LonoDatabaseConfigs.DBLONO_DBNAME = "lono";
        LonoDatabaseConfigs.DBLONO_HOSTNAME = "52.67.3.92";
        LonoDatabaseConfigs.DBLONO_USERNAME = "postgres";
        LonoDatabaseConfigs.DBLONO_PASSWORD = "fsbr@postgres";
        LonoDatabaseConfigs.DBLONO_PORT = 5431;

        String currentDir = System.getProperty("user.dir");
        System.out.println("Diretório atual: " + currentDir);

        LonoIndexerConfigs.INDEXER_DIRETORIO_PUBLICACAO = currentDir + "/publico/";
        LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS = currentDir + "/documentos/";

//        final String caminhoDirPublicacao = "C:/Projetos/FSBR/Lono/lono-processamento/lono-processamento-libraries/teste-indices";
//        TestarPesquisa("Nome", "Nome exttra", caminhoDirPublicacao);

        DbConnection dbConnection = new DbPostgres();
//        DbConnectionMarcacao dbConnectionMarcacao = new DbPostgresMarcacao(0);
        final Fachada fachada = new Fachada();

        PublicacaoJornal publicacaoJornal = fachada.listarPublicacoesPorID(1030, dbConnection);
//        FluxoCompletoIndexacaoPesquisa(dbConnection, publicacaoJornal, "Maria Jose");

//        TestarIndexacao(dbConnection, publicacaoJornal);
        String searchDir = publicacaoJornal.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS);
        searchDir = "C:\\Projetos\\FSBR\\lono\\lono-processamento-libraries\\documentos\\djpe";
        TestarPesquisa(dbConnection, null, publicacaoJornal, "bv finânceira",    "01.149.953/0001-89", searchDir);
    }

    public static void FluxoCompletoIndexacaoPesquisa(DbConnection dbConnection, PublicacaoJornal publicacao, String textoPesquisa) throws Exception {
//         Indexacao
//        LonoIndexData indexData;
//        try {
//            indexData = TestarIndexacao(dbConnection, publicacao);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }

        // Pesquisa
        DbConnectionMarcacao marcacao = new DbPostgresMarcacao(publicacao.getIdPublicacao());
        try {
            TestarPesquisa(dbConnection, marcacao, publicacao, textoPesquisa, "", publicacao.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static LonoIndexData TestarIndexacao(DbConnection dbconn, PublicacaoJornal publicacao) throws Exception {
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
        return indexerResponseData;
    }


    public static void TestarPesquisa(DbConnection dbconn, DbConnectionMarcacao dbConnectionMarcacao, PublicacaoJornal publicacaoJornal, String pesquisaNome, String pesquisaNomeExt, String caminhoDirPublicacao) throws Exception {
        final String caminhoDirPublicacaoIndiceMarcacao = caminhoDirPublicacao + "/indice";
        final String caminhoDirPublicacaoIndicePesquisa = caminhoDirPublicacao + "/indice_pesquisa";

        Directory luceneDirMarcacao = null;
        Directory luceneDirPesquisa = null;
        luceneDirMarcacao = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndiceMarcacao));
        luceneDirPesquisa = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndicePesquisa));

        final String name2srch = LonoTextSearcher.NormalizarTextoPesquisa(pesquisaNome, dbconn);
        final String nome2srchExt = LonoTextSearcher.NormalizarTextoPesquisa(pesquisaNomeExt, dbconn);

        final DirectoryReader readerMarcacao = DirectoryReader.open(luceneDirMarcacao);
        final DirectoryReader readerPesquisa = DirectoryReader.open(luceneDirPesquisa);
        Object[][] results = LonoTextSearcher.pesquisarTermo_Normal(name2srch, nome2srchExt, readerPesquisa, readerMarcacao, "contents", true, false);


        final Pattern namePattern = Pattern.compile("(" + name2srch.replaceAll(" ", ".").toLowerCase().trim() + ")");
        final Matcher matcher = namePattern.matcher("");

        // Processando os resultados
        final NomePesquisaCliente cliente = new NomePesquisaCliente();
        cliente.setIdCliente(9999);
        cliente.setIdNomePesquisa(9999);
        cliente.setNomePesquisa(pesquisaNome);
        cliente.setNomePesquisaExt(pesquisaNomeExt);

        Colisao colisaoMateria = new Colisao();
        Map<Integer, List<Integer>> clientesMaterias = new HashMap<Integer, List<Integer>>();
        List<MateriaPublicacao> materias = new ArrayList<>();
        List<PautaPublicacao> pautas = new ArrayList<>();
        for ( final Object[] array: results )
        {

            Object[] ocorrenciaTratadaObjs = LonoTextSearcher.tratarOcorrenciaLucene(array, readerMarcacao, matcher, colisaoMateria, cliente, publicacaoJornal, dbConnectionMarcacao, dbconn);
            if ( ocorrenciaTratadaObjs == null ) // Estrutura de dados retornando é valido?
                continue; // Ingorando...

            // Obtendo os dados
            MateriaPublicacao materiaPub = (MateriaPublicacao) ocorrenciaTratadaObjs[0];
            PautaPublicacao pautaPub = (PautaPublicacao) ocorrenciaTratadaObjs[1];
            if ( materiaPub != null && !materias.contains(materiaPub) )
                materias.add(materiaPub);
            if ( pautaPub != null && !pautas.contains(pautaPub) )
                pautas.add(pautaPub);
        }

        System.out.println("Resultados encontrados: " + materias.size());

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
