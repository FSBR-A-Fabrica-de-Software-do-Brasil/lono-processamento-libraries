package espe.lono.db.dao;

import espe.lono.db.models.BlogJornal;
import espe.lono.db.models.Blogs.ReprocessarClienteBlog;
import espe.lono.db.models.Jornal;
import espe.lono.db.models.PublicacaoJornal;
import espe.lono.db.utils.DatabaseMapper;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.Blogs.MonitorConteudo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class BlogsDAO {
    final private static String TABLE_BLOG_MONITOR_CONTEUDO = "blog_monitor_conteudo";
    final private static String TABLE_BLOG_JORNAL = "blog_jornal";
    final private static String TABLE_BLOG_REPROCESSAMENTO = "reprocessar_cliente_blog";

    private JornalDAO jornalDAO = new JornalDAO();

    public boolean atualizarStatusConteudo(int id, String newStatus, String newHash, String newUltUrl, DbConnection dbConnection ) throws SQLException
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("UPDATE %s SET sit_cad = '%s'", TABLE_BLOG_MONITOR_CONTEUDO, newStatus));
        if ( newHash != null && newHash.length() > 0 ) stringBuilder.append(String.format(", ult_hash = '%s'", newHash));
        if ( newUltUrl != null && newUltUrl.length() > 0 ) stringBuilder.append(String.format(", ult_url_publicada = '%s'", newUltUrl));
        stringBuilder.append(", updated_at = NOW()");
        stringBuilder.append(String.format(" WHERE id = %d", id));

        return dbConnection.executarSql(stringBuilder.toString());
    }

    public boolean atualizarStatusReprocessamento(int id, String newStatus, DbConnection dbConnection) throws SQLException
    {
        String sql = "UPDATE " + TABLE_BLOG_REPROCESSAMENTO + " SET proc_dat = NOW(), updated_at = NOW(), sit_cad = '" + newStatus + "' WHERE id = " + id;
        return dbConnection.executarSql(sql);
    }

    public ReprocessarClienteBlog[] obterBlogsAReprocessar(String newStatus, int limit, DbConnection dbConnection) throws SQLException
    {
        if ( newStatus == null ) newStatus = "";

        final Statement stm = dbConnection.obterStatement();
        final String sql = "select * from obter_reprocessamento_blogs__pendentes('" + newStatus + "', " + limit + ")";

        // Realizando consulta e obtendo resposta
        ResultSet resultSet = dbConnection.abrirConsultaSql(stm, sql);
        List<ReprocessarClienteBlog> reprocessarClienteBlogList = new DatabaseMapper<ReprocessarClienteBlog>().mapResultToObject(resultSet, ReprocessarClienteBlog.class);

        resultSet.close();
        stm.close();

        return reprocessarClienteBlogList.toArray(new ReprocessarClienteBlog[0]);
    }

    public MonitorConteudo[] obterBlogsAPesquisar(String newStatus, int diffLastMinutes, int limit, DbConnection dbConnection) throws SQLException
    {
        // Validando entrada
        if ( newStatus == null ) newStatus = "";

        // Executando a query
        final Statement stm = dbConnection.obterStatement();
        final String sql = "select * from obter_blogs__a_verficar('" + newStatus + "', " + diffLastMinutes + ", " + limit + ")";
        System.out.println(sql);

        // Realizando consulta e obtendo resposta
        ResultSet resultSet = dbConnection.abrirConsultaSql(stm, sql);
        List<MonitorConteudo> monitorConteudoModelList = new DatabaseMapper<MonitorConteudo>().mapResultToObject(resultSet, MonitorConteudo.class);
        resultSet.close();

        if ( monitorConteudoModelList != null && monitorConteudoModelList.size() > 0 ) {
            // Modificando todos os obtidos p/ o novo status
            for ( int idx = 0; idx < monitorConteudoModelList.size(); idx++ )
                monitorConteudoModelList.get(idx).setSitCad(newStatus);
        }

        // Retornando os dados obtidos
        return (monitorConteudoModelList != null) ? monitorConteudoModelList.toArray( new MonitorConteudo[0] ) : new MonitorConteudo[0];
    }

    public boolean incluirNovaPublicacao(BlogJornal blogJornal, DbConnection dbConnection) throws SQLException
    {
        String sql = "INSERT INTO " + TABLE_BLOG_JORNAL + " (id_jornal, url_publicacao, dt_publicacao, sit_cad, usu_cad, blog_ult_hash, inicio_processamento, dat_cad) VALUES(?,?,?,?,?,?,?,NOW()) RETURNING id";
        PreparedStatement preparedStatement = dbConnection.obterPreparedStatement(sql);
        preparedStatement.setInt(1, blogJornal.getIdJornal());
        preparedStatement.setString(2, blogJornal.getUrlPublicacao());
        preparedStatement.setDate(3, new java.sql.Date( blogJornal.getDtPublicacao().getTime() ));
        preparedStatement.setString(4, blogJornal.getSitCad());
        preparedStatement.setInt(5, blogJornal.getUsuCad());
        preparedStatement.setString(6, blogJornal.getBlogUltHash());
        preparedStatement.setDate(7, new java.sql.Date( blogJornal.getInicioProcessamento().getTime()));

        ResultSet resultSet = dbConnection.executarSql(preparedStatement, true);
        if ( !resultSet.next() ) {
            resultSet.close();
            return false;
        }

        blogJornal.setId(resultSet.getInt("id"));
        resultSet.close();
        return true;
    }

    public BlogJornal obterPublicacao(int idPublicacao, DbConnection dbConnection) throws SQLException
    {
        String sql = "SELECT * FROM " + TABLE_BLOG_JORNAL + " WHERE id = " + idPublicacao;
        Statement statement = dbConnection.obterStatement();
        ResultSet resultSet = dbConnection.abrirConsultaSql(statement, sql);

        List<BlogJornal> blogJornalList = new DatabaseMapper<BlogJornal>().mapResultToObject(resultSet, BlogJornal.class);
        for ( int i = 0; i < blogJornalList.size(); i++ ) {
            Jornal jornal = jornalDAO.obterDadosDoJornal(blogJornalList.get(i).getIdJornal(),dbConnection);
            blogJornalList.get(i).setBlog(jornal);
        }
        resultSet.close();
        statement.close();

        return (blogJornalList.size() > 0) ? blogJornalList.get(0) : null;
    }

    public boolean informarFimProcessamentoPublicacao(BlogJornal blogJornal, DbConnection dbConnection) throws SQLException
    {
        String sql = "UPDATE " + TABLE_BLOG_JORNAL + " SET fim_processamento = NOW() WHERE id = ?";
        PreparedStatement preparedStatement = dbConnection.obterPreparedStatement(sql);
        preparedStatement.setInt(1, blogJornal.getId());

        boolean response = dbConnection.executarSql(preparedStatement);
        preparedStatement.close();
        return response;
    }
}
