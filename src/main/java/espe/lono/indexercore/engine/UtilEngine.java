package espe.lono.indexercore.engine;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 *
 * @author Espe
 */
public class UtilEngine {
    
    public static long codigoArquivoHtml;
    public static int paginaAtual;
    public static String caminhoArquivoPaginaHtml;
    public static String arquivoPaginaHtml;
    private static File arquivoHtmlNumerado;
    private static OutputStream outputStreamHtmlNumerado;
    private static OutputStreamWriter outputStreamWriterHtmlNumerado;
    public static StringBuffer fontesArquivos;
    
   

    //#8048
//    public static void abrirArquivoPaginaHtml() throws IOException{
//        UtilEngine.arquivoHtmlNumerado = new File(UtilEngine.caminhoArquivoPaginaHtml + "\\pagina_" /*+ UtilEngine.codigoArquivoHtml + "_"*/ + UtilEngine.paginaAtual + "_" + UtilEngine.arquivoPaginaHtml );
//        UtilEngine.arquivoHtmlNumerado.createNewFile();
//        UtilEngine.outputStreamHtmlNumerado = new FileOutputStream(UtilEngine.arquivoHtmlNumerado);
//        UtilEngine.outputStreamWriterHtmlNumerado = new OutputStreamWriter(UtilEngine.outputStreamHtmlNumerado, "ISO-8859-1");
//    }
//    
//    //#8048
//    public static void escreverPaginaHtml(String linha, int numeroLinha) throws IOException{
//        int verificacaoPagina = UtilEngine.getNumeroPagina(linha);
//        if(UtilEngine.arquivoHtmlNumerado != null && verificacaoPagina == paginaAtual){
//            UtilEngine.outputStreamWriterHtmlNumerado.write(UtilEngine.getLinhaHTMLNumerada(linha, numeroLinha) + "\r\n");
//        }else{
//            UtilEngine.paginaAtual = verificacaoPagina;
//            UtilEngine.fecharArquivoPaginaHtml();
//            UtilEngine.abrirArquivoPaginaHtml();
//            UtilEngine.outputStreamWriterHtmlNumerado.write("<STYLE type=\"text/css\">" + "\r\n" + UtilEngine.fontesArquivos.toString() + "</STYLE>" + "\r\n");
//            UtilEngine.outputStreamWriterHtmlNumerado.write(UtilEngine.getLinhaHTMLNumerada(linha, numeroLinha) + "\r\n");
//        }
//    }
//    
//    //#8048
//    public static void fecharArquivoPaginaHtml() {
//        try {
//            
//            UtilEngine.outputStreamHtmlNumerado.flush();
//             UtilEngine.outputStreamWriterHtmlNumerado.flush();
//            
//            UtilEngine.outputStreamHtmlNumerado.close();
//             UtilEngine.outputStreamWriterHtmlNumerado.close();
//        } catch (IOException ex) {
//            Logger.getLogger(UtilEngine.class.getName()).log(Level.SEVERE, null, ex);
//        }
//       
//    }
    
    //#8048
    private static int getNumeroPagina(String linha){
        int pagina = UtilEngine.paginaAtual;
        try{
            if(linha.matches("<!--.*?-->") && linha.contains("Page")){
                pagina = Integer.parseInt(linha.split(" ")[2]);
            }else if(linha.contains("font-size")){
                UtilEngine.fontesArquivos.append(linha);
                UtilEngine.fontesArquivos.append("\r\n");
            }
        }finally{
            return pagina;
        }
    }
    
    public static String removeAccents(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD); 
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
    
    public static String removeHtmlTags(String str) { 
        //str = str.replaceAll("<br>", "");
        str = str.replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "");
        return str;   
    }
    
    public static String[] quebraLinhaTagBR(String str) {
        if(str == null){
            return null;
        }else{
            return str.split("<br>");
        }
    }
    
    //#8047
    public static String getLinhaHTMLNumerada(String str, int linhaAtual) {
        if(str == null){
            return "";
        }else{
            return str.replaceAll("DIV style=\"position:absolute", "DIV id=\"linha-" + linhaAtual + "\" style=\"position:absolute");
        }
    }
    
    public static String[] dadosTopLeftLinha(String linha){
        String[] dadosTopLeft = new String[2];
        dadosTopLeft[0] = new DecimalFormat("00000").format(0);
        dadosTopLeft[1] = new DecimalFormat("00000").format(0);
        String[] temp1 = linha.split("\"");
        if ( temp1.length  > 1 )
        {
            String[] temp2 = temp1[1].split(";");
            if(temp2.length  > 2)
            {
                String[] tempTop = temp2[1].replaceAll("px", "").split(":");
                String[] tempLeft = temp2[2].replaceAll("px", "").split(":");
                if( tempTop.length == 2 && tempLeft.length == 2 )
                {
                    try
                    {
                        dadosTopLeft[0] = new DecimalFormat("00000").format(Integer.parseInt(tempTop[1]));
                        dadosTopLeft[1] = new DecimalFormat("00000").format(Integer.parseInt(tempLeft[1]));
                    }
                    catch(NumberFormatException ex)
                    {
                        dadosTopLeft[0] = new DecimalFormat("00000").format(0);
                        dadosTopLeft[1] = new DecimalFormat("00000").format(0);
                    }
                }
            }
        }
        
        return dadosTopLeft;
    }
    
}
