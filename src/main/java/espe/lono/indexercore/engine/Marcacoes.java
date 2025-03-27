package espe.lono.indexercore.engine;

import espe.lono.db.connections.*;
import espe.lono.db.models.*;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import espe.lono.db.Fachada;
import espe.lono.indexercore.LonoIndexerConfigs;
import espe.lono.indexercore.exceptions.MarcacoesException;
import espe.lono.indexercore.util.Util;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.Version;

/**
 * @author ESPE
 * @coders Petrus Augusto/Luiz Diniz
 */
public class Marcacoes 
{
    final static private Logger logger = Logger.getLogger("mercurio3");
    /* ---------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------- */
    public static void processarMateriasComplexas(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite) throws MarcacoesException
    {
        try {
            Marcacoes.executarProcessarMateriasComplexas(dir, publicacaoJornal, sqlite);
        } catch (IOException | SQLException | ParseException ex) {
            throw new MarcacoesException("Excessao disparada -> " + ex.getClass().toString() + ":" + ex.getMessage(), -1);
        }
    }
    public static void processarRemocaoPaginas(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite) throws MarcacoesException
    {
        try {
            Marcacoes.executarProcessamentoRemocaoPaginas(dir, publicacaoJornal, sqlite);
        } catch (IOException | SQLException | ParseException ex) {
            throw new MarcacoesException("Excessao disparada -> " + ex.getClass().toString() + ":" + ex.getMessage(), -1);
        }
    }
    
    public static void processarPautas(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite) throws MarcacoesException
    {
        try {
            Marcacoes.executarProcessamentoPauta(dir, publicacaoJornal, sqlite);
        } catch (IOException | SQLException ex) {
            throw new MarcacoesException("Excessao disparada -> " + ex.getClass().toString() + ":" + ex.getMessage(), -1);
        }
    }
    
    public static void processarPreMarcacoes(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite, DbConnection dbconn) throws MarcacoesException
    {
        try {
            Marcacoes.executarMarcacoes(dir, publicacaoJornal, "P", sqlite, dbconn);
        } catch (IOException | SQLException | ParseException ex) {
            throw new MarcacoesException("Excessao disparada -> " + ex.getClass().toString() + ":" + ex.getMessage(), -1);
        }
    }

    public static void processarMarcacoesPosExclusoes(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite, DbConnection dbconn) throws MarcacoesException
    {
        try {
            Marcacoes.executarMarcacoes(dir, publicacaoJornal, "D", sqlite, dbconn);
        } catch (IOException | SQLException | ParseException ex) {
            throw new MarcacoesException("Excessao disparada -> " + ex.getClass().toString() + ":" + ex.getMessage(), -1);
        }
    }

    public static void processarMarcacoes(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite, DbConnection dbconn) throws MarcacoesException
    {
        try {
            Marcacoes.executarMarcacoes(dir, publicacaoJornal, "M", sqlite, dbconn);
            Marcacoes.executarMarcacoesRegraIndividual(dir, publicacaoJornal, sqlite, dbconn);
        } catch (IOException | SQLException | ParseException ex) {
            throw new MarcacoesException("Excessao disparada -> " + ex.getClass().toString() + ":" + ex.getMessage(), -1);
        }   
    }
    

    private static boolean verificarRegex(final String regexPattern, final String text)
    {
        // Verificando se contem REGEX a ser testado
        // Nota: caso não existe, ira retornar TRUE (pois, ao chegar nesse ponto)
        //       é pq o resultado da query 'Lucene' foi retornando com sucesso.
        if ( regexPattern == null || regexPattern.length() <= 0 )
            return true;
        
        // Nota: todos os regex na lista devem ser validos p/ retornar TRUE
        String[] rgxPatternsSpt = regexPattern.split("#");
        for ( final String rgxPatt: rgxPatternsSpt )
        {
            if ( rgxPatt.length() <= 0 ) continue;
            
            final Pattern pattern = Pattern.compile(rgxPatt);
            final Matcher match = pattern.matcher(text);
            if ( !match.find() && !match.matches() )
                return false;
        }
        
        return true;
    }
    
    private static void executarProcessarMateriasComplexas(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite) throws IOException, ParseException, SQLException
    {
        final Fachada fachada = new Fachada();
        MarcacaoPublicacao marcacaoPub;
        
        // Obtendo as regras de padroes de materia/processos que contenham
        // a flag 'complex_mode'. Isso indica que a marcacao ira ser obtida
        // Obtendo o processo/inicio_de_materia mais proximo desse doc.
        // Obtendo o processo/inicio_de_materia mais proximo desse doc.
        final String table_name = sqlite.obterNomeTabela();
        final String sql = "SELECT DISTINCT(num_doc_lucene),pagina, id_tipo_padrao, complex "
            + "FROM " + table_name + " "
            + "WHERE id_tipo_padrao IN (21,1,2,3,4,5) AND num_doc_lucene > ? "
            + "ORDER BY num_doc_lucene ASC "
            + "LIMIT 1";
        final PreparedStatement stm;
        stm = sqlite.obterPreparedStatement(sql);
        ResultSet result;
        
        long[][] listMarcComplex = fachada.listarMarcacaoComplexas(sqlite);
        for ( long complexMarc[] : listMarcComplex ) {
            // Obtendo o numero do documento que termina esta linha
            final long inicioRegiao = complexMarc[0];
            final int id_tipo_padrao = (int) complexMarc[1]; // Posso realizar cast, pois, o ID não é realmente um LongValue
            
            // Obtendo a marcacao pelo ID
            TipoPadraoJornal tipoPadrao = publicacaoJornal.getTiposPadraoPorIdTipoPadrao(id_tipo_padrao)[0];
            
            // Obtendo o termino desta marcacao
            // Obtendo dados de fim de pauta...
            stm.setLong(1, inicioRegiao);
            result = sqlite.abrirConsultaSql(stm);

            if ( !result.next() ) continue;
            
            // Obtendo todo o texto nesta regiao
            int num_pagina = result.getInt("pagina");
            int fimRegiao = result.getInt("num_doc_lucene");
            int resultIdTipoPadrao = result.getInt("id_tipo_padrao");
            boolean resultTipoPadraoComplex = result.getBoolean("complex");
            
            if ( resultTipoPadraoComplex ) fimRegiao += 1;
            
            // Obtendo o texto a ser tratado
            final String text2work = Marcacoes.obterTextoRangeDocLucene(dir, publicacaoJornal, inicioRegiao, fimRegiao);
            
            // Trabalhando o texto p/ gerar uma nova marcacao
            //@warning Terminar est econdigo depois
        }
    }
    /**
     * Algoritmo de remoção de blocos/grupos de linhas...
     * Atenção: As marcações para esse recurso DEVEM ser sempre em pares
     *       não sera tolerada divergencia de quantidades das marcações
     *       iniciais e finais...
     * Nota: Este algoritmo se baseia apenas nas querys Lucene...
     *       não supote o processamento de regex junto as querys... porem,
     *       suporte a navegação por incremento/decremento linha/ocorrência
     * @param publicacaoJornal
     * @param sqlite 
     */
    private static void executarProcessamentoRemocaoPaginas(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite) throws IOException, ParseException, SQLException
    {
        final Fachada fachada = new Fachada();
        long[][] linhasIniciaisRemocao = fachada.listarMarcacaoPorTiposPadroes("23", sqlite);
        final String sqlLinhasFinais = "SELECT num_doc_lucene FROM " + sqlite.obterNomeTabela() + " "
                + "WHERE num_doc_lucene > ? AND id_tipo_padrao=24";
        final PreparedStatement pm = sqlite.obterPreparedStatement(sqlLinhasFinais);
        
        final IndexWriter idxWriter = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
        final QueryParser parser = new QueryParser("line", new StandardAnalyzer());
        
        // Listando linhas e removendo-as...
        Query query;
        ResultSet resultado;
        try
        {
            for ( final long[] linhaInicial: linhasIniciaisRemocao)
            {
                pm.setLong(1, linhaInicial[0]);
                resultado = pm.executeQuery();
                if ( !resultado.next() ) continue;

                final int linhaFinal = (resultado.getInt("num_doc_lucene") - 1);
                final String rangeInicial = new DecimalFormat("00000").format(linhaInicial[0]);
                final String rangeFinal = new DecimalFormat("00000").format(linhaFinal);
                final String luceneQueryStr = "line:[" + rangeInicial + " TO " + rangeFinal + "]";
                query = parser.parse(luceneQueryStr);
                
                logger.debug("Removendo linhas:" + luceneQueryStr + " - " + publicacaoJornal.getIdPublicacao());

                resultado.close();
                idxWriter.deleteDocuments(query);
            }
        }
        catch ( IOException | SQLException | ParseException ex )
        {
            idxWriter.commit();
            idxWriter.deleteUnusedFiles();
            idxWriter.close();
        }
        
        idxWriter.commit();
        idxWriter.deleteUnusedFiles();
        idxWriter.close();
    }
    
    private static void executarProcessamentoPauta(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite) throws SQLException, IOException
    {
        final Fachada fachada = new Fachada();
        final int max_pauta_range = 30;
        MarcacaoPublicacao marcacaoPub;
        
        // Obtendo o processo/inicio_de_materia mais proximo desse doc.
        final String table_name = sqlite.obterNomeTabela();
        final String sql = "SELECT DISTINCT(num_doc_lucene),pagina, id_tipo_padrao "
            + "FROM " + table_name + " "
            + "WHERE id_tipo_padrao IN (22,1,2,3,4) AND num_doc_lucene > ? "
            + "ORDER BY num_doc_lucene ASC "
            + "LIMIT 1";
        final PreparedStatement stm;
        stm = sqlite.obterPreparedStatement(sql);
        ResultSet result;
        
        long[][] inicioPautaLista = fachada.listarMarcacaoPorTiposPadroes("1,2,21", sqlite);
        System.out.println("Iniciando processamento de pautas -> " + inicioPautaLista.length);
        for ( long pautaLinhaInfo[]: inicioPautaLista)
        {
            long inicioPauta = pautaLinhaInfo[0];
            final long tipoPadrao = pautaLinhaInfo[1];

            // Obtendo dados de fim de pauta...
            stm.setLong(1, inicioPauta);
            result = sqlite.abrirConsultaSql(stm);

            if ( !result.next() ) continue;

            int num_pagina = result.getInt("pagina");
            long fimPauta = result.getLong("num_doc_lucene");
            int resultIdTipoPadrao = result.getInt("id_tipo_padrao");

            // Checando se deve ignorar essa marcacao
            if ( (tipoPadrao == 1 || tipoPadrao == 2) && (resultIdTipoPadrao == 2 || resultIdTipoPadrao == 3 || resultIdTipoPadrao == 4)) {
                // Ex: Se for titulo e logo apos (prox. linha) vem um subtitulo/materia, é pq nao ha pauta
                if ( (inicioPauta + 1) == fimPauta )
                    continue; // Ignorando-a... Tem que ser uma pauta
            }

            // Checando linha inicial (se for com base no titulo/subtitulo, incrementa a linha)
            if ( tipoPadrao == 1 || tipoPadrao == 2 ) inicioPauta += 1;

            
            // Checando o tipo padrao p/ saber qual linhas deve ser 
            // incrementada
            if ( resultIdTipoPadrao == 22 )
                fimPauta += 1;
            
            /* Petrus A. 2019-04-17: A regiao da pauta deve terminar ja termina 
                UMA linha antes da materia ... Nao precisa decrementar novamente */
            /*if ( resultIdTipoPadrao == 3 || resultIdTipoPadrao == 4 )
                fimPauta -= 1;*/

            // Checando tamanh o range...
            if ( (fimPauta - inicioPauta) > max_pauta_range && resultIdTipoPadrao != 22 )
                fimPauta = inicioPauta + 6;

            // Obtendo texto nesse range de documentos...
            String marcacao_texto = Marcacoes.obterTextoRangeDocLucene(dir, publicacaoJornal, inicioPauta, fimPauta);
            if ( marcacao_texto == null || marcacao_texto.length() <= 0 )
                continue;

            // Armazenando a pauta
            marcacaoPub = new MarcacaoPublicacao();
            marcacaoPub.setIdPublicacao(publicacaoJornal.getIdPublicacao());
            marcacaoPub.setIdTipoPadrao(50);
            marcacaoPub.setNumDocLucene( inicioPauta );
            marcacaoPub.setLinhaPublicacao( 0 );
            marcacaoPub.setMarcacao( marcacao_texto.replaceAll("'", "''") );
            marcacaoPub.setPagina( num_pagina );
            marcacaoPub.setLinhaPagina( 0 );
            marcacaoPub.setSitCad("A");
            marcacaoPub.setUsuCad(1);
            marcacaoPub.setIdTipoPadraoJornal(0);

            fachada.incluirMarcacaoPublicacao(marcacaoPub, sqlite);
        }
    }

    private static String obterTextoRangeDocLucene(IndexReader reader, PublicacaoJornal publicacaoJornal, long inicio, long fim) throws IOException
    {
        final String fixedIdNumber = String.valueOf(inicio).substring(0, 10);
        StringBuilder sb = new StringBuilder("");
        boolean ignore_coment = false;

        // Definindo os valores de inicio e fim
        inicio = Long.parseLong( (String.valueOf(inicio).substring(10)) );
        fim = Long.parseLong( (String.valueOf(fim).substring(10)) );

        // Obtendo textos
        for ( long x = inicio; x < fim; x++ )
        {
            // Gereando o id REAL do documento
            final long realID = Long.parseLong(String.format("%s%d", fixedIdNumber, x));

            //Document doc = reader.document(x);
            Document doc = Util.getLuceneDocumentByID(realID, reader);
            if ( doc == null ) continue;
            
            final String doc_line = doc.getField("textoLinhaLimpa").stringValue();
            
            // Checando se e um comentario (inicio ou fim)
            if ( ignore_coment == false && doc_line.contains("<!--") )
            {
                // Inicio de comentario
                ignore_coment = true;
                continue;
            }
            else if ( ignore_coment == true && doc_line.contains("-->") )
            {
                // Fim de comentario
                ignore_coment = false;
                continue;
            }
            else if ( ignore_coment )
            {
                // Ainda nos comentarios. ignorando linha
                continue;
            }
            
            sb.append(doc_line).append(" ");
        }
        
        return sb.toString();
    }
    
    private static String obterTextoRangeDocLucene(Directory dir, PublicacaoJornal publicacaoJornal, long inicio, long fim) throws IOException
    {
        final String diretorioDocumentos = LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS;
        final String caminhoDirPublicacaoIndice = publicacaoJornal.getCaminhoDirPublicacaoIndice(diretorioDocumentos);
        final IndexReader reader = DirectoryReader.open(dir);
        final String texto = Marcacoes.obterTextoRangeDocLucene(reader, publicacaoJornal, inicio, fim);
        
        reader.close();
        return texto.trim();
    }
    
    private static void executarMarcacoes(Directory dir, PublicacaoJornal publicacaoJornal, String acao, DbConnectionMarcacao sqlite, DbConnection dbconn) throws IOException, SQLException, ParseException, MarcacoesException
    {
        long qtdMarcacoesEncontradas;
        int qtdMarcacoesGravadas = 0;
        MarcacaoPublicacao marcacaoPub = null;
        
        final Fachada fachada = new Fachada();

        List<AdicionalMarcacaoTipoPadrao> listaMarcacaoValida;
        List<ExclusaoMarcacaoTipoPadrao> listaMarcacaoIgnorada;
        AdicionalMarcacaoTipoPadrao addMarcTipoTemp;
        ExclusaoMarcacaoTipoPadrao excMarcTipoTemp;

        //Listando todos os itens do tipo ação passado como parametro do padrao da publicação
        TipoPadraoJornal[] tiposPadraoMarcacao = publicacaoJornal.getTiposPadraoPorTipoChave(acao);
        logger.debug("Qtd. de marcacoe encontradas: " + tiposPadraoMarcacao.length);
        if ( tiposPadraoMarcacao.length <= 0 ) return;

        // Inicializando Lucene
        final DirectoryReader reader = DirectoryReader.open(dir);
        final IndexSearcher searcher = new IndexSearcher(reader);
        final Analyzer analyzer = new StandardAnalyzer();
        final QueryParser parser = new QueryParser("conteudo", analyzer);
        int totalPadraoComErros = 0;
        Query query;
        try
        {
            for (int x = 0; x < tiposPadraoMarcacao.length; x++)
            {
                //Atualizando a referencia da fonte se tiver
                tiposPadraoMarcacao[x] = fachada.atualizaQueryFontes(
                        tiposPadraoMarcacao[x], 
                        publicacaoJornal.getIdPublicacao(), 
                        sqlite
                );

                //Luiz Diniz - 30/08/2013
                /* Carregando as marcações conhecidas(Válidas e Ignoradas) do padrao da publicação.*/
                listaMarcacaoValida = Arrays.asList(
                        fachada.listarAdicionalMarcacaoTipoPadrao(
                                'R', 
                                tiposPadraoMarcacao[x].getIdTipoPadrao(), 
                                tiposPadraoMarcacao[x].getIdPadrao(),
                                dbconn
                        )
                );

                listaMarcacaoIgnorada = Arrays.asList(
                        fachada.listarExclusaoMarcacaoTipoPadrao(
                                'R', 
                                tiposPadraoMarcacao[x].getIdTipoPadrao(), 
                                tiposPadraoMarcacao[x].getIdPadrao(),
                                dbconn
                        )
                );


                //Verificando se o campo da query está vazio
                if ( tiposPadraoMarcacao[x].getQueryIni() != null && !(tiposPadraoMarcacao[x].getQueryIni().equals("")) )
                {
                    try
                    {
                        query = parser.parse(tiposPadraoMarcacao[x].getQueryIni());
                    }
                    catch ( ParseException ex)
                    {
                        // Checando o tipo de padrao, se for um ESSESENCIAL(3 ou 4), dispara a excessao e para a execucao deste jornal
                        final int idTipoPadrao = tiposPadraoMarcacao[x].getIdTipoPadrao();
                        if ( idTipoPadrao == 4 || idTipoPadrao == 3 ) {
                            String queryLucene = tiposPadraoMarcacao[x].getQueryIni();
                            throw new MarcacoesException(
                                    "Erro no padrao essencial! Finalizando Processamento de Marcacoes",
                                    queryLucene,
                                    tiposPadraoMarcacao[x].getIdTipoPadraoJornal());
                        }
                            
                        // Erro no padrao, mas nao é uma essencial
                        logger.error("Padrão errado:" + tiposPadraoMarcacao[x].getIdTipoPadraoJornal() + " : " + tiposPadraoMarcacao[x].getQueryIni());
                        totalPadraoComErros += 1;
                        continue;
                    }

                    TopDocs results = searcher.search(query, tiposPadraoMarcacao[x].getQtdMaxResultQuery());
                    qtdMarcacoesEncontradas = (long) tiposPadraoMarcacao[x].getQtdMaxResultQuery() != 1000000 ? tiposPadraoMarcacao[x].getQtdMaxResultQuery() : results.totalHits;
                    int qntLinhasAcima = tiposPadraoMarcacao[x].getQtdLinhaAjusteAcima();
                    int qntLinhasAbaixo = tiposPadraoMarcacao[x].getQtdLinhaAjusteAbaixo();

                    ScoreDoc[] resultado = results.scoreDocs;
                    Document doc, doc_original;
                    for ( ScoreDoc resultado1 : resultado )
                    {
                        final int NUMDOCLUCENE = ((resultado1.doc + qntLinhasAcima) - qntLinhasAbaixo);
                        doc = reader.document( NUMDOCLUCENE );
                        doc_original = reader.document( resultado1.doc );
                        final int tipoMarc = tiposPadraoMarcacao[x].getTipoPadrao().getIdTipoPadrao();
                        String marcacaoText = doc.getField("textoLinhaLimpa").stringValue().replace("'", "''");
                        String marcacaoOriginalText = doc_original.getField("textoLinhaLimpa").stringValue().replace("'", "''");
                        
                        // Checando o tipo de marcacao, se for fonte, garante que
                        // obtenha apenas a definicao da 'CLASSE' da fonte (ftxx)
                        if ( tipoMarc == 14 ) // Nota, definir constante de tipos_padrao
                        {
                            String[] spplitted_text = marcacaoText.split("\t");
                            if ( spplitted_text != null && spplitted_text.length > 2 && marcacaoText.contains("p {") )
                            {
                                marcacaoText = "\t" + spplitted_text[2];
                            }
                        }

                        // Obtendo o real_doc_id do documento
                        final long realNumDocLucene = Long.parseLong(doc.getField("real_doc_id").stringValue());
                        marcacaoPub = new MarcacaoPublicacao();
                        marcacaoPub.setIdPublicacao(publicacaoJornal.getIdPublicacao());
                        marcacaoPub.setIdTipoPadrao(tiposPadraoMarcacao[x].getTipoPadrao().getIdTipoPadrao());
                        marcacaoPub.setNumDocLucene(realNumDocLucene);
                        marcacaoPub.setLinhaPublicacao(Integer.parseInt(doc.getField("line").stringValue()) + tiposPadraoMarcacao[x].getQtdLinhaAjusteAbaixo() - tiposPadraoMarcacao[x].getQtdLinhaAjusteAcima());
                        marcacaoPub.setMarcacao( marcacaoText );
                        marcacaoPub.setMarcacaoOriginal(marcacaoOriginalText);
                        marcacaoPub.setPagina( Integer.parseInt(doc.getField("pagina").stringValue()) );
                        marcacaoPub.setLinhaPagina( Integer.parseInt(doc.getField("line").stringValue()) );
                        marcacaoPub.setIdTipoPadraoJornal(tiposPadraoMarcacao[x].getIdTipoPadraoJornal());
                        marcacaoPub.setComplex(tiposPadraoMarcacao[x].isComplex_mode());

                        //Luiz Diniz - 30/08/2013
                        /* Antes de incluir a marcação verificar se a marcação é uma marcação conhecida ou ignorada.
                         * Se não for, a marcação ficará com o status de pendente.*/
                        addMarcTipoTemp = new AdicionalMarcacaoTipoPadrao();
                        addMarcTipoTemp.setTextoAdicional(marcacaoPub.getMarcacao());
                        addMarcTipoTemp.setIdTipoPadrao(tiposPadraoMarcacao[x].getTipoPadrao().getIdTipoPadrao());
                        addMarcTipoTemp.setTipoAdicional('R');
                        addMarcTipoTemp.setIdPadrao(tiposPadraoMarcacao[x].getIdPadrao());
                        excMarcTipoTemp = new ExclusaoMarcacaoTipoPadrao();
                        excMarcTipoTemp.setTextoExclusao(marcacaoPub.getMarcacao());
                        excMarcTipoTemp.setIdTipoPadrao(tiposPadraoMarcacao[x].getTipoPadrao().getIdTipoPadrao());
                        excMarcTipoTemp.setTipoExclusao('R');
                        excMarcTipoTemp.setIdPadrao(tiposPadraoMarcacao[x].getIdPadrao());

                        marcacaoPub.setUsuCad(1);
                        if ( listaMarcacaoValida.contains(addMarcTipoTemp) )
                            marcacaoPub.setSitCad("A"); //Marcação aprovada
                        else if ( listaMarcacaoIgnorada.contains(excMarcTipoTemp) )
                            marcacaoPub.setSitCad("I"); //Marcação Ignorada
                        else 
                            marcacaoPub.setSitCad("P"); //Marcação Pendente

                        //verificando se a marcacao tem outra marcação para checar
                        if ( tiposPadraoMarcacao[x].getIdTipoPadraoCheck() == 0
                                || fachada.checarMarcacao(marcacaoPub.getMarcacao(), tiposPadraoMarcacao[x].getIdTipoPadraoCheck(), publicacaoJornal.getIdPublicacao(), sqlite) )
                        {
                            //Verificando o regex pelo java antes incluir. O regex aqui poderá ser
                            //verificado case sensitive, e no lucene o texto está sendo indexado insensitive
                            String rgxTxtPattern = tiposPadraoMarcacao[x].getRegexIni();
                            if ( Marcacoes.verificarRegex(rgxTxtPattern, marcacaoPub.getMarcacao()) )
                            {
                                fachada.incluirMarcacaoPublicacao(marcacaoPub, sqlite);
                            }
                        }
                    } //for

                    //fachada.executarUltimaParteAcumuladaSql();

                    //verrificando se todas as marcações foram gravadas na base
                    if ( qtdMarcacoesEncontradas > 0 )
                    {
                        qtdMarcacoesGravadas = fachada.checkQuantidadeMarcacoes(marcacaoPub, sqlite);
                    }
                } // if

            } //for
            
            reader.close();
            
            // Checando o numero de erros de padrao
            if ( totalPadraoComErros > (tiposPadraoMarcacao.length / 3) )
            {
                // Error... Muitos padroes com erro,
                String exMessage = "Ha uma grande quantidade de erros nos padroes na publicacao " + 
                        publicacaoJornal.getIdPublicacao() + 
                        ", do jornal " + publicacaoJornal.getIdJornal();
                throw new MarcacoesException(exMessage, -1);
            }
        }
        catch ( MarcacoesException ex ) {
            reader.close();
            throw ex;
        }
        catch ( IOException | NumberFormatException | SQLException ex)
        {
            reader.close();
            throw new MarcacoesException("Excessao disparada -> " + ex.getClass().toString() + ":" + ex.getMessage(), -1);
        }
    }

    public static void removerLinhasIndesejadas(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite) throws IOException
    {
        IndexReader reader = null;
        IndexWriter idxWriter = null;
        try
        {

            File file = new File(publicacaoJornal.getCaminhoDirPublicacaoIndice(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS));
            idxWriter = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
            QueryParser parser = new QueryParser("conteudo", new StandardAnalyzer());
            reader = DirectoryReader.open(dir);
            final IndexSearcher searcher = new IndexSearcher(reader);
            Query query;
            
            // Obtendo lista de padroes a serem excluidos/removidos
            // - Rodape, Cabecalho, N. Pagina e as acoes marcadas como 'E'xcl.
            TipoPadraoJornal[] tiposPadraoMarcacao = publicacaoJornal.getTiposParaRemocao();
            Fachada fachada = new Fachada();

            long numDeleted = 0;
            for (int x = 0; x < tiposPadraoMarcacao.length; x++)
            {
                //Atualizando a referencia da fonte se tiver
                tiposPadraoMarcacao[x] = fachada.atualizaQueryFontes(tiposPadraoMarcacao[x], publicacaoJornal.getIdPublicacao(), sqlite);

                //Verificando se o campo da query está vazio
                if ( tiposPadraoMarcacao[x].getQueryIni() != null && (tiposPadraoMarcacao[x].getQueryIni().trim().length() > 0) )
                {
                    query = parser.parse(tiposPadraoMarcacao[x].getQueryIni());
                    
                    // Checando a necessidade de Regex
                    if ( tiposPadraoMarcacao[x].getRegexIni() != null && tiposPadraoMarcacao[x].getRegexIni().length() > 0 )
                    {
                        // 1- Obtendo documentos (Query Lucene)...
                        final TopDocs results = searcher.search(query, Integer.MAX_VALUE);
                        Document doc;
                        for ( ScoreDoc scoreDoc: results.scoreDocs )
                        {
                            doc = reader.document( scoreDoc.doc );
                            final String content = doc.getField("textoLinhaLimpa").stringValue();
                            
                            // 2 - Checando Regex...
                            if ( Marcacoes.verificarRegex(tiposPadraoMarcacao[x].getRegexIni(), content) )
                            {
                                String line = doc.getField("line").stringValue();
                                query = parser.parse("line:" + line);
                                numDeleted += idxWriter.deleteDocuments(query);
                            }
                        }
                    }
                    else
                    {
                        System.out.println("Delete -> " + tiposPadraoMarcacao[x].getQueryIni());
                        numDeleted += idxWriter.deleteDocuments(query);
                    }
                }
            }

            idxWriter.commit();
            idxWriter.deleteUnusedFiles();
            idxWriter.close();
            reader.close();
        }
        catch (SQLException | ParseException | IOException ex)
        {
            // Grava no log o erro fatal e dispara a Excessao ocorrida
            if ( reader != null ) reader.close();
            if ( idxWriter != null ) idxWriter.close();
            logger.fatal(ex.getClass().getName() + " - " + ex.getMessage(), ex);
        }
    }

    private static void executarMarcacoesRegraIndividual(Directory dir, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite, DbConnection dbconn) throws IOException
    {
        // Checando se existe marcacoes
        if ( publicacaoJornal.getPadraoJornalPublicacao().getTiposPadraoJornal().length <= 0 )
            return; // Nao existe marcacoes...
        
        IndexReader reader = null;
        long qtdMarcacoesEncontradas;
        int qtdMarcacoesGravadas = 0;

        MarcacaoPublicacao marcacaoPub = null;
        try
        {
            reader = DirectoryReader.open(dir);
            
            final IndexSearcher searcher = new IndexSearcher(reader);
            final Analyzer analyzer = new StandardAnalyzer();
            final Fachada fachada = new Fachada();
            final QueryParser parser = new QueryParser("contentline", analyzer);
            Query query;

            AdicionalMarcacaoTipoPadrao[] listaMarcacaoIndividual = fachada.listarAdicionalMarcacaoTipoPadrao('I', 0, publicacaoJornal.getPadraoJornalPublicacao().getTiposPadraoJornal()[0].getIdPadrao(), dbconn);
            for (int x = 0; x < listaMarcacaoIndividual.length; x++)
            {
                //Atualizando a referencia da fonte se tiver
                listaMarcacaoIndividual[x] = fachada.atualizaQueryFontes(listaMarcacaoIndividual[x], publicacaoJornal.getIdPublicacao(), sqlite);

                //Verificando se o campo da query NÃO está vazio
                if ( listaMarcacaoIndividual[x].getTextoAdicional() != null && !(listaMarcacaoIndividual[x].getTextoAdicional().equals("")) )
                {
                    query = parser.parse(listaMarcacaoIndividual[x].getTextoAdicional());
                    TopDocs results = searcher.search(query, listaMarcacaoIndividual[x].getTipoPadraoJornal().getQtdMaxResultQuery());

                    qtdMarcacoesEncontradas = (long) listaMarcacaoIndividual[x].getTipoPadraoJornal().getQtdMaxResultQuery() != 1000000 ? listaMarcacaoIndividual[x].getTipoPadraoJornal().getQtdMaxResultQuery() : results.totalHits;
                    ScoreDoc[] resultado = results.scoreDocs;
                    Document doc;
                    for ( ScoreDoc resultado1 : resultado )
                    {
                        doc = reader.document(resultado1.doc);
                        final long realNumDocID = Long.parseLong(doc.getField("real_doc_id").stringValue());
                        marcacaoPub = new MarcacaoPublicacao();
                        marcacaoPub.setIdPublicacao(publicacaoJornal.getIdPublicacao());
                        marcacaoPub.setIdTipoPadrao(listaMarcacaoIndividual[x].getIdTipoPadrao());
                        marcacaoPub.setNumDocLucene(realNumDocID + listaMarcacaoIndividual[x].getTipoPadraoJornal().getQtdLinhaAjusteAbaixo() - listaMarcacaoIndividual[x].getTipoPadraoJornal().getQtdLinhaAjusteAcima());
                        marcacaoPub.setLinhaPublicacao(Integer.parseInt(doc.getField("line").stringValue()) + listaMarcacaoIndividual[x].getTipoPadraoJornal().getQtdLinhaAjusteAbaixo() - listaMarcacaoIndividual[x].getTipoPadraoJornal().getQtdLinhaAjusteAcima());
                        marcacaoPub.setMarcacao(doc.getField("textoLinhaLimpa").stringValue().replace("'", "''"));
                        marcacaoPub.setSitCad("A");
                        marcacaoPub.setUsuCad(1);
                        marcacaoPub.setIdTipoPadraoJornal(listaMarcacaoIndividual[x].getIdTipoPadraoJornal());
                        
                        //verificando se a marcacao tem outra marcação para checar
                        if ( listaMarcacaoIndividual[x].getTipoPadraoJornal().getIdTipoPadraoCheck() == 0
                             || fachada.checarMarcacao(marcacaoPub.getMarcacao(), listaMarcacaoIndividual[x].getTipoPadraoJornal().getIdTipoPadraoCheck(), publicacaoJornal.getIdPublicacao(), sqlite) ){

                            //Verificando o regex pelo java antes incluir. O regex aqui poderá ser
                            //verificado case sensitive, e no lucene o texto está sendo indexado insensitive
                            final String rgxPattern = listaMarcacaoIndividual[x].getTipoPadraoJornal().getRegexIni();
                            if (rgxPattern == null || rgxPattern.length() <= 0 || marcacaoPub.getMarcacao().trim().matches(rgxPattern) )
                            {
                                fachada.incluirMarcacaoPublicacao(marcacaoPub, sqlite);
                            }
                        }
                    }

                    //verrificando se todas as marcações foram gravadas na base
                    if (qtdMarcacoesEncontradas > 0)
                    {
                        qtdMarcacoesGravadas = fachada.checkQuantidadeMarcacoes(marcacaoPub, sqlite);
                    }

                    if (qtdMarcacoesGravadas != qtdMarcacoesEncontradas)
                    {
                        //throw new QtdDifMarcacoesGravadaException(qtdMarcacoesEncontradas,qtdMarcacoesGravadas);
                    }

                }

            }

            reader.close();
        }
        catch (IOException | SQLException | ParseException ex)
        {
            if ( reader != null ) reader.close();
            logger.fatal(ex.getClass().getName() + " - " + ex.getMessage(), ex);
        }
    }
}
