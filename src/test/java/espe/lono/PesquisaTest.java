package espe.lono;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.PublicacaoJornal;
import espe.lono.textsearcher.textsearcher.LonoTextSearcher;
import espe.lono.utilTest.DiretorioUtil;
import espe.lono.utilTest.PublicacaoJornalUtil;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static espe.lono.utilTest.DatabaseUtilTest.getTestDbConnection;

public class PesquisaTest {


    @Test
    void testar() throws Exception {

        DbConnection dbConnection = getTestDbConnection();

        // Manualmente configurar Diretorios
        DiretorioUtil.configurarDiretoriosIndexacao();

        // Manualmente configurar publicacao Jornal
        PublicacaoJornalUtil publicacaoJornalUtil = new PublicacaoJornalUtil();

        PublicacaoJornal publicacaoJornal = publicacaoJornalUtil.getPublicacaoJornal(dbConnection);

        String nome = "Maria";
        String nomeExt = " ";
        String caminhoDirPublicacao = "C:/Projetos/FSBR/lono/arquivos/documentos/2023/08/07/9/1031";
        testarPesquisa(nome, nomeExt, caminhoDirPublicacao);

    }

    private void testarPesquisa(String pesquisaNome, String pesquisaNomeExt, String caminhoDirPublicacao) throws Exception {
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

}
