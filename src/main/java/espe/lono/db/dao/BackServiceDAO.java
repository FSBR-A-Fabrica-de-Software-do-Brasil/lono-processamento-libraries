package espe.lono.db.dao;

import espe.lono.db.Fachada;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.BackserviceActions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Nota: A definicao dessa classe sao estaticas pois a ideia e que a execucao dela seja atomica,
 *       isso e, que no projeto que a chamar, so posso ser executado/chamado uma unica vez
 *
 * Nota2: Ao termino da listagem, antes de retornar os dados, ele percorre e define todos os status para 'W' (Working)
 *        deixando os dados retornados exclusivos de quem o chamou
 */
public class BackServiceDAO {
    public static synchronized List<BackserviceActions> ObterRequisicaoProcessamento_Veiculos(DbConnection dbConnection, int limit) throws SQLException {
        final String sqlcmd = "SELECT bs.* FROM backservice_actions AS bs inner join veiculos v on v.id = bs.id_veiculo " +
                "FULL OUTER JOIN cliente_plano cp ON cp.id_cliente = bs.id_cliente " +
                "WHERE bs.sit_cad = 'A' AND bs.id_veiculo is not null and v.sit_cad = 'A' " +
                "LIMIT " + limit;

        // Realizando a consulta e obtendo os dados
        List<BackserviceActions> backserviceActionsList = new ArrayList<>();
        final Statement stm = dbConnection.obterStatement();
        Fachada fachada = new Fachada();
        ResultSet resultado = dbConnection.abrirConsultaSql(stm, sqlcmd);
        while ( resultado.next() ) {
            BackserviceActions backserviceActions = populateResultsetResponse(resultado, dbConnection);
            backserviceActionsList.add(backserviceActions);
//            resultado.close();
//            stm.close();
//            return backserviceActions;
        }

        resultado.close();
        stm.close();
        return backserviceActionsList;
    }

    public static synchronized List<BackserviceActions> ObterRequisicaoProcessamento_Jornal(DbConnection dbConnection, int limit) throws SQLException {
        final String sqlcmd = "SELECT bs.* FROM backservice_actions AS bs inner join jornal  v on v.id_jornal = bs.id_jornal " +
                "FULL OUTER JOIN cliente_plano cp ON cp.id_cliente = bs.id_cliente " +
                "WHERE bs.sit_cad = 'A' AND bs.id_jornal is not null and v.sit_cad = 'A' " +
                "LIMIT " + limit;

        // Realizando a consulta e obtendo os dados
        final List<BackserviceActions> backserviceActionsList = new ArrayList<>();
        final Statement stm = dbConnection.obterStatement();
        Fachada fachada = new Fachada();
        ResultSet resultado = dbConnection.abrirConsultaSql(stm, sqlcmd);
        while ( resultado.next() ) {
            BackserviceActions backserviceActions = populateResultsetResponse(resultado, dbConnection);
            backserviceActionsList.add(backserviceActions);
        }

        resultado.close();
        stm.close();
        return backserviceActionsList;
    }

    public static synchronized List<BackserviceActions> ObterRequisicaoProcessamento_Jornal(DbConnection dbConnection, String siglaFilter, int limit) throws SQLException {
        final String sqlcmd = "SELECT bs.* FROM backservice_actions AS bs inner join jornal  v on v.id_jornal = bs.id_jornal " +
                "FULL OUTER JOIN cliente_plano cp ON cp.id_cliente = bs.id_cliente " +
                "WHERE bs.sit_cad = 'A' AND bs.id_jornal is not null and v.sit_cad = 'A' " +
                "AND v.sigla_jornal like '" + siglaFilter + "%' " +
                "LIMIT  " + limit;

        // Realizando a consulta e obtendo os dados
        final List<BackserviceActions> backserviceActionsList = new ArrayList<>();
        final Statement stm = dbConnection.obterStatement();
        ResultSet resultado = dbConnection.abrirConsultaSql(stm, sqlcmd);
        while ( resultado.next() ) {
            BackserviceActions backserviceActions = populateResultsetResponse(resultado, dbConnection);
            backserviceActionsList.add(backserviceActions);
        }

        resultado.close();
        stm.close();
        return backserviceActionsList;
    }


    public static boolean AtualizarRequisicaoProcessamentoStatus(BackserviceActions backserviceActions, String status, DbConnection dbConnection) throws SQLException {
        StringBuilder sqlcmd = new StringBuilder();
        sqlcmd.append("UPDATE backservice_actions ");
        sqlcmd.append("SET sit_cad = '" + status + "' ");

        // Definindo o horario inicio/fim processamneto com base no status informado
        if ( status == "I" ) sqlcmd.append(", dat_inicio_processamento = NOW() ");
        else sqlcmd.append(", dat_fim_processamento = NOW() ");

        // Completando a query
        sqlcmd.append("WHERE id_backservice_req = " + backserviceActions.getIdBackserviceReq());

        return dbConnection.executarSql(sqlcmd.toString());
    }

    public static void LiberarBackServiceActionsList(List<BackserviceActions> backserviceActionsList, DbConnection dbConnection)
    {
        for ( BackserviceActions backserviceActions: backserviceActionsList ) {
            try { AtualizarRequisicaoProcessamentoStatus(backserviceActions, "A", dbConnection); }
            catch (Exception ignore ) {}
        }
    }

    private static BackserviceActions populateResultsetResponse(ResultSet resultado, DbConnection dbConnection) throws SQLException {
        BackserviceActions backserviceActions = new BackserviceActions();
        backserviceActions.setAcao(resultado.getString("acao"));
        backserviceActions.setDatCad(resultado.getDate("dat_cad"));
        backserviceActions.setIdCliente(resultado.getInt("id_cliente"));
        backserviceActions.setIdJornal(resultado.getInt("id_jornal"));
        backserviceActions.setIdVeiculo(resultado.getInt("id_veiculo"));
        backserviceActions.setIdNomePesquisa(resultado.getInt("id_nome_pesquisa"));
        backserviceActions.setUsuCad(resultado.getInt("usu_cad"));
        backserviceActions.setIdBackserviceReq(resultado.getInt("id_backservice_req"));
        backserviceActions.setTermo(resultado.getString("termo"));
        backserviceActions.setSitCad(resultado.getString("sit_cad"));

        // Atualizando o status para 'W' (Working)
        AtualizarRequisicaoProcessamentoStatus(backserviceActions, "W", dbConnection);

        return backserviceActions;
    }
}
