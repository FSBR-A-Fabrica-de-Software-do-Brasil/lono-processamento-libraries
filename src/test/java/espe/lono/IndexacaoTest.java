package espe.lono;

import espe.lono.db.Fachada;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.PublicacaoJornal;
import espe.lono.indexercore.LonoIndexerConfigs;
import espe.lono.indexercore.LonoIndexerCore;
import espe.lono.indexercore.data.LonoIndexData;
import espe.lono.util.DiretorioUtil;
import espe.lono.util.PublicacaoJornalUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import static espe.lono.indexercore.util.Util.criarDiretorio;
import static espe.lono.indexercore.util.Util.moverArquivo;
import static espe.lono.util.DatabaseUtilTest.getTestDbConnection;

public class IndexacaoTest {


    @Test
    void testarIndexacao() throws Exception {

        DbConnection dbConnection = getTestDbConnection();

        // Manualmente configurar Diretorios
        DiretorioUtil.configurarDiretoriosIndexacao();

        // Manualmente configurar publicacao Jornal
        PublicacaoJornalUtil publicacaoJornalUtil = new PublicacaoJornalUtil();

        PublicacaoJornal publicacaoJornal = publicacaoJornalUtil.getPublicacaoJornal(dbConnection);
        testarIndexacao(publicacaoJornal, dbConnection);

    }

    private static void testarIndexacao(PublicacaoJornal publicacaoJornal, DbConnection dbConnection) throws Exception {


        final String diretorio_indice = publicacaoJornal.getCaminhoDirPublicacaoIndice(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS);
        final String diretorio_origem = LonoIndexerConfigs.INDEXER_DIRETORIO_PUBLICACAO;
        criarDiretorio(diretorio_indice);

        // ATENÇÃO Manualmente: colocar o pdf no caminhoArquivo
        // Verificar se o nome do arquivo local é o mesmo do banco de dados
        String caminhoArquivo = diretorio_origem + publicacaoJornal.getArqPublicacao();
        String diretorioDestino = publicacaoJornal.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS);

        if(!testarmoverArquivo(caminhoArquivo, diretorioDestino)){
            throw new Exception("Erro ao mover o arquivo de publicacao para o diretorio de processamento");
        }

        // apos mover, muda status
        final Fachada fachada = new Fachada();
        fachada.alterarSituacaoPublicacao(publicacaoJornal.getIdPublicacao(), PublicacaoJornal.Status.SIT_ARQ_MOV_AGUARDANDO_PROCESSAMENTO, dbConnection);

        LonoIndexerCore indexerCore = new LonoIndexerCore(dbConnection, new Object());
        LonoIndexData indexerResponseData = indexerCore.ExecutarIndexacao();
        System.out.println();
    }

    private static boolean testarmoverArquivo(String caminhoArquivo, String diretorioDestino) {
        File arquivo = new File(caminhoArquivo);
        File destinoDir = new File(diretorioDestino);

        // Verifica se o arquivo existe
        if (!arquivo.exists()) {
            System.err.println("Arquivo não existe: " + caminhoArquivo);
            return false;
        }

        // Cria o diretório de destino se necessário
        if (!destinoDir.exists()) {
            destinoDir.mkdirs();
        }

        Path origem = arquivo.toPath();
        Path destino = Paths.get(diretorioDestino, arquivo.getName());

        try {
            Files.move(origem, destino, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao mover arquivo: " + e.getMessage());
            return false;
        }
    }


}
