package espe.lono.db;

/**
 * @author Petrus Auguato - Espe
 * @since 07/12/2017
 * @version 2.0
 */
public class LonoDatabaseConfigs {
    // Referente a versao do LonoIndexer
    // -> Constantes
    final public static String LONODB_VERSION = "1.8.03";
    final public static Integer LONODB_VERSIONCODE = 1803;
    
    // Referentes ao banco de marcacoes
    public static String DBMARCACOES_HOSTNAME = "localhost";
    public static String DBMARCACOES_USERNAME = "root";
    public static String DBMARCACOES_PASSWORD = "mysqlroot";
    public static String DBMARCACOES_DATABASE = "lonomarcacoes";
    public static int    DBMARCACOES_PORT = 3306;
    
    // Referente ao banco base do Lono
    public static String DBLONO_HOSTNAME = "192.168.0.220";
    public static String DBLONO_USERNAME = "postgresql";
    public static String DBLONO_PASSWORD = "postgresql";
    public static int DBLONO_PORT = 5432;
    public static String DBLONO_DBNAME = "Lono";
    public static int DBLONO_MAXCONN = 50;
    public static int DBLONO_MAXRETRYCONN = 3;
    
    // Referente ao Firebase
    public static String FBLONO_PATH = "development";
    public static String FBLONO_ACCESS_FILE = "conf/google-services.json";
    public static String FBLONO_URL = "https://lonoapp.firebaseio.com";

    // Referente ao LonoBackEnd
    public static String LONOBACKEND_BASEURL = "http://192.168.0.220:80/services/";
    public static String LONOBACKEND_KEY = "A%D*G-KaNdRgUkXp2s5v8y/B?E(H+MbQeShVmYq3t6w9z$C&F)J@NcRfUjWnZr4u";
}
