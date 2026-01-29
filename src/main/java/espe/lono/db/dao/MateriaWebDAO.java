package espe.lono.db.dao;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.MateriasWeb;
import espe.lono.db.models.TipoConteudoWeb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MateriaWebDAO {
    public TipoConteudoWeb[] listarTiposConteudoWeb(DbConnection dbConnection) throws SQLException {
        String sql = "SELECT * FROM tipo_conteudo_web WHERE id < 100 AND sit_cad = 'A' ORDER BY descricao";
        ResultSet resultSet = dbConnection.abrirConsultaSql(sql);

        List<TipoConteudoWeb> tiposConteudoWeb = new ArrayList<>();
        while (resultSet.next()) {
            TipoConteudoWeb conteudoWeb = new TipoConteudoWeb();
            conteudoWeb.setId(resultSet.getLong("id"));
            conteudoWeb.setDescricao(resultSet.getString("descricao"));
            tiposConteudoWeb.add(conteudoWeb);
        }

        resultSet.close();
        return tiposConteudoWeb.toArray(new TipoConteudoWeb[0]);
    }
    public TipoConteudoWeb localizarTipoConteudoWebPorId(long id, DbConnection dbConnection) throws SQLException {
        String sql = "SELECT * FROM tipo_conteudo_web WHERE id = " + id;
        ResultSet resultSet = dbConnection.abrirConsultaSql(sql);
        if ( !resultSet.next() ) return null;


        TipoConteudoWeb conteudoWeb = new TipoConteudoWeb();
        conteudoWeb.setId(resultSet.getLong("id"));
        conteudoWeb.setDescricao(resultSet.getString("descricao"));

        resultSet.close();
        return conteudoWeb;
    }

    public MateriasWeb localizarMateriaWebPorId(Long idMateria, DbConnection dbConnection) throws SQLException {
        String sql = "SELECT * FROM materias_web WHERE id = " + idMateria;
        ResultSet resultSet = dbConnection.abrirConsultaSql(sql);
        if ( !resultSet.next() ) return null;

        MateriasWeb materiasWeb = new MateriasWeb();
        materiasWeb.setId(resultSet.getLong("id"));
        materiasWeb.setVeiculoId(resultSet.getLong("veiculo_id"));
        materiasWeb.setTipoConteudoId(resultSet.getLong("tipo_conteudo_id"));
        materiasWeb.setResumida(resultSet.getString("resumida"));
        materiasWeb.setIntegral(resultSet.getString("integral"));
        materiasWeb.setTitulo(resultSet.getString("titulo"));
        materiasWeb.setSubTitulo(resultSet.getString("sub_titulo"));
        materiasWeb.setDataPublicacao(resultSet.getTimestamp("data_publicacao"));
        materiasWeb.setDatCad(resultSet.getTimestamp("dat_cad"));
        materiasWeb.setSitCad(resultSet.getString("sit_cad"));
        materiasWeb.setStaCad(resultSet.getString("sta_cad"));
        materiasWeb.setUrlMateria(resultSet.getString("url_materia"));
        materiasWeb.setFavorita(resultSet.getBoolean("favorita"));
        materiasWeb.setContemVideo(resultSet.getBoolean("contem_video"));
        materiasWeb.setUrlVideo(resultSet.getString("url_video"));

        materiasWeb.setTipoConteudo(localizarTipoConteudoWebPorId(resultSet.getLong("tipo_conteudo_id"), dbConnection));

        resultSet.close();
        return materiasWeb;
    }

    public MateriasWeb localizarMateriaWebPorUrlTituloVeiculo(String titulo, String url, Long idVeiculo, DbConnection dbConnection) {
        String sql = "SELECT * FROM materias_web WHERE titulo = ? AND url_materia = ? AND veiculo_id = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = dbConnection.obterPreparedStatement(sql);
            preparedStatement.setString(1, titulo);
            preparedStatement.setString(2, url);
            preparedStatement.setLong(3, idVeiculo);

            resultSet = preparedStatement.executeQuery();
            if ( !resultSet.next() ) return null;

            MateriasWeb materiasWeb = new MateriasWeb();
            materiasWeb.setId(resultSet.getLong("id"));
            materiasWeb.setVeiculoId(resultSet.getLong("veiculo_id"));
            materiasWeb.setTipoConteudoId(resultSet.getLong("tipo_conteudo_id"));
            materiasWeb.setResumida(resultSet.getString("resumida"));
            materiasWeb.setIntegral(resultSet.getString("integral"));
            materiasWeb.setTitulo(resultSet.getString("titulo"));
            materiasWeb.setSubTitulo(resultSet.getString("sub_titulo"));
            materiasWeb.setDataPublicacao(resultSet.getTimestamp("data_publicacao"));
            materiasWeb.setDatCad(resultSet.getTimestamp("dat_cad"));
            materiasWeb.setSitCad(resultSet.getString("sit_cad"));
            materiasWeb.setStaCad(resultSet.getString("sta_cad"));
            materiasWeb.setUrlMateria(resultSet.getString("url_materia"));
            materiasWeb.setFavorita(resultSet.getBoolean("favorita"));
            materiasWeb.setContemVideo(resultSet.getBoolean("contem_video"));
            materiasWeb.setUrlVideo(resultSet.getString("url_video"));

            materiasWeb.setTipoConteudo(localizarTipoConteudoWebPorId(resultSet.getLong("tipo_conteudo_id"), dbConnection));

            return materiasWeb;
        } catch (SQLException e) {
            return null;
        } finally {
            try { if (resultSet != null) resultSet.close(); }
            catch (Exception ignore ) {}
            try { if (preparedStatement != null) preparedStatement.close(); }
            catch (Exception ignore ) {}
        }
    }
}
