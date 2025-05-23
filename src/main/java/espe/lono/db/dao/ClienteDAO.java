package espe.lono.db.dao;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.*;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Luiz Diniz/Petrus Augusto - Espe
 * @since 10/09/2015
 * @hidden 
 */
public class ClienteDAO
{
    final static private Logger logger = Logger.getLogger("mercurio3");

    public boolean inserirDadosPesquisa(int idCliente, int idNomePesquisa, int idPublicacao, int idJornal, int qtdLocatedTerms, int qtdPjeProcs, DbConnection dbconn) throws SQLException {
        String sql = "INSERT INTO analytic_cliente_pesquisa (id_cliente, id_nome_pesquisa, id_jornal, id_publicacao, qtd_ocorrencia, qtd_ocorrencia_pje, dat_cad) VALUES (" +
                "" + idCliente + ", " + idNomePesquisa + ", " + idJornal + ", " + idPublicacao + ", " + qtdLocatedTerms + ", " + qtdPjeProcs + ", NOW())";
        return dbconn.executarSql(sql);
    }

    public List<Cliente> buscarTodosClientesAtivos(DbConnection dbconn) throws SQLException{
        List<Cliente> clientesAtivos = new ArrayList<>();

        String sql = "SELECT * FROM cliente WHERE sit_cad = 'A'";
        try (Statement statement = dbconn.obterStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(resultSet.getInt("id_cliente"));
                cliente.setIdConta(resultSet.getInt("id_conta"));
                cliente.setNome(resultSet.getString("nome"));
                cliente.setSexo(resultSet.getString("sexo"));
                cliente.setNumOab(resultSet.getString("num_oab"));
                cliente.setTpPessoa(resultSet.getString("tipo_pessoa"));
                cliente.setReceberEmail(resultSet.getString("receber_email"));
                cliente.setFone1(resultSet.getString("fone1"));
                cliente.setNomeContato(resultSet.getString("nome_contato"));
                cliente.setEndereco(resultSet.getString("endereco"));
                cliente.setBairro(resultSet.getString("bairro"));
                cliente.setCidade(resultSet.getString("cidade"));
                cliente.setDatCad(resultSet.getTimestamp("dat_cad"));
                cliente.setSitCad(resultSet.getString("sit_cad"));
                cliente.setUsuCad(resultSet.getInt("usu_cad"));
                cliente.setEmail(resultSet.getString("email"));

                clientesAtivos.add(cliente);
            }
            return clientesAtivos;
        }
    }

    public boolean atualizarStatusNomePesquisa(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        String sql = "UPDATE nome_pesquisa SET sit_cad='S' WHERE id_nome_pesquisa='" + idNomePesquisa + "'";
        return dbconn.executarSql(sql);
    }

    public boolean atualizarBlacklistNotifyDat(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        String sql = "UPDATE nome_pesquisa SET blacklist_notify_dat = NOW() WHERE id_nome_pesquisa='" + idNomePesquisa + "'";
        return dbconn.executarSql(sql);
    }
    
    public NomePesquisaCliente[] dadoslistarNumeroOABJornal(int idCliente, int idJornal, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<NomePesquisaCliente> listaNomesPesq = new ArrayList();
        
        // Comando 1, obtendo lista de termos referenciados um-a-um
        String sqlcmd = "SELECT  " +
                "    tb.id_termo_recusado,  " +
                "    np.blacklist_notify_dat,  " +
                "    cl.id_cliente,  " +
                "    cl.id_conta,  " +
                "    np.nome_pesquisa,  " +
                "    j.id_jornal,  " +
                "    np.id_nome_pesquisa,  " +
                "    np.literal,  " +
                "    cl.taxa_proximidade,  " +
                "    np.todos_jornais,  " +
                "    np.processo " +
                "FROM  " +
                "    cliente cl " +
                "INNER JOIN  " +
                "    conta c ON c.id_conta = cl.id_conta " +
                "INNER JOIN  " +
                "    nome_pesquisa np ON np.id_cliente = cl.id_cliente " +
                "INNER JOIN  " +
                "    nome_pesquisa_jornal npj ON npj.id_nome_pesquisa = np.id_nome_pesquisa " +
                "INNER JOIN  " +
                "    jornal j ON j.id_jornal = npj.id_jornal " +
                "INNER JOIN  " +
                "    jornal_conta jc ON jc.id_conta = c.id_conta " +
                "LEFT JOIN  " +
                "    termos_bloqueados as tb ON tb.termo = np.nome_pesquisa " +
                "WHERE  " +
                "    c.sit_cad = 'A' " +
                "    AND j.id_jornal = " + idJornal + " " +
                "    AND np.id_cliente = cl.id_cliente " +
                "    AND cl.sit_cad = 'A' " +
                "    AND np.sit_cad = 'A' " +
                "    AND j.sit_cad = 'A' " +
                "    AND np.oab = true " +
                "    AND np.id_termo_pai is NULL ";
        
        // Adicionando o ID do cliente (se tiver/for_necessario)
        if ( idCliente != 0 )
            sqlcmd += " AND cl.id_cliente = " + idCliente + " ";

        // Completando comando SQL
        sqlcmd += " GROUP BY tb.id_termo_recusado, cl.id_cliente, cl.id_conta, np.nome_pesquisa, j.id_jornal, np.id_nome_pesquisa, np.literal, cl.taxa_proximidade, np.todos_jornais, np.processo ";
        sqlcmd += " ORDER BY cl.id_cliente ASC";
        
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        NomePesquisaCliente nomePesqCliente = null;

        while(resultado.next())
        {
            // 0 == Numero OAB, 1 == UF OAB
            final String[] termoNomePesquisa_oab = resultado.getString("nome_pesquisa").trim().split("[#|]");
            if ( termoNomePesquisa_oab.length != 2 ) {
                logger.error("Termo OAB invalido -> " + resultado.getString("nome_pesquisa"));
                continue;
            }
            
            nomePesqCliente = new NomePesquisaCliente();
            
            nomePesqCliente.setLiteral( resultado.getBoolean("literal") );
            nomePesqCliente.setIdCliente( resultado.getInt("id_cliente") );
            nomePesqCliente.setIdNomePesquisa( resultado.getInt("id_nome_pesquisa") );
            nomePesqCliente.setNomePesquisa( resultado.getString("nome_pesquisa") );
            nomePesqCliente.setUfOAB( termoNomePesquisa_oab[1].trim() );
            nomePesqCliente.setNomePesquisaLimpo( termoNomePesquisa_oab[0].trim() );
            nomePesqCliente.setPorcetualColisao( (float) resultado.getInt("taxa_proximidade") );
            nomePesqCliente.setNumProcesso( resultado.getBoolean("processo") );
            listaNomesPesq.add(nomePesqCliente);
        }
        
        resultado.close();
        stm.close();
        
        return listaNomesPesq.toArray(new NomePesquisaCliente[0]);
    }
    
    public NomePesquisaCliente listarNomePesquisaPorID(int idNomePesquisa, DbConnection dbconn) throws SQLException
    {
        NomePesquisaCliente nomePesqCliente = new NomePesquisaCliente();
        final String sqlcmd =
                "SELECT " +
                        "    tb.id_termo_recusado, " +
                        "    np.blacklist_notify_dat, " +
                        "    np.literal, " +
                        "    np.id_cliente, " +
                        "    np.id_nome_pesquisa, " +
                        "    np.sit_cad, " +
                        "    np.nome_pesquisa, " +
                        "    c.taxa_proximidade, " +
                        "    cp.id_cliente_plano, " +
                        "    np.processo " +
                        "FROM nome_pesquisa np " +
                        "INNER JOIN cliente c ON c.id_cliente = np.id_cliente " +
                        "LEFT JOIN termos_bloqueados tb ON tb.termo = np.nome_pesquisa " +
                        "FULL OUTER JOIN cliente_plano cp ON cp.id_cliente = c.id_cliente " +
                        "WHERE " +
                        "    np.id_nome_pesquisa = " + idNomePesquisa +
                        "    AND c.sit_cad = 'A' " +
                        "GROUP BY " +
                        "    tb.id_termo_recusado, " +
                        "    np.blacklist_notify_dat, " +
                        "    np.literal, " +
                        "    np.id_cliente, " +
                        "    np.sit_cad, " +
                        "    np.id_nome_pesquisa, " +
                        "    np.nome_pesquisa, " +
                        "    c.taxa_proximidade, " +
                        "    cp.id_cliente_plano, " +
                        "    np.processo;";

        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        while ( resultado.next() )
        {
            final String idTermoRecusado = resultado.getString("id_termo_recusado");

            // Definindo o restulado
            nomePesqCliente.setBlacklist((idTermoRecusado == null || idTermoRecusado.length() <= 0) ? false : true);
            nomePesqCliente.setBlacklistNotifyDat(resultado.getDate("blacklist_notify_dat"));
            nomePesqCliente.setLiteral( resultado.getBoolean("literal") );
            nomePesqCliente.setIdCliente( resultado.getInt("id_cliente") );
            nomePesqCliente.setIdNomePesquisa( resultado.getInt("id_nome_pesquisa") );
            nomePesqCliente.setNomePesquisa( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setNomePesquisaLimpo( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setPorcetualColisao( (float) resultado.getInt("taxa_proximidade") );
            nomePesqCliente.setNumProcesso( resultado.getBoolean("processo") );
            nomePesqCliente.setSitCad( resultado.getString("sit_cad") );
        }
        
        resultado.close();
        stm.close();
        
        return (nomePesqCliente.getIdNomePesquisa() == null) ? null : nomePesqCliente;
    }

    public NomePesquisaCliente listarNomePesquisaPorID(int idNomePesquisa, int idJornal, DbConnection dbconn) throws SQLException
    {
        NomePesquisaCliente nomePesqCliente = new NomePesquisaCliente();
        final String sqlcmd =
                "SELECT " +
                        "    tb.id_termo_recusado, " +
                        "    np.blacklist_notify_dat, " +
                        "    np.literal, " +
                        "    np.id_cliente, " +
                        "    np.id_nome_pesquisa, " +
                        "    np.sit_cad, " +
                        "    np.nome_pesquisa, " +
                        "    c.taxa_proximidade, " +
                        "    cp.id_cliente_plano, " +
                        "    np.processo " +
                        "FROM nome_pesquisa np " +
                        "INNER JOIN cliente c ON c.id_cliente = np.id_cliente " +
                        "INNER JOIN nome_pesquisa_jornal npj ON npj.id_nome_pesquisa = np.id_nome_pesquisa " +
                        "LEFT JOIN termos_bloqueados tb ON tb.termo = np.nome_pesquisa " +
                        "FULL OUTER JOIN cliente_plano cp ON cp.id_cliente = c.id_cliente " +
                        "WHERE " +
                        "    np.id_nome_pesquisa = " + idNomePesquisa +
                        "    AND c.sit_cad = 'A' " +
                        "    AND npj.id_jornal = " + idJornal + " " +
                        "GROUP BY " +
                        "    tb.id_termo_recusado, " +
                        "    np.blacklist_notify_dat, " +
                        "    np.literal, " +
                        "    np.id_cliente, " +
                        "    np.sit_cad, " +
                        "    np.id_nome_pesquisa, " +
                        "    np.nome_pesquisa, " +
                        "    c.taxa_proximidade, " +
                        "    cp.id_cliente_plano, " +
                        "    np.processo;";

        final Statement stm = dbconn.obterStatement();
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        while ( resultado.next() )
        {
            final String idTermoRecusado = resultado.getString("id_termo_recusado");

            // Definindo o restulado
            nomePesqCliente.setBlacklist((idTermoRecusado == null || idTermoRecusado.length() <= 0) ? false : true);
            nomePesqCliente.setBlacklistNotifyDat(resultado.getDate("blacklist_notify_dat"));
            nomePesqCliente.setLiteral( resultado.getBoolean("literal") );
            nomePesqCliente.setIdCliente( resultado.getInt("id_cliente") );
            nomePesqCliente.setIdNomePesquisa( resultado.getInt("id_nome_pesquisa") );
            nomePesqCliente.setNomePesquisa( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setNomePesquisaLimpo( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setPorcetualColisao( (float) resultado.getInt("taxa_proximidade") );
            nomePesqCliente.setNumProcesso( resultado.getBoolean("processo") );
            nomePesqCliente.setSitCad( resultado.getString("sit_cad") );
        }

        resultado.close();
        stm.close();

        return (nomePesqCliente.getIdNomePesquisa() == null) ? null : nomePesqCliente;
    }

    public String dadosListarNomePesquisaConcatenado(int idTermoPai, DbConnection dbconn) throws SQLException {
        String termoAuxiliar = null;
        final Statement stm = dbconn.obterStatement();
        final String sqlcmd = "SELECT np.nome_pesquisa FROM nome_pesquisa np WHERE np.id_termo_pai = " + idTermoPai + " AND np.tipo_termo_id = 4 ";
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        if ( resultado.next() ) {
            termoAuxiliar = resultado.getString("nome_pesquisa");
        }
        resultado.close();
        stm.close();
        return termoAuxiliar;
    }

    public NomePesquisaCliente[] dadosListarNomesPesquisaJornal(int idCliente, int idJornal, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<NomePesquisaCliente> listaNomesPesq = new ArrayList();
        
        // Comando 1, obtendo lista de termos referenciados um-a-um
        String sqlcmd = "SELECT  " +
                "    tb.id_termo_recusado,  " +
                "    np.blacklist_notify_dat,  " +
                "    cl.id_cliente,  " +
                "    cl.id_conta,  " +
                "    np.nome_pesquisa,  " +
                "    j.id_jornal,  " +
                "    np.id_nome_pesquisa,  " +
                "    np.literal,  " +
                "    np.sit_cad,  " +
                "    cl.taxa_proximidade,  " +
                "    np.todos_jornais,  " +
                "    np.processo " +
                "FROM  " +
                "    cliente cl " +
                "INNER JOIN  " +
                "    conta c ON c.id_conta = cl.id_conta " +
                "INNER JOIN  " +
                "    nome_pesquisa np ON np.id_cliente = cl.id_cliente " +
                "INNER JOIN  " +
                "    nome_pesquisa_jornal npj ON npj.id_nome_pesquisa = np.id_nome_pesquisa " +
                "INNER JOIN  " +
                "    jornal j ON j.id_jornal = npj.id_jornal " +
                "INNER JOIN  " +
                "    jornal_conta jc ON jc.id_conta = c.id_conta " +
                "LEFT JOIN  " +
                "    termos_bloqueados as tb ON tb.termo = np.nome_pesquisa " +
                "WHERE  " +
                "    c.sit_cad = 'A' " +
                "    AND j.id_jornal = " + idJornal + " " +
                "    AND np.id_cliente = cl.id_cliente " +
                "    AND cl.sit_cad = 'A' " +
                "    AND np.sit_cad = 'A' " +
                "    AND j.sit_cad = 'A' " +
                "    AND np.oab = false " +
                "    AND np.id_termo_pai is NULL ";
        
        // Adicionando o ID do cliente (se tiver/for_necessario)
        if ( idCliente != 0 )
            sqlcmd += " AND cl.id_cliente = " + idCliente + " ";

        // Completando comando SQL
        sqlcmd += " GROUP BY tb.id_termo_recusado, np.sit_cad, np.blacklist_notify_dat, cl.id_cliente, cl.id_conta, np.nome_pesquisa, j.id_jornal, np.id_nome_pesquisa, np.literal, cl.taxa_proximidade, np.todos_jornais ";
        sqlcmd += " ORDER BY cl.id_cliente ASC";
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        NomePesquisaCliente nomePesqCliente = null;

        while(resultado.next())
        {
            final String idTermoRecusado = resultado.getString("id_termo_recusado");

            nomePesqCliente = new NomePesquisaCliente();
            nomePesqCliente.setBlacklistNotifyDat(resultado.getDate("blacklist_notify_dat"));
            nomePesqCliente.setBlacklist((idTermoRecusado == null || idTermoRecusado.length() <= 0) ? false : true);
            nomePesqCliente.setLiteral( resultado.getBoolean("literal") );
            nomePesqCliente.setIdCliente( resultado.getInt("id_cliente") );
            nomePesqCliente.setIdNomePesquisa( resultado.getInt("id_nome_pesquisa") );
            nomePesqCliente.setNomePesquisa( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setNomePesquisaLimpo( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setPorcetualColisao( (float) resultado.getInt("taxa_proximidade") );
            nomePesqCliente.setNumProcesso( resultado.getBoolean("processo") );
            nomePesqCliente.setSitCad(resultado.getString("sit_cad"));

            // Anexando o termo filho (se houver)
            nomePesqCliente.setNomePesquisaExt(this.dadosListarNomePesquisaConcatenado(nomePesqCliente.getIdNomePesquisa(), dbconn));

            listaNomesPesq.add(nomePesqCliente);
        }
        
        resultado.close();
        stm.close();
        
        return listaNomesPesq.toArray(new NomePesquisaCliente[0]);
    }
    
    public Usuario[] dadosListarUsuarios(int idCliente, DbConnection dbconn)  throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<Usuario> listaUsuarios = new ArrayList();
        
        String sqlcmd = "SELECT u.id, u.id_cliente, u.nome, us.notification_token "
                + "FROM usuario AS u " 
                + "INNER JOIN usuario_session AS us ON us.id_usuario = u.id "
                + "WHERE u.sit_cad = 'A' AND id_cliente = '" + idCliente + "' "
                + "GROUP BY id, id_cliente, nome, notification_token";
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        Usuario usuarioLocated = null;
        while(resultado.next()) {
            usuarioLocated = new Usuario();
            usuarioLocated.setId(resultado.getInt("id"));
            usuarioLocated.setNome(resultado.getString("nome"));
            usuarioLocated.setIdCliente(resultado.getInt("id_cliente"));
            usuarioLocated.setNotificationToken(resultado.getString("notification_token"));
            
            listaUsuarios.add(usuarioLocated);
        }
        
        resultado.close();
        stm.close();
        
        return listaUsuarios.toArray(new Usuario[0]);
    }

    public Cliente listarClientePorID(int idCliente, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        String sqlcmd = "SELECT * FROM cliente WHERE id_cliente = " + idCliente;
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        if ( !resultado.next() )
            return null;

        Cliente cliente = new Cliente();
        cliente.setIdCliente(resultado.getInt("id_cliente"));
        cliente.setNome(resultado.getString("nome"));

        return cliente;
    }

    public List<Cliente> buscarClientesPorVeiculo(long veiculoId, DbConnection dbconn) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();


        final Statement stm = dbconn.obterStatement();
        String sqlcmd = "SELECT c.* " +
                "FROM cliente c " +
                "INNER JOIN perfil_pesquisa_cliente_web ppcw ON c.id_cliente = ppcw.cliente_id " +
                "WHERE ppcw.veiculo_id = " + veiculoId +
                " AND c.sit_cad = 'A'";
        ResultSet resultSet = dbconn.abrirConsultaSql(stm, sqlcmd);

        while (resultSet.next()) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(resultSet.getInt("id_cliente"));
            cliente.setIdConta(resultSet.getInt("id_conta"));
            cliente.setNome(resultSet.getString("nome"));
            cliente.setSexo(resultSet.getString("sexo"));
            cliente.setNumOab(resultSet.getString("num_oab"));
            cliente.setTpPessoa(resultSet.getString("tipo_pessoa"));
            cliente.setReceberEmail(resultSet.getString("receber_email"));
            cliente.setFone1(resultSet.getString("fone1"));
            cliente.setNomeContato(resultSet.getString("nome_contato"));
            cliente.setEndereco(resultSet.getString("endereco"));
            cliente.setBairro(resultSet.getString("bairro"));
            cliente.setCidade(resultSet.getString("cidade"));
            cliente.setDatCad(resultSet.getTimestamp("dat_cad"));
            cliente.setSitCad(resultSet.getString("sit_cad"));
            cliente.setUsuCad(resultSet.getInt("usu_cad"));
            cliente.setEmail(resultSet.getString("email"));

            clientes.add(cliente);
        }

        return clientes;
    }

    public List<Cliente> buscarClientesPorVeiculo_Historico(long veiculoId, DbConnection dbconn) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();


        final Statement stm = dbconn.obterStatement();
        String sqlcmd = "SELECT c.* " +
                "FROM cliente c " +
                "INNER JOIN perfil_pesquisa_cliente_web ppcw ON c.id_cliente = ppcw.cliente_id " +
                "WHERE ppcw.veiculo_id = " + veiculoId +
                " AND c.sit_cad = 'A' AND ppcw.novo_interesse = TRUE";
        ResultSet resultSet = dbconn.abrirConsultaSql(stm, sqlcmd);

        while (resultSet.next()) {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(resultSet.getInt("id_cliente"));
            cliente.setIdConta(resultSet.getInt("id_conta"));
            cliente.setNome(resultSet.getString("nome"));
            cliente.setSexo(resultSet.getString("sexo"));
            cliente.setNumOab(resultSet.getString("num_oab"));
            cliente.setTpPessoa(resultSet.getString("tipo_pessoa"));
            cliente.setReceberEmail(resultSet.getString("receber_email"));
            cliente.setFone1(resultSet.getString("fone1"));
            cliente.setNomeContato(resultSet.getString("nome_contato"));
            cliente.setEndereco(resultSet.getString("endereco"));
            cliente.setBairro(resultSet.getString("bairro"));
            cliente.setCidade(resultSet.getString("cidade"));
            cliente.setDatCad(resultSet.getTimestamp("dat_cad"));
            cliente.setSitCad(resultSet.getString("sit_cad"));
            cliente.setUsuCad(resultSet.getInt("usu_cad"));
            cliente.setEmail(resultSet.getString("email"));

            clientes.add(cliente);
        }

        return clientes;
    }

    public NomePesquisaCliente[] dadosListarNomesPesquisaVeiculo(long idCliente, long idVeiculo, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<NomePesquisaCliente> listaNomesPesq = new ArrayList();

        // Comando 1, obtendo lista de termos referenciados um-a-um
        String sqlcmd = "select tb.id_termo_recusado, np.sit_cad, np.blacklist_notify_dat, np.id_cliente, cl.id_conta, np.nome_pesquisa, v.id as id_jornal, np.id_nome_pesquisa, np.literal, cl.taxa_proximidade, np.todos_jornais, np.processo\n" +
                "from nome_pesquisa np \n" +
                "join nome_pesquisa_veiculos npv on npv.id_nome_pesquisa = np.id_nome_pesquisa\n" +
                "join cliente cl on cl.id_cliente = np.id_cliente \n" +
                "JOIN conta c ON c.id_conta = cl.id_conta\n" +
                "join veiculos v on v.id = npv.id_veiculo \n" +
                "LEFT JOIN termos_bloqueados as tb on tb.termo = np.nome_pesquisa\n" +
                "WHERE c.sit_cad='A' \n" +
                "       AND npv.id_veiculo = " + idVeiculo + " \n" +
                "       AND np.id_cliente = cl.id_cliente  \n" +
                "       AND cl.sit_cad='A' \n" +
                "       AND np.sit_cad='A' \n" +
                "       AND v.sit_cad='A' \n" +
                "       AND cl.mail_ready = TRUE\n" +
                "       AND np.oab = FALSE\n";

        // Adicionando o ID do cliente (se tiver/for_necessario)
        if ( idCliente != 0 )
            sqlcmd += " AND cl.id_cliente = " + idCliente + " ";

        // Completando comando SQL
        sqlcmd += " GROUP BY tb.id_termo_recusado, np.sit_cad, np.blacklist_notify_dat, cl.id_cliente, cl.id_conta, np.nome_pesquisa, v.id, np.id_nome_pesquisa, np.literal, cl.taxa_proximidade, np.todos_jornais ";
        sqlcmd += " ORDER BY cl.id_cliente asc";
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        NomePesquisaCliente nomePesqCliente = null;

        while(resultado.next())
        {
            final String idTermoRecusado = resultado.getString("id_termo_recusado");

            nomePesqCliente = new NomePesquisaCliente();
            nomePesqCliente.setBlacklistNotifyDat(resultado.getDate("blacklist_notify_dat"));
            nomePesqCliente.setBlacklist((idTermoRecusado == null || idTermoRecusado.length() <= 0) ? false : true);
            nomePesqCliente.setLiteral( resultado.getBoolean("literal") );
            nomePesqCliente.setIdCliente( resultado.getInt("id_cliente") );
            nomePesqCliente.setIdNomePesquisa( resultado.getInt("id_nome_pesquisa") );
            nomePesqCliente.setNomePesquisa( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setNomePesquisaLimpo( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setSitCad(resultado.getString("sit_cad"));
            System.out.println("------------------------------- Resultado: " + resultado.getString("nome_pesquisa").trim());
            nomePesqCliente.setPorcetualColisao( (float) resultado.getInt("taxa_proximidade") );
            nomePesqCliente.setNumProcesso( resultado.getBoolean("processo") );
            listaNomesPesq.add(nomePesqCliente);
        }

        resultado.close();
        stm.close();

        return listaNomesPesq.toArray(new NomePesquisaCliente[0]);
    }

    public NomePesquisaCliente[] dadosListarNomesPesquisaVeiculo_Historico(long idCliente, DbConnection dbconn) throws SQLException
    {
        final Statement stm = dbconn.obterStatement();
        ArrayList<NomePesquisaCliente> listaNomesPesq = new ArrayList();

        // Comando 1, obtendo lista de termos referenciados um-a-um
        String sqlcmd = "select np.*, npv.id_veiculo " +
                "from nome_pesquisa np " +
                "join nome_pesquisa_veiculos npv on np.id_nome_pesquisa = npv.id_nome_pesquisa " +
                "where np.sit_cad = 'A' and npv.novo_termo = true";
        // Adicionando o ID do cliente (se tiver/for_necessario)
        if ( idCliente != 0 )
            sqlcmd += " AND np.id_cliente = " + idCliente + " ";

        // Completando comando SQL
        ResultSet resultado = dbconn.abrirConsultaSql(stm, sqlcmd);
        NomePesquisaCliente nomePesqCliente = null;

        while(resultado.next())
        {
            final String idTermoRecusado = resultado.getString("id_termo_recusado");

            nomePesqCliente = new NomePesquisaCliente();
            nomePesqCliente.setBlacklistNotifyDat(resultado.getDate("blacklist_notify_dat"));
            nomePesqCliente.setBlacklist((idTermoRecusado == null || idTermoRecusado.length() <= 0) ? false : true);
            nomePesqCliente.setLiteral( resultado.getBoolean("literal") );
            nomePesqCliente.setIdCliente( resultado.getInt("id_cliente") );
            nomePesqCliente.setIdNomePesquisa( resultado.getInt("id_nome_pesquisa") );
            nomePesqCliente.setNomePesquisa( resultado.getString("nome_pesquisa").trim() );
            nomePesqCliente.setNomePesquisaLimpo( resultado.getString("nome_pesquisa").trim() );
            System.out.println("------------------------------- Resultado: " + resultado.getString("nome_pesquisa").trim());
            nomePesqCliente.setPorcetualColisao( (float) resultado.getInt("taxa_proximidade") );
            nomePesqCliente.setNumProcesso( resultado.getBoolean("processo") );
            nomePesqCliente.setSitCad( resultado.getString("sit_cad") );
            nomePesqCliente.setIdVeiculo( resultado.getLong("id_veiculo") );
            listaNomesPesq.add(nomePesqCliente);
        }

        resultado.close();
        stm.close();

        return listaNomesPesq.toArray(new NomePesquisaCliente[0]);
    }
}
