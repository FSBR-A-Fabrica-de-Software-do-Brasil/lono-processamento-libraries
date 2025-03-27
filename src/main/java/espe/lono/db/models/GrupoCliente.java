package espe.lono.db.models;

import java.util.Date;

/**
 *
 * @author Espe
 */
public class GrupoCliente {    
    private Integer idGrupoCliente;    
    private Integer idGrupo;    
    private Integer idCliente;   
    private Date datCad;   
    private String sitCad;    
    private int usuCad;
    
    public GrupoCliente() {
    }

    public GrupoCliente(Integer idGrupoCliente) {
        this.idGrupoCliente = idGrupoCliente;
    }

    public GrupoCliente(Integer idGrupoCliente, Date datCad, String sitCad, int usuCad) {
        this.idGrupoCliente = idGrupoCliente;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdGrupoCliente() {
        return idGrupoCliente;
    }

    public void setIdGrupoCliente(Integer idGrupoCliente) {
        this.idGrupoCliente = idGrupoCliente;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
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
        hash += (idGrupoCliente != null ? idGrupoCliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GrupoCliente)) {
            return false;
        }
        GrupoCliente other = (GrupoCliente) object;
        if ((this.idGrupoCliente == null && other.idGrupoCliente != null) || (this.idGrupoCliente != null && !this.idGrupoCliente.equals(other.idGrupoCliente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "testelucene.classes.GrupoCliente[ idGrupoCliente=" + idGrupoCliente + " ]";
    }
    
}
