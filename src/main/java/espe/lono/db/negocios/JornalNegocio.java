package espe.lono.db.negocios;

import espe.lono.db.Fachada;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.dao.JornalDAO;
import espe.lono.db.models.AdicionalMarcacaoTipoPadrao;
import espe.lono.db.models.Jornal;
import espe.lono.db.models.PadraoJornal;
import espe.lono.db.models.PublicacaoJornal;
import espe.lono.db.models.TipoPadrao;
import espe.lono.db.models.TipoPadraoJornal;
import java.sql.SQLException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Espe
 */
public class JornalNegocio {    
    protected JornalDAO jornalDAO = new JornalDAO();
    final static private Logger logger = Logger.getLogger("mercurio3");
    
    public PadraoJornal[] obterListaPadraoJornal(int idJornal, DbConnection dbconn) throws SQLException  {
        return jornalDAO.obterListaPadraoJornal(idJornal, dbconn);
    }
    
    public Jornal[] obterListaJornais(DbConnection dbconn) throws SQLException {
        return jornalDAO.obterListaJornais(dbconn);
    }
    
    public Jornal obterDadosJornalPorID(int idJornal, DbConnection dbconn) throws SQLException
    {
        return jornalDAO.obterDadosDoJornal(idJornal, dbconn);
    }
    
    public TipoPadraoJornal[] listarTiposPadraoPublicacao(PublicacaoJornal pubJornal, DbConnection dbconn) throws SQLException
    {     
        return jornalDAO.dadosListarTiposPadraoPublicacao(pubJornal, dbconn);      
    }
    
    public TipoPadraoJornal[] listarTiposPadraoPublicacao(int idPadrao, DbConnection dbconn) throws SQLException
    {     
        return jornalDAO.dadosListarTiposPadraoPublicacao(idPadrao, dbconn);      
    }
    
    public boolean adicionarTipoPadraoJornal(TipoPadraoJornal tipoPadraoJornal, DbConnection dbconn) throws SQLException {
        return jornalDAO.adicionarTipoPadraoJornal(tipoPadraoJornal, dbconn);
    }
    
    public boolean atualizarTipoPadraoJornal(TipoPadraoJornal tipoPadraoJornal, DbConnection dbconn) throws SQLException {
        return jornalDAO.atualizarTipoPadraoJornal(tipoPadraoJornal, dbconn);
    }
    
    public String listarFontesTipoPadraoPublicacao(int idPublicacao, String mapFonte, DbConnectionMarcacao sqlite) throws SQLException
    {
        return jornalDAO.dadosListarFontesTipoPadraoPublicacao(idPublicacao, mapFonte,sqlite);
    }
    
    public String listarMapaFonteTipoPadraoPublicacao(int idPublicacao, String fontValue, DbConnectionMarcacao sqlite) throws SQLException
    {
        return jornalDAO.dadosListarMapaFontesTipoPadraoPublicacao(idPublicacao, fontValue,sqlite);
    }
    
    public TipoPadraoJornal atualizaQueryFontes(TipoPadraoJornal tipoPadraoJornal, int idPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        // Copiando a classe original (caso ocorra erro na obtencao da fonte)
        TipoPadraoJornal originalFont = (TipoPadraoJornal) Fachada.CopyObject(tipoPadraoJornal);
        
        //Realizando substituicoes na query nas referencias das fontes se exisitir
        boolean anyFontChanged = false;
        if ( tipoPadraoJornal.getMapeamentoFonte() != null && !tipoPadraoJornal.getMapeamentoFonte().trim().equals("") )
        {
            String[] fontes = tipoPadraoJornal.getMapeamentoFonte().split(",");
            for (int x = 0; x < fontes.length; x++ )
            {
                String[] dadosMapFonte = fontes[x].split("\\|");

                //Para evitar erro, só realiza a substituição da fonte caso exista a referência
                if(dadosMapFonte.length > 0)
                {
                    String newFontText = jornalDAO.dadosListarFontesTipoPadraoPublicacao(idPublicacao, dadosMapFonte[1],sqlite);
                    if ( newFontText.length() > 0 ) {
                        anyFontChanged = true;
                        tipoPadraoJornal.setQueryIni(tipoPadraoJornal.getQueryIni().replaceAll("ft" + dadosMapFonte[0] + "#", "(" + newFontText + ")"));
                    } else {
                        tipoPadraoJornal.setQueryIni(tipoPadraoJornal.getQueryIni().replaceAll("\\|ft" + dadosMapFonte[0] + "#", ""));
                        tipoPadraoJornal.setQueryIni(tipoPadraoJornal.getQueryIni().replaceAll("ft" + dadosMapFonte[0] + "#", ""));
                        logger.error("Fonte nao encontrada: IDTipoPadraoJornal " + tipoPadraoJornal.getIdTipoPadraoJornal() + ", FontMap: " + dadosMapFonte[1]);
                    }
                }                    
            } // for
        }//if

        if ( anyFontChanged == false ) return originalFont; // Not Changed
        else return tipoPadraoJornal;
    }
    
    public AdicionalMarcacaoTipoPadrao atualizaQueryFontes(AdicionalMarcacaoTipoPadrao addMarcacao, int idPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
            //Realizando substituicoes na query nas referencias das fontes se exisitir
            if ( addMarcacao.getTipoPadraoJornal().getMapeamentoFonte() != null && !addMarcacao.getTipoPadraoJornal().getMapeamentoFonte().trim().equals("") )
            {
                String[] fontes = addMarcacao.getTipoPadraoJornal().getMapeamentoFonte().split(",");
                for(int x = 0; x < fontes.length; x++)
                {
                    String[] dadosMapFonte = fontes[x].split("\\|");
                    addMarcacao.getTipoPadraoJornal().setQueryIni(addMarcacao.getTipoPadraoJornal().getQueryIni().replaceAll("ft" + dadosMapFonte[0] + "#", "(" + jornalDAO.dadosListarFontesTipoPadraoPublicacao(idPublicacao, dadosMapFonte[1], sqlite) + ")"));
                } //for   
            } //if
            
            int tamanhoTexto = addMarcacao.getTextoAdicional().length();
            String[] marcacaoIndividual = addMarcacao.getTextoAdicional().trim().split(" ");
            StringBuilder textoBuscaMarcacao = new StringBuilder();
            for ( int x = 0; x < marcacaoIndividual.length; x++ )
            {
                textoBuscaMarcacao.append("+conteudo:");
                if(x == 0)
                {
                    //Utilizado na rotina adicional para pegar alinha exata, pode melhorar
                    textoBuscaMarcacao.append("\"");
                    textoBuscaMarcacao.append(marcacaoIndividual[x]);
                    textoBuscaMarcacao.append(" class\"~4 ");
                    //Limitando a pesquisa a largura do texto
                    textoBuscaMarcacao.append("+tamanho:[");
                    textoBuscaMarcacao.append(String.format("%05d", (tamanhoTexto - 1)));
                    textoBuscaMarcacao.append(" TO ");
                    textoBuscaMarcacao.append(String.format("%05d", (tamanhoTexto + 1)));
                    textoBuscaMarcacao.append("]");
                }
                else
                {
                    textoBuscaMarcacao.append(marcacaoIndividual[x]);
                }
                
                textoBuscaMarcacao.append(" ");
            }
            
            addMarcacao.setTextoAdicional(textoBuscaMarcacao.toString() + addMarcacao.getTipoPadraoJornal().getQueryIni());
            return addMarcacao;
    }
    
    public boolean checarMarcacao(String textoCheck, int idTipoPadraoCheck, int idPublicacao, DbConnectionMarcacao sqlite) throws SQLException
    {
        return jornalDAO.checarMarcacao(textoCheck, idTipoPadraoCheck, idPublicacao, sqlite);
    }
    
    public TipoPadrao[] listarTiposPadrao(DbConnection dbconn) throws SQLException {
        return jornalDAO.listarTiposPadrao(dbconn);
    }
    
    public boolean adicionarPadraoJornal(PadraoJornal padraoJornal, DbConnection dbconn) throws SQLException {
        return jornalDAO.adicionarPadraoJornal(padraoJornal, dbconn);
    }
    
    public boolean atualizarPadraoJornal(PadraoJornal padraoJornal, DbConnection dbconn) throws SQLException {
        return jornalDAO.atualizarPadraoJornal(padraoJornal, dbconn);
    }

    public Jornal localizarJornalSigla(String sigla_jornal, DbConnection dbconn) throws SQLException {
        return jornalDAO.obterDadosDoJornal(sigla_jornal, dbconn);
    }

    public void atualizarJornalLastProc(Jornal jornal, DbConnection dbconn) throws SQLException {
        jornalDAO.atualizarJornalLastProc(jornal.getIdJornal(), dbconn);
    }
}
