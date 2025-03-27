package espe.lono.db.models;

import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author Espe
 */

public class MateriaPublicacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer idMateria;    
    private Integer idJornal;    
    private String siglaJornal;    
    private Integer idPublicacao;    
    private Integer idCliente;   
    private String tituloMateria;   
    private String subtituloMateria;   
    private String preMateria;   
    private String processo;    
    private String materiaHash;    
    private boolean pesquisaProximidade;    
    private String termoPesquisaProximidade;
    private String materia;   
    private Integer pagina;    
    private Date datCad;   
    private String sitCad;   
    private int usuCad;
    private long linhaCliente;
    private long linhaTitulo;
    private long linhaInicialMateria;
    private long linhaFinalMateria;

    private int idNomePesquisa;    
    private int paginaFimMateria;
    private boolean corte_lono;

    // Other
    private String termo;

    public boolean isPesquisaProximidade()
    {
        return pesquisaProximidade;
    }

    public void setPesquisaProximidade( boolean pesquisaProximidade )
    {
        this.pesquisaProximidade = pesquisaProximidade;
    }

    public String getMateriaHash()
    {
        return materiaHash;
    }

    public void setMateriaHash( String materiaHash )
    {
        this.materiaHash = materiaHash;
    }

    public String getProcesso()
    {
        return processo;
    }

    public void setProcesso( String processo )
    {
        this.processo = processo;
    }

    public String getTermoPesquisaProximidade()
    {
        return termoPesquisaProximidade;
    }

    public void setTermoPesquisaProximidade( String termoPesquisaProximidade )
    {
        this.termoPesquisaProximidade = termoPesquisaProximidade;
    }
    

    public MateriaPublicacao() {
        this.termoPesquisaProximidade = "";
    }

    public MateriaPublicacao(Integer idMateria) {
        this.idMateria = idMateria;
        this.termoPesquisaProximidade = "";
    }

    public MateriaPublicacao(Integer idMateria, Date datCad, String sitCad, int usuCad) {
        this.idMateria = idMateria;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
        this.termoPesquisaProximidade = "";
    }

    public int getPaginaFimMateria()
    {
        return paginaFimMateria;
    }

    public void setPaginaFimMateria( int paginaFimMateria )
    {
        this.paginaFimMateria = paginaFimMateria;
    }

    
    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }

    public Integer getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTituloMateria() {
        return tituloMateria;
    }

    public void setTituloMateria(String tituloMateria) {
        this.tituloMateria = tituloMateria;
    }

    public String getSubtituloMateria() {
        return subtituloMateria;
    }

    public void setSubtituloMateria(String subtituloMateria) {
        this.subtituloMateria = subtituloMateria;
    }

    public String getPreMateria() {
        return preMateria;
    }

    public void setPreMateria(String preMateria) {
        this.preMateria = preMateria;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
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

    public int getIdNomePesquisa() {
        return idNomePesquisa;
    }

    public void setIdNomePesquisa(int idNomePesquisa) {
        this.idNomePesquisa = idNomePesquisa;
    }

    public boolean getCorteLono()
    {
        return corte_lono;
    }

    public void setCorteLono( boolean corte_lono )
    {
        this.corte_lono = corte_lono;
    }

    public Integer getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(Integer idJornal) {
        this.idJornal = idJornal;
    }    

    public String getSiglaJornal() {
        return siglaJornal;
    }

    public void setSiglaJornal(String siglaJornal) {
        this.siglaJornal = siglaJornal;
    }

    public long getLinhaCliente() {
        return linhaCliente;
    }

    public void setLinhaCliente(long linhaCliente) {
        this.linhaCliente = linhaCliente;
    }

    public long getLinhaTitulo() {
        return linhaTitulo;
    }

    public void setLinhaTitulo(long linhaTitulo) {
        this.linhaTitulo = linhaTitulo;
    }

    public long getLinhaInicialMateria() {
        return linhaInicialMateria;
    }

    public void setLinhaInicialMateria(long linhaInicialMateria) {
        this.linhaInicialMateria = linhaInicialMateria;
    }

    public long getLinhaFinalMateria() {
        return linhaFinalMateria;
    }

    public void setLinhaFinalMateria(long linhaFinalMateria) {
        this.linhaFinalMateria = linhaFinalMateria;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMateria != null ? idMateria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MateriaPublicacao)) {
            return false;
        }
        MateriaPublicacao other = (MateriaPublicacao) object;
        if ((this.idMateria == null && other.idMateria != null) || (this.idMateria != null && !this.idMateria.equals(other.idMateria))) {
            return false;
        }
        return true;
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(String termo) {
        this.termo = termo;
    }
}