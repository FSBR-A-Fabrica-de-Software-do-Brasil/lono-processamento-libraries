package espe.lono.indexercore;

import com.sun.jna.Platform;

/**
 * @author ESPE
 * @date 06/12/2017
 */
public class LonoIndexerConfigs {
    // Referente a versao do LonoIndexer
    public static String INDEXER_VERSION = "1.3.3";
    public static Integer INDEXER_VERSIONCODE = 133;
    
    // Configuracoes relacionados as pastas de publicacao/processamento
    public static String INDEXER_DIRETORIO_PUBLICACAO = "C:/Lono/publico/";
    public static String INDEXER_DIRETORIO_DOCUMENTOS = "C:/Lono/documentos/";

    // LOG4J utilizado no Indexador
    public static org.apache.log4j.Logger INDEXER_LOG4_LOGGER = null;
    
    /**
     * Obtem o nome/comando do conversao de PDF para HTML
     * @return Nome do comando/programa
     */
    public static String GetPDFConversorAppName()
    {
        String app_name = "";
        if ( Platform.isWindows()) app_name = "extra/poppler/Library/bin/pdftohtml.exe";
        else if ( Platform.isLinux() ) app_name = "pdftohtml";
        
        return app_name;
    }
    
    /**
     * Obtem o nome do comando para executar os scripts Python
     * @return Nome do comando/programa
     */
    public static String GetPythonAppName()
    {
        return "python";
    }
}
