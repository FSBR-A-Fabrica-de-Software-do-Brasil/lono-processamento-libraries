package espe.lono.indexercore.engine;

import java.io.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import espe.lono.db.models.PublicacaoJornal;
import espe.lono.indexercore.util.Util;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import espe.lono.indexercore.analyzer.NGramAnalyzer.NGramAnalyzer;
import espe.lono.db.connections.*;
import espe.lono.db.models.ClassificacaoReplaceTipo;
import espe.lono.indexercore.log.Logger;

/**
 *
 * @author Espe
 */
public class Indexacao
{
    //final static private Logger logger = Logger.getLogger("mercurio3");
    final static private TipoIndexacao indexacaoTipo = TipoIndexacao.PESQUISA_100;
    
    // Enumeracao informando o tipo de indexacao aplicada na pesquisa
    public static enum TipoIndexacao
    {
        PESQUISA_100(1), // Default (Standar Analyzer)
        PESQUISA_101(2); // 101 ( PLuceneAnalyzer )
        
        private final int value;
        private TipoIndexacao(int value)
        { this.value = value; }
        
        @Override
        public String toString() { return Integer.toString(value); }
    }
    
    
    public static TipoIndexacao obterTipoIndexacaoAtual()
    {
        return Indexacao.indexacaoTipo;
    }
    
    /**
     * Chamada principal p/ iniciar a indexação
     * @param dirMaracaco
     * @param dirPesquisa
     * @param diretorioArquivos
     * @param classifReplaceTipo
     * @param pubJornal
     * @param pdfFName
     * @throws IOException
     * @throws SQLException 
     */
    public static void IndexarArquivosDiretorio(Directory dirMaracaco, Directory dirPesquisa, String diretorioArquivos, ClassificacaoReplaceTipo[] classifReplaceTipo, PublicacaoJornal pubJornal, String pdfFName, DbConnection dbconn) throws IOException, SQLException, Exception
    {
        // Abrindo analyzer padrao de marcacao
        final Analyzer analyzerMarc = new StandardAnalyzer();
        final IndexWriterConfig iwcMarcacao = new IndexWriterConfig(analyzerMarc);

        // Abrindo nalayzer padrao de pesquisa
        Analyzer analyzerPesq = Indexacao.obterAnalyzerPesquisa();
        IndexWriterConfig iwcPesquisa = new IndexWriterConfig(analyzerPesq);
        
        // Verificando dretorio
        final File docDir = new File(diretorioArquivos + "/" + pdfFName + ".html");
        if ( !docDir.exists() || !docDir.canRead() )
        {
            Logger.fatal("O diretorio '" + docDir.getAbsolutePath() + "' não existe ou sem permissão, verifique a pasta.");
        }

        // Preparando Lucene...
        iwcMarcacao.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writerMarcacao = new IndexWriter(dirMaracaco, iwcMarcacao);
        writerMarcacao.deleteAll(); // Garantindo que o index esta limpo
        
        iwcPesquisa.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writerPesquisa = new IndexWriter(dirPesquisa, iwcPesquisa);
        writerPesquisa.deleteAll(); // Garantindo que o index esta limpo

        // writer: Lucene document writer
        // docDir: dreor of document (html)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final long baseNumDocID = Long.parseLong(String.format("%s%02d", sdf.format(pubJornal.getDtPublicacao()), pubJornal.getIdJornal()));
        indexDocs(writerMarcacao, writerPesquisa, docDir, classifReplaceTipo, dbconn, baseNumDocID);
        
        // Comitando
        writerMarcacao.commit();
        writerPesquisa.commit();
        
        // Fecheando
        writerMarcacao.close();
        writerPesquisa.close();
    }

    /**
     * Adiciona indexacao para o algoritimo de pesquisa
     * @param writerPesquisa
     * @param linha
     * @param linhaLimpa
     * @param content
     * @param lineNum
     * @param pageNum
     * @param DadosTopLeftLinha
     * @throws IOException 
     */
    public static void indexDocs_AddPesquisa(IndexWriter writerPesquisa, String linha, String linhaLimpa, String content, int lineNum, int pageNum, String[] DadosTopLeftLinha, long docID) throws IOException
    {
        final Document doc = new Document();
        doc.add(new TextField("contents", content, Field.Store.YES));
        doc.add(new StringField("real_doc_id",  new DecimalFormat("00000000").format(docID), Field.Store.YES));
        writerPesquisa.addDocument(doc);
    }
    
    /**
     * Adiciona indexacao para  algoritmo de marcacao
     * @param writerMarcacao
     * @param linha
     * @param linhaLimpa
     * @param lineNum
     * @param pageNum
     * @param DadosTopLeftLinha
     * @param classeLinha
     * @throws IOException 
     */
    public static void indexDocs_AddMarcacao(IndexWriter writerMarcacao, String linha, String linhaLimpa, String searchString, int lineNum, int pageNum, String[] DadosTopLeftLinha, String classeLinha, long docID) throws IOException
    {
        final Document doc = new Document();
        doc.add(new StringField("textoLinha", linha, Field.Store.YES));
        doc.add(new StringField("textoLinhaLimpa", linhaLimpa, Field.Store.YES));
        doc.add(new TextField("conteudo", linha, Field.Store.YES));
        doc.add(new TextField("contentline", linhaLimpa, Field.Store.YES));
        doc.add(new StringField("contents", searchString, Field.Store.YES));
        doc.add(new StringField("line",  new DecimalFormat("00000").format(lineNum), Field.Store.YES));
        doc.add(new StringField("linha",  new DecimalFormat("00000").format(lineNum), Field.Store.YES));
        doc.add(new TextField("tamanho", new DecimalFormat("00000").format(linhaLimpa.length()), Field.Store.NO));
        doc.add(new TextField("top",  DadosTopLeftLinha[0], Field.Store.NO));
        doc.add(new TextField("left", DadosTopLeftLinha[1], Field.Store.NO));
        doc.add(new TextField("classe", classeLinha, Field.Store.NO));
        doc.add(new StringField("pagina",  new DecimalFormat("00000").format(pageNum), Field.Store.YES));
        doc.add(new StringField("real_doc_id",  new DecimalFormat("00000000").format(docID), Field.Store.YES));
        
        writerMarcacao.addDocument(doc);
    }
    
    private static String[] obterClasseLinha(String linha, ClassificacaoReplaceTipo[] classifReplaceTipo)
    {
        String classeLinha = "";
        for ( ClassificacaoReplaceTipo classifReplaceTipo1 : classifReplaceTipo )
        {
            //classificando a linha
            if ( classifReplaceTipo1.getTipoAcao() == 'C' )
            {
                if ( linha.contains(classifReplaceTipo1.getPesquisa()) )
                {
                    classeLinha = classifReplaceTipo1.getValor();
                }
            }
            else if ( classifReplaceTipo1.getTipoAcao() == 'R' )
            {
                linha = linha.replace(classifReplaceTipo1.getPesquisa(), classifReplaceTipo1.getValor());
            }
            else if ( classifReplaceTipo1.getTipoAcao() == 'X' )
            {
                if ( linha.contains(classifReplaceTipo1.getPesquisa()) ) 
                {
                    classeLinha = classifReplaceTipo1.getValor();
                    linha = linha.replace(classifReplaceTipo1.getPesquisa(), classifReplaceTipo1.getValor());
                }
            } // if, else if, else if, else...
        } // for
        
        String[] returnValues = new String[2];
        returnValues[0] = linha;
        returnValues[1] = classeLinha;
        return returnValues;
    }
    
    /**
     * Algoritmo de indexacao, a partir deste, cada secao sera indexada.
     * @param writerMarcacao
     * @param writerPesquisa
     * @param file
     * @param classifReplaceTipo
     * @throws Exception 
     */
    private static void indexDocs(IndexWriter writerMarcacao, IndexWriter writerPesquisa, File file, ClassificacaoReplaceTipo[] classifReplaceTipo, DbConnection dbconn, long baseNumber) throws Exception
    {
        // Checano se aponta p/ o diretorio correto
        // Verificando permissao de leitura e escrita na pasta
        if ( !file.canRead() ) return;
        
        // Verificando se esta apontando p/ o arquivo correto
        if ( !file.getName().contains(".html") ) return;
        
        // Processando arquivos
        BufferedReader buffer = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8")
        );

        // Inicializnd outras variaveis
        String classeLinha;
        String linhaLimpa;
        String linhaLimpaAnterior = "";
        String linhaArquivo = "";
        String[] linhasTagBR;
        String[] DadosTopLeftLinha;
        int x = 1;
        int current_page = 0;
        int current_line = -1;
        
        
        // Lendo documeto
        long docId = 1;
        while ( linhaArquivo != null )
        {
            // Lendo linha o arquivo e removendo html_entities
            linhaArquivo = buffer.readLine();
            if ( linhaArquivo == null )
                continue;
            
            // Checando se houve mudanca de pagina
            linhaArquivo = linhaArquivo.replaceAll("<br>", "<br/>");
            if ( linhaArquivo.matches("<!--.*?-->") && linhaArquivo.contains("Page") )
            {
                int now_page = Integer.parseInt(linhaArquivo.split(" ")[2]);
                if ( now_page != current_page )
                {
                    // Mudanca de pagina
                    current_page = now_page;
                    current_line = -1;
                }
            }
            
            // Checando se esta no inicio da pagina
            if ( current_line < 0 && linhaArquivo.contains("<div id=\"page") )
                current_line = 1;
            
            // Quebrando linhas sobre as tags BR
            linhasTagBR = linhaArquivo.split("<br/>");
            
            // Trabalhando com as linhas cortadas/splitted
            for ( String linha: linhasTagBR )
            {
                if ( linha == null ) continue;
                
                classeLinha = "";
                
                // Obtendo linha limpa (sem cometario ou tags html)
                linhaLimpa = UtilEngine.removeHtmlTags(linha).replaceAll("OAB[/\\-:]", "OAB /");
                
                // Obtendo dados de posicionamento do texto (left e top)
                DadosTopLeftLinha = UtilEngine.dadosTopLeftLinha(linha);
                
                // Obtendo dados de classe da linha(legado)
                String[] classeLinhaReturnValues = obterClasseLinha(linha, classifReplaceTipo);
                linha = classeLinhaReturnValues[0];
                classeLinha = classeLinhaReturnValues[1];
                
                // Formantando a string de pesquisa
                final String searchString = Util.normalizeText(UtilEngine.removeAccents(linhaLimpaAnterior + linhaLimpa), dbconn);
                
                // Adicionando documento de Marcacao
                final long finalDocID = Long.parseLong(String.format("%d%d", baseNumber, docId));

                //final long finalDocID = docId;
                Indexacao.indexDocs_AddMarcacao(
                        writerMarcacao, // Writer da marcacacao
                        linha, // Linha onde sera pesquisa as marcacacoes
                        linhaLimpa, // Linha limpa (sem HTML)
                        searchString, // Linha para teste de ocorrencia...
                        current_line, // Numero atual da linha
                        current_page,  // Numero da pagina atual
                        DadosTopLeftLinha, // Dados de posicionamento do HTML
                        classeLinha, // Classe da linha
                        finalDocID // ID do documento atual (garantindo que ambos sao iguais)
                );
                
                // Adicionando docmento de Pesquisa
                Indexacao.indexDocs_AddPesquisa(
                        writerPesquisa, // Writer da pesquisa
                        linha, // Linha com os dados HTML
                        linhaLimpa,  // Linha de onde sera obido os dados HTML
                        searchString, // Linha onde sera realizado as pesquisas
                        current_line, // Numero da linha atual (usado apenas no corte)
                        current_page, // Numero da pagina atual
                        DadosTopLeftLinha, // Dadosde posicionamento HTML
                        finalDocID // ID do documento atual (garantindo que ambas sao iguais)
                );

                docId++; // Incrementando o ID do document
                if ( current_line >= 0 ) current_line++;
                linhaLimpaAnterior = linhaLimpa + " ";
            } // for
            
        } // while
        
        buffer.close();
    }
    
    /**
     * Obtem o analyzer de pesquisa para ser usado no momento...
     * @return 
     */
    public static Analyzer obterAnalyzerPesquisa()
    {
        Analyzer analyzerPesq;
        if ( Indexacao.indexacaoTipo == TipoIndexacao.PESQUISA_100 )
             analyzerPesq = new StandardAnalyzer();
        else
            analyzerPesq = new NGramAnalyzer();
        
        return analyzerPesq;
    }
}
