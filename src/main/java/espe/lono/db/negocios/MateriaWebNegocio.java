package espe.lono.db.negocios;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.dao.MateriaWebDAO;
import espe.lono.db.models.MateriasWeb;
import espe.lono.db.models.TipoConteudoWeb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MateriaWebNegocio {
    private MateriaWebDAO materiaWebDAO = new MateriaWebDAO();

    public TipoConteudoWeb[] listarTiposConteudoWeb(DbConnection dbConnection) throws SQLException {
        return materiaWebDAO.listarTiposConteudoWeb(dbConnection);
    }

    public TipoConteudoWeb obterTipoConteudoWebPorId(long id, DbConnection dbConnection) throws SQLException {
        return materiaWebDAO.localizarTipoConteudoWebPorId(id, dbConnection);
    }

    public MateriasWeb obterMateriaWebPorId(Long idMateria, DbConnection dbConnection) throws SQLException {
        return materiaWebDAO.localizarMateriaWebPorId(idMateria, dbConnection);
    }

    public MateriasWeb obterMateriaWebPorUrlTituloVeiculo(String titulo, String url, Long idVeiculo, DbConnection dbConnection) {
        return materiaWebDAO.localizarMateriaWebPorUrlTituloVeiculo(titulo, url, idVeiculo, dbConnection);
    }
}
