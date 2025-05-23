package espe.lono.db;

import espe.lono.db.negocios.*;
import espe.lono.db.connections.*;
import espe.lono.db.models.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import espe.lono.db.negocios.Blogs.BlogNegocio;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Luiz Diniz/Petrus Augusto - Espe
 * @since 11/09/2016
 * @version 1.2
 */
public class Fachada {
    private PublicacaoNegocio negPublicacao = new PublicacaoNegocio();
    private JornalNegocio negJornal = new JornalNegocio();
    private ClienteNegocio negCliente = new ClienteNegocio();
    private NotificacaoNegocio notificacaoNegocio = new NotificacaoNegocio();

    final public BlogNegocio blog = new BlogNegocio();

    public Integer[] listarPublicacoesAntigas(int idJornal, int idPublicacaoToIgnore,  DbConnection dbconn) throws SQLException {
        return negPublicacao.listarPublicacoesAntigas(idJornal, idPublicacaoToIgnore, dbconn);
    }

    /**************************************************************************
     * Metodos  ligados ao 'Reprocessamento'
     **************************************************************************/
    /**
     * Obtem a lista de IDs de Publicacao aguardando o reprocessamento
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array com os IDs de publicacoes
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public Integer[] listarPublicacoesAguardandoReprocessamento(DbConnection dbconn) throws SQLException
    {
        return negPublicacao.listarPublicacoesAguardandoReprocessamento(dbconn, 0);
    }

    /**
     * Obtém a publicação mais recente do cliente-jornal
     * @param idJornal -> ID do jornal
     * @param idCliente -> ID do cliente
     * @param dbConn -> Conexão com o banco principal do Lono
     * @return -> PublicacaoJornal: Objeto com os dados da publicacao obtida
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public PublicacaoJornal obterPublicacaoRecenteClienteJornal(int idJornal, int idCliente, int idNomePesquisa, DbConnection dbConn) throws SQLException
    {
        return negPublicacao.obterPublicacaoRecenteClienteJornal(idJornal, idCliente, idNomePesquisa, dbConn);
    }

    /**
     * Obtem a lista de IDs de Publicacao aguardando o reprocessamento
     * @param dbconn -> Conexão com o banco principal do Lono
     * @param limit -> Limite de entradas dentro da array
     * @return -> Array com os IDs de publicacoes
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public Integer[] listarPublicacoesAguardandoReprocessamento(DbConnection dbconn, int limit) throws SQLException
    {
        return negPublicacao.listarPublicacoesAguardandoReprocessamento(dbconn, limit);
    }

    /**
     * Obtem a lista de ReprocessarClientePublicacao com base no ID da publicacao
     * @param id_publicacao -> ID da Publicacao
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array com a lista de ReprocessarClientePublicacao
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public ReprocessarClientePublicacao[] listaReprocessarClientePublicacaoPorPublicacao(int id_publicacao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.listaReprocessarClientePublicacaoPorPublicacao(dbconn, id_publicacao, null);
    }

    public ReprocessarClientePublicacao[] listaReprocessarClientePublicacaoPorPublicacao(int id_publicacao, ReprocessarClientePublicacao.Status status, DbConnection dbconn) throws SQLException
    {
        String situacao = "" + status.toString().charAt(0);
        return negPublicacao.listaReprocessarClientePublicacaoPorPublicacao(dbconn, id_publicacao, situacao);
    }

    /** Altera o status de uma publicação em reprocessamento (no controle de reprocessmaento)
     * @param idReprocessamento -> ID de repocessamento a ser atualizado
     * @param status -> Status a ser definido
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> TRUE em sucesso, FALSE em erro.
     * @throws SQLException -> Excessão de erro com o SQL
     */
    public boolean alterarSituacaoReprocessarClientePublicacao(int idReprocessamento, ReprocessarClientePublicacao.Status status, DbConnection dbconn) throws SQLException
    {
        char situacao = status.toString().charAt(0);
        return negPublicacao.alterarSituacaoPublicacaoReprocessamento(idReprocessamento, situacao, dbconn);
    }

    /**
     * Altera o status de uma array de ids de processamento
     * @param idsReprocessamento -> Array com as IDS de reprocessamento a ser atualizado
     * @param status -> Status a ser definido
     * @param dbconn -> Conexao com o banco principal do Lono
     * @return -> TRUE em sucesso, FALSE em erro
     * @throws SQLException  -> Excessão de erro com o SQL
     */
    public boolean alterarSituacaoReprocessarClientePublicacao(int[] idsReprocessamento, ReprocessarClientePublicacao.Status status, DbConnection dbconn) throws SQLException
    {
        for (int idRep : idsReprocessamento ) {
            boolean resp = alterarSituacaoReprocessarClientePublicacao(idRep, status, dbconn);
            if ( !resp ) return false;
        }

        return true;
    }

    //*************************************************************************
    //* Metodos  ligados a 'Publicações' e 'Marcações'
    //**************************************************************************/

    /**
     * Retorna TRUE ou FALSE informando se o clinte ignourou ou não esse numero de processo!
     * @param idCliente
     * @param processo
     * @param dbconn
     * @return
     * @throws SQLException
     */
    public boolean ignorarNumeroProcesso(int idCliente, String processo, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.ignorarNumeroProcesso(idCliente, processo, dbconn);
    }

    /**
     * Lista UMA publicacao pelo ID
     * @param id_publicacao -> ID da publicação
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> PublicacaoJornal: Objeto com os dados da publicacao obtida
     * @throws SQLException -> Excessões de erro com o SQL
     */
    public PublicacaoJornal listarPublicacoesPorID(int id_publicacao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.listarPublicacoesPorID(id_publicacao, dbconn);
    }

    /**
     * Lista as publicações por Situação (status)
     * @param status -> Situação/Status das publicações a serem filtradas
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array do objeto PublicacaoJornal, com os dados das publicações localizadas
     * @throws SQLException -> Excessões de erro com o SQL
     */
    public PublicacaoJornal[] listarPublicacoesPorSituacao(PublicacaoJornal.Status status, DbConnection dbconn) throws SQLException
    {
        char situacao = status.toString().charAt(0);
        return negPublicacao.listarPublicacoesPorSituacao(situacao, dbconn, 0);
    }

    /**
     * Lista as publicações por Situação (status)
     * @param status -> Situação/Status das publicações a serem filtradas
     * @param dbconn -> Conexão com o banco principal do Lono
     * @param limit -> Limite de publicações a serem retornadas na array
     * @return -> Array do objeto PublicacaoJornal, com os dados das publicações localizadas
     * @throws SQLException -> Excessões de erro com o SQL
     */
    public PublicacaoJornal[] listarPublicacoesPorSituacao(PublicacaoJornal.Status status, DbConnection dbconn, int limit) throws SQLException
    {
        char situacao = status.toString().charAt(0);
        return negPublicacao.listarPublicacoesPorSituacao(situacao, dbconn, limit);
    }

    /**
     * Atualiza a data INICIAL de processamento de uma publicação
     * @param idPublicacao -> ID da publicação a ser atualizada
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> TREUE em sucesso, FALSE em erro.
     * @throws SQLException -> Excessão de erro com o SQL
     */
    public boolean atualizarDataInicialProcessamentoPublicacao(int idPublicacao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.atualizarDataInicialProcessamentoPublicacao(idPublicacao, dbconn);
    }

    /**
     * Atualiza a data FINAL de processamento de uma publicação
     * @param idPublicacao -> ID da publicação a ser atualizada
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> TRUE em sucesso, FALSE em erro.
     * @throws SQLException -> Excessão de erro com o SQL
     */
    public boolean atualizarDataFinalProcessamentoPublicacao(int idPublicacao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.atualizarDataFinalProcessamentoPublicacao(idPublicacao, dbconn);
    }

    /**
     * Altera a situação/status de uma publicacao
     * @param idPublicacao -> ID da publicação a ser atualizado
     * @param status -> Status a ser definido
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> TRUE em sucesso, FALSE em erro.
     * @throws SQLException -> Excessão de erro com o SQL
     */
    public boolean alterarSituacaoPublicacao(int idPublicacao, PublicacaoJornal.Status status, DbConnection dbconn) throws SQLException
    {
        char situacao = status.toString().charAt(0);
        return negPublicacao.alterarSituacaoPublicacao(idPublicacao, situacao, dbconn);
    }

    public boolean removerPublicacao(int idPublicacao, DbConnection dbConnection) throws SQLException {
        return negPublicacao.removerPublicacao(idPublicacao, dbConnection);
    }

    /**
     * Obtém a lista de padrão de uma publicação (fazendo a relação com o jornal)
     * @param pubJornal -> Obtjeto PublicacaoJornal, nele contem os dados da publicação/jornal a ser obtido os padrões
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array do objeto TipoPadraoJornal, nele contem as definições dos padrões
     * @throws SQLException -> Excessão relacionados ao SQL
     */
    public TipoPadraoJornal[] listarTiposPadraoPublicacao(PublicacaoJornal pubJornal, DbConnection dbconn) throws SQLException
    {
        return negJornal.listarTiposPadraoPublicacao(pubJornal, dbconn);
    }

    /**
     * Obtém a lista de padrão de uma publicação (fazendo a relação com o jornal)
     * @param idPadrao ID do padrao a ser obtido
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do objeto TipoPadraoJornal, nele contem as definições dos padrões
     * @throws SQLException Excessão relacionados ao SQL
     */
    public TipoPadraoJornal[] listarTiposPadraoPublicacao(int idPadrao, DbConnection dbconn) throws SQLException
    {
        return this.listarTiposPadraoPublicacao(idPadrao, "A", dbconn);
    }

    /**
     * Obtém a lista de padrão de uma publicação (fazendo a relação com o jornal)
     * @param idPadrao ID do padrao a ser obtido
     * @param SitCadFilter Filtro do status a ser obtido (em branco == todos)
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do objeto TipoPadraoJornal, nele contem as definições dos padrões
     * @throws SQLException Excessão relacionados ao SQL
     */
    public TipoPadraoJornal[] listarTiposPadraoPublicacao(int idPadrao, String SitCadFilter, DbConnection dbconn) throws SQLException
    {
        TipoPadraoJornal[] listTipoPadrao = negJornal.listarTiposPadraoPublicacao(idPadrao, dbconn);
        if ( SitCadFilter != null && SitCadFilter.length() > 0 ) {
            List<TipoPadraoJornal> newListTipoPadrao = new ArrayList();
            for ( TipoPadraoJornal tpj : listTipoPadrao ) {
                if ( tpj.getSitCad().equals(SitCadFilter) )
                    newListTipoPadrao.add(tpj);
            }

            return newListTipoPadrao.toArray(new TipoPadraoJornal[newListTipoPadrao.size()]);
        } else {
            return listTipoPadrao;
        }
    }

    /**
     * Adiciona um novo TipoPadraoJornal
     * @param tipoPadraoJornal Objeto TipoPadraoJornal com os padroes a serem adicionados
     * @param dbconn Conexão com o banco prncipal do Lono
     * @return TRUE == Sucess, FALSE == Erro
     * @throws SQLException Excessão relacionados ao SQL
     */
    public boolean adicionarTipoPadraoJornal(TipoPadraoJornal tipoPadraoJornal, DbConnection dbconn) throws SQLException {
        return negJornal.adicionarTipoPadraoJornal(tipoPadraoJornal, dbconn);
    }

    /**
     * Atualiza um TipoPadraoJornal existente
     * @param tipoPadraoJornal Objeto TipoPadraoJornal com os padroes a serem atualizados
     * @param dbconn Conexão com o banco prncipal do Lono
     * @return TRUE == Sucess, FALSE == Erro
     * @throws SQLException Excessão relacionados ao SQL
     */
    public boolean atualizarTipoPadraoJornal(TipoPadraoJornal tipoPadraoJornal, DbConnection dbconn) throws SQLException {
        if ( tipoPadraoJornal.getIdTipoPadraoJornal() == 0 )
            throw new SQLException("No ID");

        return negJornal.atualizarTipoPadraoJornal(tipoPadraoJornal, dbconn);
    }

    /**
     * Obtém a lista de marcações pelo tipo (código) de padrão
     * @param padroes -> String com a lista (separado por vírgula) dos tipos de padrões
     * @param sqlite -> Conexão com o banco e Marcação (uso interno do Processamento/Engine)
     * @return -> Arrayde inteiros
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public long[][] listarMarcacaoPorTiposPadroes(final String padroes, DbConnectionMarcacao sqlite) throws SQLException
    {
        Long[][] marc_list0 = negPublicacao.listarMarcacaoPorTipoPadraos(padroes, sqlite);
        long[][] marc_list = new long[marc_list0.length][2];
        int idx = 0;
        for ( Long value[]: marc_list0 )
        {
            marc_list[idx][0] = value[0];
            marc_list[idx][1] = value[1];
            idx++;
        }

        return marc_list;
    }

    /**
     * Obten a lista de marcações pelo Tipo de Padrão
     * @param padrao -> ID Tipo Padrão
     * @param sqlite -> Conexão com o banco de marcações (Processamento/Engine)
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array de inteiros com as marcacoes
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public long[] listarMarcacaoPorTipoPadrao(int padrao, DbConnectionMarcacao sqlite, DbConnection dbconn) throws SQLException
    {
        return ArrayUtils.toPrimitive(negPublicacao.listarMarcacaoPorTipoPadrao(padrao, sqlite, dbconn));
    }

    /**
     * Obtém as marcações marcadas como 'Complexa'
     * @param sqlite -> Conexão com o banco de marcacao (Processamento/Engine)
     * @return -> Array de longs com as marcacoes
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public long[][] listarMarcacaoComplexas(DbConnectionMarcacao sqlite) throws SQLException
    {
        Long[][] marc_list0 = negPublicacao.listarMarcacaoComplexas(sqlite);
        long[][] marc_list = new long[marc_list0.length][2];
        int idx = 0;
        for ( Long value[]: marc_list0 )
        {
            marc_list[idx][0] = value[0];
            marc_list[idx][1] = value[1];
            idx++;
        }

        return marc_list;
    }

    /**
     * Remove do banco de marcação, todas as marcações dentro do range de 'num_doc_lucene'
     * @param numdoc_inicial -> NumDocLucene inicial para o range
     * @param numdoc_final -> NumDocLucene final para o range
     * @param sqlite -> Conexão com o bancod e marcações (Processamento/Engine)
     * @return -> TRUE em sucess, FALSE em erro
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public boolean removerMarcacaoPrincipaisPorRangeNumDocLucene(int numdoc_inicial, int numdoc_final, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negPublicacao.removerMarcacaoPrincipaisPorRangeNumDocLucene(numdoc_inicial, numdoc_final, sqlite);
    }

    /**
     * Adiciona uma marcação encontrada/processada no banco de marcações
     * @param marcacaoPub -> Marcação
     * @param sqlite -> Conexão com o banco de marcações (processamento/engine)
     * @return -> TRUE em sucesso, FALSE em erro
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public boolean incluirMarcacaoPublicacao(MarcacaoPublicacao marcacaoPub, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negPublicacao.incluirMarcacaoPublicacao(marcacaoPub, sqlite);
    }

    /**
     * Adiciona um corte de PAUTA dentro do banco principal (e faz a relação com a máteria)
     * @param pautaPub -> Objeto PautaPublicacao, com os dados da pauta a ser adicionada
     * @param id_materia -> ID da matéria a ser ligada com esta pauta
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return  TRUE em sucesse, FALSE em erro
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public boolean incluirCortePautaPublicacao(PautaPublicacao pautaPub, int id_materia, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.incluirCortePautaPublicacao(pautaPub, id_materia, dbconn);
    }

    /**
     * Adiciona um corte de MATÉRIA dentro do banco principal
     * @param materiaPub -> Objeto MateriaPublicacao, com os dados da matéria a ser adicionada
     * @param dbconn -> Conexão com o banci principal do Lono
     * @return -> 1 == Materia adicionada, 0 == Materia já existe
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public int incluirCorteMateriaPublicacao(PautaPublicacao pautaPublicacao, MateriaPublicacao materiaPub, DbConnection dbconn) throws SQLException
    {
        final PublicacaoJornal publicacaoJornal = this.listarPublicacoesPorID(materiaPub.getIdPublicacao(),dbconn);
        final NomePesquisaCliente nomePesquisaCliente = this.listarNomePesquisaPorID(materiaPub.getIdNomePesquisa(), dbconn);

        return negPublicacao.incluirCorteMateriaPublicacao(publicacaoJornal, nomePesquisaCliente, materiaPub, pautaPublicacao, dbconn);
    }

    public boolean verificaColisaoMD5(int idJornal, String md5Hash, DbConnection dbconn) throws SQLException {
        return negPublicacao.verificaColisaoMD5(idJornal, md5Hash, dbconn);
    }

    public boolean execInserirPublicacaoJornal(int idJornal, String fileName, String dtPublicacao, String dtDivulgacao, String edicaoPublicacao, int totalPagina, String fileMd5, DbConnection dbconn) throws SQLException {
        return negPublicacao.execInserirPublicacaoJornal(idJornal, fileName, dtPublicacao, dtDivulgacao, edicaoPublicacao, totalPagina, fileMd5, dbconn);
    }

    public boolean numProcessoIgnorado(MateriaPublicacao materiaPub, DbConnection dbconn) throws SQLException {
        return negPublicacao.numProcessoIgnorado(materiaPub, dbconn);
    }

    /**
     * Atualiza o número da edição da publicação
     * @param id_publicacao -> ID da publicação a ser atualizada
     * @param numEdicao -> Numero da edição a ser definida
     * @param dbconn -> Conexão com o banci principal do Lono
     * @return -> TRUE == Sucesso, FALSE == Erro
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public boolean atualizarEdicaoPublicacao(int id_publicacao, String numEdicao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.atualizarEdicaoPublicacao(id_publicacao, numEdicao, dbconn);
    }

    /**
     * Obtém o numero da edição da publicacao vigente
     * @param sqlite -> Conexão com o banco de marcações do Lono
     * @return -> String (Numero da edição), NULL em caso de erro
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public String obterNumeroEdicaoPublicacao(DbConnectionMarcacao sqlite) throws SQLException
    {
        return negPublicacao.dadosObterNumeroEdicao(sqlite);
    }

    //*************************************************************************
    //* Metodos  ligados a 'Clientes'
    //**************************************************************************/
    /**
     * Obtém o termo de pesquisa pelo o ID do mesmo
     * @param idNomePesquisa -> ID do termo de pesquisa
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> NomePesquisaCliente
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public NomePesquisaCliente listarNomePesquisaPorID(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        return negCliente.listarNomePesquisaPorID(idNomePesquisa, dbconn);
    }

    public NomePesquisaCliente listarNomePesquisaPorIdJornalSituacao(int idNomePesquisa, int idJornal, String sitcad, DbConnection dbconn) throws SQLException
    {
        return negCliente.listarNomePesquisaPorIdJornalSituacao(idNomePesquisa, idJornal, sitcad, dbconn);
    }

    /**
     * Obtém a lista de Numeros OAB a serem pesquisados no Jornal
     * @param idJornal -> ID do Jornal
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array com os NomePesquisaCliente alimentados com os numéros de OABs a serm pesquisados
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public NomePesquisaCliente[] listarNumeroOABJornal(int idJornal, DbConnection dbconn) throws SQLException
    {
        return negCliente.listarNumeroOABJornal(idJornal, dbconn);
    }

    public String obterNomePesquisaConcatenado(int idTermoPai, DbConnection dbconn) throws SQLException
    {
        return negCliente.dadosListarNomePesquisaConcatenado(idTermoPai, dbconn);
    }

    /**
     * Obtém a lista de Termos a serem pesquisados no Jornal
     * @param idJornal -> ID do Jornal
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array com os NomePesquisaCliente alimentados com os numéros de OABs a serm pesquisados
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public NomePesquisaCliente[] listarNomesPesquisaJornal(int idJornal, DbConnection dbconn) throws SQLException
    {
        return negCliente.listarNomesPesquisaJornal(idJornal, dbconn);
    }


    public boolean inserirDadosPesquisa(int idCliente, int idNomePesquisa, int idJornal, int idPublicacao, int qtdLocatedTerms, int qtdPjeProcs, DbConnection dbconn) throws SQLException
    {
        return negCliente.inserirDadosPesquisa(idCliente, idNomePesquisa, idPublicacao, idJornal, qtdLocatedTerms, qtdPjeProcs, dbconn);
    }

    public boolean inserirDadosPesquisa(int idCliente, int idNomePesquisa, int idJornal, int idPublicacao, int qtdLocatedTerms, DbConnection dbconn) throws SQLException
    {
        return negCliente.inserirDadosPesquisa(idCliente, idNomePesquisa, idPublicacao, idJornal, qtdLocatedTerms, 0, dbconn);
    }

    /**
     * Modifica o status de um termo de pesquisa
     * @param idNomePesquisa -> Id do Termo de pesquisa
     * @param dbconn -> Conexão com o banco principal deo Lono
     * @return -> TRUE em sucess, FALSE em erro
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public boolean alterarSituacaoNomePesquisa(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        return negCliente.atualizarStatusNomePesquisa(idNomePesquisa, dbconn);
    }

    /**
     * Modifica a data do ultim blacklist notify enviado
     * @param idNomePesquisa -> ID do termo de pesquisa
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> TrUE em success, FALSE em erro
     * @throws SQLException -> Excessões realacionados ao SQL
     */
    public boolean atualizarBlacklistNotifyDat(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        return negCliente.atualizarBlacklistNotifyDat(idNomePesquisa, dbconn);
    }

    /**
     * Obtém a lista de usuários atrelados a este cliente
     * @param idCliente -> ID do cliente
     * @param dbconn -> Conexão com o banco principal do Lono
     * @return -> Array com os Usuarios
     * @throws SQLException -> Excessões realcionados ao SQL
     */
    public Usuario[] listarUsuariosCliente(int idCliente, DbConnection dbconn) throws SQLException
    {
        return negCliente.listarUsuariosCliente(idCliente, dbconn);
    }

    /**
     * Escreve uma notificação
     * @param clienteNotificacao -> ClienteNotification class (contém os dados da notificação)
     * @param dbConnection -> Conexão com o banco do Lono
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public void escreverNotificacao(ClienteNotificacao clienteNotificacao, DbConnection dbConnection) throws SQLException {
        this.notificacaoNegocio.escreverNotificacao(clienteNotificacao, dbConnection);
    }

    /**
     * Escreve uma notificação
     * @param idCliente -> ID do cliente
     * @param idUsuario -> ID do usuário
     * @param assunto -> Assunto
     * @param mensagem -> Mensagem
     * @param actionUrl -> ActionURL
     * @param showMobile -> Exibir no mobile
     * @param dbConnection -> Conexão com o banco do Lono
     * @throws SQLException -> Excessões relacionados ao SQL
     */
    public void escreverNotificacao(int idCliente, int idUsuario, String assunto, String mensagem, String actionUrl, boolean showMobile, DbConnection dbConnection) throws SQLException {
        this.notificacaoNegocio.escreverNotificacao(idCliente, idUsuario, assunto, mensagem, actionUrl, showMobile, dbConnection);
    }

    /**
     * Escreve uma notificação p/ os usuários na lista (os que contem ID do cliente) do Cliente informado
     * @param usuarios -> Array de Usuarios
     * @param assunto -> Assunto
     * @param mensagem -> Mensagem
     * @param actionUrl -> ActionURL
     * @param showMobile Exibir no mobile
     * @param dbConnection Conexão com o banco do Lono
     * @throws SQLException Excessões relacionados ao SQL
     */
    public void escreverNotificacaoUsuarios(Usuario[] usuarios, String assunto, String mensagem, String actionUrl, boolean showMobile, DbConnection dbConnection) throws SQLException {
        for ( Usuario usuario : usuarios) {
            if ( usuario.getIdCliente() == null || usuario.getIdCliente() <= 0 )
                continue; // Ignorando este usuário (não contém informação sobre o cliente

            // Escrevendo notificação deste usuários
            this.escreverNotificacao(usuario.getIdCliente(), usuario.getId(), assunto, mensagem, actionUrl, showMobile, dbConnection);
        }
    }

    /**
     * Escreve uma notificação p/ TODOS os usuários do Cliente informado
     * @param cliente -> Cliente
     * @param assunto -> Assunto
     * @param mensagem -> Mensagem
     * @param actionUrl -> ActionURL
     * @param showMobile -> Exibir no mobile
     * @param dbConnection -> Conexão com o banco do Lono
     * @throws SQLException Excessões relacionados ao SQL
     */
    public void escreverNotificacaoCliente(Cliente cliente, String assunto, String mensagem, String actionUrl, boolean showMobile, DbConnection dbConnection) throws SQLException {
        Usuario[] usuarios = this.listarUsuariosCliente(cliente.getIdCliente(), dbConnection);
        this.escreverNotificacaoUsuarios(usuarios, assunto, mensagem, actionUrl, showMobile, dbConnection);
    }

    /**
     * Obtém os dados do cliente pelo ID
     * @param idCliente ID do cliente
     * @param dbconn Conexão com o banco do Lono
     * @return Classe 'Cliente' com os dados alimentados (NULL se não encontrar)
     * @throws SQLException Excessões relacionados ao SQL
     */
    public Cliente listarClientePorID(int idCliente, DbConnection dbconn) throws SQLException {
        return negCliente.listarClientePorID(idCliente, dbconn);
    }

    /*
     public boolean incluirCorteMateriaPublicacaoTjPe(MateriaPublicacao materiaPub) throws SQLException {
        return negPublicacao.incluirCorteMateriaPublicacaoTjPe(materiaPub);
    }*/

    /**************************************************************************
     * Metodos  ligados a 'Materia'
     **************************************************************************/
    /**
     * Lista as linhas de inicio/fim da matéria
     * @param materiaPublicacao -> Objeto MateriaPublicacao (usado como obter os dados de inicio & fim)
     * @param sqlite -> Conexão com o banco de marcações (processamento/engine)
     * @return -> Objeto MateriaPublicacao com os dados de Inicio e Fim de máteria definidos
     * @throws SQLException -> Excessões realacionados ao SQL
     */
    public MateriaPublicacao listarLinhasInicioFimMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negPublicacao.listarLinhasInicioFimMateria(materiaPublicacao, sqlite);
    }

    /**
     * Lista/Obtém o título e subtitulo (se houver) da matéria
     * @param materiaPublicacao Objeto MateriaPublicacao, pré alimentado com os dados sobre a matéria
     * @param sqlite Conexão com o bancod e marcações (processamneto/engine)
     * @return Objeto MateriaPublicacao com os dados de Título e Subtítulo definidos
     * @throws SQLException Excessões relacioados ao SQL
     */
    public MateriaPublicacao listarTituloSubtituloMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negPublicacao.listarTituloSubtituloMateria(materiaPublicacao, sqlite);
    }

    /**
     * Obtém a pauta (se houver) da máteria
     * @param materiaPublicacao Objeto MateriaPublicacao, pré alimentado com os dados sobre a matéria
     * @param sqlite Conexão com o bancod e marcações (processamneto/engine)
     * @return Objeto PautaPublicacao com os dados da pauta (null se não encontrar)
     * @throws SQLException Excessões relacioados ao SQL
     */
    public PautaPublicacao listarPautaMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negPublicacao.listarPautaMateria(materiaPublicacao, sqlite);
    }



    //*************************************************************************
    //* Metodos  ligados a 'Marcações'
    //**************************************************************************/
    /**
     * Lista as fonts seguindo o mapFonte
     * @param idPublicacao ID da publicação
     * @param mapFonte MapFonte a ser consultado no banco de marcações
     * @param sqlite Conexão com o banco de marcações
     * @return String com as fontes enconradas
     * @throws SQLException Excessões relacioados ao SQL
     */
    public String listarFontesTipoPadraoPublicacao(int idPublicacao, String mapFonte, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negJornal.listarFontesTipoPadraoPublicacao(idPublicacao, mapFonte, sqlite);
    }

    /**
     * Obtem a MAPA da fonte
     * @param idPublicacao ID da publicação
     * @param mapFonte Valor da fotne (ft01, ft010, ft0xxx)
     * @param sqlite Conexão com o banco de marcações
     * @return String com as fontes enconradas
     * @throws SQLException Excessões relacioados ao SQL
     */
    public String listarMapaFonteTipoPadraoPublicacao(int idPublicacao, String mapFonte, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negJornal.listarMapaFonteTipoPadraoPublicacao(idPublicacao, mapFonte, sqlite).trim();
    }

    /**
     * Atualiza a Query utilizando os dados de fontes corretamente
     * @param tipoPadraoJornal Objeto TipoPadraoJornal a ser usada como base na atualização
     * @param idPublicacao ID da publicação a ser trabalhada
     * @param sqlite Conexão com o banco de marcações
     * @return Objeto TipoPadraoJornal com os dados de fonte atualizados
     * @throws SQLException Excessões relacioados ao SQL
     */
    public TipoPadraoJornal atualizaQueryFontes(TipoPadraoJornal tipoPadraoJornal, int idPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negJornal.atualizaQueryFontes(tipoPadraoJornal, idPublicacao, sqlite);
    }

    /**
     * Atualiza a Query utilizando os dados de fontes corretamente
     * @param addMarcacao Objeto AdicionalMarcacaoTipoPadrao a ser usada como base na atualização
     * @param idPublicacao ID da publicação a ser trabalhada
     * @param sqlite Conexão com o banco de marcações
     * @return Objeto AdicionalMarcacaoTipoPadrao com os dados de fonte atualizados
     * @throws SQLException Excessões relacioados ao SQL
     */
    public AdicionalMarcacaoTipoPadrao atualizaQueryFontes(AdicionalMarcacaoTipoPadrao addMarcacao, int idPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negJornal.atualizaQueryFontes(addMarcacao, idPublicacao, sqlite);
    }

    /**
     * Checa se a marcacao existe dentro do banco de marcações
     * @param textoCheck Texto/Marcação a ser chacada
     * @param idTipoPadraoCheck ID Tipo Padrao a ser checado
     * @param idPublicacao ID da publicação
     * @param sqlite Conexão com o banco de marcações
     * @return TRUE em sucesso,  FALSE em erro
     * @throws SQLException Excessões relacioados ao SQL
     */
    public boolean checarMarcacao(String textoCheck, int idTipoPadraoCheck, int idPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negJornal.checarMarcacao(textoCheck, idTipoPadraoCheck, idPublicacao, sqlite);
    }

    /**
     * Obtém a quantidade de marcações do tipo existentes no banco
     * @param marcacaoPublicacao Objeto MarcacaoPublicacao, usada como filtro
     * @param sqlite Conexão com o banco de marcações
     * @return Quantidade de marcações encontradas
     * @throws SQLException Excessões relacioados ao SQL
     */
    public int checkQuantidadeMarcacoes(MarcacaoPublicacao marcacaoPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return negPublicacao.checkQuantidadeMarcacoes(marcacaoPublicacao, sqlite);
    }

    /**
     * Obtém a quantidade de máterias
     * @param materiaPublicacao Objeto MateriaPublicacao, usada como fltro
     * @param dbconn Conexão com o banco principal do Lono
     * @return Quantidade de matérias enontradas
     * @throws SQLException Excessões relacioados ao SQL
     */
    public int checkQuantidadeMaterias(MateriaPublicacao materiaPublicacao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.checkQuantidadeMaterias(materiaPublicacao, dbconn);
    }

    /**
     * Obtém a lista de marcaçõs Adicionais
     * @param tipoAdicional Tipo de marcação adicional a ser obtida
     * @param idTipoPadrao Tipo de padrão
     * @param idPadrao ID do padrão
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do tipo AdicionalMarcacaoTipoPadrao, contendo os tipos adicionais de marcação
     * @throws SQLException Excessões relacioados ao SQL
     */
    public AdicionalMarcacaoTipoPadrao[] listarAdicionalMarcacaoTipoPadrao(char tipoAdicional, int idTipoPadrao, int idPadrao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.listarAdicionalMarcacaoTipoPadrao(tipoAdicional, idTipoPadrao, idPadrao, dbconn);
    }

    /**
     * Obtém a lista de marcações de exclusão
     * @param tipoExclusao Tipo de exlcusão
     * @param idTipoPadrao ID do Tipo de padrão
     * @param idPadrao ID do Padrão
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do tipo ExclusaoMarcacaoTipoPadrao, contendo os tipos adicionais de marcação de exclusão
     * @throws SQLException Excessões relacioados ao SQL
     */
    public ExclusaoMarcacaoTipoPadrao[] listarExclusaoMarcacaoTipoPadrao(char tipoExclusao, int idTipoPadrao, int idPadrao, DbConnection dbconn) throws SQLException
    {
        return negPublicacao.listarExclusaoMarcacaoTipoPadrao(tipoExclusao, idTipoPadrao, idPadrao, dbconn);
    }

    /**
     * Obtém a lista de jornais cadastrados no sistema
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do tipo Jornal, contendo os dados sobre os jornais
     * @throws SQLException Excessões relacioados ao SQL
     */
    public Jornal[] listaJornais(DbConnection dbconn) throws SQLException {
        return this.listaJornais("A", dbconn);
    }

    /**
     * Obtém a lista de jornais cadastrados no sistema
     * @param SitCadStatus Status do jornal a ser listado (em branco == todos)
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do tipo Jornal, contendo os dados sobre os jornais
     * @throws SQLException Excessões relacioados ao SQL
     */
    public Jornal[] listaJornais(String SitCadStatus, DbConnection dbconn) throws SQLException {
        Jornal[] listJornais = this.negJornal.obterListaJornais(dbconn);
        if ( SitCadStatus.length() > 0 ) {
            List<Jornal> finalListJornal = new ArrayList();
            for ( Jornal j: listJornais ) {
                if ( j.getSitCad().equals(SitCadStatus) ) finalListJornal.add(j);
            }

            return finalListJornal.toArray(new Jornal[finalListJornal.size()]);
        } else {
            return listJornais;
        }
    }

    /**
     * Localiza o jornal pela SIGLA
     * @param sigla_jornal Sigla do jornal a ser localizado
     * @param dbconn Conexão com o banco principal do Lono
     * @return Objeto 'Jornal' contendo o ID do jornal e outras informações
     * @throws SQLException Excessões realacionados ao SQL
     */
    public Jornal localizarJornalSigla(String sigla_jornal, DbConnection dbconn) throws  SQLException {
        return this.negJornal.localizarJornalSigla(sigla_jornal, dbconn);
    }

    /**
     * Obtém o jornal pelo ID informado
     * @param idJornal ID Jornal
     * @param dbconn Conexão com o banco principal do Lono
     * @return Objeto 'Jornal' contendo o ID do jornal e outros informações
     * @throws SQLException Excessões relacionados ao SQL
     */
    public Jornal localizarJornalID(int idJornal, DbConnection dbconn) throws SQLException {
        return this.negJornal.obterDadosJornalPorID(idJornal, dbconn);
    }

    /**
     * Atualiza a data do ultimo processamento do jornal informando
     * @param jornal Jornal
     * @param dbconn Conexão com o banco principal do Lono
     * @throws SQLException Excessões relacionados ao SQL
     */
    public void atualizarJornalLastProc(Jornal jornal, DbConnection dbconn) throws SQLException {
        negJornal.atualizarJornalLastProc(jornal, dbconn);
    }

    /**
     * Obtém a lista de padrões de corte do jornal especificado
     * @param idJornal ID do jornal
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do tipo PadraoJornal, contendo os dados sobre os padroes existentes
     * @throws SQLException Excessões relacionados ao SQL
     */
    public PadraoJornal[] listarPadroesJornais(int idJornal, DbConnection dbconn) throws SQLException {
        return this.listarPadroesJornais(idJornal, "A", dbconn);
    }

    /**
     * Obtém a lista de padrões de corte do jornal especificado
     * @param idJornal ID do jornal
     * @param SitCadStatus Status do Padrão a ser listado (em branco == todos)
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do tipo PadraoJornal, contendo os dados sobre os padroes existentes
     * @throws SQLException Excessões relacionados ao SQL
     */
    public PadraoJornal[] listarPadroesJornais(int idJornal, String SitCadStatus, DbConnection dbconn) throws SQLException {
        PadraoJornal[] listPadraoJornal = negJornal.obterListaPadraoJornal(idJornal, dbconn);
        if ( SitCadStatus.length() > 0 ) {
            List<PadraoJornal> finalListPadraoJornal = new ArrayList();
            for ( PadraoJornal pj: listPadraoJornal ) {
                if ( pj.getSitCad().equals(SitCadStatus) ) finalListPadraoJornal.add(pj);
            }

            return finalListPadraoJornal.toArray(new PadraoJornal[0]);
        } else {
            return listPadraoJornal;
        }
    }

    /**
     * Obtém a lista de tipos de padrão existente no banco
     * @param dbconn Conexão com o banco principal do Lono
     * @return Array do tipo TipoPadrao, contendo os tiposdados sobre os tipos de padroes existentes
     * @throws SQLException Excessões relacionados ao SQL
     */
    public TipoPadrao[] listarTiposPadrao(DbConnection dbconn) throws SQLException {
        return negJornal.listarTiposPadrao(dbconn);
    }

    /**
     * Adiciona um novo PadraoJornal dentro do banco Lono
     * @param padraoJornal Objeto PadraoJornal com os dados a serem salvos
     * @param dbconn Conexão com o banco principal do Lono
     * @return TRUE em sucesso, FALSE em erro
     * @throws SQLException Excessões relacionados ao SQL
     */
    public boolean adicionarPadraoJornal(PadraoJornal padraoJornal, DbConnection dbconn) throws SQLException {
        return negJornal.adicionarPadraoJornal(padraoJornal, dbconn);
    }

    /**
     * Atualiza um PadraoJornal existente
     * @param padraoJornal Objeto PadraoJornal com os dados a serem atulizados
     * @param dbconn Conexão com o banco principal do Lono
     * @return TRUE em sucesso, FALSE em erro
     * @throws SQLException Excessões relacionados ao SQL
     */
    public boolean atualizarPadraoJornal(PadraoJornal padraoJornal, DbConnection dbconn) throws SQLException {
        if ( padraoJornal.getIdPadrao() == null || padraoJornal.getIdPadrao() == 0)
            throw new SQLException("No ID");


        return negJornal.atualizarPadraoJornal(padraoJornal, dbconn);
    }

    /**
     * Filtra uma array de TipoPadraoJornal para obter por idTipoPadrao
     * @param idTipoPadrao
     * @param padraoJornals
     * @return
     */
    public TipoPadraoJornal[] filtrarTipoPadraoJornalPorTipo(int idTipoPadrao, TipoPadraoJornal[] padraoJornals)
    {
        List<TipoPadraoJornal> tipoPadraoJornalList = new ArrayList<>();
        for ( int idx = 0; idx < padraoJornals.length; idx++ ) {
            if ( padraoJornals[idx].getIdTipoPadrao() == idTipoPadrao )
                tipoPadraoJornalList.add(padraoJornals[idx]);
        }

        return tipoPadraoJornalList.toArray(new TipoPadraoJornal[0]);
    }

    /**
     * Método de cobertura, Usado para COPIAR um objeto
     * @param obj -> Objeto de origem
     * @return  -> Objeto copiado
     */
    public static Object CopyObject(Object obj){
        try {
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.get(obj) == null || Modifier.isFinal(field.getModifiers())){
                    continue;
                }
                if(field.getType().isPrimitive() || field.getType().equals(String.class)
                        || field.getType().getSuperclass().equals(Number.class)
                        || field.getType().equals(Boolean.class)){
                    field.set(clone, field.get(obj));
                }else{
                    Object childObj = field.get(obj);
                    if(childObj == obj){
                        field.set(clone, clone);
                    }else{
                        field.set(clone, Fachada.CopyObject(field.get(obj)));
                    }
                }
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }
}
