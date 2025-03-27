package espe.lono.db.models;

import java.util.Date;


/**
 *
 * @author Espe
 */
public class Grupo {
    private Integer idGrupo;    
    private String nomeGrupo;    
    private String descGrupo;    
    private Date datCad;    
    private String sitCad;    
    private int usuCad;
    
    public Grupo() {
    }

    public Grupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Grupo(Integer idGrupo, Date datCad, String sitCad, int usuCad) {
        this.idGrupo = idGrupo;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public String getDescGrupo() {
        return descGrupo;
    }

    public void setDescGrupo(String descGrupo) {
        this.descGrupo = descGrupo;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idGrupo != null ? idGrupo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Grupo)) {
            return false;
        }
        Grupo other = (Grupo) object;
        if ((this.idGrupo == null && other.idGrupo != null) || (this.idGrupo != null && !this.idGrupo.equals(other.idGrupo))) {
            return false;
        }
        return true;
    }
    
}