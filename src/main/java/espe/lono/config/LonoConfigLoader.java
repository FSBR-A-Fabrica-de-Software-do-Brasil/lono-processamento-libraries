package espe.lono.config;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 * @author Luiz Diniz / Petrus Augusto - Espe
 * @since  10/06/2013 / 06/05/2019
 */
public class LonoConfigLoader {
    public static String CONFIGLOADER_VERSION = "1.0.0";
    public static Integer CONFIGLOADER_VERSIONCODE = 100;

    private static final String pesquisa_cem_procento = "true";
    private static final String CONFIG_FILE = "lono.properties";
    private static final String LOG4J_CONFIG_FILE = "log4j.properties";
    private static final Properties properties;

    // Inicializador da statico da classe
    static
    {
        properties = new Properties();
        try
        {
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            System.out.println("Current absolute path is: " + s);

            properties.loadFromXML(new FileInputStream(new File("conf/", CONFIG_FILE)));
            final String log4jConfigFile = "conf/" + LOG4J_CONFIG_FILE;

            PropertyConfigurator.configure(log4jConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtém as configurações com base na chave passada como parâmetro
     * @param chave
     * @return
     */
    public static String getConfig(String chave) {
        if ( chave.equals("pesquisa_100") || chave.equals("lucene_method") )
            return LonoConfigLoader.pesquisa_cem_procento;

        String config = (String) properties.get(chave);
        if(config == null) {
            if ( chave.equals("DiasManterArquivosAntigo") ) config = "5";
            else if ( chave.equals("DiasManterArquivosZip") ) config = "2";
            else config = "";
        }

        return config;
    }
}
