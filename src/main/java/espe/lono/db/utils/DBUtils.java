package espe.lono.db.utils;

import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.connections.drivers.DbPostgresMarcacao;

import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * @corp ESPE
 * @author Petrus Augusto (R@g3)
 * @date 07/11/2017
 */
public class DBUtils {
    public static String FormatarData(java.util.Date data, String formatoDesejado)
    {
        String formato = formatoDesejado;
        SimpleDateFormat formata = new SimpleDateFormat(formato);
        return formata.format(data);
    }
    
    public static String RemoveAccents(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
    public static String[] DadosData(java.util.Date data)
    {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat formata = new SimpleDateFormat(formato);
        return formata.format(data).split("-");
    }


    public static DbConnectionMarcacao startDbMarcacaoConnection(int idPub) throws SQLException, ClassNotFoundException {
        return new DbPostgresMarcacao(idPub, true);
    }
    public static DbConnectionMarcacao startDbMarcacaoConnection(int idPub, boolean createTable) throws SQLException, ClassNotFoundException {
        return new DbPostgresMarcacao(idPub, createTable);
    }
}
