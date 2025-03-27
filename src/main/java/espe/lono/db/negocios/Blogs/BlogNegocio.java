package espe.lono.db.negocios.Blogs;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.dao.BlogsDAO;
import espe.lono.db.models.BlogJornal;
import espe.lono.db.models.Blogs.MonitorConteudo;
import espe.lono.db.models.Blogs.ReprocessarClienteBlog;

import java.sql.SQLException;

/**
 *
 * @corp FSBR
 * @author Petrus Augusto
 * @date 2020-07-08
 */

public class BlogNegocio {
    private BlogsDAO blogsDAO = new BlogsDAO();

    /**
     * Adiciona uma nova publicação de Blog
     * @param blogJornal
     * @param dbConnection
     * @return
     * @throws SQLException
     */
    public boolean Publicacao_incluirNova(BlogJornal blogJornal, DbConnection dbConnection) throws SQLException
    {
        return blogsDAO.incluirNovaPublicacao(blogJornal, dbConnection);
    }

    /**
     * Atualiza a fim_processamento de uma publicação de blog
     * @param blogJornal
     * @param dbConnection
     * @return
     * @throws SQLException
     */
    public boolean Publicacao_atualizarFimProcessamento(BlogJornal blogJornal, DbConnection dbConnection) throws SQLException
    {
        return blogsDAO.informarFimProcessamentoPublicacao(blogJornal, dbConnection);
    }

    /**
     * Atualiza o status no Controle de Contúedo de Blogs
     * @param conteudoModel
     * @param nSitCad
     * @param dbConnection
     * @return
     * @throws SQLException
     */
    public boolean Conteudo_ModificarStatus(MonitorConteudo conteudoModel, String nSitCad, String newHash, String newUltUrlPub, DbConnection dbConnection ) throws SQLException
    {
        return blogsDAO.atualizarStatusConteudo(conteudoModel.getId(), nSitCad, newHash, newUltUrlPub, dbConnection);
    }

    /**
     * Obtém UM Blog no qual, deve pesquisar com base na difereça de tempo (minutos) da ultima execção/atualização.
     * @param nSitCad
     * @param diffLastMinutes
     * @param dbConnection
     * @return
     * @throws SQLException
     */
    public MonitorConteudo[] Conteudo_APesquisa(String nSitCad, int diffLastMinutes, int limit, DbConnection dbConnection) throws SQLException
    {
        return blogsDAO.obterBlogsAPesquisar(nSitCad, diffLastMinutes, limit, dbConnection);
    }

    /**
     *
     * @param newStatus
     * @param limit
     * @param dbConnection
     * @return
     * @throws SQLException
     */
    public ReprocessarClienteBlog[] Reprocessamento_AProcessar(String newStatus, int limit, DbConnection dbConnection) throws SQLException
    {
        ReprocessarClienteBlog[] reprocessarClienteBlogs = blogsDAO.obterBlogsAReprocessar(newStatus, limit, dbConnection);
        for ( int i = 0; i < reprocessarClienteBlogs.length; i++ )
            reprocessarClienteBlogs[i].setBlogJornal(null) ;

        return reprocessarClienteBlogs;
    }

    /**
     *
     * @param id
     * @param newStatus
     * @param dbConnection
     * @return
     * @throws SQLException
     */
    public boolean Reprocessamento_AtualizarStatus(int id, String newStatus, DbConnection dbConnection) throws SQLException
    {
        return this.blogsDAO.atualizarStatusReprocessamento(id, newStatus, dbConnection);
    }
    /**
     *
     * @param idPublicacao
     * @param dbConnection
     * @return
     * @throws SQLException
     */
    public BlogJornal Publicacao_LocalizarID(int idPublicacao, DbConnection dbConnection) throws SQLException
    {
        return blogsDAO.obterPublicacao(idPublicacao, dbConnection);
    }
}
