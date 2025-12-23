package espe.lono.db.models;

import java.sql.Timestamp;

public class Veiculo {
    private long id;
    private long tipoVeiculoId;
    private String descricao;
    private Long paisId;
    private Long estadoId;
    private Long cidadeId;
    private Timestamp datCad;
    private String sitCad;
    private long usuCad;
    private byte[] icoVeiculo;
    private String urlIconeVeiculo;
    private String urlVeiculo;
    private boolean maisRelevante;
    private boolean paywall;
    private boolean complex;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTipoVeiculoId() {
        return tipoVeiculoId;
    }

    public void setTipoVeiculoId(long tipoVeiculoId) {
        this.tipoVeiculoId = tipoVeiculoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getPaisId() {
        return paisId;
    }

    public void setPaisId(Long paisId) {
        this.paisId = paisId;
    }

    public Long getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Long estadoId) {
        this.estadoId = estadoId;
    }

    public Long getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(Long cidadeId) {
        this.cidadeId = cidadeId;
    }

    public Timestamp getDatCad() {
        return datCad;
    }

    public void setDatCad(Timestamp datCad) {
        this.datCad = datCad;
    }

    public String getSitCad() {
        return sitCad;
    }

    public void setSitCad(String sitCad) {
        this.sitCad = sitCad;
    }

    public long getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(long usuCad) {
        this.usuCad = usuCad;
    }

    public byte[] getIcoVeiculo() {
        return icoVeiculo;
    }

    public void setIcoVeiculo(byte[] icoVeiculo) {
        this.icoVeiculo = icoVeiculo;
    }

    public String getUrlIconeVeiculo() {
        return urlIconeVeiculo;
    }

    public void setUrlIconeVeiculo(String urlIconeVeiculo) {
        this.urlIconeVeiculo = urlIconeVeiculo;
    }

    public String getUrlVeiculo() {
        return urlVeiculo;
    }

    public void setUrlVeiculo(String urlVeiculo) {
        this.urlVeiculo = urlVeiculo;
    }

    public boolean isMaisRelevante() {
        return maisRelevante;
    }

    public void setMaisRelevante(boolean maisRelevante) {
        this.maisRelevante = maisRelevante;
    }

    public boolean isPaywall() {
        return paywall;
    }

    public void setPaywall(boolean paywall) {
        this.paywall = paywall;
    }

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }
}
