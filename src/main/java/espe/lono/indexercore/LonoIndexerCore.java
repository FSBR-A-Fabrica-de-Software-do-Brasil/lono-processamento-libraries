package espe.lono.indexercore;

import espe.lono.db.connections.*;
import espe.lono.db.exceptions.LonoIndexerException;
import espe.lono.db.models.*;
import espe.lono.db.utils.DBUtils;
import espe.lono.indexercore.log.Logger;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import espe.lono.db.Fachada;
import espe.lono.indexercore.data.LonoIndexData;
import espe.lono.indexercore.engine.Indexacao;
import espe.lono.indexercore.util.*;
import espe.lono.indexercore.engine.Marcacoes;
import espe.lono.indexercore.exceptions.*;
import java.io.File;
import java.nio.file.Paths;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author ESPE
 */
public class LonoIndexerCore
{
    private DbConnection dbconn = null;
    final protected Object mutexObject;

    public String CompactaPublicacao(String outputPathame, PublicacaoJornal publicacao, int idPublicacaoAux, boolean apagarArquivosPublicacao) {
        Fachada fachada = new Fachada();
        try {
            Logger.debug("Compactando publicacação '" + publicacao.getIdPublicacao() + "'...");
            final String fullOutputFilename = this.CompactarArquivosPublicacao(fachada, publicacao.getIdJornal(), idPublicacaoAux, outputPathame, apagarArquivosPublicacao, dbconn);
            fachada.alterarSituacaoPublicacao(idPublicacaoAux, PublicacaoJornal.Status.SIT_ARQ_ARMAZENADO, dbconn);
            return fullOutputFilename;
        } catch (Exception exception) {
            Logger.fatal("Erro compactando a edição -> " + exception.getMessage());
            return null;
        }
    }
    /**
     * Construtor da classe,
     * @param lonoDbConn Conexao com o banco principal do LONO
     * @param mutexObj Mutex de controle da conexao com o banco (Nota: Foi implementando pensando em execucao paralela)
     */
    public LonoIndexerCore(DbConnection lonoDbConn, Object mutexObj)
    {
        // Definindo os dados informados para a conexao com o banco
        this.dbconn = lonoDbConn;
        this.mutexObject = mutexObj;
    }

    /**
     * Este algoritmo ira processar por completo, todas as publicações...
     * @return Classe contendo os dados referentes a indexacao (null em erro fatal)
     */
    public LonoIndexData ExecutarIndexacao()
    {
        // Note: no momento, ese algoritimo so ira ser executado uma vez...
        PublicacaoJornal[] publicacoes_lista;
        Fachada fachada = new Fachada();
        
        try
        {
            // Mutex: Garantia que apenas UM processo esta cosultando o
            //        banco de dados no momento, e obtendo a publicacao
            //        a ser processada
            synchronized( this.mutexObject )
            {
                // Otendo a lista de publicacoes que estao aguardando a serem
                // processadas... (limite de UM publicacao)
                publicacoes_lista = fachada.listarPublicacoesPorSituacao(PublicacaoJornal.Status.SIT_ARQ_MOV_AGUARDANDO_PROCESSAMENTO, dbconn, 1);
                if ( publicacoes_lista.length > 0 )
                {
                    // Modificando o status para 'Em Processamento'
                    fachada.alterarSituacaoPublicacao(publicacoes_lista[0].getIdPublicacao(), PublicacaoJornal.Status.SIT_ARQ_PROCESSANDO, dbconn);
                }
            } //synchronized( this.mutexObject )

            // Checando se houve publicacao na consulta
            if ( publicacoes_lista.length <= 0 )
            {
                // Não há dados a serem processados... Retornando NULL
                return null;
            }

            final PublicacaoJornal publicacao = publicacoes_lista[0];
            final int idPublicacaoAux = publicacao.getIdPublicacao();
            final String pubName = publicacao.getArqPublicacao();
            LonoIndexData indexationInfo = new LonoIndexData(publicacao);

            // Antes de iniciar, de fato, o processamento, deve ser
            // certificado que os arquivos das edições anteriores deste
            // jornal já foram removidos...
            // Nota: Apenas se o parameto de compactacao estiver ATIVA
//            if ( LonoIndexerConfigs.INDEXER_COMPACTAR_ANTERIORES ) {
//                // Compactando as edições antigas
//                Logger.debug("Compactando as edições antigas do jornal '" + publicacao.getJornalPublicacao().getSiglaJornal() + "'.");
//                this.RemoverArquivosEdicaoAnteriores(fachada, publicacao.getIdJornal(), idPublicacaoAux, dbconn);
//            }
//            else {
//                // Apenas marcando-a como 'movida'
//                fachada.alterarSituacaoPublicacao(idPublicacaoAux, PublicacaoJornal.Status.SIT_ARQ_ARMAZENADO, dbconn);
//            }

            // Iniciando o processamento...
            Logger.info("Iniciando processamento da publicacao. " + idPublicacaoAux + " - " + pubName);
            try
            {
                // Checando se e necessario processar/indexer essa publicacao
                // Pode ser um reprocessamento geral, logo, os dados anteriores
                // podem ainda estar pronto para ser processados
                final String caminhoDirPublicacaoIndiceMarcacao = publicacao.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS) + "/indice";
                final String caminhoDirPublicacaoIndicePesquisa = publicacao.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS) + "/indice_pesquisa";
                final File pubIndiceWriteLockFile = new File(caminhoDirPublicacaoIndiceMarcacao + "/write.lock");
                final File pubIndicePesquisaWriteLockFile = new File(caminhoDirPublicacaoIndicePesquisa + "/write.lock");
                if ( pubIndiceWriteLockFile.exists() == false || pubIndicePesquisaWriteLockFile.exists() == false )
                {
                    // Processa/indexa a publicacao (converte, indexa e pesquisa)
                    this.ProcessarPublicacao(fachada, publicacao, indexationInfo);
                    Logger.debug("Publicacao Indexada: "  + idPublicacaoAux + " - " + pubName + "");
                }
                else 
                {
                    indexationInfo.setLuceneDirs(caminhoDirPublicacaoIndiceMarcacao, caminhoDirPublicacaoIndicePesquisa);
                    indexationInfo.setMarcacaoDbConnection(DBUtils.startDbMarcacaoConnection(publicacao.getIdPublicacao(), false));
                    indexationInfo.DocumentoIndexado = true;
                    Logger.debug("A publicacao foi indexada: "  + idPublicacaoAux + " - " + pubName + "");
                }
            }
            catch ( Exception ex )
            {
                // Realizando ROLLBACK e desativando a transacao
                dbconn.finalizarTransaction_ROLLBACK();

                // Erro processando arquivo... marcando-a como 'Error'
                Logger.fatal(ex.getClass() + " <> " + ex.getMessage());
                Logger.fatal("Nao foi possivel processar a publicacao: "  + idPublicacaoAux + " - " + pubName);
                fachada.alterarSituacaoPublicacao(publicacao.getIdPublicacao(), PublicacaoJornal.Status.SIT_ERROR, dbconn);
                indexationInfo.DocumentoIndexado = false;
            }

            // Removendo diretorio de indexacao (Lucene)
            //Util.limparDiretorio(caminhoDirPublicacaoIndiceMarcacao);
            //Util.limparDiretorio(caminhoDirPublicacaoIndicePesquisa);
            return indexationInfo;
        }
        catch ( SQLException ex )
        {
            // Error fatal, em outros pontos, ainda e aceitavel algum tipo de
            // erro, mas não, de jeito algum, neste ponto... Pois indica
            // um problema serio com a conexao com o Bando de Dados...
            Logger.fatal(this.getClass().getName() + ":" + ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Obtem 'Thread' pronta para iniciar a conversao (parela) do PDF em HTML
     * paginadas (usado no corte manuel das publicacoes)
     * @param arqPDF
     * @param destiny_path
     * @param priority
     * @return
     */
    private Thread Thread_ConversaoPDFparaHTML_CortarPaginas(final String arqPDF, final String destiny_path, int priority)
    {
        final Thread conversorThread = new Thread(() -> {
            try
            { Util.converterPDFparaHTML_CortarPaginas(arqPDF, destiny_path); }
            catch ( IOException | InterruptedException ex )
            { Logger.debug("Erro Convertendo PDF", ex); }
        });

        conversorThread.setPriority(priority);
        return conversorThread;
    }


    /**
     * Algoritmo de corte para as publicações
     * @param fachada Classe Fachada, com os chamadas relacionados aos bancos
     * @param publicacao Objeto PublicacaoJornal, alimentado com os dados da publicação
     * @param indexationInfo Objeto com os dados da indexação do Lucene
     * @param pubPDFFname Arquivo PDF a ser processado (caminho completo)
     * @param caminhoDirPublicacaoIndiceMarcacao Pasta de destino dos indices de marcacao
     * @param caminhoDirPublicacaoIndicePesquisa Pasta de destino dos indices de pesquisa
     * @throws SQLException Excessões relacionadas ao SQL
     * @throws IOException Excessões relacionadas ao I.O
     * @throws InterruptedException Excessões relacionadas a Thread
     * @throws Exception Excessões genericas
     */
    public void ProcessarPublicacao(Fachada fachada, final PublicacaoJornal publicacao, LonoIndexData indexationInfo, String pubPDFFname, String caminhoDirPublicacaoIndiceMarcacao, String caminhoDirPublicacaoIndicePesquisa, String caminhoDirPublicacao) throws SQLException, IOException, InterruptedException, Exception
    {
        // Definindo/Montando os dados referentes a arquivos/pastas
        final String pdfSingleName = publicacao.getArqPublicacao();
        caminhoDirPublicacao = (caminhoDirPublicacao == null) ? publicacao.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS) : caminhoDirPublicacao;
        pubPDFFname = (pubPDFFname == null) ? caminhoDirPublicacao + pdfSingleName : pubPDFFname;
        final Thread conversorThread = this.Thread_ConversaoPDFparaHTML_CortarPaginas(pubPDFFname, caminhoDirPublicacao + "/html", Thread.MIN_PRIORITY);
        Directory luceneDirMarcacao = null;
        Directory luceneDirPesquisa = null;
        DbConnectionMarcacao marcacaoDb = null;
        caminhoDirPublicacaoIndiceMarcacao = (caminhoDirPublicacaoIndiceMarcacao == null) ? caminhoDirPublicacao + "/indice" : caminhoDirPublicacaoIndiceMarcacao;
        caminhoDirPublicacaoIndicePesquisa = (caminhoDirPublicacaoIndicePesquisa == null) ? caminhoDirPublicacao + "/indice_pesquisa" : caminhoDirPublicacaoIndicePesquisa;

        // Checando se existe o arquivo de publicacao
        if ( Util.arquivoExiste(pubPDFFname) == false )
        {
            throw new IOException("Arquivo pdf nao encontrado. (" + pubPDFFname + ")");
        }

        try
        {
            // Loop de controle para o gerenciamento das marcacoes
            // Nota: NÃO PODE HAVER erro, se oorrer, ira disparar novamente mais uma vez
            for ( int i = 0; i <= 1; i++ ) {
                /**
                 * Nota: A conversão esta sendo feita nesse trecho logo abaixo...
                 *        Isso é uma modificação feita no algoritmo original...
                 *        Onde eu removi essa etapa do algoritmo q move o PDF
                 *        e adicionei aqui, na seção de indexação/pesquisa (e conversão)
                 */
                Util.converterPDFparaHTML( pubPDFFname ); // Converte o PDF em HTML
                Util.normalizeHTMLFile(pubPDFFname + ".html"); // Converte os HTMLEntity

                // Thread da conv.PDF->HTML  por pag.
                if ( i == 0 && indexationInfo.ProcessarArquivosCorte )
                    conversorThread.start(); // executando thread

                /**
                 * Nota: A partir deste ponto, o código segue o mesmo do original, mas,
                 *       com mais comentários...
                 * Nota2: Houve algumas pequenas alterações, nada que descaracterize o
                 *       código original
                 */

                // Caregando patterns de corte/replaces...
                Logger.debug("Carregando marcacoes para a publicacao: " + pdfSingleName);
                final TipoPadraoJornal[] tiposPadraoPublicacao = fachada.listarTiposPadraoPublicacao(publicacao, dbconn);
                final PadraoJornal padraoJornal = new PadraoJornal();
                padraoJornal.setTiposPadraoJornal(tiposPadraoPublicacao);
                padraoJornal.carregarReplacesClassificacoes();
                publicacao.setPadraoJornalPublicacao(padraoJornal);

                // Garantindo que as pastas estão limpas...
                Util.limparDiretorio(caminhoDirPublicacaoIndiceMarcacao);
                Util.limparDiretorio(caminhoDirPublicacaoIndicePesquisa);

                // Inicializando Bando de Dados para as marcacoes
                marcacaoDb = indexationInfo.getDbConnMarcacao();
                if ( marcacaoDb  == null) marcacaoDb = DBUtils.startDbMarcacaoConnection(publicacao.getIdPublicacao());
                marcacaoDb.iniciarTransaction();

                // Abrindo diretoriosp para o uso no Lucene
                luceneDirMarcacao = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndiceMarcacao));
                luceneDirPesquisa = FSDirectory.open(Paths.get(caminhoDirPublicacaoIndicePesquisa));

                // Indexando arquivo e definindo o status para 'Indexado'
                Logger.debug("Indexando Publicacao: " + pdfSingleName);
                final ClassificacaoReplaceTipo[] classificacaoReplaceTipo = padraoJornal.getClassificacaoReplaceTipo();
                Indexacao.IndexarArquivosDiretorio(
                        luceneDirMarcacao,
                        luceneDirPesquisa,
                        caminhoDirPublicacao,
                        classificacaoReplaceTipo,
                        publicacao,
                        pdfSingleName,
                        this.dbconn
                );

                Util.removerArquivo(pubPDFFname + ".html"); // Nao e mais necessario

                // Seção de tratamento/processamento de marcações...
                // Nota: caso ocorra algum erro, o processamento da publcação iria
                //       continuar, mas com nenhum padrao definindo... todos os
                //       cortes serão marcado como 'invalidos/errados' pelo engine.
                try
                {
                    // Processando marcacoes do Jornal
                    Logger.debug("Processando pre-marcacoes para a publicacao: " + pdfSingleName);
                    Marcacoes.processarPreMarcacoes(luceneDirMarcacao, publicacao, marcacaoDb, dbconn);

                    // Processando as marcacoes
                    Logger.debug("Processando marcacoes para a publicacao: " + pdfSingleName);
                    Marcacoes.processarMarcacoes(luceneDirMarcacao, publicacao, marcacaoDb, dbconn);
                    marcacaoDb.finalizarTransaction_COMMIT();

                    // Remover trechos indesejados. Ex: Cabecalho e Rodapé
                    Logger.debug("Removendo trechos indesejados da publicação: " + pdfSingleName);
                    Marcacoes.removerLinhasIndesejadas(luceneDirMarcacao, publicacao, marcacaoDb);

                    // Processando marcaoes pos-exclusão
                    Logger.debug("Processando marcacoes pós-exclusão da publicação: " + pdfSingleName);
                    Marcacoes.processarMarcacoesPosExclusoes(luceneDirMarcacao, publicacao, marcacaoDb, dbconn);

                    // Obtendo e definindo pautas...
                    Logger.debug("Processando pautas da publicação: " + pdfSingleName);
                    Marcacoes.processarPautas(luceneDirMarcacao, publicacao, marcacaoDb);

                    // Removendo grupos de linhas da publicação
                    // Nota: As marcações para esse recurso DEVEM ser sempre em pares
                    //       não sera tolerada divergencia de quantidades das marcações
                    //       iniciais e finais...
                    // Nota2: Este algoritimo se baseia apenas nas querys Lucene...
                    //        não suporta o processamento de regex junto as querys...
                    // Nota3: Desativado, o risco de gerar proplemas não compensa
                    //            Marcacoes.processarRemocaoPaginas(luceneDirMarcacao, publicacao, marcacaoDb);

                    // Obtendo e definindo MATERIAS os quais, comecam/terminem no meio da linha
                    // Nota: Algoritimo gerado com base nos problemas de corte do DJPB,
                    //       existem matérias que começam no meio da linha e
                    //       terminam também no meio da linha.
                    // Nota2: Desativado pois ainda esta em implementação.
                    //Marcacoes.processarMateriasComplexas(luceneDirPesquisa, publicacao, marcacaoDb);

                    // Realizando commit (salvando efetivamente as marcações)
                    marcacaoDb.finalizarTransaction_COMMIT();
                }
                catch ( MarcacoesException ex ) {
                    // Rollback
                    marcacaoDb.finalizarTransaction_ROLLBACK();
                    if ( i == 0 ) {
                        Logger.debug("Erro com alguma das marcacao principais... Reprocessando a publicacao de ID:" + publicacao.getIdPublicacao());
                        continue;
                    }
                    else {
                        Logger.debug("Desativando padroes para a publicacao de ID:" + publicacao.getIdPublicacao());
                        indexationInfo.MarcacoesProcessadas = false;
                    }
                }
                catch ( IOException ex )
                {
                    marcacaoDb.finalizarTransaction_ROLLBACK();
                    Logger.fatal("Erro processando as marcacoes da publicacao de ID:" + publicacao.getIdPublicacao());
                    throw ex;
                }

                // All OK, parando Loop e continuando a execucao
                break;
            } // for

            // Definindo que as marcacoes foram processadas
            indexationInfo.MarcacoesProcessadas = true;
        }
        catch ( Exception ex )
        {
            // Error... Finalizando classes abertas e lançando excessao
            if ( luceneDirMarcacao != null ) luceneDirMarcacao.close();
            if ( luceneDirPesquisa != null ) luceneDirPesquisa.close();
            if ( marcacaoDb != null ) marcacaoDb.fecharConexao();
            throw ex;
        }

        // Removedo todos os dados/arquivos nao mais necessarios..
        // Fializando conexao com o banco de dados de marcacoes
        if ( marcacaoDb != null ) marcacaoDb.fecharConexao();

        // Fechando Directory (Lucene)
        if ( luceneDirMarcacao != null ) luceneDirMarcacao.close();
        if ( luceneDirPesquisa != null ) luceneDirPesquisa.close();

        // Armazenando os dados na classe de informacoes sobre o indexamento
        indexationInfo.setLuceneDirs(caminhoDirPublicacaoIndiceMarcacao, caminhoDirPublicacaoIndicePesquisa);
        indexationInfo.setMarcacaoDbConnection(marcacaoDb);
        indexationInfo.DocumentoIndexado = true;

        // Finalizando..
        // Nota: Verifca se a thread de conversao foi completada
        // Nota2: Apenas se foi incializada (por padrao, e inicializada sim)
        if ( conversorThread != null && conversorThread.isAlive() )
        {
            Logger.debug("Aguardando o fim da thread de conversao(PDF -> HTML)");
            conversorThread.join();
        }
    }

    /**
     *
     * Algoritmo de corte para as publicações
     * @param fachada Classe Fachada, com os chamadas relacionados aos bancos
     * @param publicacao Objeto PublicacaoJornal, alimentado com os dados da publicação
     * @param indexationInfo Objeto com os dados da indexação do Lucene
     * @throws SQLException Excessões relacionadas ao SQL
     * @throws IOException Excessões relacionadas ao I.O
     * @throws InterruptedException Excessões relacionadas a Thread
     */
    public void ProcessarPublicacao(Fachada fachada, final PublicacaoJornal publicacao, LonoIndexData indexationInfo) throws SQLException, IOException, InterruptedException, Exception
    {
        // Definindo/Montando os dados referentes a arquivos/pastas
        final String pdfSingleName = publicacao.getArqPublicacao();
        final String caminhoDirPublicacao = publicacao.getCaminhoDirPublicacao(LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS);
        final String pubPDFFname = caminhoDirPublicacao + pdfSingleName;
        final Thread conversorThread = this.Thread_ConversaoPDFparaHTML_CortarPaginas(pubPDFFname, caminhoDirPublicacao + "/html", Thread.MIN_PRIORITY);
        Directory luceneDirMarcacao = null;
        Directory luceneDirPesquisa = null;
        DbConnectionMarcacao marcacaoDb = null;
        final String caminhoDirPublicacaoIndiceMarcacao = caminhoDirPublicacao + "/indice";
        final String caminhoDirPublicacaoIndicePesquisa = caminhoDirPublicacao + "/indice_pesquisa";

        // Iniciando o processamento da publicacao
        fachada.atualizarDataInicialProcessamentoPublicacao(publicacao.getIdPublicacao(), this.dbconn);

        // Processando a publicacao
        this.ProcessarPublicacao(fachada, publicacao, indexationInfo, pubPDFFname, null, null,null);

        // Modificando o status da publicacao no SGBD
        fachada.alterarSituacaoPublicacao(publicacao.getIdPublicacao(), PublicacaoJornal.Status.SIT_ARQ_PROCESSADO, dbconn);
        fachada.atualizarDataFinalProcessamentoPublicacao(publicacao.getIdPublicacao(), this.dbconn);

    }

    /**
     * Remove arquivos de indexação dos processamentos anteriores
     * @param facahada Classe Fachada, contem todas as principais métodos referentes ao banco
     * @param idJornal ID do Jornal
     * @param idPublicacaoToCompact ID da Publicação a ser compactada
     * @param dbconn Conexão com o banco principal do Lono
     * @throws SQLException Excessões relacionadas ao SQL
     */
    private String CompactarArquivosPublicacao(Fachada facahada, int idJornal, int idPublicacaoToCompact, String outputFolderName, boolean deleteOldFiles, DbConnection dbconn) throws SQLException, LonoIndexerException {
        String outputCompressFName = null;
        final String sqlcmd = "SELECT id_publicacao, dt_publicacao, id_jornal, arq_publicacao, sit_cad " +
                "FROM publicacao_jornal " +
                "WHERE id_jornal = '" + idJornal + "' " +
                "   AND id_publicacao = '" + idPublicacaoToCompact + "' " +
                "   AND sit_cad NOT IN ('M') ";

        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        DbConnectionMarcacao marcacaoDb = null;
        while ( resultado.next() )
        {
            final int idPublicacao = resultado.getInt("id_publicacao");
            final String arqPublicacao = resultado.getString("arq_publicacao");

            // Gerando o nome da pasta BASE da publicacao
            String[] splitted_dtPublicacao = resultado.getString("dt_publicacao").split("-");
            String diretorioBaseArquivo = LonoIndexerConfigs.INDEXER_DIRETORIO_DOCUMENTOS + "/";
            diretorioBaseArquivo += splitted_dtPublicacao[0] + "/"; // ano
            diretorioBaseArquivo += splitted_dtPublicacao[1] + "/"; // mes
            diretorioBaseArquivo += splitted_dtPublicacao[2] + "/"; // dia
            diretorioBaseArquivo += resultado.getString("id_jornal") + "/"; // Jornal
            diretorioBaseArquivo += Integer.toString( idPublicacao ) + "/"; // Pub

            // Checando se deve ignorar o arquivo
            final File pdfFilename = new File(diretorioBaseArquivo + arqPublicacao);
            if ( arqPublicacao.equalsIgnoreCase("nofile.pdf") || !pdfFilename.exists() ) { // nofile.pdf
                facahada.alterarSituacaoPublicacao(idPublicacao, PublicacaoJornal.Status.SIT_ARQ_ARMAZENADO, dbconn);
                continue;
            }


            LonoIndexerConfigs.INDEXER_LOG4_LOGGER.debug("Compactando publicacao -> " + idPublicacao);

            // Abrindo conexão com o banco demarcacao
            try { marcacaoDb = DBUtils.startDbMarcacaoConnection( idPublicacao, false ); }
            catch (ClassNotFoundException e) { marcacaoDb = null; }

            // Definindo a lista de diretorios de processamento
            final String[] pastasProcEdicao = new String[]{
                (diretorioBaseArquivo + "indice"),
                (diretorioBaseArquivo + "indice_pesquisa"),
                (diretorioBaseArquivo + "marcacao.csv")
            };

            // Gerando (Exportando) banco de marcações
            if ( marcacaoDb != null )
                marcacaoDb.exportTable(pastasProcEdicao[2]);

            // Nome do arquivo sera o HASH do pdf
            final String pdfHashValue = Util.GenerateHashFromFile(diretorioBaseArquivo + resultado.getString("arq_publicacao"));
            final Jornal jornal = facahada.localizarJornalID(resultado.getInt("id_jornal"), dbconn);

            // Comprimindo os dados de pesquisa (lucene) desta edicao
            outputCompressFName = outputFolderName + File.pathSeparator;
            outputCompressFName += jornal.getSiglaJornal().toLowerCase() + "_" + resultado.getString("id_publicacao") + "_";
            outputCompressFName += pdfHashValue + ".zip";
            Util.compactarDiretorios(pastasProcEdicao, outputCompressFName);
            
            // Removendo dados desta ANTIGA edicao
            if ( deleteOldFiles ) {
                Util.limparDiretorio(pastasProcEdicao[0]);
                Util.limparDiretorio(pastasProcEdicao[1]);
                Util.limparDiretorio(pastasProcEdicao[2]);
                Util.limparDiretorio(diretorioBaseArquivo + "html");

                // Apagando por completo todas as pastas da publicação anterior

                // Modificando o status desta publicacao para 'Movida e Limpa'
                facahada.alterarSituacaoPublicacao(idPublicacao, PublicacaoJornal.Status.SIT_ARQ_ARMAZENADO, dbconn);
            }
            
            // Removendo dados de marcacoes antigos...
            // Nota: Apenas se nao estiver salvando os ZIP file
            if ( marcacaoDb != null )
            {
                marcacaoDb.destruirTabela();
                marcacaoDb.fecharConexao();
            }
            else
            {
                // Removendo o arquivo PDF, pois, nessa configuração, 
                // nao tem precisa dele para reprocessar
                Util.removerArquivo(pdfFilename.getAbsolutePath());
            }

            // Fechando o banco de marcação
            if ( marcacaoDb != null)
                marcacaoDb.fecharConexao();
        }
        
        resultado.close();
        stm.close();

        return outputCompressFName;
    }
}
