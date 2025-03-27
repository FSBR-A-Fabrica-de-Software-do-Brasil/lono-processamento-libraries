package espe.lono.db.models;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Espe
 */

public class TipoPadraoJornal{    
    private Integer idTipoPadraoJornal;    
    private Integer idPadrao;    
    private Integer idTipoPadrao;    
    private String queryIni;    
    private String queryFim;    
    private String regexIni;   
    private String regexFim;    
    private String trechosReplace;    
    private Integer qtdLinhaAjusteAcima;    
    private Integer qtdLinhaAjusteAbaixo;    
    private String acao;    
    private Date datCad;    
    private String sitCad;   
    private int usuCad;    
    private TipoPadrao tipoPadrao;    
    private String mapeamentoFonte;    
    private int idTipoPadraoCheck;    
    private int qtdMaxResultQuery;    
    private String direcao;
    private boolean complex_mode;

    public boolean isComplex_mode() {
        return complex_mode;
    }

    public void setComplex_mode(boolean complex_mode) {
        this.complex_mode = complex_mode;
    }
    

    public TipoPadraoJornal() {
    }

    public TipoPadraoJornal(Integer idTipoPadraoJornal) {
        this.idTipoPadraoJornal = idTipoPadraoJornal;
    }

    public TipoPadraoJornal(Integer idTipoPadraoJornal, String acao, Date datCad, String sitCad, int usuCad) {
        this.idTipoPadraoJornal = idTipoPadraoJornal;
        this.acao = acao;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdTipoPadraoJornal() {
        return idTipoPadraoJornal;
    }

    public void setIdTipoPadraoJornal(Integer idTipoPadraoJornal) {
        this.idTipoPadraoJornal = idTipoPadraoJornal;
    }

    public Integer getIdPadrao() {
        return idPadrao;
    }

    public void setIdPadrao(Integer idPadrao) {
        this.idPadrao = idPadrao;
    }

    public Integer getIdTipoPadrao() {
        return idTipoPadrao;
    }

    public void setIdTipoPadrao(Integer idTipoPadrao) {
        this.idTipoPadrao = idTipoPadrao;
    }

    public String getQueryIni() {
        return queryIni;     
    }

    public void setQueryIni(String queryIni) {
        this.queryIni = queryIni;
    }

    public String getQueryFim() {
        return queryFim;
    }

    public void setQueryFim(String queryFim) {
        this.queryFim = queryFim;
    }

    public String getRegexIni() {
        return regexIni;
    }

    public void setRegexIni(String regexIni) {
        this.regexIni = regexIni;
    }

    public String getRegexFim() {
        return regexFim;
    }

    public void setRegexFim(String regexFim) {
        this.regexFim = regexFim;
    }

    public String getTrechosReplace() {
        return trechosReplace;
    }

    public void setTrechosReplace(String trechosReplace) {
        this.trechosReplace = trechosReplace;
    }

    public Integer getQtdLinhaAjusteAcima() {
        return qtdLinhaAjusteAcima;
    }

    public void setQtdLinhaAjusteAcima(Integer qtdLinhaAjusteAcima) {
        this.qtdLinhaAjusteAcima = (qtdLinhaAjusteAcima == null?0:qtdLinhaAjusteAcima);
    }

    public Integer getQtdLinhaAjusteAbaixo() {
        return qtdLinhaAjusteAbaixo;
    }

    public void setQtdLinhaAjusteAbaixo(Integer qtdLinhaAjusteAbaixo) {
        this.qtdLinhaAjusteAbaixo = (qtdLinhaAjusteAbaixo == null?0:qtdLinhaAjusteAbaixo);
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
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

    public TipoPadrao getTipoPadrao() {
        return tipoPadrao;
    }

    public void setTipoPadrao(TipoPadrao tipoPadrao) {
        this.tipoPadrao = tipoPadrao;
    }

    public String getMapeamentoFonte() {
        return mapeamentoFonte;
    }

    public void setMapeamentoFonte(String mapeamentoFonte) {
        this.mapeamentoFonte = mapeamentoFonte;
    }

    public int getIdTipoPadraoCheck() {
        return idTipoPadraoCheck;
    }

    public void setIdTipoPadraoCheck(int idTipoPadraoCheck) {
        this.idTipoPadraoCheck = idTipoPadraoCheck;
    }

    public int getQtdMaxResultQuery() {
        if(qtdMaxResultQuery == 0){
            qtdMaxResultQuery = 1000000; //Qtd padrão máxima de resultados de uma query lucene
        }
        return qtdMaxResultQuery;
    }

    public void setQtdMaxResultQuery(int qtdMaxResultQuery) {
        this.qtdMaxResultQuery = qtdMaxResultQuery;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoPadraoJornal != null ? idTipoPadraoJornal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoPadraoJornal)) {
            return false;
        }
        TipoPadraoJornal other = (TipoPadraoJornal) object;
        if ((this.idTipoPadraoJornal == null && other.idTipoPadraoJornal != null) || (this.idTipoPadraoJornal != null && !this.idTipoPadraoJornal.equals(other.idTipoPadraoJornal))) {
            return false;
        }
        return true;
    }
    
}