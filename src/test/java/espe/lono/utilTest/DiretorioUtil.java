package espe.lono.utilTest;

import espe.lono.indexercore.LonoIndexerConfigs;

public class DiretorioUtil {

    public static void configurarDiretoriosIndexacao() {
        // ATENÇÃO: MANUALMENTE essas pastas precisam ser criadas no sistema de arquivos
        LonoIndexerConfigs.INDEXER_DIRETORIO_PUBLICACAO = "C:/Projetos/FSBR/lono/arquivos/publico/";
        LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS = "C:/Projetos/FSBR/lono/arquivos/documentos";
    }


}
