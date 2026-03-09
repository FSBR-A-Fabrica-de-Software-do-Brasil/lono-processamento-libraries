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
import espe.lono.db.dao.PublicacaoDAO;
import espe.lono.db.models.*;
import espe.lono.engine.EngineAction;
import espe.lono.ia.IAConfigs;
import espe.lono.ia.IAEngines;
import espe.lono.ia.IARequests;
import espe.lono.ia.data.CorteJuridico;
import espe.lono.indexercore.LonoIndexerConfigs;
import espe.lono.indexercore.LonoIndexerCore;
import espe.lono.indexercore.data.LonoIndexData;
import espe.lono.textsearcher.LonoTextSearcherConfigs;
import espe.lono.textsearcher.core.Colisao;
import espe.lono.textsearcher.textsearcher.LonoTextSearcher;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        final String log4jConfigFile = "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);

        Logger logger = Logger.getLogger("engine");
        LonoIndexerConfigs.INDEXER_LOG4_LOGGER = logger;
        LonoTextSearcherConfigs.SEARCHER_LOG4_LOGGER = logger;

        // Definindo conexao com o banco de dados de homo
        LonoDatabaseConfigs.DBLONO_DBNAME = "lono_homo";
        LonoDatabaseConfigs.DBLONO_HOSTNAME = "52.67.3.92";
        LonoDatabaseConfigs.DBLONO_USERNAME = "postgres";
        LonoDatabaseConfigs.DBLONO_PASSWORD = "fsbr@postgres";
        LonoDatabaseConfigs.DBLONO_PORT = 5432;
        IAConfigs.OPENAI_TOKEN = "";

        String currentDir = System.getProperty("user.dir");
        System.out.println("Diretório atual: " + currentDir);

        LonoIndexerConfigs.INDEXER_DIRETORIO_PUBLICACAO = currentDir + "/publico/";
        LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS = currentDir + "/documentos/";

//        final String caminhoDirPublicacao = "C:/Projetos/FSBR/Lono/lono-processamento/lono-processamento-libraries/teste-indices";
//        TestarPesquisa("Nome", "Nome exttra", caminhoDirPublicacao);

        DbConnection dbConnection = new DbPostgres();
        final Fachada fachada = new Fachada();

//        PublicacaoJornal publicacaoJornal = fachada.listarPublicacoesPorID(78126, dbConnection);
//        FluxoCompletoIndexacaoPesquisa(dbConnection, publicacaoJornal, "Maria Jose");
//
//        TestarIndexacao(dbConnection, publicacaoJornal);
//        String searchDir = publicacaoJornal.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS);
//        searchDir = "C:\\Projetos\\FSBR\\lono\\lono-processamento-libraries\\documentos\\djpe";
//        TestarPesquisa(dbConnection, null, publicacaoJornal, "038.499.054-11",    "Antonio de Moraes Dourado Neto", searchDir);

        IARequests.inicializar();
        IARequests iaRequests = new IARequests();

        // Testando corte juridico da materia
        String materiaTexto = "1. Agravo em Recurso Especial interposto contra decisão que inadmitiu recurso especial fundado no  III, \"a\" e \"c\", da Constituição Federal, em que se  art. 105, discute  a  fixação  e  majoração  de  honorários  sucumbenciais  em  ação  ordinária cumulada com obrigação de fazer e não fazer. 2. A majoração dos honorários em grau recursal, nos termos do  § 11, do  art. 85,   foi  realizada  de  forma  fundamentada,  considerando  o  trabalho CPC/2015, adicional  realizado  em  segunda  instância,  não  havendo  irregularidade  na aplicação do dispositivo. 3.  Não  se  vislumbra  violação  ao   §  1º,  do   pois  a  decisão   art.  489,   CPC/2015, recorrida enfrentou os argumentos essenciais ao deslinde da controvérsia, sendo inadmissível a alegação de ausência de fundamentação apenas por não acolher a tese da parte recorrente. 4.  A  revisão  do  montante  dos  honorários  fixados  pelas  instâncias  ordinárias encontra  impedimento  na   salvo  em  hipóteses  excepcionais  de   Súmula  7/STJ, valores  manifestamente  irrisórios  ou  exorbitantes,  o  que  não  se  configura  no caso. 5. Recurso desprovido. ACÓRDÃO Vistos  e  relatados  estes  autos  em  que  são  partes  as  acima  indicadas,  acordam  os Ministros da QUARTA TURMA do Superior Tribunal de Justiça, em Sessão Virtual de 28/10/2025 a   por  unanimidade,  conhecer  do  recurso  mas  lhe  negar  provimento,  nos  termos  do   03/11/2025, voto do Sr. Ministro Relator. Os Srs. Ministros Maria Isabel Gallotti, Antonio Carlos Ferreira, Marco Buzzi e João Otávio de Noronha votaram com o Sr. Ministro Relator. Presidiu o julgamento o Sr. Ministro João Otávio de Noronha.   Brasília,  . 06 de novembro de 2025   Ministro RAUL ARAÚJO Relator AGRAVO EM RECURSO ESPECIAL Nº 2464928 - SP (2023/0346348-7) RELATOR : MINISTRO RAUL ARAÚJO AGRAVANTE : BRADESCO SAUDE S/A ADVOGADA : ALESSANDRA MARQUES MARTINI - SP270825 AGRAVADO : MARIA ROSARIA MARTINI TONETTI ADVOGADOS : RENATA VILHENA SILVA - SP147954 ISAURA MARIA MOREIRA SARTO TAGLIALEGNA - DF028195 EMENTA DIREITO  DO  CONSUMIDOR.  AGRAVO  EM  RECURSO  ESPECIAL. PLANO  DE  SAÚDE.  MANUTENÇÃO  DE  CONTRATO  APÓS  DIVÓRCIO. APLICAÇÃO  ANALÓGICA  DA  LEGISLAÇÃO  E  NORMAS  DA  ANS. RECURSO DESPROVIDO.";
        System.out.println("Texto original:\n" + materiaTexto);

        CorteJuridico corteJuridico = iaRequests.realizarCorteJuridico(IAEngines.OpenAI, materiaTexto, "BRADESCO");
        System.out.println("\nTexto após corte jurídico:\n" + corteJuridico.getMateria() + "\n\nTítulo: " + corteJuridico.getTitulo());
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
