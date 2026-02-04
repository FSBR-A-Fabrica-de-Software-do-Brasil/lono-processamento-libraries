package espe.lono.indexercore.util;

import espe.lono.db.connections.*;
import espe.lono.db.dao.PalavrasDAO;
import espe.lono.db.models.*;
import espe.lono.indexercore.LonoIndexerConfigs;
import java.io.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

/**
 *
 * @author Espe
 */
public class Util 
{
    protected static PalavrasSubstituicao[] lista_palavras_substituicao = null;
    
    public static int numeroPalavras(final String text)
    {
        String[] spt = text.replaceAll("\\s+", " ").trim().split(" ");
        return spt.length;
    }
    
    public static boolean arquivoExiste(final String arquivo)
    {
        File fd = new File(arquivo);
        return fd.exists();
    }
    
    public static boolean descompactarZIPFile(String zipfname, String destinyFolder) {
        try {
            ZipFile zipFile = new ZipFile(zipfname);
            zipFile.extractAll(destinyFolder);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static boolean compactarDiretorio(String caminho, String output) {
        return Util.compactarDiretorios(new String[] {caminho}, output);
    }
    
    public static boolean compactarDiretorios(String[] caminhos, String output) {
        try {
            ZipFile zipFile = new ZipFile(output);
            ZipParameters paramenters = new ZipParameters();
            paramenters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            paramenters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            
            // Adicionando todas as pastas
                for ( String path: caminhos ) {
                File fd = new File(path);
                if ( fd.exists() && fd.isDirectory() ) zipFile.addFolder(fd, paramenters);
                else if ( fd.exists() && fd.isFile()) zipFile.addFile(fd, paramenters);
                else throw new Exception("File Not Fould -> " + path);
            }
            
            // Retornando sucesso
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static void limparDiretorio(String caminho)
    {
        final File dir_fd = new File(caminho);
        
        // Checando se e um diretorio
        if ( dir_fd.isDirectory() == false ) return;
        
        // Obtendo lista de arquivo dentro do diretorio e apagando-os
        final File[] dir_filelist = dir_fd.listFiles();
        for ( File dir_file: dir_filelist )
        {
            dir_file.delete();
        }
        
        // Todo o diretorio foi limpo...
    }
    
    public static String gerarHashMD5(final String text)
    {
        return DigestUtils.md5Hex(text);
    }

    /**
     * Gera o hash (SH-256) do arquivo
     * @param filename -> Nome do arquivo
     * @return -> String com o hash MD5
     */
    public static String GenerateHashFromFile(String filename) {
        return Util.GenerateHashFromFile(new File(filename));
    }

    /**
     * Gera o hash (SHA-256) do arquivo
     * @param fd -> FileDescriptor
     * @return -> Strinh com o hash MD5
     */
    public static String GenerateHashFromFile(File fd) {
        try {
            return DigestUtils.sha256Hex(FileUtils.readFileToByteArray(fd));
        } catch (IOException e) {
            return null;
        }
    }
    
    public static boolean criarDiretorio(String caminho) 
    {
        // Diratorio raiz
        File dir = new File(caminho);
        dir.mkdirs();
        
        // Diretorio onde contem os dados convertidos em HTML/PDF
        final String html_path = dir.getParent();
        dir = new File(html_path + "/html");
        dir.mkdirs();
        
        // Diretorio onde seram armazenados os dados de pesquisa...
        dir = new File(html_path + "/indice_pesquisa");
        dir.mkdirs();
        
        return true;
    }

    public static String[] dadosData(java.util.Date data)
    {
        String formato = "yyyy-MM-dd";
        SimpleDateFormat formata = new SimpleDateFormat(formato);
        return formata.format(data).split("-");
    }

    public static boolean removerArquivo(String arquivoNome)
    {
        // Removendo arquivo
        File arquivo = new File(arquivoNome);
//        System.out.println("DEL:" + arquivo.delete());
        return arquivo.delete();
    }
    
    public static boolean moverArquivo(String caminhoArquivo, String diretorioDestino)
    {
        File arquivo = new File(caminhoArquivo);
        return arquivo.renameTo(new File(diretorioDestino, arquivo.getName()));
    }

    public static String formatarData(java.util.Date data, String formatoDesejado)
    {
        String formato = formatoDesejado;
        SimpleDateFormat formata = new SimpleDateFormat(formato);
        return formata.format(data);
    }
    
    public static String implode(final String delimiter, String[] strArr)
    {
        StringBuilder sb = new StringBuilder();
        for ( String str: strArr )
        {
            if ( sb.length() > 0 ) sb.append(delimiter);
            sb.append(str);
        }
        
        return sb.toString();
    }
    
    public static int converterPDFparaHTML_CortarPaginas(String arqPDF, String dest_path) throws InterruptedException, IOException 
    {
        //Verifica se é um pdf
        if ( arqPDF.toLowerCase().contains(".pdf") == false ) return -1;
        
        final String app_name = LonoIndexerConfigs.GetPDFConversorAppName();
        ProcessBuilder pb = new ProcessBuilder(
                app_name, // Nome do aplicativo (logo abaixo, vem os argumntos)
                "-c", "-i", "" + arqPDF + "", "" + dest_path + "/doc.html");

        // Execuando conversao  
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ( br.readLine() != null ) {  }

        return 0;
    }
    
    //#7545
    public static int converterPDFparaHTML(String arquivoPDF) throws InterruptedException, IOException 
    {
        //Verifica se é um pdf
        if ( arquivoPDF.toLowerCase().contains(".pdf") == false ) return -1;
        
        final String app_name = LonoIndexerConfigs.GetPDFConversorAppName();
        ProcessBuilder pb = new ProcessBuilder(
                app_name, // Nome do aplicativo (logo abaixo, vem os argumntos)
                "-c", "-i", "-noframes", "-nomerge", "" + arquivoPDF + "", "" + arquivoPDF + ".html");

        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ( br.readLine() != null ) { /* Do Nothing */ }

        return 0;
    }
    
    public static int normalizeHTMLFile(String htmlFile) throws IOException
    {
        final String app_name = LonoIndexerConfigs.GetPythonAppName();
        ProcessBuilder pb = new ProcessBuilder(
                app_name, // Nome do aplicativo
                "extra/html_normalize/normalize.py",
                htmlFile
        );

        //System.out.println(pb.toString());
        pb.redirectErrorStream(true);

        Process p = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ( br.readLine() != null ) { /* Do Nothing */ }
        
        return 0;
    }

    /**
     * OVERLOAD
     * Trabalha/Normaliza o texto para ser pesquisado/usado no Lucene
     * @param txt
     * @return Texto trabalhado/normalizado
     * @throws SQLException 
     */
    public static String normalizeText(final String txt, DbConnection dbconn) throws SQLException
    {
        // Inicializando conexao com o BD se nao ativo
        return Util.normalizeText(txt, true, dbconn);
    }
    
    /**
     * Trabalha/Normaliza o texto para ser pesquisado/usado no Lucene
     * @param txt
     * @param remove_2points
     * @return Texto trabalhado/normalizado
     * @throws SQLException 
     */
    public static String normalizeText(final String txt, boolean remove_2points, DbConnection dbconn) throws SQLException
    {
        // Removendo acentos
        String str = Util.removeAccents(txt.toLowerCase());
        str = Normalizer.normalize(str.toLowerCase(), Normalizer.Form.NFKD);
        //str = str.replaceAll("[^\\p{InCombiningDiacriticalMarks}]", "");
        
        // Checando se a lista de palavras ja foi inicializada
        if ( Util.lista_palavras_substituicao == null )
        {
            PalavrasDAO palavrasDao = new PalavrasDAO();
            Util.lista_palavras_substituicao = palavrasDao.listarPalavrasSubstituicao(dbconn);
        }
        
        // Substituindo palavras definidas na lista...
        for ( PalavrasSubstituicao palavraSub: Util.lista_palavras_substituicao )
        {
            final String iniRgx = " " + palavraSub.getPalavraOriginal().trim() + " ";
            final String fimRgx = " " + palavraSub.getPalavraNova().trim() + " ";
            str = str.replaceAll(iniRgx, fimRgx);
        }
        
        // Completando normalizacao
        str = str.replaceAll(" - ", " ");
        str = str.replaceAll("[/\\\\'`´^~\\-.\",]ºª", " ");
        if ( remove_2points ) str = str.replaceAll("[:]", "");
        else str = str.replaceAll("[:]", " ");
        
        // Petrus Augusto: 10-11-2017
        //      Preparando texto para pesquisa numeros de processo
        //      Isso é: Remove os chars '-' e '.' anexados aos numeros de processo
        //      1 - Checando se contem numero de Proceso
        Pattern pattern = Pattern.compile("[0-9]{5,}[^A-Za-z]{4,}[0-9]{3,}");
        Matcher match = pattern.matcher(str);
        if ( match.find() || match.matches()) {
            // Removendo espacos entre grupos de numeros seguidos diretamente
            //str = str.replaceAll("([0-9]{3,})\\s+([0-9]{3,})", "$1$2");
            
            // Removendo os pontos/hifens dentro de numeros
            str = str.replaceAll("(\\d(\\s+|))[.]|[-]((\\s+|)\\d)", "$1$2$3$4");
            
            // Removendo ligacao juntas de letras,hifens/pontos com os numeros de processo
            str = str.replaceAll("([A-Za-z]{1,})[-]|[.]|[:]([0-9]{5,})", "$1 $2");
            str = str.replaceAll("([A-Za-z]{1,})([0-9]{10,})", "$1 $2");
        }
        
        // Retornando string tratada, obviamente removendo multiplos 
        // espacos seguidos.
        return str.replaceAll("\\s+", " ").trim();
    }
    
    /**
     * Criar o Pattern para regex,
     * Nota: Este código existe para poder gerar e modificar flags de todos os
     *       padrões Regex usado na Engine
     * @param rgx
     * @return 
     */
    public static Pattern criarRegexPattern(final String rgx)
    {
        Pattern patt = Pattern.compile(rgx, Pattern.UNICODE_CHARACTER_CLASS);
        return patt;
    }
    
    private static String removeAccents(String str)
    {
        String fstr = str.replaceAll("á|à|â|ã|ä", "a");
        fstr = fstr.replaceAll("é|è|ê|ë", "e");
        fstr = fstr.replaceAll("í|ì|î|ï", "i");
        fstr = fstr.replaceAll("ó|ò|ô|õ|ö", "o");
        fstr = fstr.replaceAll("ú|ù|û|ü", "u");
        fstr = fstr.replaceAll("ç", "c");
        return fstr;
    }
    
    public static void timeWaiter(int seconds) throws Exception
    {
        for ( int idx = 0; idx < (seconds * 2); idx++ )
        {
            Thread.sleep(500);
        }
    }
    
    public static Object[][] mergeArraysPesquisa(Object[][] array1, Object[][] array2)
    {
        if ( array1 != null && array2 == null ) return array1;
        else if ( array1 == null && array2 != null ) return array2;
        else if ( array1 == null && array2 == null ) return null;
        
        Object[][] newArray = new Object[array1.length + array2.length][];
        System.arraycopy(array1, 0, newArray, 0, array1.length);
        System.arraycopy(array2, 0, newArray, array1.length, array2.length);
        return newArray;
    }
    
    /*public static Document getLuceneDocumentByID(long idDoc, Directory dir) throws IOException
    {
        final IndexReader reader = DirectoryReader.open(dir);
        final IndexSearcher searcher = new IndexSearcher(reader);
        final String realDocIDValue = new DecimalFormat("00000000").format(idDoc);
        
        // Obtendo o documento pelo ID dentro do 'real_doc_id'        
        TermQuery realDocQuery = new TermQuery(new Term("real_doc_id",realDocIDValue));
        final TopDocs marcacaoResults = searcher.search(realDocQuery, 1);
        
        // Fechando o reader
        reader.close();
        
        // Retornando o documento encontrado!
        if ( marcacaoResults.totalHits <= 0 ) return null; // Not found
        else return reader.document( marcacaoResults.scoreDocs[0].doc );
    }*/
    
    public static Document getLuceneDocumentByID(long idDoc, IndexReader reader) throws IOException
    {
        final IndexSearcher searcher = new IndexSearcher(reader);
        final String realDocIDValue = new DecimalFormat("00000000").format(idDoc);
        
        // Obtendo o documento pelo ID dentro do 'real_doc_id'        
        TermQuery realDocQuery = new TermQuery(new Term("real_doc_id",realDocIDValue));
        final TopDocs marcacaoResults = searcher.search(realDocQuery, 1);
        
        // Retornando o documento encontrado!
        if ( marcacaoResults.totalHits <= 0 ) return null; // Not found
        else return reader.document( marcacaoResults.scoreDocs[0].doc );
    }

    public static String[] listarArquivosDiretorioExtensao(String diretorio, String extensao)
    {
        File dir_fd = new File(diretorio);
        if (!dir_fd.isDirectory()) return new String[0];

        File[] file_list = dir_fd.listFiles();
        Pattern ext_pattern = Pattern.compile(".*[.]"+extensao+"$", Pattern.CASE_INSENSITIVE);

        // Filtrando arquivos
        java.util.List<String> arquivos = new java.util.ArrayList<>();
        for ( File fd: file_list )
        {
            Matcher match = ext_pattern.matcher( fd.getName() );
            if ( match.find() )
            {
                arquivos.add( fd.getAbsolutePath() );
            }
        }

        arquivos.sort(Comparator.comparingInt(nome ->
            Integer.parseInt(nome.replaceAll(".*_p(\\d+)\\.json", "$1"))
        ));
        return arquivos.toArray(new String[arquivos.size()]);
    }
}
