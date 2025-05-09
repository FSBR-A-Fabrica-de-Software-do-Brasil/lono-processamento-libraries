package espe.lono.db.negocios;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.dao.PublicacaoDAO;
import espe.lono.db.models.*;

import java.sql.SQLException;

/**
 *
 * @author Espe
 */
public class PublicacaoNegocio
{
    final PublicacaoDAO publicacaoDAO;

    public PublicacaoNegocio()
    {
        this.publicacaoDAO = new PublicacaoDAO();
    }

    public Integer[] listarPublicacoesAntigas(int idJornal, int idPublicacaoToIgnore,  DbConnection dbconn) throws SQLException {
        return publicacaoDAO.dadosListarPublicacoesAntigas(idJornal, idPublicacaoToIgnore, dbconn);
    }

    public boolean ignorarNumeroProcesso(int idCliente, String processo, DbConnection dbconn) throws SQLException {
        return publicacaoDAO.ignorarNumeroProcesso(idCliente, processo, dbconn);
    }

    public boolean atualizarDataInicialProcessamentoPublicacao(int id_publicacao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.atualizarDataInicialProcessamentoPublicacao(id_publicacao, dbconn);
    }

    public PublicacaoJornal obterPublicacaoRecenteClienteJornal(int idJornal, int idCliente, int idNomePesquisa, DbConnection dbConn) throws SQLException
    {
        return publicacaoDAO.obterPublicacaoRecenteClienteJornal(idJornal, idCliente, idNomePesquisa, dbConn);
    }
    
    public boolean atualizarDataFinalProcessamentoPublicacao(int id_publicacao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.atualizarDataFinalProcessamentoPublicacao(id_publicacao, dbconn);
    }
    
    public PublicacaoJornal listarPublicacoesPorID(int id, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.dadosListarPublicacoesPorID(id, dbconn);
    }
    
    public PublicacaoJornal[] listarPublicacoesPorSituacao(char situacao, DbConnection dbconn, int limit) throws SQLException
    {
        return publicacaoDAO.dadosListarPublicacoesPorSituacao(situacao, dbconn, limit);
    }

    public boolean alterarSituacaoPublicacao(int idPublicacao, char situacao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.execAlterarSituacaoPublicacao(idPublicacao, situacao, dbconn);
    }

    public boolean removerPublicacao(int idPublicacao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.execRemoverPublicacao(idPublicacao, dbconn);
    }
    
    public Integer[] listarPublicacoesAguardandoReprocessamento(DbConnection dbconn, int limit) throws SQLException
    {
        return publicacaoDAO.listarPublicacoesAguardandoReprocessamento(dbconn, limit);
    }
    
    public ReprocessarClientePublicacao[] listaReprocessarClientePublicacaoPorPublicacao(DbConnection dbconn, int idPublicacao, String sitCad) throws SQLException
    {
        return publicacaoDAO.listaReprocessarClientePublicacaoPorPublicacao(dbconn, idPublicacao, sitCad);
    }
    
    public boolean alterarSituacaoPublicacaoReprocessamento(int idReprocessamento, char situacao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.execAlterarSituacaoPublicacaoReprocessamento(idReprocessamento, situacao, dbconn);
    }

    public Long[][] listarMarcacaoPorTipoPadraos(String padroes, DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.listarMarcacoesPorTipoPadraos(padroes, sqlite);
    }
    
    public Long[] listarMarcacaoPorTipoPadrao(int padrao, DbConnectionMarcacao sqlite, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.listarMarcacoesPorTipoPadrao(padrao, sqlite, dbconn);
    }
    
    public Long[][] listarMarcacaoComplexas(DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.listarMarcacoesComplexas(sqlite);
    }
    
    public boolean incluirMarcacaoPublicacao(MarcacaoPublicacao marcacaoPub, DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.execIncluirMarcacaoPublicacao(marcacaoPub, sqlite);
    }

    public boolean incluirCortePautaPublicacao(PautaPublicacao materiaPub, int id, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.execIncluirPautaPublicacao(materiaPub, id, dbconn);
    }

    public boolean numProcessoIgnorado(MateriaPublicacao materiaPub, DbConnection dbconn) throws SQLException {
        final String numprocesso = (materiaPub.getProcesso() == null) ? "" : materiaPub.getProcesso();
        return publicacaoDAO.numProcessoIgnorado(numprocesso, materiaPub.getIdCliente(), dbconn);
    }

    public int incluirCorteMateriaPublicacao(PublicacaoJornal publicacaoJornal, NomePesquisaCliente nomePesquisaCliente, MateriaPublicacao materiaPub, PautaPublicacao pautaPublicacao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.execIncluirCorteMateriaPublicacao(publicacaoJornal, nomePesquisaCliente, materiaPub, pautaPublicacao, dbconn);
    }

    public boolean verificaColisaoMD5(int idJornal, String md5, DbConnection dbconn) throws SQLException {
        return publicacaoDAO.execVerificaColisaoMD5(idJornal, md5, dbconn);
    }

    public boolean execInserirPublicacaoJornal(int idJornal, String fileName, String dtPublicacao, String dtDivulgacao, String edicaoPublicacao, int totalPagina, String fileMd5, DbConnection dbconn) throws SQLException {
        return publicacaoDAO.execInserirPublicacaoJornal(idJornal, fileName, dtPublicacao, dtDivulgacao, edicaoPublicacao, totalPagina, fileMd5, dbconn);
    }
    
    
    public MateriaPublicacao listarLinhasInicioFimMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.dadosListarLinhasInicioFimMateria(materiaPublicacao, sqlite);
    }
    
    public PautaPublicacao listarPautaMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.dadosListarPautaMateria(materiaPublicacao, sqlite);
    }

    public MateriaPublicacao listarTituloSubtituloMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.dadosListarTituloSubtituloMateria(materiaPublicacao, sqlite);
    }

    public int checkQuantidadeMarcacoes(MarcacaoPublicacao marcacaoPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.dadosCheckQuantidadeMarcacoes(marcacaoPublicacao, sqlite);
    }

    public int checkQuantidadeMaterias(MateriaPublicacao materiaPublicacao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.dadosCheckQuantidadeMaterias(materiaPublicacao, dbconn);
    }

    //#7335
    public AdicionalMarcacaoTipoPadrao[] listarAdicionalMarcacaoTipoPadrao(char tipoAdicional, int idTipoPadrao, int idPadrao, DbConnection dbconn) throws SQLException 
    {
        return publicacaoDAO.dadosListarAdicionalMarcacaoTipoPadrao(tipoAdicional, idTipoPadrao, idPadrao, dbconn);
    }

    //#7335
    public ExclusaoMarcacaoTipoPadrao[] listarExclusaoMarcacaoTipoPadrao(char tipoExclusao, int idTipoPadrao, int idPadrao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.dadosListarExclusaoMarcacaoTipoPadrao(tipoExclusao, idTipoPadrao, idPadrao, dbconn);
    }
    
    public boolean removerMarcacaoPrincipaisPorRangeNumDocLucene(int numdoc_inicial, int numdoc_final, DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.removerMarcacaoPrincipaisRangeNDocLucene(numdoc_inicial, numdoc_final, sqlite);
    }
    
    public boolean atualizarEdicaoPublicacao(int id_publicacao, String numEdicao, DbConnection dbconn) throws SQLException
    {
        return publicacaoDAO.atualizarEdicaoPublicacao(id_publicacao, numEdicao, dbconn);
    }
    
    public String dadosObterNumeroEdicao(DbConnectionMarcacao sqlite) throws SQLException
    {
        return publicacaoDAO.dadosObterNumeroEdicao(sqlite);
    }
}
