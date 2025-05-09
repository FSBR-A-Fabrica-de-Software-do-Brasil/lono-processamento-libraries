package espe.lono.db.dao;

import espe.lono.db.backend.BackendDBActions;
import espe.lono.db.negocios.JornalNegocio;
import espe.lono.db.connections.*;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;
import espe.lono.db.models.*;

/**
 *
 * @author Espe
 */
public class PublicacaoDAO
{
    public Integer[] dadosListarPublicacoesAntigas(int idJornal, int idPublicacaoToIgnore,  DbConnection dbconn) throws SQLException {
        List<Integer> idsPubList = new ArrayList<>();
        String sql = "SELECT id_publicacao FROM publicacao_jornal WHERE id_jornal = " + idJornal + " AND id_publicacao != " + idPublicacaoToIgnore + " AND sit_cad IN ('F','X')  ORDER BY id_publicacao ASC";
        ResultSet resultado = dbconn.abrirConsultaSql(sql);
        while (resultado.next()) {
            idsPubList.add(resultado.getInt("id_publicacao"));
        }
        resultado.close();
        return idsPubList.toArray(new Integer[0]);
    }

    public boolean ignorarNumeroProcesso(int idCliente, String processo, DbConnection dbconn) throws SQLException
    {
        if ( processo == null || processo.length() <= 0)
            return false;

        final String numeroProcesso = processo.replaceAll("[^\\d]", "");
        final Statement stm = dbconn.obterStatement();
        final String sql ="SELECT ignorar_materia_cliente(" + idCliente + ", '" + numeroProcesso + "') as response";
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        resultado.next();
        int response = resultado.getInt("response");

        resultado.close();
        stm.close();
        return (response == 1);
    }

    public boolean execVerificaColisaoMD5(int idJornal, String md5, DbConnection dbconn) throws SQLException {
        String sql = "SELECT COUNT(id_publicacao) FROM publicacao_jornal WHERE id_jornal = " + idJornal + " AND hash = '" + md5 + "'";
        ResultSet resultSet = dbconn.abrirConsultaSql(sql);
        resultSet.next();
        boolean exists = resultSet.getBoolean(1);
        resultSet.close();

        return exists;
    }

    public PublicacaoJornal dadosListarPublicacoesPorID(int id, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        PublicacaoJornal pubJornal = null;
        final String sql = " SELECT id_publicacao, id_jornal, arq_publicacao, total_pagina, dt_publicacao, "
                + " dt_divulgacao, edicao_publicacao, dat_cad, sit_cad, usu_cad "
                + " FROM publicacao_jornal "
                + " WHERE id_publicacao = '" + id + "'";
        
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        JornalNegocio jornalNegocio = new JornalNegocio();
        
        if ( resultado.next() )
        {
            pubJornal = new PublicacaoJornal();
            Jornal jornal = jornalNegocio.obterDadosJornalPorID(resultado.getInt("id_jornal"), dbconn);

            pubJornal.setIdPublicacao(resultado.getInt("id_publicacao"));
            pubJornal.setIdJornal(resultado.getInt("id_jornal"));
            pubJornal.setArqPublicacao(resultado.getString("arq_publicacao").trim());
            pubJornal.setTotalPagina(resultado.getInt("total_pagina"));
            pubJornal.setDtPublicacao(resultado.getDate("dt_publicacao"));
            pubJornal.setDtDivulgacao(resultado.getDate("dt_divulgacao"));
            pubJornal.setEdicaoPublicacao(resultado.getString("edicao_publicacao"));
            pubJornal.setDatCad(resultado.getDate("dat_cad"));
            pubJornal.setSitCad(resultado.getString("sit_cad"));
            pubJornal.setUsuCad(resultado.getInt("usu_cad"));
            pubJornal.setJornalPublicacao(jornal);
        }

        resultado.close();
        stm.close();
        return pubJornal;
    }

    public PublicacaoJornal obterPublicacaoRecenteClienteJornal(int idJornal, int idCliente, int idNomePesquisa, DbConnection dbConn) throws SQLException
    {
        final Statement stm = dbConn.obterStatement();
        String sqlcmd = "SELECT pj.id_publicacao FROM publicacao_jornal AS pj " +
                "INNER JOIN materia_cliente AS cm ON cm.id_publicacao = pj.id_publicacao " +
                "WHERE cm.id_cliente = " + idCliente + " AND pj.id_jornal = " + idJornal + " AND cm.id_nome_pesquisa = " + idNomePesquisa + " " +
                "ORDER BY pj.dt_publicacao DESC LIMIT 1";

        ResultSet resultado = dbConn.abrirConsultaSql(stm, sqlcmd);
        final int idPublicacao = (resultado.next()) ? resultado.getInt("id_publicacao") : 0;
        resultado.close();
        stm.close();

        if ( idPublicacao == 0) return null;
        else return dadosListarPublicacoesPorID(idPublicacao, dbConn);
    }
    
    private int obterIdPublicacaoAnteriorJornal(int idJornal, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        String sql = " SELECT id_publicacao "
                + " FROM publicacao_jornal "
                + " WHERE id_jornal = '" + idJornal + "'"
                + " AND sit_cad NOT IN ('X','M')"
                + " ORDER BY id_publicacao DESC LIMIT 1 OFFSET 1";
        
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        JornalNegocio jornalNegocio = new JornalNegocio();
        final int idPublicacao = (resultado.next()) ? resultado.getInt("id_publicacao") : 0;
        
        resultado.close();
        stm.close();
        return idPublicacao;
    }
    
    public PublicacaoJornal[] dadosListarPublicacoesPorSituacao(char situacao, DbConnection dbconn, int limit) throws SQLException
    {
        ArrayList<PublicacaoJornal> listaPublicacoes = new ArrayList();
        final Statement stm = dbconn.obterStatement();
        String sql = "SELECT id_publicacao, id_jornal, arq_publicacao, total_pagina, dt_publicacao, "
                + " dt_divulgacao, edicao_publicacao, dat_cad, sit_cad, usu_cad "
                + " FROM publicacao_jornal "
                + " WHERE sit_cad = '" + situacao + "' "
                + " ORDER BY total_pagina ASC";
        if ( limit > 0 ) sql += " LIMIT " + limit; // Adicionando limite
        
//        logger.debug(sql);
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        JornalNegocio jornalNegocio = new JornalNegocio();
        
        while (resultado.next()) 
        {
            PublicacaoJornal pubJornal = new PublicacaoJornal();
            Jornal jornal = jornalNegocio.obterDadosJornalPorID(resultado.getInt("id_jornal"), dbconn);

            pubJornal.setIdPublicacao(resultado.getInt("id_publicacao"));
            pubJornal.setIdJornal(resultado.getInt("id_jornal"));
            pubJornal.setArqPublicacao(resultado.getString("arq_publicacao").trim());
            pubJornal.setTotalPagina(resultado.getInt("total_pagina"));
            pubJornal.setDtPublicacao(resultado.getDate("dt_publicacao"));
            pubJornal.setDtDivulgacao(resultado.getDate("dt_divulgacao"));
            pubJornal.setEdicaoPublicacao(resultado.getString("edicao_publicacao"));
            pubJornal.setDatCad(resultado.getDate("dat_cad"));
            pubJornal.setSitCad(resultado.getString("sit_cad"));
            pubJornal.setUsuCad(resultado.getInt("usu_cad"));
            pubJornal.setJornalPublicacao(jornal);
            pubJornal.setIdPublicacaoAnteriorJornal( this.obterIdPublicacaoAnteriorJornal(pubJornal.getIdJornal(), dbconn) );
            // Armazenando na lista
            listaPublicacoes.add(pubJornal);
        }

        resultado.close();
        stm.close();
        
        return listaPublicacoes.toArray(new PublicacaoJornal[0]);
    }

    public boolean execAlterarSituacaoPublicacao(int id_publicacao, char situacao, DbConnection dbconn) throws SQLException 
    {
        final String sql = "UPDATE publicacao_jornal SET sit_cad = '" + situacao + "' WHERE id_publicacao = " + id_publicacao;
        return dbconn.executarSql(sql);
    }

    public boolean execRemoverPublicacao(int id_publicacao, DbConnection dbconn) throws SQLException
    {
        final String sql = "DELETE FROM publicacao_jornal WHERE id_publicacao = " + id_publicacao;
        return dbconn.executarSql(sql);
    }

    public boolean execInserirPublicacaoJornal(int idJornal, String fileName, String dtPublicacao, String dtDivulgacao, String edicaoPublicacao, int totalPagina, String fileMd5, DbConnection dbconn) throws SQLException {
        final String sql = String.format("INSERT INTO publicacao_jornal (id_jornal, arq_publicacao, total_pagina, dt_publicacao, dt_divulgacao, edicao_publicacao, dat_cad, sit_cad, usu_cad, hash) VALUES " +
                "(%d, '%s', %d, '%s', '%s', '%s', now(), 'A', 1, '%s')", idJornal, fileName, totalPagina, dtPublicacao, dtDivulgacao, edicaoPublicacao, fileMd5);
        return dbconn.executarSql(sql);
    }
    
    public boolean execAlterarSituacaoPublicacaoReprocessamento(int id_reprocessamento, char situacao, DbConnection dbconn) throws SQLException 
    {
        final String sql = "UPDATE reprocessar_cliente_publicacao "
                + "SET sit_cad = '" + situacao + "', proc_dat = NOW() "
                + " WHERE id_reprocessamento = " + id_reprocessamento;
        return dbconn.executarSql(sql);
    }
    
    public Integer[] listarPublicacoesAguardandoReprocessamento(DbConnection dbconn, int limit) throws SQLException 
    {
        final Statement stm = dbconn.obterStatement();
        final String sql = "SELECT DISTINCT(rcp.id_publicacao) "
                + "FROM reprocessar_cliente_publicacao AS rcp "
                + "INNER JOIN publicacao_jornal AS pj ON pj.id_publicacao = rcp.id_publicacao "
                + "WHERE rcp.sit_cad = 'A' AND pj.sit_cad IN ('F','M') "
                + "ORDER BY id_publicacao ASC "
                + ((limit > 0) ? ("LIMIT " + limit) : "");
        
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        List<Integer> idsPubList = new ArrayList<>();
        while (resultado.next()) 
        {
            idsPubList.add( resultado.getInt("id_publicacao") );
        }
        
        resultado.close();
        stm.close();
        
        return idsPubList.toArray( new Integer[0] );
    }
    
    public ReprocessarClientePublicacao[] listaReprocessarClientePublicacaoPorPublicacao(DbConnection dbconn, int idPublicacao, String sitcad) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        final String sql = "SELECT * "
                + "FROM reprocessar_cliente_publicacao AS rcp "
                + "INNER JOIN publicacao_jornal as pj ON pj.id_publicacao = rcp.id_publicacao "
                + "WHERE rcp.id_publicacao = '" + idPublicacao + "' AND pj.sit_cad IN ('F','M') "
                + (( sitcad != null && sitcad.length() > 0 ) ? ("AND rcp.sit_cad = '" + sitcad + "'") : "");
        
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        List<ReprocessarClientePublicacao> rePubList = new ArrayList<>();
        while (resultado.next()) 
        {
            ReprocessarClientePublicacao rcp = new ReprocessarClientePublicacao();
            rcp.setIdCliente(resultado.getInt("id_cliente"));
            rcp.setIdPublicacao(resultado.getInt("id_publicacao"));
            rcp.setIdNomePesquisa(resultado.getInt("id_nome_pesquisa"));
            rcp.setIdCliente(resultado.getInt("id_cliente"));
            rcp.setUsuCad(resultado.getInt("usu_cad"));
            rcp.setIdReprocessamento(resultado.getInt("id_reprocessamento"));
            rcp.setSitCad(resultado.getString("sit_cad"));
            
            rePubList.add(rcp);
        }
        
        resultado.close();
        stm.close();
        
        return rePubList.toArray(new ReprocessarClientePublicacao[0]);
    }

    public Long[] listarMarcacoesPorTipoPadrao(int tipo_padrao, DbConnectionMarcacao sqlite, DbConnection dbconn) throws SQLException
    {
        final String table_name = sqlite.obterNomeTabela();
        final String sql = "SELECT num_doc_lucene, id_tipo_padrao "
                + "FROM " + table_name + " "
                + "WHERE id_tipo_padrao=" + tipo_padrao + " "
                + "ORDER BY num_doc_lucene ASC";
        
        final ResultSet result = sqlite.abrirConsultaSql(sql);
        List<Long> marcList = new ArrayList<>();
        while ( result.next() )
        {
            marcList.add( result.getLong("num_doc_lucene"));
        }
        
        result.close();
        return marcList.toArray( new Long[0] );
        
    }
    
    public Long[][] listarMarcacoesComplexas(DbConnectionMarcacao sqlite) throws SQLException
    {
        final String table_name = sqlite.obterNomeTabela();
        final String sql = "SELECT num_doc_lucene, id_tipo_padrao "
                + "FROM " + table_name + " "
                + "WHERE complex = TRUE "
                + "ORDER BY num_doc_lucene ASC";
        
        final ResultSet result = sqlite.abrirConsultaSql(sql);
        List<Integer> marcList = new ArrayList<>();
        while ( result.next() )
        {
            Long[] data = new Long[2];
            data[0] = result.getLong("num_doc_lucene");
            data[1] = result.getLong("id_tipo_padrao");
        }
        
        result.close();
        return marcList.toArray( new Long[0][] );
    }
    
    public Long[][] listarMarcacoesPorTipoPadraos(String tipo_padraos, DbConnectionMarcacao sqlite) throws SQLException
    {
        final String table_name = sqlite.obterNomeTabela();
        final String sql = "SELECT DISTINCT(num_doc_lucene), id_tipo_padrao "
                + "FROM " + table_name + " "
                + "WHERE id_tipo_padrao IN (" + tipo_padraos + ") "
                + "ORDER BY num_doc_lucene ASC";
        
        final ResultSet result = sqlite.abrirConsultaSql(sql);
        List<Long[]> marcList = new ArrayList<>();
        while ( result.next() )
        {
            Long[] data = new Long[2];
            data[0] = result.getLong("num_doc_lucene");
            data[1] = result.getLong("id_tipo_padrao");
            
            marcList.add( data );
        }
        
        return marcList.toArray( new Long[0][] );
        
    }
    
    public boolean execIncluirMarcacaoPublicacao(MarcacaoPublicacao marcacaoPub, DbConnectionMarcacao sqlite) throws SQLException
    {
        final String table_name = sqlite.obterNomeTabela();
        final String sql2 = "INSERT INTO " + table_name + "( "
                + "  num_doc_lucene,  "
                + "  marcacao,  "
                + "  marcacao_original, "
                + "  linha_publicacao,  "
                + "  dat_cad,  "
                + "  sit_cad,  "
                + "  usu_cad,  "
                + "  id_tipo_padrao, "
                + "  pagina, " 
                + "  linha_pagina, "
                + "  id_tipo_padrao_jornal,"
                + "  complex) "
                + " VALUES (?,?,?,?," + sqlite.getNowSQLCommand() + ",?,?,?,?,?,?,?)";
        final PreparedStatement pm = sqlite.obterPreparedStatement(sql2);
        pm.setLong(1, marcacaoPub.getNumDocLucene());
        pm.setString(2, marcacaoPub.getMarcacao());
        pm.setString(3, marcacaoPub.getMarcacaoOriginal());
        pm.setInt(4, marcacaoPub.getLinhaPublicacao());
        pm.setString(5, "A");
        pm.setInt(6, 1);
        pm.setInt(7, marcacaoPub.getIdTipoPadrao());
        pm.setInt(8, marcacaoPub.getPagina());
        pm.setInt(9, marcacaoPub.getLinhaPagina());
        pm.setInt(10, marcacaoPub.getIdTipoPadraoJornal());
        pm.setBoolean(11, marcacaoPub.isComplex());
        
        boolean resultado = sqlite.executarSql(pm);
        pm.close();
        return resultado;
        //}
    }

    public boolean execIncluirPautaPublicacao(PautaPublicacao pautaPub, int id_materia, DbConnection dbconn) throws SQLException
    {
        final int pautaLength = (pautaPub.getPauta() != null) ? pautaPub.getPauta().trim().length() : 0;
        
        // Adicionando pauta no banco de dados
        // Nota; apenas se for uma nova pauta e se tiver texto de pauta...
        if ( pautaPub.getIdPauta() == 0 && pautaLength > 0 )
        {
            // Adicionando uma nova pauta o banco de dados
            String pauta_text = pautaPub.getPauta().replaceAll("'", "''");
            final String sql = "INSERT INTO pauta_publicacao("
                    + "pagina, "
                    + "id_publicacao,"
                    + "pauta_hash, "
                    + "pauta, "
                    + "dat_cad, "
                    + "usu_cad "
                    + ") VALUES( "
                    + pautaPub.getPagina() + ", "
                    + pautaPub.getIdPublicacao() + ", "
                    + "'" + pautaPub.getPautaHash()+ "', "
                    + "'" + pauta_text + "', "
                    + "NOW(), "
                    + pautaPub.getUsuCad()
                    + " )";
            
            int insert_id = dbconn.executeSqlLID(sql);
            if ( insert_id == 0 ) return false;
            else pautaPub.setIdPauta(insert_id);
        }
        
        // Adicionando referenca materia_pauta (apenas se existe texto de pauta)
        if ( pautaLength > 0 )
        {
            // Checando se ja existe referencia de pauta_materia
            final String sql = "INSERT INTO pauta_materia VALUES ("
                    + pautaPub.getIdPauta() + ", "
                    + id_materia + ", "
                    + pautaPub.getIdPublicacao() + ", "
                    + "'A'"
                    + ")";

            boolean resultado = dbconn.executarSql(sql);
            return resultado;
        }
        else
        {
            // Sem texto de pauta... retornando 'sucesso'
            return true;
        }
    }

    public boolean numProcessoIgnorado(String numProcesso, int idCliente, DbConnection dbconn) throws SQLException {
        final String sql = "select num_processo_ignorado('" + numProcesso + "', " + idCliente + ")";
        ResultSet results = dbconn.abrirConsultaSql(sql);
        boolean isBlocked = false;
        if ( results.next() ) isBlocked = results.getBoolean(1);
        results.close();
        return isBlocked;
    }

    public int execIncluirCorteMateriaPublicacao(PublicacaoJornal publicacaoJornal, NomePesquisaCliente nomePesquisaCliente,  MateriaPublicacao materiaPub, PautaPublicacao pautaPublicacao, DbConnection dbconn) throws SQLException
    {
        boolean ignorarMateria = this.ignorarNumeroProcesso(materiaPub.getIdCliente(),materiaPub.getProcesso(), dbconn);
        final String tabela_destino = (ignorarMateria) ? "materia_ignorada_cliente" : "materia_cliente";

        boolean materiaJaAdicionada = (materiaPub.getIdMateria() != null && materiaPub.getIdMateria() != 0);
        String sql;
        if ( materiaPub.getIdMateria() == null || materiaPub.getIdMateria() == 0 )
        {
            sql = "INSERT INTO materia_publicacao( "
                    + " id_publicacao,  "
                    + " titulo_materia, "
                    + " subtitulo,  "
                    + " pre_materia,  "
                    + " processo,  "
                    + " materia,  "
                    + " dat_cad,  "
                    + " sit_cad,  "
                    + " usu_cad,  "
                    + " corte_lono, "
                    + " corte_manual, "
                    + " materia_hash, "
                    + " historico "
                    + " ) VALUES ( "
                    + materiaPub.getIdPublicacao() + ", "
                    + "'" + materiaPub.getTituloMateria() + "', "
                    + "'" + materiaPub.getSubtituloMateria() + "', "
                    + "'" + materiaPub.getPreMateria() + "', "
                    + "'" + materiaPub.getProcesso() + "', "
                    + "'" + materiaPub.getMateria().replaceAll("'", "''") + "', "
                    + " now(), "
                    + "'" + materiaPub.getSitCad() + "', "
                    + materiaPub.getUsuCad() + ", "
                    + (materiaPub.getCorteLono() ? "'t'":"'f'") + ", 'f', "
                    + "'" + materiaPub.getMateriaHash() + "', "
                    + " " + (materiaPub.isHistorico() ? "true":"false") + " "
                    + "  )";

//            System.out.println("SQL -> " + sql);
            int insert_id = dbconn.executeSqlLID(sql);
            if ( insert_id == 0 ) throw new SQLException("Erro adding data inside database");
            else materiaPub.setIdMateria(insert_id);
        }
        
        // Checando se ja existe materia com este ID na lista do cliente
        int qntMaterias = 0;
        sql = "SELECT COUNT(id_materia) FROM " + tabela_destino + " "
                + "WHERE id_materia=" + materiaPub.getIdMateria() + " AND "
                + "id_cliente=" + materiaPub.getIdCliente();

        ResultSet results = dbconn.abrirConsultaSql(sql);

        // Obtendo resultados da pesquisa...

        if ( results.next() ) qntMaterias = results.getInt(1);
        else throw new SQLException("Erro consultando DB:" + sql);
        
        // Checando resposta
        if ( qntMaterias > 0 ) return 1;
        
        // Adicionandoa a referencia materia_cliente
        sql = "INSERT INTO " + tabela_destino + " ("
                + "id_cliente, "
                + "id_materia, "
                + "id_publicacao, "
                + "id_nome_pesquisa, "
                + "pagina, "
                + "linha_cliente, "
                + "percentual, "
                + "termo_percentual, "
                + "sit_cad"
                + ") VALUES("
                + materiaPub.getIdCliente() + ","
                + materiaPub.getIdMateria() + ","
                + materiaPub.getIdPublicacao() + ","
                + materiaPub.getIdNomePesquisa() + ","
                + materiaPub.getPagina() + ","
                + materiaPub.getLinhaCliente() + ", "
                + (materiaPub.isPesquisaProximidade() ? "'t'":"'f'") + ", "
                + "'" + materiaPub.getTermoPesquisaProximidade().replaceAll("'", "''") + "', "
                + "'A')"; // Necessita ativar na secao Administrativa // TODO: Modificar p/ 'I' quandoa seção adm estiver OK

        dbconn.executarSql(sql);

        // Checando se deve criar dados na tabela de apoio
        if ( !ignorarMateria ) {
//            BackendDBActions.AdicionarNovaMateriaCliente(publicacaoJornal, nomePesquisaCliente, materiaPub, pautaPublicacao);
        }

        return (materiaJaAdicionada == true) ? 1:0;
    }
    
    private boolean checarSentencaoDJPE(final String marcacao)
    {
        Pattern pattern = Pattern.compile("senten.a(\\s+|)n.*\\d+$", Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(marcacao);
        
        return (match.find() || match.matches() );
    }
    
    private boolean compararNumProcesso(final String proc1, final String proc2)
    {
        if ( proc1 == null || proc1.length() <= 0 || proc2 == null || proc2.length() <= 0 )
            return false;
        
        final String numProc1 = proc1.replaceAll("[^0-9]+", "").trim();
        final String numProc2 = proc2.replaceAll("[^0-9]+", "").trim();
        if ( numProc1.length() <= 0 || numProc2.length() <= 0 )
            return false;
        
        return numProc1.equals(numProc2);
    }
    
    public int[] dadosListaLinhasInicioFimPauta(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException, IOException
    {
        long linha_cliente = materiaPublicacao.getLinhaCliente();
        final String table_name = sqlite.obterNomeTabela();
        String sql = "SELECT "
                + "DISTINCT(num_doc_lucene) AS inicio, id_tipo_padrao AS tipo "
                + "FROM " + table_name + " "
                + "WHERE id_tipo_padrao IN (21,1,2) AND num_doc_lucene <= " + linha_cliente + " "
                + "ORDER BY num_doc_lucene DESC LIMIT 1";
        
        ResultSet resultado = sqlite.abrirConsultaSql(sql);
        int linhaInicioPauta = 0;
        int linhaFimPauta = 0;
        if ( resultado.next() )
        {
            linhaInicioPauta = resultado.getInt("inicio");
            if ( resultado.getInt("tipo") != 21 ) linhaInicioPauta += 1;
        }
        else
        {
            int retError[] = {0,0};
            return retError;
        }
        
        // Obtendo numero de documento (Lucene) final
        sql = "SELECT "
                + "num_doc_lucene AS final, id_tipo_padrao as tipo "
                + "FROM " + table_name + " "
                + "WHERE num_doc_lucene > " + linhaInicioPauta + " "
                + "AND id_tipo_padrao IN(22, 3, 4) "
                + "ORDER BY num_doc_lucene ASC LIMIT 1";
        
        resultado = sqlite.abrirConsultaSql(sql);
        if ( resultado.next() )
        {
            linhaFimPauta = resultado.getInt("fim");
            if ( resultado.getInt("tipo") == 22 ) linhaFimPauta += 1;
        }
        
        int[] retValues = {linhaInicioPauta, linhaFimPauta};
        return retValues;
    }
    

    public MateriaPublicacao dadosListarLinhasInicioFimMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        // Obtendo numero de documento (Lucene) inicial
        long linha_cliente = materiaPublicacao.getLinhaCliente();
        final String table_name = sqlite.obterNomeTabela();
        String sql = "SELECT "
                + "DISTINCT(num_doc_lucene) AS inicio, marcacao_original AS marcacao, id_tipo_padrao AS tipo "
                + "FROM " + table_name + " "
                + "WHERE id_tipo_padrao IN (3,1,2,4) AND num_doc_lucene <= " + linha_cliente + " "
                + "ORDER BY num_doc_lucene DESC LIMIT 5";
        //logger.debug(sql);
        //System.out.println("INICIO -> " + sql);

        // Petrus Augusto: 08-07-2014
        //    Checa os tres ultimos dados de 'iniocio de materia' e verifica a
        //    distância entre eles... ate que a diferença seja de no minimo,
        //    duas linhas...
        ResultSet resultado = sqlite.abrirConsultaSql(sql);
        long linhaInicioMateria = 0;
        String currentMarcacao = "";
        while ( resultado.next() )
        {
            int tipo = resultado.getInt("tipo");
            long linha = resultado.getLong("inicio");
            String rdMarcacao = resultado.getString("marcacao");
            
            if ( linhaInicioMateria == 0 )
            {
                linhaInicioMateria = linha;
                currentMarcacao = rdMarcacao; 
            }
            else if ( (linhaInicioMateria - linha) <= 2 && rdMarcacao.length() <= 60 )
            {
                // DJPE contem muito de varios processos seguidos linha pos linha
                linhaInicioMateria = linha;
                currentMarcacao = rdMarcacao; 
            }
            else if ( materiaPublicacao.getSiglaJornal().equals("TRT06") )
            { 
                // TRT6 ocorre de ter varios 'indicios' de inicio de processo
                // mas, que no final, pertecem ao mesmo... p/ garantir o corte
                // correto, é necessario verificar os numeros do processo...
                // enquanto for igual, continua... caso contrario, para o loop
                if ( this.compararNumProcesso(currentMarcacao, rdMarcacao) ) {
                    linhaInicioMateria = linha;
                    currentMarcacao = rdMarcacao; 
                }
            }
            else
            {
                // parando loop...
                break;
            }
        }

        if ( linhaInicioMateria != 0 ) materiaPublicacao.setLinhaInicialMateria(linhaInicioMateria);
        else materiaPublicacao.setLinhaInicialMateria(linha_cliente);
        
        // Obtendo numero de documento (Lucene) final
        // Petrus: 2019-04-23, Achei um bug na finalização da matéria.
        sql = "SELECT "
                + "num_doc_lucene AS final, marcacao_original AS marcacao "
                + "FROM " + table_name + " "
                + "WHERE num_doc_lucene > " + linha_cliente + " "
                + "AND id_tipo_padrao IN(1,2,3,4,5,21,25) "
                + "ORDER BY num_doc_lucene ASC LIMIT 2";
        
        resultado = sqlite.abrirConsultaSql(sql);
        if ( resultado.next() )
        {
            if ( linha_cliente > resultado.getLong("final") ) materiaPublicacao.setLinhaFinalMateria(linha_cliente);
            else materiaPublicacao.setLinhaFinalMateria(resultado.getLong("final"));
        }
        else // Dados de documento nao encontrado...
        {
            materiaPublicacao.setLinhaInicialMateria(linha_cliente - 15);
            materiaPublicacao.setLinhaFinalMateria(linha_cliente + 15);
            return materiaPublicacao;
        }

        // Checando o texto da marcacao obtida como 'final' da materia...
        final String marcacao = resultado.getString("marcacao");
        if ( this.checarSentencaoDJPE(marcacao) )
        {
            // Por causa do DJPE, nao e permitido ter dois finais de materia
            // tendo o mesmo tipo de texto (Sentenca)... caso ocorra, indica que
            // o primeiro (obtedo anteriormente) NAO e o fim da materia
            if ( resultado.next() == false )
                return materiaPublicacao; // Nao tem mais resultados alem desse
            
            // Checando o texto
            final String marcacao2 = resultado.getString("marcacao");
            if ( this.checarSentencaoDJPE(marcacao2) )
                materiaPublicacao.setLinhaFinalMateria( resultado.getLong("final") );
        }

        return materiaPublicacao;
    }
    
    public PautaPublicacao dadosListarPautaMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException {
        final String table_name = sqlite.obterNomeTabela();
        final String sql = "SELECT mp.marcacao, mp.num_doc_lucene, mp.pagina "
                + "FROM " + table_name + " mp "
                + "WHERE mp.num_doc_lucene <= " + materiaPublicacao.getLinhaInicialMateria() + " "
                + "AND id_tipo_padrao=50 "
                + "ORDER BY mp.num_doc_lucene DESC LIMIT 1";
        
        final ResultSet resultado = sqlite.abrirConsultaSql(sql);
        if ( !resultado.next() ) return null;
        
        // Checando se apauta e rerente ao titulo/secao atual
        if ( resultado.getLong("num_doc_lucene") < materiaPublicacao.getLinhaTitulo() )
            return null;
        
        final PautaPublicacao pauta = new PautaPublicacao();
        pauta.setIdPublicacao( materiaPublicacao.getIdPublicacao());
        pauta.setNumDocLucene(resultado.getLong("num_doc_lucene"));
        pauta.setPagina(resultado.getInt("pagina"));
        pauta.setSitCad("A");
        pauta.setPauta(resultado.getString("marcacao"));
        pauta.setUsuCad(1);
        
        resultado.close();
        
        return pauta;
    }
    
    public String dadosObterNumeroEdicao(DbConnectionMarcacao sqlite) throws SQLException
    {
        final String table_name = sqlite.obterNomeTabela();
        final String sql_edicao = "SELECT mp.marcacao edicao " +
                "FROM " + table_name + " mp " +
                "WHERE mp.id_tipo_padrao = 6 " + 
                "LIMIT 1";
        
        final ResultSet resultado = sqlite.abrirConsultaSql(sql_edicao);
        if ( !resultado.next() ) return null;
        
        // Obtendo APENAS os numeros do texto
        String numero_edicao = resultado.getString("edicao");
        
        // Aplicando regex p/ obter APENAS o primeiro grupo de numeros
        Pattern r = Pattern.compile("(Edi..o\\s+[0-9]{2,})|([0-9]{1,}[0-9.]{0,})");
        Matcher m = r.matcher(numero_edicao);
        
        // Fechando statement
        resultado.close();
        
        // Verificando a ocorrencia
        // PS: Dando prioridade os valores LONGOS (caso venha mais de um)
        numero_edicao = "";
        final String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if ( m.find() ) {
            do {
                String rd = m.group(0).replaceAll("[^\\d.]", "" ).replace(".", "");
                if ( rd != null && (rd.length() > numero_edicao.length() && rd.equals(currentYear) == false) )
                    numero_edicao = rd;
            } while ( m.find() );
        } else {
            numero_edicao = "";
        }
        
        return (numero_edicao.length() > 0) ? numero_edicao : null;
    }
    
    public MateriaPublicacao dadosListarTituloSubtituloMateria(MateriaPublicacao materiaPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        // Obtendo Titulo
        final String table_name = sqlite.obterNomeTabela();
        final String sql_titulo = "SELECT mp.num_doc_lucene linha, mp.marcacao titulo "
                + "FROM " + table_name + " mp "
                + "WHERE mp.num_doc_lucene < " + materiaPublicacao.getLinhaCliente() + " "
                + "AND mp.id_tipo_padrao = 1 "
                + "ORDER by mp.num_doc_lucene desc limit 1";
        
        ResultSet resultado = sqlite.abrirConsultaSql(sql_titulo);
        long num_doc_titulo = 0;
        if ( resultado.next() )
        {
            num_doc_titulo = resultado.getLong("linha");
            materiaPublicacao.setLinhaTitulo(num_doc_titulo);
            materiaPublicacao.setTituloMateria(resultado.getString("titulo"));
        }
        
        // Obtendo SubTitulo
        final String sql_subtitulo = "SELECT mp.marcacao subtitulo "
                + "FROM " + table_name + " mp WHERE mp.num_doc_lucene < " + materiaPublicacao.getLinhaCliente() + " "
                + "AND mp.id_tipo_padrao = 2 AND mp.num_doc_lucene > " + num_doc_titulo + " "
                + "ORDER by mp.num_doc_lucene desc limit 1";
        resultado = sqlite.abrirConsultaSql(sql_subtitulo);
        if ( resultado.next() ) materiaPublicacao.setTituloMateria(resultado.getString("subtitulo"));
        
        return materiaPublicacao;
    }

    public int dadosCheckQuantidadeMarcacoes(MarcacaoPublicacao marcacaoPub, DbConnectionMarcacao sqlite) throws SQLException {
        int qtdCheck = 0;
        final String table_name = sqlite.obterNomeTabela();
        final String sql = " SELECT count(*) as qtd "
                + " FROM " + table_name + " "
                + " WHERE id_tipo_padrao = " + marcacaoPub.getIdTipoPadrao();

        final ResultSet resultado = sqlite.abrirConsultaSql(sql);
        if (resultado.next())
        {
            qtdCheck = resultado.getInt("qtd");
        }

        return qtdCheck;
    }

    public int dadosCheckQuantidadeMaterias(MateriaPublicacao materiaPub, DbConnection dbconn) throws SQLException 
    {
        final Statement stm = dbconn.obterStatement();
        int qtdCheck = 0;
        final String sql = " SELECT count(*) as qtd "
                + "  FROM materia_publicacao "
                + "  where id_publicacao = " + materiaPub.getIdPublicacao()
                + "  and id_nome_pesquisa = " + materiaPub.getIdNomePesquisa();
       
        final ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        if ( resultado.next() )
        {
            qtdCheck = resultado.getInt("qtd");
        }

        resultado.close();
        stm.close();
        
        return qtdCheck;
    }

    //#7335
    public AdicionalMarcacaoTipoPadrao[] dadosListarAdicionalMarcacaoTipoPadrao(char tipoAdicional, int idTipoPadrao, int idPadrao, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<AdicionalMarcacaoTipoPadrao> listaAddMarcacao = new ArrayList();
        String sql = " SELECT amtp.id_add_tipo_padrao_jornal, amtp.id_tipo_padrao_jornal, amtp.id_tipo_padrao, "
                + "      amtp.id_padrao, amtp.tipo_adicional, amtp.texto_adicional, amtp.regex_adicional, amtp.add_linha_acima, "
                + "      amtp.add_linha_abaixo, amtp.dat_cad, amtp.sit_cad, amtp.usu_cad, tpj.query_ini, tpj.map_font "
                + " FROM adicional_marcacao_tipo_padrao amtp, "
                + "      tipo_padrao_jornal tpj "
                + " where amtp.id_padrao = " + idPadrao
                + " and amtp.tipo_adicional = '" + tipoAdicional + "'"
                + " and amtp.id_tipo_padrao_jornal = tpj.id_tipo_padrao_jornal"
                + " and amtp.sit_cad='A'"; // Adicionada
        // Completando o SQL se necessario
        if (idTipoPadrao != 0)
            sql = sql + " and amtp.id_tipo_padrao = " + idTipoPadrao;

        final ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        TipoPadraoJornal tipoPadraoJornalTemp;
        AdicionalMarcacaoTipoPadrao addMarcacaoTipo = null;
        while (resultado.next())
        {
            addMarcacaoTipo = new AdicionalMarcacaoTipoPadrao();
            addMarcacaoTipo.setIdAddTipoPadraoJornal(resultado.getInt("id_add_tipo_padrao_jornal"));
            addMarcacaoTipo.setIdTipoPadraoJornal(resultado.getInt("id_tipo_padrao_jornal"));
            addMarcacaoTipo.setIdTipoPadrao(resultado.getInt("id_tipo_padrao"));
            addMarcacaoTipo.setIdPadrao(resultado.getInt("id_padrao"));
            addMarcacaoTipo.setTipoAdicional(resultado.getString("tipo_adicional").charAt(0));
            addMarcacaoTipo.setTextoAdicional(resultado.getString("texto_adicional"));
            addMarcacaoTipo.setRegexAdicional(resultado.getString("regex_adicional"));
            addMarcacaoTipo.setAddLinhaAcima(resultado.getInt("add_linha_acima"));
            addMarcacaoTipo.setAddLinhaAbaixo(resultado.getInt("add_linha_abaixo"));
            addMarcacaoTipo.setDatCad(resultado.getDate("dat_cad"));
            addMarcacaoTipo.setSitCad(resultado.getString("sit_cad"));
            addMarcacaoTipo.setUsuCad(resultado.getInt("usu_cad"));
            
            tipoPadraoJornalTemp = new TipoPadraoJornal();
            tipoPadraoJornalTemp.setQueryIni(resultado.getString("query_ini"));
            tipoPadraoJornalTemp.setMapeamentoFonte(resultado.getString("map_font"));
            tipoPadraoJornalTemp.setQtdMaxResultQuery(0);
            tipoPadraoJornalTemp.setQtdLinhaAjusteAcima(0);
            tipoPadraoJornalTemp.setQtdLinhaAjusteAbaixo(0);
            tipoPadraoJornalTemp.setIdTipoPadraoCheck(0);
            addMarcacaoTipo.setTipoPadraoJornal(tipoPadraoJornalTemp);

            listaAddMarcacao.add(addMarcacaoTipo);
        }

        resultado.close();
        stm.close();
        
        return listaAddMarcacao.toArray(new AdicionalMarcacaoTipoPadrao[0]);
    }

    //#7335
    public ExclusaoMarcacaoTipoPadrao[] dadosListarExclusaoMarcacaoTipoPadrao(char tipoExclusao, int idTipoPadrao, int idPadrao, DbConnection dbconn) throws SQLException 
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<ExclusaoMarcacaoTipoPadrao> listaExcMarcacao = new ArrayList();
        final String sql = " SELECT id_exc_tipo_padrao_jornal, id_tipo_padrao_jornal, id_tipo_padrao, "
                + " tipo_exclusao, texto_exclusao, regex_exclusao, dat_cad, sit_cad, "
                + " usu_cad, id_padrao "
                + " FROM exclusao_marcacao_tipo_padrao "
                + " where id_tipo_padrao = " + idTipoPadrao
                + " and id_padrao = " + idPadrao
                + " and tipo_exclusao = '" + tipoExclusao + "'";

        final ResultSet resultado = dbconn.abrirConsultaSql(stm, sql);
        ExclusaoMarcacaoTipoPadrao excMarcacaoTipo = null;
        while (resultado.next())
        {
            excMarcacaoTipo = new ExclusaoMarcacaoTipoPadrao();
            excMarcacaoTipo.setIdExcTipoPadraoJornal(resultado.getInt("id_exc_tipo_padrao_jornal"));
            excMarcacaoTipo.setIdTipoPadraoJornal(resultado.getInt("id_tipo_padrao_jornal"));
            excMarcacaoTipo.setIdTipoPadrao(resultado.getInt("id_tipo_padrao"));
            excMarcacaoTipo.setIdPadrao(resultado.getInt("id_padrao"));
            excMarcacaoTipo.setTipoExclusao(resultado.getString("tipo_exclusao").charAt(0));
            excMarcacaoTipo.setTextoExclusao(resultado.getString("texto_exclusao"));
            excMarcacaoTipo.setRegexExclusao(resultado.getString("regex_exclusao"));
            excMarcacaoTipo.setDatCad(resultado.getDate("dat_cad"));
            excMarcacaoTipo.setSitCad(resultado.getString("sit_cad"));
            excMarcacaoTipo.setUsuCad(resultado.getInt("usu_cad"));

            listaExcMarcacao.add(excMarcacaoTipo);
        }

        resultado.close();
        stm.close();
        
        return listaExcMarcacao.toArray(new ExclusaoMarcacaoTipoPadrao[0]);
    }
    
    public boolean atualizarDataFinalProcessamentoPublicacao(int id_publicacao, DbConnection dbconn) throws SQLException
    {
        final String sql = " UPDATE publicacao_jornal "
                + "SET fim_processamento = NOW() "
                + "WHERE id_publicacao = " + id_publicacao;
        
        return dbconn.executarSql(sql);
    }
    
    public boolean atualizarDataInicialProcessamentoPublicacao(int id_publicacao, DbConnection dbconn) throws SQLException
    {
        final String sql = " UPDATE publicacao_jornal "
                + "SET inicio_processamento = NOW(), fim_processamento=NULL "
                + "WHERE id_publicacao = " + id_publicacao;
        
        return dbconn.executarSql(sql);
    }
    
    public boolean removerMarcacaoPrincipaisRangeNDocLucene(int numdoc_inicial, int numdoc_final, DbConnectionMarcacao sqlite) throws SQLException
    {
        final String table_name = sqlite.obterNomeTabela();
        final String sql = "DELETE FROM " + table_name + " "
                + "WHERE num_doc_lucene >= " + numdoc_inicial + " "
                + "AND num_doc_lucene <= " + numdoc_final + " "
                + "AND id_tipo_padrao IN (1,2,3,4,5,21,22) ";
        
        return sqlite.executarSql(sql);
    }
    
    public boolean atualizarEdicaoPublicacao(int id_publicacao, String numEdicao, DbConnection dbconn) throws SQLException {
        final String sql = "UPDATE publicacao_jornal " + 
                "SET edicao_publicacao = '" + Integer.parseInt(numEdicao) + "' " +
                " WHERE id_publicacao = " + id_publicacao;
        
        // Executando o comando
        return dbconn.executarSql(sql);
    }
}
