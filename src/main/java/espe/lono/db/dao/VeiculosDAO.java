package espe.lono.db.dao;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.PadraoVeiculo;
import espe.lono.db.models.TipoPadrao;
import espe.lono.db.models.TipoPadraoVeiculos;
import espe.lono.db.models.Veiculo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VeiculosDAO {
    public PadraoVeiculo obterListaPadraoVeiculo(long idVeiculo, String sitCad, DbConnection dbconn) throws SQLException
    {
        String sql = "SELECT * FROM padrao_veiculos WHERE id_veiculo=" + idVeiculo;
        if (!sitCad.isEmpty()) {
            sql += " AND sit_cad = '" + sitCad + "'";
        }

        final List<PadraoVeiculo> listPadraoJornal = new ArrayList();
        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        while ( resultado.next() ) {
            PadraoVeiculo padraoVeiculo = new PadraoVeiculo();
            padraoVeiculo.setIdPadrao(resultado.getInt("id_padrao"));
            padraoVeiculo.setIdVeiculo(resultado.getInt("id_veiculo"));
            padraoVeiculo.setInicioValidade(resultado.getDate("inicio_validade"));
            padraoVeiculo.setFimValidade(resultado.getDate("fim_validade"));
            padraoVeiculo.setDatCad(resultado.getDate("dat_cad"));
            padraoVeiculo.setSitCad(resultado.getString("sit_cad"));
            padraoVeiculo.setUsuCad(resultado.getInt("usu_cad"));

            listPadraoJornal.add(padraoVeiculo);
            break;
        }

        resultado.close();
        stm.close();
        return (listPadraoJornal.isEmpty()) ? null : listPadraoJornal.get(0);
    }

    public TipoPadraoVeiculos[] dadosListarTiposPadraoPorTipo(TipoPadraoVeiculos[] tipoPadraoVeiculosList, int codTipoPadrao) {
        List<TipoPadraoVeiculos> tipoPadraoVeiculosResponseList = new ArrayList<>();
        for ( TipoPadraoVeiculos tipoPadraoVeiculos: tipoPadraoVeiculosList ) {
            if ( tipoPadraoVeiculos.getTipoPadrao().getIdTipoPadrao() == codTipoPadrao )
                tipoPadraoVeiculosResponseList.add(tipoPadraoVeiculos);
        }

        return tipoPadraoVeiculosResponseList.toArray(new TipoPadraoVeiculos[0]);
    }

    public TipoPadraoVeiculos tipoPadraoVeiculosPorTipo(TipoPadraoVeiculos[] tipoPadraoVeiculosList, int codTipoPadrao) {
        for ( TipoPadraoVeiculos tipoPadraoVeiculos: tipoPadraoVeiculosList ) {
            if ( tipoPadraoVeiculos.getTipoPadrao().getIdTipoPadrao() == codTipoPadrao )
                return tipoPadraoVeiculos;
        }
        return null;
    }

    public TipoPadraoVeiculos[] dadosListarTiposPadraoVeiculos(int idPadrao, DbConnection dbconn) throws SQLException {
        final Statement stm = dbconn.obterStatement();

        ArrayList<TipoPadraoVeiculos> listaTiposPadrao = new ArrayList<>();
        String sql = "SELECT " +
                " pv.id_padrao pv_id_padrao, " +
                " pv.id_veiculo pv_id_veiculo, " +
                " pv.inicio_validade pv_inicio_validade, " +
                " pv.fim_validade pv_fim_validade, " +
                " pv.dat_cad pv_dat_cad, " +
                " pv.sit_cad pv_sit_cad, " +
                " pv.usu_cad pv_usu_cad, " +
                " tp.id_tipo_padrao tp_id_tipo_padrao, " +
                " tp.chave_tipo tp_chave_tipo, " +
                " tp.nm_tipo tp_nm_tipo, " +
                " tp.desc_tipo tp_desc_tipo, " +
                " tp.dat_cad tp_dat_cad, " +
                " tp.sit_cad tp_sit_cad, " +
                " tp.usu_cad tp_usu_cad, " +
                " tpv.id_tipo_padrao_veiculos tpv_id_tipo_padrao_veiculos, " +
                " tpv.id_padrao tpv_id_padrao, " +
                " tpv.id_tipo_padrao tpv_id_tipo_padrao, " +
                " tpv.query_ini tpv_query_ini, " +
                " tpv.query_fim tpv_query_fim, " +
                " tpv.regex_ini tpv_regex_ini, " +
                " tpv.regex_fim tpv_regex_fim, " +
                " tpv.trechos_replace tpv_trechos_replace, " +
                " tpv.qtd_linha_ajuste_acima tpv_qtd_linha_ajuste_acima, " +
                " tpv.qtd_linha_ajuste_abaixo tpv_qtd_linha_ajuste_abaixo, " +
                " tpv.acao tpv_acao, " +
                " tpv.dat_cad tpv_dat_cad, " +
                " tpv.sit_cad tpv_sit_cad, " +
                " tpv.usu_cad tpv_usu_cad, " +
                " tpv.map_font tpv_map_font, " +
                " tpv.id_tipo_padrao_check tpv_id_tipo_padrao_check, " +
                " tpv.qtd_max_result_query tpv_qtd_max_result_query, " +
                " tpv.direcao tpv_direcao, " +
                " tpv.complex_mode tpv_complex_mode " +
                " FROM " +
                " padrao_veiculos pv, " +
                " tipo_padrao tp, " +
                " tipo_padrao_veiculos tpv " +
                " WHERE " +
                " pv.id_padrao = tpv.id_padrao " +
                " AND tpv.id_tipo_padrao = tp.id_tipo_padrao " +
                " AND pv.id_padrao = " + idPadrao;

        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);

        TipoPadraoVeiculos tipoPadraoVeiculo = null;
        TipoPadrao tipoPadrao = null;

        while(resultado.next()) {
            tipoPadraoVeiculo = new TipoPadraoVeiculos();
            tipoPadrao = new TipoPadrao();

            tipoPadraoVeiculo.setIdTipoPadraoVeiculos(resultado.getInt("tpv_id_tipo_padrao_veiculos"));
            tipoPadraoVeiculo.setIdPadrao(resultado.getInt("pv_id_padrao"));
            tipoPadraoVeiculo.setIdTipoPadrao(resultado.getInt("tp_id_tipo_padrao"));
            tipoPadraoVeiculo.setQueryIni(resultado.getString("tpv_query_ini"));
            tipoPadraoVeiculo.setQueryFim(resultado.getString("tpv_query_fim"));
            tipoPadraoVeiculo.setRegexIni(resultado.getString("tpv_regex_ini"));
            tipoPadraoVeiculo.setRegexFim(resultado.getString("tpv_regex_fim"));
            tipoPadraoVeiculo.setTrechosReplace(resultado.getString("tpv_trechos_replace"));
            tipoPadraoVeiculo.setQtdLinhaAjusteAcima(resultado.getInt("tpv_qtd_linha_ajuste_acima"));
            tipoPadraoVeiculo.setQtdLinhaAjusteAbaixo(resultado.getInt("tpv_qtd_linha_ajuste_abaixo"));
            tipoPadraoVeiculo.setAcao(resultado.getString("tpv_acao"));
            tipoPadraoVeiculo.setDatCad(resultado.getDate("tpv_dat_cad"));
            tipoPadraoVeiculo.setSitCad(resultado.getString("tpv_sit_cad"));
            tipoPadraoVeiculo.setUsuCad(resultado.getInt("tpv_usu_cad"));
            tipoPadraoVeiculo.setMapFont(resultado.getString("tpv_map_font"));
            tipoPadraoVeiculo.setIdTipoPadraoCheck(resultado.getInt("tpv_id_tipo_padrao_check"));
            tipoPadraoVeiculo.setQtdMaxResultQuery(resultado.getInt("tpv_qtd_max_result_query"));
            tipoPadraoVeiculo.setDirecao(resultado.getString("tpv_direcao"));
            tipoPadraoVeiculo.setComplexMode(resultado.getBoolean("tpv_complex_mode"));

            tipoPadrao.setIdTipoPadrao(resultado.getInt("tp_id_tipo_padrao"));
            tipoPadrao.setChaveTipo(resultado.getString("tp_chave_tipo"));
            tipoPadrao.setNmTipo(resultado.getString("tp_nm_tipo"));
            tipoPadrao.setDescTipo(resultado.getString("tp_desc_tipo"));
            tipoPadrao.setDatCad(resultado.getDate("tp_dat_cad"));
            tipoPadrao.setSitCad(resultado.getString("tp_sit_cad"));
            tipoPadrao.setUsuCad(resultado.getInt("tp_usu_cad"));

            tipoPadraoVeiculo.setTipoPadrao(tipoPadrao);

            listaTiposPadrao.add(tipoPadraoVeiculo);
        }

        stm.close();
        resultado.close();

        return listaTiposPadrao.toArray(new TipoPadraoVeiculos[0]);

    }

//    public VeiculoMonitorConteudo buscarPorIdVeiculo(long idVeiculo, DbConnection dbconn) throws SQLException {
//        VeiculoMonitorConteudo veiculoMonitorConteudo = null;
//
//        String sql = "SELECT * FROM veiculo_monitor_conteudo WHERE id_veiculo = " + idVeiculo;
//
//        try (Statement statement = dbconn.obterStatement();
//             ResultSet resultSet = statement.executeQuery(sql)) {
//
//            if (resultSet.next()) {
//                veiculoMonitorConteudo = new VeiculoMonitorConteudo();
//                veiculoMonitorConteudo.setId(resultSet.getLong("id"));
//                veiculoMonitorConteudo.setIdVeiculo(resultSet.getLong("id_veiculo"));
//                veiculoMonitorConteudo.setUltUrlPublicada(resultSet.getString("ult_url_publicada"));
//                veiculoMonitorConteudo.setUltHash(resultSet.getString("ult_hash"));
//                veiculoMonitorConteudo.setSitCad(resultSet.getString("sit_cad"));
//                veiculoMonitorConteudo.setCreatedAt(resultSet.getTimestamp("created_at"));
//                veiculoMonitorConteudo.setUpdatedAt(resultSet.getTimestamp("updated_at"));
//            }
//        }
//
//        return veiculoMonitorConteudo;
//    }
//


    public Veiculo buscarVeiculoById(Long id, DbConnection dbconn) throws SQLException {
        Veiculo veiculo = null;
        String sql = "select * from veiculos v where v.id =" + id;

        try (Statement statement = dbconn.obterStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                veiculo = new Veiculo();
                veiculo.setId(resultSet.getLong("id"));
                veiculo.setTipoVeiculoId(resultSet.getLong("tipo_veiculo_id"));
                veiculo.setDescricao(resultSet.getString("descricao"));
                veiculo.setPaisId(resultSet.getLong("pais_id"));
                veiculo.setEstadoId(resultSet.getLong("estado_id"));
                veiculo.setCidadeId(resultSet.getLong("cidade_id"));
                veiculo.setDatCad(resultSet.getTimestamp("dat_cad"));
                veiculo.setSitCad(resultSet.getString("sit_cad"));
                veiculo.setUsuCad(resultSet.getLong("usu_cad"));
                veiculo.setIcoVeiculo(resultSet.getBytes("ico_veiculo"));
                veiculo.setUrlIconeVeiculo(resultSet.getString("url_icone_veiculo"));
                veiculo.setUrlVeiculo(resultSet.getString("url_veiculo"));
                veiculo.setMaisRelevante(resultSet.getBoolean("mais_relevante"));
                veiculo.setPaywall(resultSet.getBoolean("paywall"));
            }
        }

        return veiculo;
    }

    public void atualizarPaywallVeiculo(DbConnection dbconn, long veiculoId, boolean paywall) throws SQLException {
        String sql = "UPDATE veiculos SET paywall = '" + paywall + "' WHERE id = " + veiculoId;

        try (Statement statement = dbconn.obterStatement()) {
            int rowsAffected = statement.executeUpdate(sql);

            if (rowsAffected > 0)
                System.out.println("Paywall atualizado com sucesso para o veículo ID: " + veiculoId);
            else
                System.out.println("Nenhum veículo encontrado com ID: " + veiculoId);

        } catch (SQLException e) {
            System.err.println("Erro ao atualizarr o paywall: " + e.getMessage());
            throw e;
        }
    }

    public List<Veiculo> buscarVeiculosAtivos(DbConnection dbconn, boolean prioritario) throws SQLException {
        List<Veiculo> veiculosAtivos = new ArrayList<>();

        String sql = "SELECT * FROM veiculos " +
                "WHERE sit_cad = 'A' and url_veiculo is not null and url_veiculo like('%.%') " +
                (prioritario ? " AND searchnow = true" : "") + " " +
                "ORDER BY id ASC";

        try (Statement statement = dbconn.obterStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(resultSet.getLong("id"));
                veiculo.setTipoVeiculoId(resultSet.getLong("tipo_veiculo_id"));
                veiculo.setDescricao(resultSet.getString("descricao"));
                veiculo.setPaisId(resultSet.getLong("pais_id"));
                veiculo.setEstadoId(resultSet.getLong("estado_id"));
                veiculo.setCidadeId(resultSet.getLong("cidade_id"));
                veiculo.setDatCad(resultSet.getTimestamp("dat_cad"));
                veiculo.setSitCad(resultSet.getString("sit_cad"));
                veiculo.setUsuCad(resultSet.getLong("usu_cad"));
                veiculo.setIcoVeiculo(resultSet.getBytes("ico_veiculo"));
                veiculo.setUrlIconeVeiculo(resultSet.getString("url_icone_veiculo"));
                veiculo.setUrlVeiculo(resultSet.getString("url_veiculo"));
                veiculo.setMaisRelevante(resultSet.getBoolean("mais_relevante"));
                veiculo.setPaywall(resultSet.getBoolean("paywall"));

                veiculosAtivos.add(veiculo);
            }
        }

        return veiculosAtivos;
    }

    public List<Veiculo> buscarVeiculosRelevantesAtivos(DbConnection dbconn) throws SQLException {
        List<Veiculo> veiculosAtivos = new ArrayList<>();

        String sql = "SELECT * FROM veiculos WHERE sit_cad = 'A' and mais_relevante == true and url_veiculo is not null and url_veiculo like('%.%')";

        try (Statement statement = dbconn.obterStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(resultSet.getLong("id"));
                veiculo.setTipoVeiculoId(resultSet.getLong("tipo_veiculo_id"));
                veiculo.setDescricao(resultSet.getString("descricao"));
                veiculo.setPaisId(resultSet.getLong("pais_id"));
                veiculo.setEstadoId(resultSet.getLong("estado_id"));
                veiculo.setCidadeId(resultSet.getLong("cidade_id"));
                veiculo.setDatCad(resultSet.getTimestamp("dat_cad"));
                veiculo.setSitCad(resultSet.getString("sit_cad"));
                veiculo.setUsuCad(resultSet.getLong("usu_cad"));
                veiculo.setIcoVeiculo(resultSet.getBytes("ico_veiculo"));
                veiculo.setUrlIconeVeiculo(resultSet.getString("url_icone_veiculo"));
                veiculo.setUrlVeiculo(resultSet.getString("url_veiculo"));
                veiculo.setMaisRelevante(resultSet.getBoolean("mais_relevante"));
                veiculo.setPaywall(resultSet.getBoolean("paywall"));

                veiculosAtivos.add(veiculo);
            }
        }

        return veiculosAtivos;
    }

}
