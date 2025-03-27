package espe.lono.db.models;

import java.util.Date;


/**
 *
 * @author Espe
 */

public class Jornal {
    private Integer idJornal;    
    private String siglaOrgao;    
    private String orgaoJornal;    
    private String siglaJornal;    
    private String descJornal;    
    private String urlJornal;   
    private Date dtCad;    
    private String sitCad;    
    private int usuCad;
    private String nomeJornal;
    private Integer limiteLinhaMateria;
    private boolean edicaoDetectavel;

    public Jornal() {
    }

    public Jornal(Integer idJornal) {
        this.idJornal = idJornal;
    }

    public Jornal(Integer idJornal, Date dtCad, String sitCad, int usuCad) {
        this.idJornal = idJornal;
        this.dtCad = dtCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(Integer idJornal) {
        this.idJornal = idJornal;
    }

    public String getSiglaOrgao() {
        return siglaOrgao;
    }

    public void setSiglaOrgao(String siglaOrgao) {
        this.siglaOrgao = siglaOrgao;
    }

    public String getOrgaoJornal() {
        return orgaoJornal;
    }

    public void setOrgaoJornal(String orgaoJornal) {
        this.orgaoJornal = orgaoJornal;
    }

    public String getSiglaJornal() {
        return siglaJornal;
    }

    public void setSiglaJornal(String siglaJornal) {
        this.siglaJornal = siglaJornal;
    }

    public String getDescJornal() {
        return descJornal;
    }

    public void setDescJornal(String descJornal) {
        this.descJornal = descJornal;
    }

    public String getUrlJornal() {
        return urlJornal;
    }

    public void setUrlJornal(String urlJornal) {
        this.urlJornal = urlJornal;
    }

    public Date getDtCad() {
        return dtCad;
    }

    public void setDtCad(Date dtCad) {
        this.dtCad = dtCad;
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

    public Integer getLimiteLinhaMateria()
    {
        return limiteLinhaMateria;
    }

    public void setLimiteLinhaMateria( Integer limiteLinhaMateria )
    {
        this.limiteLinhaMateria = limiteLinhaMateria;
    }

    public boolean isEdicaoDetectavel() {
        return edicaoDetectavel;
    }

    public void setEdicaoDetectavel(boolean edicaoDetectavel) {
        this.edicaoDetectavel = edicaoDetectavel;
    }
    

    // --- Métodos estaticos
    public static Jornal LocateJornalInList_BySigla(Jornal[] listJornal, String sigla) {
        if ( sigla == null ) return null;

        for ( Jornal j : listJornal ) {
            if ( j.getSiglaJornal().toUpperCase().equals(sigla) ) return j;
        }

        return null;
    }

    public static Jornal LocateJornalInList_ByID(Jornal[] listJornal, int idJornal ) {
        if ( idJornal == 0) return null;

        for ( Jornal j : listJornal ) {
            if ( j.getIdJornal() == idJornal ) return j;
        }

        return null;
    }

    public static int GetIDJornalInList_BySigla(Jornal[] listJornal, String sigla) {
        Jornal j = Jornal.LocateJornalInList_BySigla(listJornal, sigla);
        return (j == null) ? 0 : j.getIdJornal();
    }
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idJornal != null ? idJornal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Jornal)) {
            return false;
        }
        Jornal other = (Jornal) object;
        if ((this.idJornal == null && other.idJornal != null) || (this.idJornal != null && !this.idJornal.equals(other.idJornal))) {
            return false;
        }
        return true;
    }

    public String getNomeJornal() {
        return nomeJornal;
    }

    public void setNomeJornal(String nomeJornal) {
        this.nomeJornal = nomeJornal;
    }
}