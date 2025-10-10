package espe.lono.utilTest;

import espe.lono.db.Fachada;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.PublicacaoJornal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static espe.lono.utilTest.DatabaseUtilTest.getTestDbConnection;

public class PublicacaoJornalUtil {

    public int idPublicacao() {

        /*
        Manualmente
        Baixe uma publicacao
        Ex: Portal DJPE

        https://www2.tjpe.jus.br/dje/djeletronico
        Baixei o 2023-08-17
        DJ126_2023-ASSINADO.PDF

        tabela jornal - Pegue o idJornal
        Ex: na tabela jornal o id_jornal do DJPE = 9

        tabela materia_publicacao =
        Crie um novo ROW
        Adicione o nome do arquivo
        Adicione o nome do id_jornal
        copie o materia_publicacao.id gerado
        retorne o id
         */
        return 1031;
    }

    public PublicacaoJornal getPublicacaoJornal(DbConnection dbConnection) throws SQLException {
        final Fachada fachada = new Fachada();

        return fachada.listarPublicacoesPorID(idPublicacao(), dbConnection);
    }

    @Test
    void verificaRetornodeMateriaPublicacaoPorID() throws SQLException {
        final Fachada fachada = new Fachada();
        DbConnection dbConnection = getTestDbConnection();

        int id = idPublicacao();
        PublicacaoJornal publicacao = fachada.listarPublicacoesPorID(id, dbConnection);


        // se a tabela materia_publicacao nao tiver o id ele retornara null
        // exemplo de falha = id -1
        Assertions.assertNotNull(publicacao);
    }





}
