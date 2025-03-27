package espe.lono.db.models;


import java.util.Date;

/**
 *
 * @author Espe
 */

public class MarcacaoPublicacao  {
    private Integer idMarcacao;    
    private Integer idPublicacao;   
    private Long numDocLucene;
    private Integer idTipoPadrao;    
    private String marcacao;    
    private String marcacao_original;    
    private Integer linhaPublicacao;    
    private Date datCad;   
    private String sitCad;   
    private int usuCad;
    private int pagina;    
    private int linha_pagina;    
    private int id_tipo_padrao_jornal;
    private boolean complex;

    public boolean isComplex() {
        return complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }
    
    
    public int getPagina()
    {
        return pagina;
    }

    public void setPagina( int pagina )
    {
        this.pagina = pagina;
    }

    public int getLinhaPagina()
    {
        return linha_pagina;
    }

    public void setLinhaPagina( int linha_pagina )
    {
        this.linha_pagina = linha_pagina;
    }
    

    public MarcacaoPublicacao() {
    }

    public MarcacaoPublicacao(Integer idMarcacao) {
        this.idMarcacao = idMarcacao;
    }

    public MarcacaoPublicacao(Integer idMarcacao, Date datCad, String sitCad, int usuCad) {
        this.idMarcacao = idMarcacao;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdMarcacao() {
        return idMarcacao;
    }

    public void setIdMarcacao(Integer idMarcacao) {
        this.idMarcacao = idMarcacao;
    }

    public Integer getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public Long getNumDocLucene() {
        return numDocLucene;
    }

    public void setNumDocLucene(Long numDocLucene) {
        this.numDocLucene = numDocLucene;
    }

    public String getMarcacaoOriginal()
    {
        return marcacao_original;
    }

    public void setMarcacaoOriginal( String marcacao_original )
    {
        this.marcacao_original = marcacao_original;
    }

    
    public String getMarcacao() {
        return marcacao;
    }

    public void setMarcacao(String marcacao) {
        this.marcacao = marcacao;
    }

    public Integer getLinhaPublicacao() {
        return linhaPublicacao;
    }

    public void setLinhaPublicacao(Integer linhaPublicacao) {
        this.linhaPublicacao = linhaPublicacao;
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

    public Integer getIdTipoPadrao() {
        return idTipoPadrao;
    }

    public void setIdTipoPadrao(Integer idTipoPadrao) {
        this.idTipoPadrao = idTipoPadrao;
    }
    
    public void setIdTipoPadraoJornal(Integer idTipoPadraoJornal) {
        this.id_tipo_padrao_jornal = idTipoPadraoJornal;
    }
    
    public Integer getIdTipoPadraoJornal() {
        return this.id_tipo_padrao_jornal;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMarcacao != null ? idMarcacao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MarcacaoPublicacao)) {
            return false;
        }
        MarcacaoPublicacao other = (MarcacaoPublicacao) object;
        if ((this.idMarcacao == null && other.idMarcacao != null) || (this.idMarcacao != null && !this.idMarcacao.equals(other.idMarcacao))) {
            return false;
        }
        return true;
    }

    
}
