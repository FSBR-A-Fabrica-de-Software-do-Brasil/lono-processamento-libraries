package espe.lono.db.models;

import java.util.Date;

public class TipoPadraoVeiculos {
    private Integer idTipoPadraoVeiculos;
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
    private Integer usuCad;
    private String mapFont;
    private Integer idTipoPadraoCheck;
    private Integer qtdMaxResultQuery;
    private String direcao;
    private Boolean complexMode;
    private TipoPadrao tipoPadrao;

    public TipoPadraoVeiculos(){}

    public TipoPadraoVeiculos(Integer idTipoPadraoVeiculos, Integer idPadrao, Integer idTipoPadrao,
                              String queryIni, String acao, Date datCad, String sitCad, Integer usuCad) {
        this.idTipoPadraoVeiculos = idTipoPadraoVeiculos;
        this.idPadrao = idPadrao;
        this.idTipoPadrao = idTipoPadrao;
        this.queryIni = queryIni;
        this.acao = acao;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public TipoPadrao getTipoPadrao() {
        return tipoPadrao;
    }

    public void setTipoPadrao(TipoPadrao tipoPadrao) {
        this.tipoPadrao = tipoPadrao;
    }

    public Integer getIdTipoPadraoVeiculos() {
        return idTipoPadraoVeiculos;
    }

    public void setIdTipoPadraoVeiculos(Integer idTipoPadraoVeiculos) {
        this.idTipoPadraoVeiculos = idTipoPadraoVeiculos;
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
        return (qtdLinhaAjusteAcima == 0) ? null : (qtdLinhaAjusteAcima - 1);
    }

    public void setQtdLinhaAjusteAcima(Integer qtdLinhaAjusteAcima) {
        this.qtdLinhaAjusteAcima = qtdLinhaAjusteAcima;
    }

    public Integer getQtdLinhaAjusteAbaixo() {
        return (qtdLinhaAjusteAbaixo == 0) ? null : (qtdLinhaAjusteAbaixo - 1);
    }

    public void setQtdLinhaAjusteAbaixo(Integer qtdLinhaAjusteAbaixo) {
        this.qtdLinhaAjusteAbaixo = qtdLinhaAjusteAbaixo;
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

    public Integer getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(Integer usuCad) {
        this.usuCad = usuCad;
    }

    public String getMapFont() {
        return mapFont;
    }

    public void setMapFont(String mapFont) {
        this.mapFont = mapFont;
    }

    public Integer getIdTipoPadraoCheck() {
        return idTipoPadraoCheck;
    }

    public void setIdTipoPadraoCheck(Integer idTipoPadraoCheck) {
        this.idTipoPadraoCheck = idTipoPadraoCheck;
    }

    public Integer getQtdMaxResultQuery() {
        return qtdMaxResultQuery;
    }

    public void setQtdMaxResultQuery(Integer qtdMaxResultQuery) {
        this.qtdMaxResultQuery = qtdMaxResultQuery;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    public Boolean getComplexMode() {
        return complexMode;
    }

    public void setComplexMode(Boolean complexMode) {
        this.complexMode = complexMode;
    }
}
