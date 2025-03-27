package espe.lono.db.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import espe.lono.db.exceptions.*;
import espe.lono.db.utils.DBUtils;

/**
 *
 * @author Espe
 *
 */
public class PublicacaoJornal
{
    /**
     * Lista(enum) de status da 'Publicação'
     * Nota: usada mais como referencias ao consultar no banco de dados
     */
    public enum Status
    {
        SIT_ARQ_PUBLICADO("A"), // Arquivo publicado... Ainda não foi 'tocado'
        SIT_ARQ_MOV_AGUARDANDO_PROCESSAMENTO("E"), // Movido, aguard. processamento
        SIT_ARQ_INDEXADO("I"), // Publicação indexado, pronto para a pesquisa/corte
        SIT_ARQ_PROCESSANDO("I"), // Publicação sendo processado no momento...
        SIT_ARQ_PROCESSADO("F"), // Publicacao processada com sucesso.
        SIT_ERROR("X"), // Publicacao com erro no processamento
        SIT_ARQ_ARMAZENADO("M"), // Publicacao movida e armazenada externamente
        SIT_ARQ_REPROCESSAMENTO("R"), // Em status de Reprocessamento
        SIT_PUB_HISTORICO("H"); // Anexada ao histórico!
        
        private final String value;
        private Status(String value)
        {
            this.value = value;
        }
        @Override
        public String toString() { return this.value; }
    }
    
    private Integer idPublicacao;
    private Integer idPublicacaoAnteriorJornal;
    private Integer idJornal;
    private String arqPublicacao;
    private Integer totalPagina;
    private Date dtPublicacao;
    private Date dtDivulgacao;
    private String edicaoPublicacao;
    private Date datCad;
    private String sitCad;
    private int usuCad;
    //Padrao a ser utilizado de acordo com o período(validade do padrão) da publicacao/jornal
    private PadraoJornal padraoJornalPublicacao;
    //Dados do jornal vinculado a publicação
    private Jornal jornalPublicacao;

    public PublicacaoJornal() {
    }

    public PublicacaoJornal(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public PublicacaoJornal(Integer idPublicacao, Date datCad, String sitCad, int usuCad) {
        this.idPublicacao = idPublicacao;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public Integer getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(Integer idJornal) {
        this.idJornal = idJornal;
    }

    public String getArqPublicacao() {
        return arqPublicacao;
    }

    public void setArqPublicacao(String arqPublicacao) {
        this.arqPublicacao = arqPublicacao;
    }

    public Integer getTotalPagina() {
        return totalPagina;
    }

    public void setTotalPagina(Integer totalPagina) {
        this.totalPagina = totalPagina;
    }

    public Date getDtPublicacao() {
        return dtPublicacao;
    }

    public void setDtPublicacao(Date dtPublicacao) {
        this.dtPublicacao = dtPublicacao;
    }

    public void setDtPublicacao(String dtPublicacao, String fmtPattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(fmtPattern);
        this.dtPublicacao = sdf.parse(dtPublicacao);
    }

    public Date getDtDivulgacao() {
        return dtDivulgacao;
    }

    public void setDtDivulgacao(Date dtDivulgacao) {
        this.dtDivulgacao = dtDivulgacao;
    }

    public void setDtDivulgacao(String dtDivulgacao, String fmtPattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(fmtPattern);
        this.dtDivulgacao = sdf.parse(dtDivulgacao);
    }

    public String getEdicaoPublicacao() {
        return edicaoPublicacao;
    }

    public void setEdicaoPublicacao(String edicaoPublicacao) {
        this.edicaoPublicacao = edicaoPublicacao;
    }

    public Date getDatCad() {
        return datCad;
    }

    public void setDatCad(Date datCad) {
        this.datCad = datCad;
    }

    public String getSitCad() {
        return sitCad;
    }

    public void setSitCad(String sitCad) {
        this.sitCad = sitCad;
    }

    public int getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(int usuCad) {
        this.usuCad = usuCad;
    }

    public PadraoJornal getPadraoJornalPublicacao() {
        return padraoJornalPublicacao;
    }

    public void setPadraoJornalPublicacao(PadraoJornal padraoJornalPublicacao) {
        this.padraoJornalPublicacao = padraoJornalPublicacao;
    }

    public Jornal getJornalPublicacao() {
        return jornalPublicacao;
    }

    public void setJornalPublicacao(Jornal jornalPublicacao) {
        this.jornalPublicacao = jornalPublicacao;
    }
    
    public Integer getIdPublicacaoAnteriorJornal()
    {
        return idPublicacaoAnteriorJornal;
    }

    public void setIdPublicacaoAnteriorJornal( Integer idPublicacaoAnteriorJornal )
    {
        this.idPublicacaoAnteriorJornal = idPublicacaoAnteriorJornal;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPublicacao != null ? idPublicacao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PublicacaoJornal)) {
            return false;
        }
        PublicacaoJornal other = (PublicacaoJornal) object;
        if ((this.idPublicacao == null && other.idPublicacao != null) || (this.idPublicacao != null && !this.idPublicacao.equals(other.idPublicacao))) {
            return false;
        }
        return true;
    }

    public String getCaminhoDirPublicacaoIndice(String diretorioDocs) {
        String[] dadosDtPub = DBUtils.DadosData(this.getDtPublicacao());
        return diretorioDocs + "/" + dadosDtPub[0] + "/" + dadosDtPub[1] + "/" + dadosDtPub[2] + "/" + this.getIdJornal() + "/" + this.getIdPublicacao() + "/indice/";
    }

    public String getCaminhoDirPublicacao(String diretorioDocs) {
        String[] dadosDtPub = DBUtils.DadosData(this.getDtPublicacao());
        return diretorioDocs + "/" + dadosDtPub[0] + "/" + dadosDtPub[1] + "/" + dadosDtPub[2] + "/" + this.getIdJornal() + "/" + this.getIdPublicacao() + "/";
    }

    public TipoPadraoJornal getTipoPadraoPorChave(String chave) throws ChavePadraoNaoEncontradaException {
        TipoPadraoJornal[] tiposPadraoJornal = this.getPadraoJornalPublicacao().getTiposPadraoJornal();
        for (int x = 0; x < tiposPadraoJornal.length; x++) {
            if (tiposPadraoJornal[x].getTipoPadrao().getChaveTipo().equalsIgnoreCase(chave)) {
                return tiposPadraoJornal[x];
            }
        }
        throw new ChavePadraoNaoEncontradaException();
    }

    public TipoPadraoJornal[] getTiposParaRemocao()
    {
        TipoPadraoJornal[] tiposPadraoJornal = this.getPadraoJornalPublicacao().getTiposPadraoJornal();
        ArrayList<TipoPadraoJornal> listaTiposPadrao = new ArrayList();
        
        for ( TipoPadraoJornal padraoJornal: tiposPadraoJornal )
        {
            // Checando se o status e ATIVO
            if ( padraoJornal.getSitCad().equals("A") == false )
                continue; // Inativo
            
            // Checando Acao
            if ( padraoJornal.getAcao().equalsIgnoreCase("E") )
            {
                listaTiposPadrao.add(padraoJornal);
                continue;
            }
            
            // Checando o tipo de padrao
            int tipoPadrao = padraoJornal.getIdTipoPadrao();
            switch ( tipoPadrao )
            {
                //case 1: // Titulo
                //case 2: // Subtitulo
                //case 8: // Rodape
                case 9: // Cabecalho
                case 25: // Conteudo Fixo Materia ?? Pq essa regra?
                    listaTiposPadrao.add(padraoJornal);
                    break;
                default:
                    break;
            }
        }
        
        return listaTiposPadrao.toArray(new TipoPadraoJornal[0]);
    }
    
    public TipoPadraoJornal[] getTiposPadraoPorIdTipoPadrao(int id_tipo_padrao)
    {
        TipoPadraoJornal[] tiposPadraoJornal = this.getPadraoJornalPublicacao().getTiposPadraoJornal();
        ArrayList<TipoPadraoJornal> listaTiposPadrao = new ArrayList();
        
        for (int x = 0; x < tiposPadraoJornal.length; x++)
        {
            if (tiposPadraoJornal[x].getIdTipoPadrao() == id_tipo_padrao )
            {
                listaTiposPadrao.add(tiposPadraoJornal[x]);
            }
        }
        return listaTiposPadrao.toArray(new TipoPadraoJornal[0]);
    }
    
    public TipoPadraoJornal[] getTiposPadraoPorTipoChave(String tipo) {
        TipoPadraoJornal[] tiposPadraoJornal = this.getPadraoJornalPublicacao().getTiposPadraoJornal();
        ArrayList<TipoPadraoJornal> listaTiposPadrao = new ArrayList();
        
        for (int x = 0; x < tiposPadraoJornal.length; x++) {
            if (tiposPadraoJornal[x].getAcao().equalsIgnoreCase(tipo)) {
                listaTiposPadrao.add(tiposPadraoJornal[x]);
            }
        }
        return listaTiposPadrao.toArray(new TipoPadraoJornal[listaTiposPadrao.size()]);
    }
    
    public TipoPadraoJornal[] getTiposPadraoComplexos() {
        TipoPadraoJornal[] tiposPadraoJornal = this.getPadraoJornalPublicacao().getTiposPadraoJornal();
        ArrayList<TipoPadraoJornal> listaTiposPadrao = new ArrayList();
        
        for (int x = 0; x < tiposPadraoJornal.length; x++) {
            if (tiposPadraoJornal[x].isComplex_mode()) {
                listaTiposPadrao.add(tiposPadraoJornal[x]);
            }
        }
        return listaTiposPadrao.toArray(new TipoPadraoJornal[listaTiposPadrao.size()]);
    }
}