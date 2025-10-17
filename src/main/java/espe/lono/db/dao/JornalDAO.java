package espe.lono.db.dao;

import espe.lono.db.connections.*;
import espe.lono.db.models.*;
import espe.lono.db.utils.DBUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Espe
 */
public class JornalDAO {
   
    private String sql = "";
    
    public Jornal obterDadosDoJornal(int id, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        sql = "SELECT j.id_jornal j_id_jornal, o.sigla j_sigla_orgao," +
                "       o.nome j_orgao_jornal, j.sigla_jornal j_sigla_jornal," +
                "       j.desc_jornal j_desc_jornal, j.url_jornal j_url_jornal," +
                "       j.dt_cad j_dt_cad, j.sit_cad j_sit_cad, j.usu_cad j_usu_cad," +
                "       j.limite_linhas_materia limite_linhas_materia, j.edicao_detectavel, j.nome_jornal as j_nome_jornal" +
                " FROM jornal j, orgao o WHERE j.id_jornal = " + id + " AND o.id_orgao = j.id_orgao";

        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        if ( !resultado.next() ) return null; // Not located
        
        // Preenchendo classe Jornal com os dados obtido, e retornando-a
        Jornal jornal = new Jornal();
        jornal.setIdJornal(resultado.getInt("j_id_jornal"));
        jornal.setOrgaoJornal(resultado.getString("j_orgao_jornal"));
        jornal.setNomeJornal(resultado.getString("j_nome_jornal"));
        jornal.setSiglaJornal(resultado.getString("j_sigla_jornal"));
        jornal.setDescJornal(resultado.getString("j_desc_jornal"));
        jornal.setUrlJornal(resultado.getString("j_url_jornal"));
        jornal.setDtCad(resultado.getDate("j_dt_cad"));
        jornal.setSitCad(resultado.getString("j_sit_cad"));
        jornal.setUsuCad(resultado.getInt("j_usu_cad"));
        jornal.setSiglaOrgao(resultado.getString("j_sigla_orgao"));
        jornal.setLimiteLinhaMateria(resultado.getInt("limite_linhas_materia"));
        jornal.setEdicaoDetectavel(resultado.getBoolean("edicao_detectavel"));
        
        resultado.close();
        stm.close();
        
        return jornal;
    }

    public Jornal obterDadosDoJornal(String sigla_jornal, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        sql = "SELECT j.id_jornal j_id_jornal, o.sigla j_sigla_orgao," +
                "       o.nome j_orgao_jornal, j.sigla_jornal j_sigla_jornal," +
                "       j.desc_jornal j_desc_jornal, j.url_jornal j_url_jornal," +
                "       j.dt_cad j_dt_cad, j.sit_cad j_sit_cad, j.usu_cad j_usu_cad," +
                "       j.limite_linhas_materia limite_linhas_materia, j.edicao_detectavel, j.nome_jornal as j_nome_jornal" +
                " FROM jornal j, orgao o WHERE LOWER(j.sigla_jornal) = '" + sigla_jornal.toLowerCase() + "' AND o.id_orgao = j.id_orgao";

        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        if ( !resultado.next() ) return null; // Not located

        // Preenchendo classe Jornal com os dados obtido, e retornando-a
        Jornal jornal = new Jornal();
        jornal.setIdJornal(resultado.getInt("j_id_jornal"));
        jornal.setOrgaoJornal(resultado.getString("j_orgao_jornal"));
        jornal.setSiglaJornal(resultado.getString("j_sigla_jornal"));
        jornal.setNomeJornal(resultado.getString("j_nome_jornal"));
        jornal.setDescJornal(resultado.getString("j_desc_jornal"));
        jornal.setUrlJornal(resultado.getString("j_url_jornal"));
        jornal.setDtCad(resultado.getDate("j_dt_cad"));
        jornal.setSitCad(resultado.getString("j_sit_cad"));
        jornal.setUsuCad(resultado.getInt("j_usu_cad"));
        jornal.setSiglaOrgao(resultado.getString("j_sigla_orgao"));
        jornal.setLimiteLinhaMateria(resultado.getInt("limite_linhas_materia"));
        jornal.setEdicaoDetectavel(resultado.getBoolean("edicao_detectavel"));

        resultado.close();
        stm.close();

        return jornal;
    }
    
    public TipoPadraoJornal[] dadosListarTiposPadraoPublicacao(int idPadrao, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<TipoPadraoJornal> listaTiposPadrao = new ArrayList();
        sql = "SELECT pj.id_padrao pj_id_padrao, pj.id_jornal pj_id_jornal, pj.inicio_validade pj_inicio_validade, pj.fim_validade pj_fim_validade, pj.dat_cad pj_dat_cad, pj.sit_cad pj_sit_cad, pj.usu_cad pj_usu_cad, " +
              "      tp.id_tipo_padrao tp_id_tipo_padrao, tp.chave_tipo tp_chave_tipo, tp.nm_tipo tp_nm_tipo, tp.desc_tipo tp_desc_tipo, tp.dat_cad tp_dat_cad, tp.sit_cad tp_sit_cad, tp.usu_cad tp_usu_cad, " +
              "      tpj.id_tipo_padrao_jornal tpj_id_tipo_padrao_jornal, tpj.id_padrao tpj_id_padrao, tpj.id_tipo_padrao tpj_id_tipo_padrao, tpj.query_ini tpj_query_ini,  " +
              "      tpj.query_fim tpj_query_fim, tpj.regex_ini tpj_regex_ini, tpj.regex_fim tpj_regex_fim, tpj.trechos_replace tpj_trechos_replace, tpj.qtd_linha_ajuste_acima tpj_qtd_linha_ajuste_acima,  " +
              "      tpj.qtd_linha_ajuste_abaixo tpj_qtd_linha_ajuste_abaixo, tpj.acao tpj_acao, tpj.dat_cad tpj_dat_cad, tpj.sit_cad tpj_sit_cad, tpj.usu_cad tpj_usu_cad, tpj.map_font tpj_map_font, " +
              "      tpj.id_tipo_padrao_check tpj_id_tipo_padrao_check, tpj.qtd_max_result_query tpj_qtd_max_result_query, tpj.direcao tpj_direcao " +
              " FROM  " +
              "       padrao_jornal pj, " +
              "       tipo_padrao tp, " +
              "       tipo_padrao_jornal tpj " +
              " WHERE " +
              "       pj.id_padrao = tpj.id_padrao  " +
              "       and tpj.id_tipo_padrao = tp.id_tipo_padrao  " +
              "       and pj.id_padrao = " + idPadrao +
              "";
              //"       and pj.sit_cad = 'A'  " +
              //"       and tpj.sit_cad = 'A'";
        
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        
        TipoPadraoJornal tipoPadraoJornal = null;
        TipoPadrao tipoPadrao = null;
        
        while(resultado.next())
        {
            tipoPadraoJornal = new TipoPadraoJornal();
            tipoPadrao = new TipoPadrao();
            
            tipoPadraoJornal.setIdTipoPadraoJornal(resultado.getInt("tpj_id_tipo_padrao_jornal"));
            tipoPadraoJornal.setIdPadrao(resultado.getInt("tpj_id_padrao"));
            tipoPadraoJornal.setIdTipoPadrao(resultado.getInt("tpj_id_tipo_padrao"));
            tipoPadraoJornal.setQueryIni(resultado.getString("tpj_query_ini"));
            tipoPadraoJornal.setQueryFim(resultado.getString("tpj_query_fim"));
            tipoPadraoJornal.setRegexIni(resultado.getString("tpj_regex_ini"));
            tipoPadraoJornal.setRegexFim(resultado.getString("tpj_regex_fim"));
            tipoPadraoJornal.setTrechosReplace(resultado.getString("tpj_trechos_replace"));
            tipoPadraoJornal.setQtdLinhaAjusteAcima(resultado.getInt("tpj_qtd_linha_ajuste_acima"));
            tipoPadraoJornal.setQtdLinhaAjusteAbaixo(resultado.getInt("tpj_qtd_linha_ajuste_abaixo"));
            tipoPadraoJornal.setAcao(resultado.getString("tpj_acao"));
            tipoPadraoJornal.setDatCad(resultado.getDate("tpj_dat_cad"));
            tipoPadraoJornal.setSitCad(resultado.getString("tpj_sit_cad"));
            tipoPadraoJornal.setUsuCad(resultado.getInt("tpj_usu_cad"));
            tipoPadraoJornal.setMapeamentoFonte(resultado.getString("tpj_map_font"));
            tipoPadraoJornal.setIdTipoPadraoCheck(resultado.getInt("tpj_id_tipo_padrao_check"));
            tipoPadraoJornal.setQtdMaxResultQuery(resultado.getInt("tpj_qtd_max_result_query")); 
            tipoPadraoJornal.setDirecao(resultado.getString("tpj_direcao"));

            //TipoPadrao
            tipoPadrao.setIdTipoPadrao(resultado.getInt("tp_id_tipo_padrao"));
            tipoPadrao.setChaveTipo(resultado.getString("tp_chave_tipo"));
            tipoPadrao.setNmTipo(resultado.getString("tp_nm_tipo"));
            tipoPadrao.setDescTipo(resultado.getString("tp_desc_tipo"));
            tipoPadrao.setDatCad(resultado.getDate("tp_dat_cad"));
            tipoPadrao.setSitCad(resultado.getString("tp_sit_cad"));
            tipoPadrao.setUsuCad(resultado.getInt("tp_usu_cad"));

            // Fim - TipoPadrao
            tipoPadraoJornal.setTipoPadrao(tipoPadrao);
           
            listaTiposPadrao.add(tipoPadraoJornal);
        }
        
        stm.close();
        resultado.close();
        
        return listaTiposPadrao.toArray(new TipoPadraoJornal[0]);
    }
    
    public TipoPadraoJornal[] dadosListarTiposPadraoPublicacao(PublicacaoJornal pubJornal, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<TipoPadraoJornal> listaTiposPadrao = new ArrayList();
        sql = "SELECT pj.id_padrao pj_id_padrao, pj.id_jornal pj_id_jornal, pj.inicio_validade pj_inicio_validade, pj.fim_validade pj_fim_validade, pj.dat_cad pj_dat_cad, pj.sit_cad pj_sit_cad, pj.usu_cad pj_usu_cad, " +
              "      tp.id_tipo_padrao tp_id_tipo_padrao, tp.chave_tipo tp_chave_tipo, tp.nm_tipo tp_nm_tipo, tp.desc_tipo tp_desc_tipo, tp.dat_cad tp_dat_cad, tp.sit_cad tp_sit_cad, tp.usu_cad tp_usu_cad, " +
              "      tpj.id_tipo_padrao_jornal tpj_id_tipo_padrao_jornal, tpj.id_padrao tpj_id_padrao, tpj.id_tipo_padrao tpj_id_tipo_padrao, tpj.query_ini tpj_query_ini,  " +
              "      tpj.query_fim tpj_query_fim, tpj.regex_ini tpj_regex_ini, tpj.regex_fim tpj_regex_fim, tpj.trechos_replace tpj_trechos_replace, tpj.qtd_linha_ajuste_acima tpj_qtd_linha_ajuste_acima,  " +
              "      tpj.qtd_linha_ajuste_abaixo tpj_qtd_linha_ajuste_abaixo, tpj.acao tpj_acao, tpj.dat_cad tpj_dat_cad, tpj.sit_cad tpj_sit_cad, tpj.usu_cad tpj_usu_cad, tpj.map_font tpj_map_font, " +
              "      tpj.id_tipo_padrao_check tpj_id_tipo_padrao_check, tpj.qtd_max_result_query tpj_qtd_max_result_query, tpj.direcao tpj_direcao, tpj.complex_mode tpj_complex_mode " +
              " FROM  " +
              "       padrao_jornal pj, " +
              "       tipo_padrao tp, " +
              "       tipo_padrao_jornal tpj " +
              " WHERE " +
              "       pj.id_padrao = tpj.id_padrao  " +
              "       and tpj.id_tipo_padrao = tp.id_tipo_padrao  " +
              "       and pj.id_jornal = " + pubJornal.getIdJornal() +
              "       and pj.sit_cad = 'A'  " +
              "       and tpj.sit_cad = 'A'  " +
              "       and to_date('" + DBUtils.FormatarData(pubJornal.getDtPublicacao(), "dd-MM-yyyy") + "','dd/mm/yyyy') > pj.inicio_validade " +
              "       and (to_date('" + DBUtils.FormatarData(pubJornal.getDtPublicacao(), "dd-MM-yyyy") + "','dd/mm/yyyy') < pj.fim_validade or pj.fim_validade is null)";
        
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        
        TipoPadraoJornal tipoPadraoJornal = null;
        TipoPadrao tipoPadrao = null;
        
        while(resultado.next())
        {
            tipoPadraoJornal = new TipoPadraoJornal();
            tipoPadraoJornal.setIdTipoPadraoJornal(resultado.getInt("tpj_id_tipo_padrao_jornal"));
            tipoPadraoJornal.setIdPadrao(resultado.getInt("tpj_id_padrao"));
            tipoPadraoJornal.setIdTipoPadrao(resultado.getInt("tpj_id_tipo_padrao"));
            tipoPadraoJornal.setQueryIni(resultado.getString("tpj_query_ini"));
            tipoPadraoJornal.setQueryFim(resultado.getString("tpj_query_fim"));
            tipoPadraoJornal.setRegexIni(resultado.getString("tpj_regex_ini"));
            tipoPadraoJornal.setRegexFim(resultado.getString("tpj_regex_fim"));
            tipoPadraoJornal.setTrechosReplace(resultado.getString("tpj_trechos_replace"));
            tipoPadraoJornal.setQtdLinhaAjusteAcima(resultado.getInt("tpj_qtd_linha_ajuste_acima"));
            tipoPadraoJornal.setQtdLinhaAjusteAbaixo(resultado.getInt("tpj_qtd_linha_ajuste_abaixo"));
            tipoPadraoJornal.setAcao(resultado.getString("tpj_acao"));
            tipoPadraoJornal.setDatCad(resultado.getDate("tpj_dat_cad"));
            tipoPadraoJornal.setSitCad(resultado.getString("tpj_sit_cad"));
            tipoPadraoJornal.setUsuCad(resultado.getInt("tpj_usu_cad"));
            tipoPadraoJornal.setMapeamentoFonte(resultado.getString("tpj_map_font"));
            tipoPadraoJornal.setIdTipoPadraoCheck(resultado.getInt("tpj_id_tipo_padrao_check"));
            tipoPadraoJornal.setQtdMaxResultQuery(resultado.getInt("tpj_qtd_max_result_query")); 
            tipoPadraoJornal.setDirecao(resultado.getString("tpj_direcao"));
            tipoPadraoJornal.setComplex_mode(resultado.getBoolean("tpj_complex_mode"));

            //TipoPadrao
            tipoPadrao = new TipoPadrao();
            tipoPadrao.setIdTipoPadrao(resultado.getInt("tp_id_tipo_padrao"));
            tipoPadrao.setChaveTipo(resultado.getString("tp_chave_tipo"));
            tipoPadrao.setNmTipo(resultado.getString("tp_nm_tipo"));
            tipoPadrao.setDescTipo(resultado.getString("tp_desc_tipo"));
            tipoPadrao.setDatCad(resultado.getDate("tp_dat_cad"));
            tipoPadrao.setSitCad(resultado.getString("tp_sit_cad"));
            tipoPadrao.setUsuCad(resultado.getInt("tp_usu_cad"));

            // Fim - TipoPadrao
            tipoPadraoJornal.setTipoPadrao(tipoPadrao);
           
            listaTiposPadrao.add(tipoPadraoJornal);
        }
        
        stm.close();
        resultado.close();
        
        return listaTiposPadrao.toArray(new TipoPadraoJornal[0]);
    }   
    
    public String dadosListarFontesTipoPadraoPublicacao(int idPublicacao, String mapFonte, DbConnectionMarcacao sqlite) throws SQLException
    {
        if (mapFonte == null || mapFonte.equals("")) {
            return "";
        }

        // Suporte ao formato múltiplo: (estilo1 OR estilo2 OR ...)
        if (mapFonte.startsWith("(") && mapFonte.endsWith(")") && mapFonte.contains(" OR ")) {
            String fontesSemParenteses = mapFonte.substring(1, mapFonte.length() - 1);
            String[] estilos = fontesSemParenteses.split(" OR ");
            for (String estilo : estilos) {
                String resultado = buscarFonte(idPublicacao, estilo.trim(), sqlite);
                if (!resultado.isEmpty()) {
                    return resultado;
                }
            }
            return "";
        } else {
            // Formato simples
            return buscarFonte(idPublicacao, mapFonte, sqlite);
        }
    }

    // Novo método auxiliar para evitar duplicação de código
    private String buscarFonte(int idPublicacao, String fonte, DbConnectionMarcacao sqlite) throws SQLException {
        StringBuilder fontes = new StringBuilder();
        final String table_name = sqlite.obterNomeTabela();
        String sql = " SELECT  marcacao " +
                " FROM " + table_name + " " +
                " WHERE id_tipo_padrao = 14 " + /*Fonte css*/
                " AND marcacao like '%" + fonte + "%'" +
                " ORDER BY num_doc_lucene";

        ResultSet resultado = sqlite.abrirConsultaSql(sql);
        while (resultado.next()) {
            fontes.append((resultado.getString("marcacao").split("\\{"))[0].substring(2) + " ");
        }
        resultado.close();
        return fontes.toString();
    }
    
    public String dadosListarMapaFontesTipoPadraoPublicacao(int idPublicacao, String mapFonte, DbConnectionMarcacao sqlite) throws SQLException
    {
        StringBuilder fontes = new StringBuilder();
        if ( mapFonte == null || mapFonte.equals("") )
        {
            return "";
        }
        
        final String table_name = sqlite.obterNomeTabela();
        sql = " SELECT  marcacao " +
              " FROM " + table_name + " " +
              " WHERE id_tipo_padrao = 14 " + /*Fonte css*/
              " AND marcacao like '%" + mapFonte + "%'" +
              " ORDER BY num_doc_lucene";

        ResultSet resultado = sqlite.abrirConsultaSql(sql);        
        if ( resultado.next() )
        {
            String buff = resultado.getString("marcacao").split("\\{")[1];
            fontes.append( buff.split("\\}")[0] );
        }
        resultado.close();
        return fontes.toString();
    }
    
    public boolean checarMarcacao(String textoCheck, int idTipoPadraoCheck, int idPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        boolean retornoCheck = false;
        
        final String table_name = sqlite.obterNomeTabela();
        sql = " SELECT  marcacao " +
              " FROM " + table_name + " " +
              " WHERE id_tipo_padrao = " + idTipoPadraoCheck + 
              " AND marcacao like '%" + textoCheck + "%'" +
              " ORDER BY num_doc_lucene";

        ResultSet resultado = sqlite.abrirConsultaSql(sql);
        retornoCheck = resultado.next();
        resultado.close();
        return retornoCheck;
    }
    
    public Jornal[] obterListaJornais(DbConnection dbconn) throws SQLException
    {
        sql = "SELECT j.id_jornal j_id_jornal, o.sigla j_sigla_orgao," +
                "       o.nome j_orgao_jornal, j.sigla_jornal j_sigla_jornal," +
                "       j.desc_jornal j_desc_jornal, j.url_jornal j_url_jornal," +
                "       j.dt_cad j_dt_cad, j.sit_cad j_sit_cad, j.usu_cad j_usu_cad," +
                "       j.limite_linhas_materia limite_linhas_materia, j.edicao_detectavel" +
                " FROM jornal j, orgao o" +
                " WHERE o.id_orgao = j.id_orgao" +
                " ORDER BY j_sigla_jornal ASC";

        final List<Jornal> listJornais = new ArrayList();
        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        
        while (resultado.next()) {
            // Preenchendo classe Jornal com os dados obtido, e retornando-a
            Jornal jornal = new Jornal();
            jornal.setIdJornal(resultado.getInt("j_id_jornal"));
            jornal.setOrgaoJornal(resultado.getString("j_orgao_jornal"));
            jornal.setSiglaJornal(resultado.getString("j_sigla_jornal"));
            jornal.setDescJornal(resultado.getString("j_desc_jornal"));
            jornal.setUrlJornal(resultado.getString("j_url_jornal"));
            jornal.setDtCad(resultado.getDate("j_dt_cad"));
            jornal.setSitCad(resultado.getString("j_sit_cad"));
            jornal.setUsuCad(resultado.getInt("j_usu_cad"));
            jornal.setSiglaOrgao(resultado.getString("j_sigla_orgao"));
            jornal.setLimiteLinhaMateria(resultado.getInt("limite_linhas_materia"));
            jornal.setEdicaoDetectavel(resultado.getBoolean("edicao_detectavel"));
            
            listJornais.add(jornal);
        }
        
        resultado.close();
        stm.close();
        
        return listJornais.toArray(new Jornal[listJornais.size()]);
    }
    
    public PadraoJornal[] obterListaPadraoJornal(int idJornal, DbConnection dbconn) throws SQLException
    {
        sql = "SELECT * FROM padrao_jornal WHERE id_jornal=" + idJornal;
        
        final List<PadraoJornal> listPadraoJornal = new ArrayList();
        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        while ( resultado.next() ) {
            PadraoJornal padraoJornal = new PadraoJornal();
            padraoJornal.setIdPadrao(resultado.getInt("id_padrao"));
            padraoJornal.setIdJornal(resultado.getInt("id_jornal"));
            padraoJornal.setInicioValidade(resultado.getDate("inicio_validade"));
            padraoJornal.setFimValidade(resultado.getDate("fim_validade"));
            padraoJornal.setDatCad(resultado.getDate("dat_cad"));
            padraoJornal.setSitCad(resultado.getString("sit_cad"));
            padraoJornal.setUsuCad(resultado.getInt("usu_cad"));
            
            listPadraoJornal.add(padraoJornal);
        }
        
        resultado.close();
        stm.close();
        
        return listPadraoJornal.toArray( new PadraoJornal[listPadraoJornal.size()] );
    }
    
    public TipoPadrao[] listarTiposPadrao(DbConnection dbconn) throws SQLException
    {
        List<TipoPadrao> listTipoPadrao = new ArrayList();
        sql = "SELECT tp.id_tipo_padrao tp_id_tipo_padrao, tp.chave_tipo tp_chave_tipo, tp.nm_tipo tp_nm_tipo, tp.desc_tipo tp_desc_tipo, tp.dat_cad tp_dat_cad, tp.sit_cad tp_sit_cad, tp.usu_cad tp_usu_cad " +
              " FROM  " +
              "       tipo_padrao tp " +
              " WHERE " +
              "       tp.sit_cad = 'A'  ";
        
        final List<PadraoJornal> listPadraoJornal = new ArrayList();
        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        while ( resultado.next() ) {
            TipoPadrao tipoPadrao = new TipoPadrao();
            tipoPadrao.setIdTipoPadrao(resultado.getInt("tp_id_tipo_padrao"));
            tipoPadrao.setChaveTipo(resultado.getString("tp_chave_tipo"));
            tipoPadrao.setNmTipo(resultado.getString("tp_nm_tipo"));
            tipoPadrao.setDescTipo(resultado.getString("tp_desc_tipo"));
            tipoPadrao.setDatCad(resultado.getDate("tp_dat_cad"));
            tipoPadrao.setSitCad(resultado.getString("tp_sit_cad"));
            tipoPadrao.setUsuCad(resultado.getInt("tp_usu_cad"));
            
           listTipoPadrao.add(tipoPadrao);
        }
        
        resultado.close();
        stm.close();
        
        return listTipoPadrao.toArray(new TipoPadrao[listTipoPadrao.size()]);
    }
    
    public boolean adicionarTipoPadraoJornal(TipoPadraoJornal tipoPadraoJornal, DbConnection dbconn) throws SQLException
    {
        sql = "INSERT INTO tipo_padrao_jornal ("
                + "id_padrao, "
                + "id_tipo_padrao, "
                + "query_ini, "
                + "query_fim, "
                + "regex_ini, "
                + "regex_fim, "
                + "trechos_replace, "
                + "qtd_linha_ajuste_acima, "
                + "qtd_linha_ajuste_abaixo, "
                + "acao, "
                + "dat_cad, "
                + "sit_cad, "
                + "usu_cad, "
                + "map_font, "
                + "id_tipo_padrao_check, "
                + "qtd_max_result_query, "
                + "direcao ) VALUES(?,?,?,?,?,?,?,?,?, '" + tipoPadraoJornal.getAcao() + "',NOW(),'" + tipoPadraoJornal.getSitCad() + "',?,?,?,?,?)";
        final PreparedStatement pm = dbconn.obterPreparedStatement(sql);
        pm.setInt(1, tipoPadraoJornal.getIdPadrao());
        pm.setInt(2, tipoPadraoJornal.getTipoPadrao().getIdTipoPadrao());
        pm.setString(3, tipoPadraoJornal.getQueryIni());
        pm.setString(4, tipoPadraoJornal.getQueryFim());
        pm.setString(5, tipoPadraoJornal.getRegexIni());
        pm.setString(6, tipoPadraoJornal.getRegexFim());
        pm.setString(7, tipoPadraoJornal.getTrechosReplace());
        pm.setInt(8, tipoPadraoJornal.getQtdLinhaAjusteAcima());
        pm.setInt(9, tipoPadraoJornal.getQtdLinhaAjusteAbaixo());
        //pm.setString(10, tipoPadraoJornal.getAcao());
        //pm.setString(10, tipoPadraoJornal.getSitCad());
        pm.setInt(10, 1);
        pm.setString(11, tipoPadraoJornal.getMapeamentoFonte());
        pm.setInt(12, tipoPadraoJornal.getIdTipoPadraoCheck());
        pm.setInt(13, tipoPadraoJornal.getQtdMaxResultQuery());
        pm.setString(14, tipoPadraoJornal.getDirecao());
        
        boolean resultado = dbconn.executarSql(pm);
        pm.close();
        return resultado;
    }
    
    public boolean atualizarTipoPadraoJornal(TipoPadraoJornal tipoPadraoJornal, DbConnection dbconn) throws SQLException
    {
        sql = "UPDATE tipo_padrao_jornal SET "
                + "id_padrao = ?, "
                + "id_tipo_padrao = ?, "
                + "query_ini = ?, "
                + "query_fim = ?, "
                + "regex_ini = ?, "
                + "regex_fim = ?, "
                + "trechos_replace = ?, "
                + "qtd_linha_ajuste_acima = ?, "
                + "qtd_linha_ajuste_abaixo = ?, "
                + "acao = '" + tipoPadraoJornal.getAcao() +"', "
                + "sit_cad = '" + tipoPadraoJornal.getSitCad() + "', "
                + "map_font = ?, "
                + "id_tipo_padrao_check = ?, "
                + "qtd_max_result_query = ?, "
                + "direcao = ? "
                + "WHERE id_tipo_padrao_jornal = " + tipoPadraoJornal.getIdTipoPadraoJornal();
        
        final PreparedStatement pm = dbconn.obterPreparedStatement(sql);
        pm.setInt(1, tipoPadraoJornal.getIdPadrao());
        pm.setInt(2, tipoPadraoJornal.getIdTipoPadrao());
        pm.setString(3, tipoPadraoJornal.getQueryIni());
        pm.setString(4, tipoPadraoJornal.getQueryFim());
        pm.setString(5, tipoPadraoJornal.getRegexIni());
        pm.setString(6, tipoPadraoJornal.getRegexFim());
        pm.setString(7, tipoPadraoJornal.getTrechosReplace());
        pm.setInt(8, tipoPadraoJornal.getQtdLinhaAjusteAcima());
        pm.setInt(9, tipoPadraoJornal.getQtdLinhaAjusteAbaixo());
        //pm.setString(10, tipoPadraoJornal.getAcao());
        //pm.setString(10, tipoPadraoJornal.getSitCad());
        pm.setString(10, tipoPadraoJornal.getMapeamentoFonte());
        pm.setInt(11, tipoPadraoJornal.getIdTipoPadraoCheck());
        pm.setInt(12, tipoPadraoJornal.getQtdMaxResultQuery());
        pm.setString(13, tipoPadraoJornal.getDirecao());
        
        boolean resultado = dbconn.executarSql(pm);
        pm.close();
        return resultado;
    }
    
    public boolean adicionarPadraoJornal(PadraoJornal padraoJornal, DbConnection dbconn) throws SQLException
    {
        sql = "INSRT INTO padrao_jornal ("
                + "id_jornal, "
                + "inicio_validade, "
                + "fim_validade, "
                + "dat_cad, "
                + "sit_cad, "
                + "usu_cad ) VALUES (?,?,?,NOW(),?,?)";
        final PreparedStatement pm = dbconn.obterPreparedStatement(sql);
        pm.setInt(1, padraoJornal.getIdJornal());
        pm.setDate(2, new java.sql.Date(padraoJornal.getInicioValidade().getTime()));
        pm.setDate(3, new java.sql.Date(padraoJornal.getFimValidade().getTime()));
        pm.setString(4, padraoJornal.getSitCad());
        pm.setInt(5, 1);
        
        boolean resultado = dbconn.executarSql(pm);
        pm.close();
        return resultado;
    }
    
    public boolean atualizarPadraoJornal(PadraoJornal padraoJornal, DbConnection dbconn) throws SQLException
    {
        sql = "UPDATE padrao_jornal SET "
                + "id_jornal = ?, "
                + "inicio_validade = ?, "
                + "fim_validade = ?, "
                + "sit_cad = ?, "
                + "usu_cad = ? ) "
                + "WHERE id_padrao = " + padraoJornal.getIdPadrao();
        
        final PreparedStatement pm = dbconn.obterPreparedStatement(sql);
        pm.setInt(1, padraoJornal.getIdJornal());
        pm.setDate(2, new java.sql.Date(padraoJornal.getInicioValidade().getTime()));
        pm.setDate(3, new java.sql.Date(padraoJornal.getFimValidade().getTime()));
        pm.setString(4, padraoJornal.getSitCad());
        pm.setInt(5, padraoJornal.getUsuCad());
        
        boolean resultado = dbconn.executarSql(pm);
        pm.close();
        return resultado;
    }

    public void atualizarJornalLastProc(int idJornal, DbConnection dbconn) throws SQLException
    {
        dbconn.executarSql("UPDATE jornal SET last_proc = NOW() WHERE id_jornal = " + idJornal);
    }
}
