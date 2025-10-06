package espe.lono.textsearcher.textsearcher;

import espe.lono.db.LonoConfigDB;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.enums.LonoConfigDB_Codes;
import espe.lono.db.models.*;
import espe.lono.indexercore.engine.Indexacao;
import espe.lono.indexercore.engine.UtilEngine;
import espe.lono.indexercore.util.Util;
import espe.lono.textsearcher.LonoTextSearcherConfigs;
import espe.lono.textsearcher.core.Colisao;
import espe.lono.textsearcher.core.SendEmailInterface;
import espe.lono.textsearcher.database.Fachada;
import espe.lono.textsearcher.query.DocIdFilterQuery;
import espe.lono.textsearcher.utils.LuceneUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LonoTextSearcher {
    public static int SEARCH_MAX_RESULTS = Integer.MAX_VALUE; // Modifique este campo p/ limitar a qtd. de ocorrencias
    final static private Logger logger = LonoTextSearcherConfigs.SEARCHER_LOG4_LOGGER;
    public static SendEmailInterface sendEmailInterface = null;
    /* ---------------------------------------------------------------------- */
    /**
     * Algoritmo de pesquisa de termo para os DJe com a lista termos específicos
     * @param dir
     * @param publicacaoJornal
     * @param sqlite
     * @param dbconn
     * @throws Exception
     */
    public static void pesquisarNomes(Directory dir, Directory dirPesquisa, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite, DbConnection dbconn) throws Exception
    {
        LonoTextSearcher.pesquisarNomes(dir, dirPesquisa, publicacaoJornal, null, sqlite, dbconn);
    }

    /**
     * Algoritmo de pesquisa para numero de OAB dos clientes cadastrados (Sobrecarga)
     * @param readerMarcacao
     * @param readerPesquisa
     * @param publicacaoJornal
     * @param preListaNomes
     * @param sqlite
     * @param dbconn
     * @throws Exception
     */
    private static void pesquisaOAB(DirectoryReader readerMarcacao, DirectoryReader readerPesquisa, PublicacaoJornal publicacaoJornal, NomePesquisaCliente[] preListaNomes, DbConnectionMarcacao sqlite, DbConnection dbconn) throws Exception
    {
        final String pubPDFfname = publicacaoJornal.getArqPublicacao();
        final Fachada fachada = new Fachada();
        NomePesquisaCliente[] listaNomes = null;

        // Obtendo a lista de nomes/termos a serem pesquisadas na publicacao
        // Nota: se necessario
        if ( preListaNomes == null )
        {
            logger.debug("Obtendo os num. de OABs a serem pesquisa no jornal '" + publicacaoJornal.getJornalPublicacao().getSiglaJornal() + "'");
            listaNomes = fachada.listarNumeroOABJornal(publicacaoJornal.getIdJornal(), dbconn);
        }
        else
        {
            // Obtendo a lista de TERMOS (APENAS N. OAB) a serem pesquisados
            List<NomePesquisaCliente> nomeToSearch = new ArrayList<NomePesquisaCliente>();
            for ( NomePesquisaCliente pn: preListaNomes) {
                if ( pn.getUfOAB() != null && pn.getUfOAB().length() > 0 )
                    nomeToSearch.add(pn);
            }

            if ( nomeToSearch.size() > 0 ) {
                listaNomes = new NomePesquisaCliente[nomeToSearch.size()];
                listaNomes = nomeToSearch.toArray(listaNomes);
            }
        }

        // Checando/Validando lista de nomes
        if ( listaNomes == null ) return;

        // log
        logger.debug("Pesquisando '" + listaNomes.length + "' numeros de OAB na publicacao:" + pubPDFfname);

        // Inicializando 'TRANSACTION'
        dbconn.iniciarTransaction();

        // Classe onde sera armazenado e gerenciads os dados de ocorrencias dos
        // clientes (evita colisao/exibicao do msm resultado p o mesmo cliente)
        Colisao colisaoMateria = new Colisao();

        // Montando a lista de clientes/ocorrencias
        Map<Integer, List<Integer>> clientesMaterias = new HashMap<Integer, List<Integer>>();

        // Lista de ocorrencias este cliente
        for ( NomePesquisaCliente cliente: listaNomes )
        {
            // Garantindo que e um numero de OAB
            if ( cliente.getUfOAB() == null || cliente.getUfOAB().length() <= 0 )
                continue;

            final String numeroOAB = cliente.getNomePesquisaLimpo().trim();
            final String ufOAB = cliente.getUfOAB().toLowerCase();
            final String ufOABRegex = "(" + numeroOAB + "(| |-|/|:)" + ufOAB + "|" + ufOAB + "(| |-|/|:)" + numeroOAB + ")";
            Pattern pattern = Pattern.compile(ufOABRegex);
            final String nomePesquisaLimpo = cliente.getNomePesquisaLimpo().trim();
            final String name2srch = LonoTextSearcher.NormalizarTextoPesquisa(nomePesquisaLimpo, dbconn);
            final String nome2srchExt = LonoTextSearcher.NormalizarTextoPesquisa(cliente.getNomePesquisaExt(), dbconn);
            //logger.debug("Pesquisado OAB: '" + nomePesquisaLimpo.toUpperCase() + "'/" + ufOAB + " em " + pubPDFfname);

            // Montantando texto/termo do lucene e pesquisando-o
            // Nota: este algoritmo é unificado, serve tanto p/ o DJPE como os
            //       outros jornais
            Object[][] results;
            try
            {
                // Pesquisa padrao (com recurso 'Literal' ou não)
                results = LonoTextSearcher.pesquisarTermo_Normal(name2srch, nome2srchExt, readerPesquisa, readerMarcacao, "contents", false, false);
            }
            catch ( Exception ex )
            {
                String message = "Não foi possivel pesquisar o termo: " + name2srch.toUpperCase();
                message += "... Ignorando a pesquisa para este termo";
                logger.error(message + " - Exception: " + ex.getMessage());

                // Marcando o termo como 'Suspenso'...
                //fachada.alterarSituacaoNomePesquisa(cliente.getIdNomePesquisa(), dbconn);
                continue;
            }

            // Checando se houve resultados
            if ( results.length <= 0 )
            {
                // Escrevendo dados sobre métrica
                fachada.inserirDadosPesquisa(
                        cliente.getIdCliente(),
                        cliente.getIdNomePesquisa(),
                        publicacaoJornal.getIdJornal(),
                        publicacaoJornal.getIdPublicacao(),
                        0,
                        dbconn);

                // Termo não encontrado,, passando p/ o próximo
                continue;
            }

            //logger.debug("Qtd. de ocorrencias PARCIAIS do número '" + name2srch.toUpperCase() + "'(OAB) foi de " + results.length);

            // Obtendo linha da ocorrencia e verificando se a UF definida existe
            // na proximidade (na mesma linha)
            int num_efetivas = 0;
            final Matcher matcher = pattern.matcher("");
            for ( final Object[] array: results )
            {
                // Tratando a ocorrência
                Object[] ocorrenciaTratadaObjs = LonoTextSearcher.tratarOcorrenciaLucene(array, readerMarcacao, matcher, colisaoMateria, cliente, publicacaoJornal, sqlite, dbconn);
                if ( ocorrenciaTratadaObjs == null ) // Estrutura de dados retornando é valido?
                    continue; // Ingorando...

                // Obtendo os dados
                MateriaPublicacao materiaPub = (MateriaPublicacao) ocorrenciaTratadaObjs[0];
                PautaPublicacao pautaPub = (PautaPublicacao) ocorrenciaTratadaObjs[1];

                // Alimentando vara juridica
                materiaPub.setVaraJurdica(obterVariaJuridicaJornal(publicacaoJornal.getJornalPublicacao()));

                // Checando se o termo encontrado está ignotado
                if ( fachada.numProcessoIgnorado(materiaPub, dbconn) )
                    continue; // Ignorando a matéria, o num do processo está ignorado.

                // Adicionando dados (materia e pauta) no banco de dados
                // Nota: se o ID da materia/pauta for diferente de zero, é pq já
                //       existe no banco e sera apenas feita a ligação
                // Nota2: Só adiciona a pauta se a materia foi adicionada (ret:0)
                //        caso contrario, nao adiciona por ja existir relacao
                int pubAddStatusCode = fachada.incluirCorteMateriaPublicacao(pautaPub, materiaPub, dbconn);
                if ( pubAddStatusCode == 0 ) {
                    fachada.incluirCortePautaPublicacao(pautaPub, materiaPub.getIdMateria(), dbconn);
                }

                num_efetivas += 1;

                // Adicionando a materia na lista do cliente (apenas as efetivas/salvas)
                if ( clientesMaterias.containsKey(materiaPub.getIdCliente()) ) {
                    clientesMaterias.get(materiaPub.getIdCliente()).add(materiaPub.getIdMateria());
                } else {
                    clientesMaterias.put(materiaPub.getIdCliente(), new ArrayList<Integer>());
                    clientesMaterias.get(materiaPub.getIdCliente()).add(materiaPub.getIdMateria());
                }
            }

            if ( num_efetivas > 0 ) {
                logger.debug("Qtd. de ocorrencias EFETIVAS/SALVAS do numero OAB '" + name2srch.toUpperCase() + "' foi de " + num_efetivas);
            }

            // Escrevendo dados sobre métrica
            fachada.inserirDadosPesquisa(
                    cliente.getIdCliente(),
                    cliente.getIdNomePesquisa(),
                    publicacaoJornal.getIdJornal(),
                    publicacaoJornal.getIdPublicacao(),
                    results.length,
                    num_efetivas,
                    dbconn);
        }

        // Dando COMMIT no banco de dados
        dbconn.finalizarTransaction_COMMIT();

        // Alimentando os dados do cliente
        logger.info("Foram reazalidos " + clientesMaterias.size() + " matchs para ese jornal.");
        for ( Integer idCliente : clientesMaterias.keySet() ) {
            logger.debug("Escrevendo os dados de Notificacao para o cliente -> " + idCliente);
            Integer num_efetivas = clientesMaterias.get(idCliente).size();
            final String[] messageContents = LonoTextSearcher.GerarTextosNotificacao(publicacaoJornal, num_efetivas);
            Usuario[] listUsuariosCliente = fachada.listarUsuariosCliente(idCliente, dbconn);

            // Escrevendo os dados de notificação no banco de dados
            fachada.escreverNotificacaoUsuarios(listUsuariosCliente, messageContents[0], messageContents[1], messageContents[2], false, dbconn);

            // Disparando o envio dos emails
            LonoTextSearcher.sendEmailInterface.sendEmail(idCliente, clientesMaterias.get(idCliente).toArray(new Integer[0]), "publicacao");
        }
    }

    /**
     * Algoritmo de pesquisa de termo para os DJe com a lista termos específicos (Sobrecarga)
     * @param dir
     * @param publicacaoJornal
     * @param preListaNomes
     * @param sqlite
     * @param dbconn
     * @throws Exception
     */
    public static void pesquisarNomes(Directory dir, Directory dirPesquisa, PublicacaoJornal publicacaoJornal, NomePesquisaCliente[] preListaNomes, DbConnectionMarcacao sqlite, DbConnection dbconn) throws Exception
    {
        final String pubPDFfname = publicacaoJornal.getArqPublicacao();
        NomePesquisaCliente[] listaNomes = null;
        final Fachada fachada = new Fachada();

        // Obtendo a lista de nomes/termos a serem pesquisadas na publicacao
        // Nota: se necessario
        if ( preListaNomes == null )
        {
            logger.debug("Obtendo os termos a serem pesquisa no jornal '" + publicacaoJornal.getJornalPublicacao().getSiglaJornal() + "'");
            listaNomes = fachada.listarNomesPesquisaJornal(publicacaoJornal.getIdJornal(), dbconn);
        }
        else
        {
            // Obtendo a lista de TERMOS (não N. OAB) a serem pesquisados
            List<NomePesquisaCliente> nomeToSearch = new ArrayList<NomePesquisaCliente>();
            for ( NomePesquisaCliente pn: preListaNomes) {
                if ( pn.getUfOAB() == null || pn.getUfOAB().length() <= 0 )
                    nomeToSearch.add(pn);
            }

            if ( nomeToSearch.size() > 0 ) {
                listaNomes = new NomePesquisaCliente[nomeToSearch.size()];
                listaNomes = nomeToSearch.toArray(listaNomes);
            }
        }

        // Abrindo Indice do Lucene
        final DirectoryReader readerMarcacao = DirectoryReader.open(dir);
        final DirectoryReader readerPesquisa = DirectoryReader.open(dirPesquisa);

        // Checando/Validando lista de nomes
        if ( listaNomes != null ) {
            // log
            logger.debug("Pesquisando '" + listaNomes.length + "' termos na publicacao:" + pubPDFfname);

            // Inicializando 'TRANSACTION'
            dbconn.iniciarTransaction();

            // Classe onde sera armazenado e gerenciads os dados de ocorrencias dos
            // clientes (evita colisao/exibicao do msm resultado p o mesmo cliente)
            Colisao colisaoMateria = new Colisao();

            // Montando a lista de clientes/ocorrencias
            Map<Integer, List<Integer>> clientesMaterias = new HashMap<Integer, List<Integer>>();

            // Lista de ocorrencias este cliente
            for ( NomePesquisaCliente cliente: listaNomes )
            {
                logger.debug("Pesquisa o termo " + cliente.getNomePesquisa() + " no jornal " + publicacaoJornal.getJornalPublicacao().getSiglaJornal());
                // Realizando psquisa no nome indicado
                final String nomePesquisaLimpo = cliente.getNomePesquisaLimpo().trim();
                final boolean pesqLiteral = (nomePesquisaLimpo.length() <= 4) ? true:cliente.isLiteral();
                final boolean proximity_search = (cliente.getPorcetualColisao() > 0);

                final String name2srch = LonoTextSearcher.NormalizarTextoPesquisa(nomePesquisaLimpo, dbconn);
                final String nome2srchExt = LonoTextSearcher.NormalizarTextoPesquisa(cliente.getNomePesquisaExt(), dbconn);

                // Verificando se o termo está na blacklist
                // Se sim, limita em no máximo 100 ocorrências
                if ( cliente.isBlacklist() ) LonoTextSearcher.SEARCH_MAX_RESULTS = 200;
                else LonoTextSearcher.SEARCH_MAX_RESULTS = Integer.MAX_VALUE;

                // Montantando texto/termo do lucene e pesquisando-o
                // Nota: este algoritmo é unificado, serve tanto p/ o DJPE como os
                //       outros jornais
                Object[][] results;
                try {
                    // Pesquisa padrao (com recurso 'Literal' ou não)
                    Object[][] results_normal = LonoTextSearcher.pesquisarTermo_Normal(name2srch, nome2srchExt, readerPesquisa, readerMarcacao, "contents", pesqLiteral, cliente.isNumProcesso());

                    // Pesquisa por 'Aproximação'
                    Object[][] results_proximity = null;
                    if (proximity_search) {
                        // Realizando pesquisa porcentual
                        results_proximity = LonoTextSearcher.pesquisarTermo_Porcentual(name2srch, nome2srchExt, readerPesquisa, readerMarcacao, "contents", cliente.getPorcetualColisao(), results_normal);
                    }

                    // Anexando dados/resposta
                    results = Util.mergeArraysPesquisa(results_normal, results_proximity);
                }
                catch ( Exception ex ) {
                    String message = "Não foi possivel pesquisar o termo: " + name2srch.toUpperCase();
                    message += "... Ignorando a pesquisa para este termo";
                    logger.error(message + " - Exception: " + ex.getMessage());
                    continue;
                }

                // Checando se houve resultados
                if ( results == null || results.length <= 0 ) {
                    // Escrevendo dados sobre métrica (apenas se não for OAB)
                    if ( cliente.getUfOAB() == null || cliente.getUfOAB().length() <= 0 ) {
                        fachada.inserirDadosPesquisa(
                                cliente.getIdCliente(),
                                cliente.getIdNomePesquisa(),
                                publicacaoJornal.getIdJornal(),
                                publicacaoJornal.getIdPublicacao(),
                                0,
                                dbconn);
                    }

                    // Termo não encontrado,, passando p/ o próximo
                    continue;
                }

                int num_efetivas = 0;
                final Pattern namePattern = Pattern.compile("(" + name2srch.replaceAll(" ", ".").toLowerCase().trim() + ")");
                final Matcher matcher = namePattern.matcher("");
                for ( final Object[] array: results )
                {
                    // Verificando se deve tratar o blacklist e se deve parar de escrever/tratar as ocorrências encontradas
                    if ( LonoTextSearcherConfigs.TRATAR_BLACKLIST && cliente.isBlacklist() && num_efetivas >= 100 )
                        break;

                    Object[] ocorrenciaTratadaObjs = LonoTextSearcher.tratarOcorrenciaLucene(array, readerMarcacao, matcher, colisaoMateria, cliente, publicacaoJornal, sqlite, dbconn);
                    if ( ocorrenciaTratadaObjs == null ) // Estrutura de dados retornando é valido?
                        continue; // Ingorando...

                    // Obtendo os dados
                    MateriaPublicacao materiaPub = (MateriaPublicacao) ocorrenciaTratadaObjs[0];
                    PautaPublicacao pautaPub = (PautaPublicacao) ocorrenciaTratadaObjs[1];

                    // Checando se o termo encontrado está ignotado
                    if ( fachada.numProcessoIgnorado(materiaPub, dbconn) )
                        continue; // Ignorando a matéria, o num do processo está ignorado.

                    // Adicionando dados (materia e pauta) no banco de dados
                    // Nota: se o ID da materia/pauta for diferente de zero, é pq já
                    //       existe no banco e sera apenas feita a ligação
                    // Nota2: Só adiciona a pauta se a materia foi adicionada (ret:0)
                    //        caso contrario, nao adiciona por ja existir relacao
                    int pubAddStatusCode = fachada.incluirCorteMateriaPublicacao(pautaPub, materiaPub, dbconn);
                    if ( pubAddStatusCode == 0 ) {
                        fachada.incluirCortePautaPublicacao(pautaPub, materiaPub.getIdMateria(), dbconn);
                    }

                    // Incrementando o contado de materias efetivas encontradas
                    // Nota: Isso não quer dizer que foi salva no banco, e sim, encontrada.
                    num_efetivas += 1;

                    // Adicionando a materia na lista do cliente (apenas as efetivas/salvas)
                    if ( clientesMaterias.containsKey(materiaPub.getIdCliente()) ) {
                        clientesMaterias.get(materiaPub.getIdCliente()).add(materiaPub.getIdMateria());
                    } else {
                        clientesMaterias.put(materiaPub.getIdCliente(), new ArrayList<Integer>());
                        clientesMaterias.get(materiaPub.getIdCliente()).add(materiaPub.getIdMateria());
                    }
                } // for

                // Deve criar/adicionar a materia informando o limite do termo?
                if ( cliente.isBlacklist() && num_efetivas > 0 && cliente.needNotifyBlacklist() ){
                    // Criando materia fake informando sobre o blacklist
                    final MateriaPublicacao materiaPublicacao = LonoTextSearcher.GerarDadosInformandoTermoBlacklist(cliente, publicacaoJornal.getJornalPublicacao(), publicacaoJornal, dbconn);
//                    final PautaPublicacao pautaPublicacao = (PautaPublicacao) fakeMaterias[1];

                    // Checando se já existe colisão
                    if ( materiaPublicacao != null ) {
                        int id_materia_existente = colisaoMateria.obterMateriaID(materiaPublicacao, 0, 0, dbconn);
                        if (id_materia_existente > 0) materiaPublicacao.setIdMateria(id_materia_existente);

                        // Gravando a vara juridica
                        materiaPublicacao.setVaraJurdica(obterVariaJuridicaJornal(publicacaoJornal.getJornalPublicacao()));

                        // Adicionando a materia na lista p/ a notificação
                        clientesMaterias.get(materiaPublicacao.getIdCliente()).add(materiaPublicacao.getIdMateria());
                    }

                    fachada.atualizarBlacklistNotifyDat(cliente.getIdNomePesquisa(), dbconn);
                }

                // Informando LOG
                if ( num_efetivas > 0 ) {
                    logger.debug("Qtd. de ocorrencias EFETIVAS/SALVAS do termo '" + name2srch.toUpperCase()+ "' foi de " + num_efetivas);
                }

                // Escrevendo dados sobre métrica (APENAS se não for OAB)
                if ( cliente.getUfOAB() == null || cliente.getUfOAB().length() <= 0 ) {
                    fachada.inserirDadosPesquisa(
                            cliente.getIdCliente(),
                            cliente.getIdNomePesquisa(),
                            publicacaoJornal.getIdJornal(),
                            publicacaoJornal.getIdPublicacao(),
                            results.length,
                            num_efetivas,
                            dbconn);
                }
            } // for

            // Dando COMMIT no banco de dados
            dbconn.finalizarTransaction_COMMIT();

            // Alimentando os dados do cliente
            for ( Integer idCliente : clientesMaterias.keySet() ) {
                Integer num_efetivas = clientesMaterias.get(idCliente).size();
                final String[] messageContents = LonoTextSearcher.GerarTextosNotificacao(publicacaoJornal, num_efetivas);
                Usuario[] listUsuariosCliente = fachada.listarUsuariosCliente(idCliente, dbconn);

                // Escrevendo os dados de notificação no banco de dados
                fachada.escreverNotificacaoUsuarios(listUsuariosCliente, messageContents[0], messageContents[1], messageContents[2], false, dbconn);
                
                // Disparando o envio dos e-mails
                LonoTextSearcher.sendEmailInterface.sendEmail(idCliente, clientesMaterias.get(idCliente).toArray(new Integer[0]),"publicacao");
            }
        }

        // Realizando pesquisa de Numeros de OAB
        LonoTextSearcher.pesquisaOAB(readerMarcacao, readerPesquisa, publicacaoJornal, preListaNomes, sqlite, dbconn);

        // Fechando Lucene...
        readerMarcacao.close();
        readerPesquisa.close();
    }

    /**
     * Extrai a materia (corte), usada no algoritmo: pesquisarNomes
     * @param materiaPublicacao
     * @param reader de Marcacao
     * @param limiteLinhaMateria
     * @return Uma array com a materia ja cortada (se possivel)
     *         e o status do corte(true == OK, false == NOT_OK)
     * @throws IOException
     */
    public static Object[] textoMateria(MateriaPublicacao materiaPublicacao, DirectoryReader reader, int limiteLinhaMateria, DbConnectionMarcacao sqlite) throws Exception
    {
        Document doc = null;
        final StringBuilder processo = new StringBuilder();
        long inicioMateria, fimMateria, linhaCliente, realInicioMateria = 0;
        boolean corte_lono = true;

        // Checando tamanho/distancia do INICIO da materia p/ a linha da
        // ocorrencian... (evita corte (errados?) muito longos)
        linhaCliente = materiaPublicacao.getLinhaCliente();
        if ( (linhaCliente - materiaPublicacao.getLinhaInicialMateria()) > limiteLinhaMateria )
        {
            corte_lono = false;
            inicioMateria = linhaCliente - 15;
        }
        else
        {
            inicioMateria = materiaPublicacao.getLinhaInicialMateria();
            if  ( inicioMateria < 1 ) inicioMateria = 1;
        }

        // Checando tamanho/distancia do FIM da materia p/ a linha da
        // ocorrencian... (evita corte (errados?) muito longos)
        if ( (materiaPublicacao.getLinhaFinalMateria() - linhaCliente) > limiteLinhaMateria )
        {
            corte_lono = false;
            fimMateria = linhaCliente + 15;
        }
        else
        {
            fimMateria = materiaPublicacao.getLinhaFinalMateria();
            TopDocs fimTopDoc = LuceneUtils.GetTopDocByRealDocIdValue(fimMateria,reader);
            if ( fimTopDoc != null && fimTopDoc.scoreDocs[0].doc > reader.maxDoc() )
            {
                fimMateria = reader.maxDoc();
                corte_lono = false;
            }
        }

        if ( fimMateria == inicioMateria ) fimMateria += 1;

        String processo_linha = "";
        boolean ignore_coment = false;

        // Petrus Augusto - 09-12-2017
        //      Suporte a ignorar conteudo desnecessario/inutil
        final String sql = "SELECT mp.marcacao "
                + "FROM " + sqlite.obterNomeTabela() + " mp "
                + "WHERE mp.num_doc_lucene = ? "
                + "AND id_tipo_padrao IN (6, 7, 8, 9, 10, 11, 12) ";
        final PreparedStatement pstm = sqlite.obterPreparedStatement(sql);

        // Percorrendo os documentos para obter o texto linha por texto
        for ( long x = inicioMateria; x < fimMateria; x++ )
        {
            // Checando se o document foi removeido
            doc = LuceneUtils.GetDocumentByRealDocIdValue(x, reader);
            if ( doc == null )
            {
                // Documento removido, ignorando esta linha..
                continue;
            } else if ( realInicioMateria <= 0 ) {
                realInicioMateria = x;
            }

            // Petrus Augusto -> 09-11-2017
            //      Checando se o documento e de um tipo que deve ser ignorada
            //      Exemplo: Cabecalho, Rodape, N. Pagina, N. Jornal, etc...
            pstm.setLong(1, x);
            final ResultSet result = sqlite.abrirConsultaSql(pstm);
            if ( result.next() ) {
                // Deve ignorar esta marcacao
//                System.err.println("Ignorando documento -> " + x + ", Texto -> " + doc.getField("textoLinhaLimpa").stringValue());
                continue;
            }

            // Obtendo documento e o conteudo
            //doc = espe.lono.indexercore.util.Util.getLuceneDocumentByID(x, reader);
            final String doc_line = doc.getField("textoLinhaLimpa").stringValue();
            if ( doc_line == null || doc_line.length() <= 0 )
                continue;

            // Checando se é as primeiras 'X' linha da materia
            if ( x <= (inicioMateria + 4) && corte_lono )
            {
                // Salvando o numero do processo
                // Apenas p/ cortes validos...
                processo_linha += doc_line.trim() + " ";
            }

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

            // Anexando linha
            processo.append(doc_line).append(" ");
        }

        // Obtendo os valores reais de inicio/fim da materia
        if ( doc == null ) return null; // Corte nao obtido!

        int real_linha_final_materia = Integer.parseInt(doc.getField("line"). stringValue());
        int real_pagina_final_materia = Integer.parseInt(doc.getField("pagina").stringValue());

        //doc = reader.document(inicioMateria);
        doc = LuceneUtils.GetDocumentByRealDocIdValue(realInicioMateria, reader);

        //doc = espe.lono.indexercore.util.Util.getLuceneDocumentByID(inicioMateria, reader);
        int real_linha_inicio_materia = Integer.parseInt(doc.getField("line"). stringValue());
        int real_pagina_inicio_materia = Integer.parseInt(doc.getField("pagina").stringValue());

        if ( real_linha_final_materia == 0 ) real_linha_final_materia = real_linha_inicio_materia;
        if ( real_pagina_final_materia == 0 ) real_pagina_final_materia = real_pagina_inicio_materia;

        // Definindo os valores de inicio/fim da materia
        materiaPublicacao.setLinhaInicialMateria(real_linha_inicio_materia);
        materiaPublicacao.setPagina(real_pagina_inicio_materia);
        materiaPublicacao.setLinhaFinalMateria(real_linha_final_materia);
        materiaPublicacao.setPaginaFimMateria(real_pagina_final_materia);

        // Deixando apenas os numeros
        if ( processo_linha.length() > 0 )
        {
            // Tratando a linha p/ remover possiveis espaços entre os numeros (se houver)
            final String betweneNumberPattern = "([0-9\\-.]{4,})(\\s+|)([0-9\\-.]{2,})";
            if ( processo_linha.matches(betweneNumberPattern) ) {
                processo_linha = processo_linha.replaceAll(betweneNumberPattern, "$1$3");
            }

            // Pega apenas as quatro primeiras palavras
            String[] spt = processo_linha.split(" ");
            processo_linha = "-";
            for ( String spt1 : spt )
            {
                // Obtendo  numero do processo...
                final String proc_num = spt1.replaceAll("[^\\d+.\\-]", "");
                if ( proc_num.length() >= 8 )
                {
                    char fst_char = proc_num.charAt(0);
                    if ( fst_char == '-' || fst_char == ':' )
                        processo_linha = proc_num.substring(1);
                    else
                        processo_linha = proc_num;
                    break;
                }
            }
        }

        // Montando a array a ser retornado
        // Nota: lembrar que:
        //       na posicao '0' e um objeto Boolean
        //       na posicao '1' é um objeto String (materia)
        //       na posicao '2' é um objeto String (n. processo)
        Object[] retArray = new Object[3];
        retArray[0] = corte_lono;
        retArray[1] = processo.toString();
        retArray[2] = (processo_linha.length() <= 100) ? processo_linha : "";
        return retArray;
    }

    private static boolean locatedTextInsideMateria(MateriaPublicacao materiaPublicacao, String nomePesquisaLimpo, DbConnection dbconn) {
        try {
            // Normalizando o texto de pesquisa (remoção de acentos e etc)
            final String normalizedMateriaText = Util.normalizeText(UtilEngine.removeAccents(materiaPublicacao.getMateria().replaceAll("\n", " ").trim()), dbconn);
            return (normalizedMateriaText.contains(nomePesquisaLimpo));
        } catch (SQLException exception) {
            logger.error("Erro durante a normalização do texto para a pesquisa concatednada. -> " + exception.getMessage());
            return false;
        }
    }

    public static Object[] tratarOcorrenciaLucene(Object[] ocorrenciaLucene, DirectoryReader readerMarcacao, Matcher matcher, Colisao colisaoMateria, NomePesquisaCliente nomePesquisaCliente, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite, DbConnection dbconn) throws IOException, SQLException, ParseException
    {
        return tratarOcorrenciaLucene(ocorrenciaLucene, readerMarcacao, matcher, colisaoMateria, nomePesquisaCliente, publicacaoJornal, sqlite, dbconn, false);
    }
    /**
     *
     * @param ocorrenciaLucene -> Ocorrencias Lucene encontrada
     * @param readerMarcacao -> DirectoryReader das marcações
     * @param matcher -> Regex para a verificação/validação do termo encontrado
     * @param colisaoMateria -> Classe de controle p/ as Coliçoes (Opcional)
     * @param nomePesquisaCliente -> Classe do tipo NomePesquisaCliente (contem informações referente ao termo pesquisado)
     * @param publicacaoJornal -> PublicacaoJornal (contém informações referentes a publicação)
     * @param sqlite -> Conexão com o banco de marcação
     * @param dbconn -> Conexão com o banco principal
     * @return -> Array e objeto GENERICO de duas posições contendo : 0 == MateriaPublicacao, 1 ==  PautaPublicacao
     * @throws IOException
     * @throws SQLException
     * @throws ParseException
     */
    public static Object[] tratarOcorrenciaLucene(Object[] ocorrenciaLucene, DirectoryReader readerMarcacao, Matcher matcher, Colisao colisaoMateria, NomePesquisaCliente nomePesquisaCliente, PublicacaoJornal publicacaoJornal, DbConnectionMarcacao sqlite, DbConnection dbconn, boolean isHistorico) throws IOException, SQLException, ParseException
    {
        final Fachada fachada = new Fachada();
        final int idPublicacao = publicacaoJornal.getIdPublicacao();
        final int limiteLinhaMateria = publicacaoJornal.getJornalPublicacao().getLimiteLinhaMateria();
        final Pattern stfDepachoAttachPattern = Pattern.compile("despacho.(\\s+|)id.ntico ao de", Pattern.CASE_INSENSITIVE);

        if ( ocorrenciaLucene == null || ocorrenciaLucene.length < 2 ) // Estrutura de dados retornando é valido?
            return null;

        // Obtendo dados usados ao longo do loop
//        logger.debug("Obtendo dados do documento");
        final int doc_id = (int) ocorrenciaLucene[0];
        final Document doc = readerMarcacao.document(doc_id);
        //final Document doc = espe.lono.indexercore.util.Util.getLuceneDocumentByID(doc_id, readerMarcacao);
        final boolean verificar_sentenca_texto = (boolean) ocorrenciaLucene[1];
        final boolean pesquisa_proximidade = (boolean) ocorrenciaLucene[2];
        final String termo_pesquisa_proximidade = (pesquisa_proximidade) ? (String) ocorrenciaLucene[3] : "";

        // Obtendo valores da linha real do cliente e da pagina
        int real_line = Integer.parseInt(doc.getField("line").stringValue());
        int real_page = Integer.parseInt(doc.getField("pagina").stringValue());
        long num_doc_lucene = Long.parseLong(doc.getField("real_doc_id").stringValue());

        // Criando classe p/ a materia
//        logger.debug("Criando classes de materia");
        MateriaPublicacao materiaPub = new MateriaPublicacao();
        materiaPub.setSiglaJornal(publicacaoJornal.getJornalPublicacao().getSiglaJornal());
        materiaPub.setIdJornal(publicacaoJornal.getIdJornal());
        materiaPub.setPesquisaProximidade(pesquisa_proximidade);
        materiaPub.setTermoPesquisaProximidade(termo_pesquisa_proximidade );
        materiaPub.setIdCliente( nomePesquisaCliente.getIdCliente() );
        materiaPub.setIdPublicacao(idPublicacao);
        materiaPub.setHistorico(isHistorico);

//        logger.debug("Iniciando sessão com varias consultas ao banco de dados da marcação...");
//        logger.debug("Dados das linhas de inicio/fim da materia");
        // temporario, p obtencao do titulo/subtitulo e texto
        materiaPub.setLinhaCliente( num_doc_lucene );
        materiaPub = fachada.listarLinhasInicioFimMateria(materiaPub, sqlite);
//        logger.debug("Dados das linhas do titulo/subitutlo");
        materiaPub = fachada.listarTituloSubtituloMateria(materiaPub, sqlite);

//        logger.debug("Dados da pauta");
        PautaPublicacao pautaPub = fachada.listarPautaMateria(materiaPub, sqlite);
        if( pautaPub == null )
        {
            pautaPub = new PautaPublicacao();
            pautaPub.setIdPublicacao(idPublicacao);
            pautaPub.setPauta("");
            pautaPub.setPagina(0);
        }

        // Checando se esta publicacao ja esta na lista deste cliente,
        // se ja foi lido/salvo esse documento p/ esse cliente...
        if ( colisaoMateria != null && colisaoMateria.checarColisao(materiaPub) )
        {
            // Materia ja esta na lista deste cliente... ignorando-a
            return null;
        }

        // Salvando dados do documento para a adicao no index de 'Colisao'
        long[] materiaColisaoInfo = {
                materiaPub.getLinhaInicialMateria(),
                materiaPub.getLinhaFinalMateria()
        };

        // Obtendo texto/conteudo fixo inicial das materia (se exisitr)
//        logger.debug("Dados de conteudo fixo da materia");
        String inicioMateria = fachada.obterConteudoFixoInicioMateria(readerMarcacao, materiaPub, sqlite);
        if ( inicioMateria == null ) inicioMateria = "";
        else inicioMateria += " ";

        // Obtendo texto/conteudo fixo FINAL das nateruas (se existir)
        String fimMateria = fachada.obterConteudoFixoFimMateria(readerMarcacao, materiaPub, sqlite);
        if ( fimMateria == null ) inicioMateria = "";
        else fimMateria = " " + fimMateria;

//        logger.debug("Seção ligado as consultas do BD foram, praticamente, completadas");

        // Ontendo corte da materia e o status do corte (boolean)
        // Obtendo o texto da materia, n. processo e o status do corte
        // Nota: anexa o texto de Pauta no inicio do texto da materia
        Object[] textoMateriaReturnArray;
        try {
//            logger.debug("Obtendo o corte da máteria");
            textoMateriaReturnArray = LonoTextSearcher.textoMateria(materiaPub, readerMarcacao, limiteLinhaMateria, sqlite);
        } catch ( Exception ex ) {
            logger.error("Erro obtendo a materia:" + ex.getClass().getName() + " <> " + ex.getMessage());
            return null;
        }

        if ( textoMateriaReturnArray == null || textoMateriaReturnArray.length < 3 )
            return null;
        if ( textoMateriaReturnArray[0] == null || textoMateriaReturnArray[1] == null )
            return null;

        // Salvando o numero do processo obtido na pesquisa anterior
        final String processo_num = ((textoMateriaReturnArray[2] != null) ? (String) textoMateriaReturnArray[2]:"-");
        String materia_texto = (String) textoMateriaReturnArray[1];

        // Verificando sentenca
        if (verificar_sentenca_texto) {
            if ( nomePesquisaCliente.isNumProcesso() ) {
                // Comparando os numeros de processo
                final String numProcessoLimpo = processo_num.replaceAll("[^\\d+]","");
                final String numProcessoLimpo_Srch = nomePesquisaCliente.getNomePesquisa().replaceAll("[^\\d+]","");
                if ( numProcessoLimpo_Srch != numProcessoLimpo )
                    return null;
            } else {
                // Verificando sentenca/ocorrencia do texto
                final String materiaTextoParaRegex = LonoTextSearcher.NormalizarTextoPesquisa(materia_texto, dbconn);
                matcher.reset(materiaTextoParaRegex);
                if (!matcher.matches() && !matcher.find())
                    return null;
            }
        }

        // Checando jornal para açoes relacionados especificas
        if ( publicacaoJornal.getJornalPublicacao().getSiglaJornal().contains("STF") )
        {
            // Checando se tem o termo 'Despacho; Identico ao de nº X' (STF)
            final Matcher match = stfDepachoAttachPattern.matcher(materia_texto);
            if ( match.find() || match.matches() )
            {
                // Psquisando anexo de despacho do STF
                final String despachoAnexo = LonoTextSearcher.STFObterDespachoCitado(publicacaoJornal.getDtPublicacao(), publicacaoJornal.getIdJornal(), materia_texto, readerMarcacao, fachada, sqlite);
                if ( despachoAnexo != null && despachoAnexo.length() > 0 )
                {
                    // Anexando despacha encontrado
                    materia_texto = materia_texto.replaceAll("(?i)despacho.(\\s+|)id.ntico ao de.*\\d+", "");
                    materia_texto += despachoAnexo;
                }
                else
                {
                    // Nao foi possivel encontrar o despacho...
                    // Marcando o 'Corte Lono' como FALSO
                    textoMateriaReturnArray[0] = (boolean) false;
                }
            }
        }

        // Definindo...
        final String finalMateriaTexto = inicioMateria + materia_texto + fimMateria;
        materiaPub.setMateria( finalMateriaTexto.trim() );
        materiaPub.setCorteLono( (boolean) textoMateriaReturnArray[0] );
        materiaPub.setProcesso( processo_num );

        // Definindo outras opcoes..
        materiaPub.setLinhaCliente(real_line);
        materiaPub.setPagina(real_page);
        materiaPub.setIdNomePesquisa( nomePesquisaCliente.getIdNomePesquisa() );
        materiaPub.setUsuCad(1);
        materiaPub.setSitCad("A");

        // Checando se deve tratar as colisões
        if ( colisaoMateria != null ) {
            // Obtem o ID da materia existent
            // Nota: retorna '0' se nao existir esta materia
            int id_materia_existente = colisaoMateria.obterMateriaID(materiaPub, materiaColisaoInfo[0], materiaColisaoInfo[1], dbconn);
            materiaPub.setIdMateria(id_materia_existente);

            // Obtem o ID da pauta desta materia
            // Nota: retorna '0' se nao existir esta pauta
            int id_pauta_existente = colisaoMateria.obterPautaID(pautaPub, dbconn);
            pautaPub.setIdPauta(id_pauta_existente);

            // Adicionando info da materia na lista de colisao deste cliente
            // Nota: faz isso apenas se o corte estiver marcado como correto
            if ( materiaPub.getCorteLono() ) {
                colisaoMateria.adicionaMateria(
                    materiaPub.getIdCliente(), // ID do cliente
                    materiaColisaoInfo[0],  // InicioDocumento
                    materiaColisaoInfo[1] // FimDocumento
                );
            }
        }

        // Retornando os dados
        return new Object[] { materiaPub, pautaPub};
    }

    /**
     *
     * @param materia_original
     * @param reader
     * @param fachada
     * @param sqlite
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws SQLException
     */
    static public String STFObterDespachoCitado(final Date dtPublicacao, final int idJornal, final String materia_original, IndexReader reader, Fachada fachada, DbConnectionMarcacao sqlite) throws ParseException, IOException, SQLException
    {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        final long baseNumDocID = Long.parseLong(String.format("%s%02d", sdf.format(dtPublicacao), idJornal));
        final IndexSearcher searcher = new IndexSearcher(reader);
        final Analyzer analyzer = new StandardAnalyzer();

        // Isolando numero do despacho
        Pattern pattern = Pattern.compile("Despacho.*ao de n.(\\s+|)\\d+\\s+", Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(materia_original.replaceAll("\\s+", " ").trim());
        if ( match.find() == false ) return null;

        // Cortando todos os caracteres e deixando apenas o numero relacionado
        // ao despacho da mateia citada
        final String despachoNumero = match.group().replaceAll("[^\\d+]","");
        final String luceneQuery  = "+conteudo:" + despachoNumero + " +left:([00350 TO 00450]|[00800 TO 00900]) + real_doc_id:[" + baseNumDocID + "01 TO *] + conteudo:href";

        // Procurando ocorecia deste numero
        final QueryParser parser = new QueryParser("conteudo", analyzer);
        final Query query = parser.parse(luceneQuery);
        final TopDocs results = searcher.search(query, 10);
        final Bits liveDocs = MultiFields.getLiveDocs(reader);
        int documentNumber = 0;
        for ( final ScoreDoc located_doc: results.scoreDocs )
        {
            // Checando se o documento ja foi removido
            if ( liveDocs != null && !liveDocs.get(located_doc.doc) )
            {
                // Documento removido, ignorando-o
                continue;
            }

            final Document doc = reader.document(located_doc.doc);
            final String contentText = doc.getField("conteudo").stringValue();
            if ( !contentText.contains("href") &&
                    contentText.contains("<b>") && contentText.contains("(" + despachoNumero + ")") )
            {
                documentNumber = located_doc.doc;
                break;
            }
        }

        if ( documentNumber == 0 )
            return ""; // Nao localizado

        // Obtendo a materia do documento localizado...
        MateriaPublicacao materiaPub = new MateriaPublicacao();
        materiaPub.setLinhaCliente( documentNumber );
        materiaPub = fachada.listarLinhasInicioFimMateria(materiaPub, sqlite);

        // Pesquisando regiao/posicao do texto p/ localizar o texto de despacho
        Document doc;
        StringBuilder despachoSB = new StringBuilder();
        boolean writing = false;
        pattern = Pattern.compile("DESPACHO", Pattern.CASE_INSENSITIVE);
        match = pattern.matcher("");
        for ( long x = materiaPub.getLinhaInicialMateria(); x < materiaPub.getLinhaFinalMateria(); x++ )
        {
            // Checando se o documento ja foi removido
            doc = LuceneUtils.GetDocumentByRealDocIdValue(x, reader);
            if ( doc == null )
            {
                // Documento removido, ignorando-o
                continue;
            }

            final String text = doc.getField("textoLinhaLimpa").stringValue().trim();

            // Checando posicao do texto
            if ( writing == false )
            {
                // Checando o conteudo o regex do conteudo inicial
                match.reset(text);
                if ( match.find() || match.matches() )
                    writing = true;
                else
                    continue;
            }

            // Checando se deve gravar o texto atual
            if ( writing )
            {
                despachoSB.append(text);
                despachoSB.append(" ");
            }
        }

        // Retornando o texto encontrado
        return despachoSB.toString();
    }

    static public Query montarQueryPesquisa(final String text, final String fieldName, final boolean literal)
    {
        Query query; // Query de pesquisa
        //final Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

        // Montando query (com base no tamanho da string)
        final String[] splitted_string = text
            .replaceAll("-", " ") // Substituindo o traço por espaço para buscas por CPF
            .replaceAll("/", " ") // Substituindo a barra por espaço para buscas por CNPJ
            .replaceAll("\\s+", " ")
            .trim().split(" ");

        if ( splitted_string.length == 1 ) {
            // Montando o WildCardQuery
            String termText = ( literal == false ) ? "*" : "";
            termText += splitted_string[0] + (( literal == false ) ? "*" : "");
            query = new WildcardQuery(new Term(fieldName, termText.toLowerCase()));
        } else {
            // Montando o SpanNearQuery
            final SpanQuery[] clauses = new SpanQuery[ splitted_string.length ];
            final int lastIdx = splitted_string.length - 1;
            for ( int idx = 0; idx < splitted_string.length; idx++ )
            {
                String termText = "";
                if ( idx == 0 && literal == false ) termText += "*";
                termText += splitted_string[idx];
                if ( idx == lastIdx && literal == false ) termText += "*";

                WildcardQuery clauseQuery = new WildcardQuery(new Term(fieldName, termText.toLowerCase()));
                clauses[idx] = new SpanMultiTermQueryWrapper( clauseQuery );
            }
            query = new SpanNearQuery(clauses, 0, true);
        }

        return query;
    }

    static public Object[][] pesquisarTermo_Normal(final String text, IndexReader readerPesquisa, IndexReader readerMarcacao, String fieldName, boolean literal, boolean isNumProcesso) throws Exception
    {
        return pesquisarTermo_Normal(text, null, readerPesquisa, readerMarcacao, fieldName, literal, isNumProcesso);
    }

    /**
     * Pesquisa Normal (100%)
     * @param text
     * @param textExtra
     * @param readerPesquisa
     * @param readerMarcacao
     * @param fieldName
     * @param literal
     * @return
     * @throws Exception
     */
    static public Object[][] pesquisarTermo_Normal(final String text, final String textExtra, IndexReader readerPesquisa, IndexReader readerMarcacao, String fieldName, boolean literal, boolean isNumProcesso) throws Exception
    {
        final IndexSearcher searcher = new IndexSearcher(readerPesquisa);
        Query query = LonoTextSearcher.montarQueryPesquisa(text, fieldName, (literal || isNumProcesso)); // Query de pesquisa

        ArrayList<Object[]> locatedValues = new ArrayList();
        final Sort sort = new Sort(new SortField(null, SortField.Type.DOC, true));
        TopDocs results = searcher.search(query, LonoTextSearcher.SEARCH_MAX_RESULTS, sort);

        // Pesquisando o termo extra
        if ( textExtra != null && !textExtra.isEmpty()) {
            // Tratando o texto-entra (AND)
            boolean isMultiple = textExtra.replaceAll("\\s+", " ").trim().split(" ").length > 1;

            Query wildCardQuery = LonoTextSearcher.montarQueryPesquisa(textExtra, fieldName, (literal || isNumProcesso));
            Query extraQuery = new DocIdFilterQuery(wildCardQuery, results.scoreDocs, isMultiple);
            TopDocs results2 = searcher.search(extraQuery, LonoTextSearcher.SEARCH_MAX_RESULTS, sort);
            results = results2;
        }

        final Bits liveDocs = MultiFields.getLiveDocs(readerMarcacao);
        for ( final ScoreDoc ldoc: results.scoreDocs )
        {
            // Obtendo o "real_doc_id"
            String real_doc_id_Value = readerPesquisa.document(ldoc.doc).getField("real_doc_id").stringValue();

            // Obtendo o elemento pelo 'real_doc_id' no readerMarcacao
            //TermQuery marcacoesQuery = new TermQuery(new Term("real_doc_id",real_doc_id_Value));
            //final TopDocs marcacaoResults = searcherMarcacao.search(marcacoesQuery, 1);
            final TopDocs marcacaoResults = LuceneUtils.GetTopDocByRealDocIdValue(real_doc_id_Value, readerMarcacao);
            if ( marcacaoResults == null ) {
                continue;
            }

            // Obtendo o elemento e armazenando na array
            final ScoreDoc located_doc = marcacaoResults.scoreDocs[0];
            Object[] addArray = new Object[5];
            addArray[0] = located_doc.doc; // ID do Documento-Lucene
            addArray[1] = true; // Indica se deve checar a sentenca do texto
            addArray[2] = false;
            addArray[3] = null;
            locatedValues.add( addArray );
        }

        return locatedValues.toArray( new Object[0][0] );
    }


    static public Object[][] pesquisarTermo_Porcentual(final String text, IndexReader readerPesquisa, IndexReader readerMarcacao, String fieldName, float porcentual, Object[][] normalArrayResults) throws IOException
    {
        return pesquisarTermo_Porcentual(text, null, readerPesquisa, readerMarcacao, fieldName, porcentual, normalArrayResults);
    }
    /**
     * Pesquisa Porcentual
     * @param text
     * @param readerPesquisa
     * @param readerMarcacao
     * @param fieldName
     * @param porcentual
     * @param normalArrayResults
     * @return
     * @throws IOException
     */
    static public Object[][] pesquisarTermo_Porcentual(final String text, String textExtra, IndexReader readerPesquisa, IndexReader readerMarcacao, String fieldName, float porcentual, Object[][] normalArrayResults) throws IOException
    {
        // Checando o tipo de algoritimo indexador no momento
        if ( Indexacao.obterTipoIndexacaoAtual() == Indexacao.TipoIndexacao.PESQUISA_101 )
            return null;

        // Verificando se o termo contem algum numero
        if ( text.matches(".*\\d.*") )
            return null;

        final IndexSearcher searcher = new IndexSearcher(readerPesquisa);
        final IndexSearcher searcherMarcacao = new IndexSearcher(readerMarcacao);
        Query query = createSpanNearQuery(text, fieldName, porcentual);

        // Reaizado pesquisas e retornando os resultados
        ArrayList<Object[]> locatedValues = new ArrayList();
        final Sort sort = new Sort(new SortField(null, SortField.Type.DOC, true));
        TopDocs results = searcher.search(query, LonoTextSearcher.SEARCH_MAX_RESULTS, sort);

        // Pesquisando o termo extra
        if ( textExtra != null && !textExtra.isEmpty()) {
            // Tratando o texto-entra (AND)
            boolean isMultiple = textExtra.replaceAll("\\s+", " ").trim().split(" ").length > 1;
            Query extraQuery = new DocIdFilterQuery(createSpanNearQuery(textExtra, fieldName, porcentual), results.scoreDocs, isMultiple);
            TopDocs results2 = searcher.search(extraQuery, LonoTextSearcher.SEARCH_MAX_RESULTS, sort);
            results = results2;
        }

        final Bits liveDocs = MultiFields.getLiveDocs(readerMarcacao);
        for ( final ScoreDoc ldoc: results.scoreDocs )
        {
            // Obtendo o "real_doc_id"
            String real_doc_id_Value = readerPesquisa.document(ldoc.doc).getField("real_doc_id").stringValue();

            // Obtendo o elemento pelo 'real_doc_id' no readerMarcacao
            TermQuery marcacoesQuery = new TermQuery(new Term("real_doc_id",real_doc_id_Value));
            final TopDocs marcacaoResults = searcherMarcacao.search(marcacoesQuery, 1);
            if ( marcacaoResults.totalHits <= 0 ) {
                continue;
            }

            // Obtendo o elemento e armazenando na array
            final ScoreDoc located_doc = marcacaoResults.scoreDocs[0];

            // Checando se o documento ja existe na lista originak
            if ( normalArrayResults != null && LonoTextSearcher.verificarDocumentIdInResultArray(located_doc.doc, normalArrayResults) )
                continue; // ja existe este documento...

            // Obtendo termo (Fuzzy) localizado
            // Nota: Este algoritmo usa os dados de explicacao do Searcher para
            //       tentar identificar o termo localizado...
            Document locatedDoc = readerMarcacao.document(located_doc.doc);
            Explanation exp = searcher.explain(query, located_doc.doc);
            final String fuzzyTermLocated = LonoTextSearcher.obterTermoFuzzyLocalizado(
                    exp.getDescription(), // Descricao/Informacao da ocorrencia
                    locatedDoc.getField(fieldName).stringValue(),  // Texto do documento
                    fieldName // Nome do campos
            ).trim();

            // Checando se o termo foi de fato, ecnontrado
            if ( fuzzyTermLocated.length() <= 0 )
            {
                // Ignorando esta ocorrencia...
                continue;
            }

            // Putting inside array
            Object[] addArray = new Object[4];
            addArray[0] = located_doc.doc; // ID do Documento-Lucene
            addArray[1] = false; // Indica se deve checar a sentenca do texto
            addArray[2] = true; // Indica se foi a pesquisa percentual
            addArray[3] = fuzzyTermLocated; // Apenas na percentual, o termo localizado
            locatedValues.add( addArray );
        }

        return locatedValues.toArray( new Object[0][0] );
    }

    /**
     * Normaliza/Trata o texto para ser pesquisado corretamente
     * @param text -> Texto a ser pesquisado
     * @param dbConnection -> Conexão com o banco Lono
     * @return -> Texto tratado
     */
    static public String NormalizarTextoPesquisa(String text, DbConnection dbConnection)
    {
        if ( text == null || text.trim().length() <= 0 ) return null;
        try {
            return Util.normalizeText(text, dbConnection);
        } catch (SQLException e) {
            logger.error("Erro normalizando o texto '" + text + "' -> " + e.getMessage());
            return text;
        }
    }

    /**
     * Gera uma array contendo o texto para a notificação
     * @param publicacao -> Publicacao
     * @param numMateriasNovasEncontradas -> N. matérias encontradas
     * @return -> Array[0 == Assunto, 1 == Mensagem, 2 == ActionURL]
     */
    static public String[] GerarTextosNotificacao(PublicacaoJornal publicacao, int numMateriasNovasEncontradas)
    {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Montando o texto do Assunto
        final String assunto = numMateriasNovasEncontradas + ((numMateriasNovasEncontradas > 1) ? (" ocorrências encontradas"):(" ocorrência encontrada"));

        // Montando o texto da Mensagem
        final StringBuilder messageSB = new StringBuilder();
        if ( numMateriasNovasEncontradas == 1 ) messageSB.append("Existe uma nova matéria");
        else messageSB.append("Existem " + numMateriasNovasEncontradas + " novas matérias");

        messageSB.append(" na publicação ");
        messageSB.append(publicacao.getJornalPublicacao().getSiglaJornal() + "#" + publicacao.getEdicaoPublicacao());
        messageSB.append(" (" + sdf.format(publicacao.getDtPublicacao()) + ")");

        // Texto do ActionURL
        final String actionUrl = "/application/materia";

        // Retornando os dados
        return new String[] {assunto, messageSB.toString(), actionUrl};
    }

    /**
     * Gera uma array contendo o texto para a notificação
     * @param publicacao -> Publicacao
     * @param numMateriasNovasEncontradas -> N. matérias encontradas
     * @return -> Array[0 == Assunto, 1 == Mensagem, 2 == ActionURL]
     */
    static public String[] GerarTextosNotificacaoBlog(PublicacaoJornal publicacao, int numMateriasNovasEncontradas)
    {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Montando o texto do Assunto
        final String assunto = numMateriasNovasEncontradas + ((numMateriasNovasEncontradas > 1) ? (" ocorrências encontradas"):(" ocorrência encontrada"));

        // Montando o texto da Mensagem
        final StringBuilder messageSB = new StringBuilder();
        if ( numMateriasNovasEncontradas == 1 ) messageSB.append("Existe uma nova matéria");
        else messageSB.append("Existem " + numMateriasNovasEncontradas + " novas matérias");

        messageSB.append(" no blog ");
        messageSB.append(publicacao.getJornalPublicacao().getNomeJornal());
        messageSB.append(" (" + sdf.format(publicacao.getDtPublicacao()) + ")");

        // Texto do ActionURL
        final String actionUrl = "/application/materia";

        // Retornando os dados
        return new String[] {assunto, messageSB.toString(), actionUrl};
    }

    /**
     * Gera dois objetos (retorn em array) do tipo: 'MateriaPublicacao' e 'PautaPublicacao') informando que o limite
     * de ocorrências do termo informado foi alçado,, isso só acontece se o termo estiver dentro do BlackList
     * @param nomePesquisaCliente
     * @param jornal
     * @param publicacaoJornal
     * @param dbConnection
     * @return
     */
    static public MateriaPublicacao GerarDadosInformandoTermoBlacklist(NomePesquisaCliente nomePesquisaCliente, Jornal jornal, PublicacaoJornal publicacaoJornal, DbConnection dbConnection) {
        // Obtendo o e-mail do comercial
        final String comercialEmail = LonoConfigDB.GetConfig(LonoConfigDB_Codes.LONO_COMERCIAL_EMAIL, dbConnection).getValorPrimario();
        if ( comercialEmail == null ) return null;

        // Obtendo o texto base da materia
        String textMateria = LonoConfigDB.GetConfig(LonoConfigDB_Codes.TEMPLATE_ALERT_3, dbConnection).getValorPrimario();
        if ( textMateria == null ) return null;

        // Modificando os dados o texto para informar os parâmetros necessários
        textMateria = textMateria.replaceAll("<termo>", nomePesquisaCliente.getNomePesquisa().toUpperCase().trim());
        textMateria = textMateria.replaceAll("<jornal>", jornal.getSiglaJornal().toUpperCase().trim());
        textMateria = textMateria.replaceAll("<email_comercial>", comercialEmail);

        // Retornando os dados
        return GerarMateriaNotificacaoLono(jornal, nomePesquisaCliente, "Alerta Lono: Termo na Blacklist", textMateria);
    }

    static public MateriaPublicacao GerarMateriaNotificacaoLono(Jornal jornal, NomePesquisaCliente nomePesquisaCliente, String titulo, String textMateria) {
        // Criando a MateriaPublicacao
        MateriaPublicacao materiaPublicacao = new MateriaPublicacao();
        materiaPublicacao.setIdMateria(0);
        materiaPublicacao.setIdNomePesquisa(nomePesquisaCliente.getIdNomePesquisa());
        materiaPublicacao.setIdPublicacao(null);
        materiaPublicacao.setLinhaInicialMateria(1);
        materiaPublicacao.setLinhaFinalMateria(1);
        materiaPublicacao.setIdJornal(jornal.getIdJornal());
        materiaPublicacao.setTituloMateria(titulo); //"Alerta Lono: Termo na Blacklist"
        materiaPublicacao.setPagina(1);
        materiaPublicacao.setLinhaCliente(1);
        materiaPublicacao.setCorteLono(true);
        materiaPublicacao.setDatCad(new Date());
        materiaPublicacao.setUsuCad(99);
        materiaPublicacao.setPreMateria("");
        materiaPublicacao.setIdCliente(nomePesquisaCliente.getIdCliente());
        materiaPublicacao.setPesquisaProximidade(false);
        materiaPublicacao.setMateriaHash("");
        materiaPublicacao.setMateria(textMateria);
        materiaPublicacao.setSitCad("A");
        materiaPublicacao.setSiglaJornal(jornal.getSiglaJornal());

        // Retornando os dados
        return materiaPublicacao;
    }

    private static int ObterIdPublicacaoJornal(Date dtPublicacao, int idJornal, DbConnection dbconn) throws SQLException {

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String dtPublicacaoStr = sdf.format(dtPublicacao);

        String sqlcmd = "SELECT id_publicacao " +
                "FROM publicacao_jornal " +
                "WHERE " +
                "dt_publicacao = '" + dtPublicacaoStr + "'" +
                "AND id_jornal = " + idJornal +
                "AND sit_cad = 'A'";

        // Obtendo o ID da publicacao
        Statement stm = dbconn.obterStatement();
        ResultSet result = dbconn.abrirConsultaSql(stm, sqlcmd);
        if ( result.next() ) {
            final int idPublicacao = result.getInt("id_publicacao");
            result.close();
            stm.close();
            return idPublicacao;
        } else {
            result.close();
            stm.close();
            sqlcmd = "INSERT INTO publicacao_jornal (id_jornal, arq_publicacao, total_pagina, dt_publicacao, dt_divulgacao, edicao_publicacao, dat_cad, sit_cad, usu_cad, materia_liberada) " +
                    "VALUES (" +
                    "" + idJornal + ", " +
                    "'nofile.pdf'," +
                    "0, " +
                    "'" + dtPublicacaoStr + "', " +
                    "'" + dtPublicacaoStr + "', " +
                    "0, " +
                    "NOW(), " +
                    "'F', " +
                    "99, " +
                    "TRUE" +
                    ")";
            int idPublicacao = dbconn.executeSqlLID(sqlcmd);

            return idPublicacao;
        }
    }

    // ------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------
    static private String obterTermoFuzzyLocalizado(final String explain, final String texto, final String field)
    {
        // Obtendo grupos de textos em cada spenOr
        String explainbuff = explain.replaceAll("Explain:weight", "");
        String[] splittedExplain = explainbuff.split("spanOr");
        String[] termoList = new String[splittedExplain.length - 1];
        if ( splittedExplain.length == 1 ) return "";

        for ( int idx = 0, idx1 = 0; idx < splittedExplain.length; idx++ )
        {
            if ( idx == 0 ) continue;
            if ( termoList[idx1] == null ) termoList[idx1] = "";

            splittedExplain[idx] = splittedExplain[idx].replaceAll("\\^", "|").replaceAll("[,:(\\[]", "").trim();
            if ( splittedExplain[idx].length() <= 0 ) continue;

            final String[] tmpGroupTerms = splittedExplain[idx].split(field);
            for (String tmpGroupTerm : tmpGroupTerms) {
                final String tmpGTerm = tmpGroupTerm.trim();
                if ( tmpGTerm.length() <= 0 ) continue;
                int endPos = tmpGTerm.indexOf("|");
                if ( endPos < 0 ) termoList[idx1] += tmpGTerm.trim() + " ";
                else termoList[idx1] += tmpGTerm.substring(0, endPos) + " ";
            }

            idx1++;
        }

        // Checando qual a cobinacao de termos que gera a frase da ocorrencia
        String finalTermPhrase = "";
        for ( String tmpTermo: termoList )
        {
            String possibleFinalPhrase = finalTermPhrase.trim();
            String[] sptTermo = tmpTermo.trim().split(" ");
            for ( String termo: sptTermo )
            {
                String tmpPhrase = finalTermPhrase;
                if ( tmpPhrase.length() > 0 ) tmpPhrase += " " + termo.trim();
                else tmpPhrase += termo;

                // Checando se o termo existe dentro da string
                if ( texto.contains(tmpPhrase) == true && tmpPhrase.length() > possibleFinalPhrase.length() )
                {
                    possibleFinalPhrase = tmpPhrase.trim();
                }
            }

            finalTermPhrase = possibleFinalPhrase.trim();
        }

        return finalTermPhrase;
    }

    static private boolean verificarDocumentIdInResultArray(final int doc_id, Object[][] arrayList)
    {
        for ( final Object[] array: arrayList )
        {
            if ( (int)array[0] == doc_id ) return true;
        }

        return false;
    }

    public static boolean isCPF(String vl) {
        if ( vl == null  )
            return false;

        String CPF = vl.replaceAll("[^\\d]", "").trim();

        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") ||
                CPF.equals("11111111111") ||
                CPF.equals("22222222222") || CPF.equals("33333333333") ||
                CPF.equals("44444444444") || CPF.equals("55555555555") ||
                CPF.equals("66666666666") || CPF.equals("77777777777") ||
                CPF.equals("88888888888") || CPF.equals("99999999999") ||
                (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
                // converte o i-esimo caractere do CPF em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
                num = (int)(CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else dig11 = (char)(r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

    public static boolean isCNPJ(String vl) {
        if ( vl == null )
            return false;

        String CNPJ = vl.replaceAll("[^\\d]", "").trim();

        // considera-se erro CNPJ's formados por uma sequencia de numeros iguais
        if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") ||
                CNPJ.equals("22222222222222") || CNPJ.equals("33333333333333") ||
                CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555") ||
                CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") ||
                CNPJ.equals("88888888888888") || CNPJ.equals("99999999999999") ||
                (CNPJ.length() != 14))
            return(false);

        char dig13, dig14;
        int sm, i, r, num, peso;

// "try" - protege o código para eventuais erros de conversao de tipo (int)
        try {
// Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i=11; i>=0; i--) {
// converte o i-ésimo caractere do CNPJ em um número:
// por exemplo, transforma o caractere '0' no inteiro 0
// (48 eh a posição de '0' na tabela ASCII)
                num = (int)(CNPJ.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else dig13 = (char)((11-r) + 48);

// Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i=12; i>=0; i--) {
                num = (int)(CNPJ.charAt(i)- 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else dig14 = (char)((11-r) + 48);

// Verifica se os dígitos calculados conferem com os dígitos informados.
            if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13)))
                return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }

    private static Query createSpanNearQuery(String text, String fieldName, float porcentual) {
        final String[] splitted_string = text.replaceAll("\\s+", " ").trim().split(" ");
        Query query;
        if ( splitted_string.length == 1 ) {
            // Montando o FuzzyQuery
            int maxFuzzyEdit = Math.round( ((splitted_string[0].length() / 100) * porcentual) );
            if ( maxFuzzyEdit == 0 ) maxFuzzyEdit = 1;
            query = new FuzzyQuery(new Term(fieldName, splitted_string[0]), maxFuzzyEdit);
        } else {
            // Montando o SpanNearQuery
            final SpanQuery[] clauses = new SpanQuery[ splitted_string.length ];
            for ( int idx = 0; idx < splitted_string.length; idx++ )
            {
                int maxFuzzyEdit = Math.round( ((splitted_string[idx].length() / 100) * porcentual) );
                if ( maxFuzzyEdit == 0 ) maxFuzzyEdit = 1;
                FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(fieldName, splitted_string[idx]), maxFuzzyEdit);
                clauses[idx] = new SpanMultiTermQueryWrapper( fuzzyQuery );
            }
            query = new SpanNearQuery(clauses, 0, true);
        }

        return query;
    }

    private static String obterVariaJuridicaJornal(Jornal jornal) {
        if ( jornal.getSiglaJornal().startsWith("TRF") )
            return "Criminal";
        else if ( jornal.getSiglaJornal().startsWith("TRT") )
            return "Trabalhista";
        else
            return "Outros";
    }
}
