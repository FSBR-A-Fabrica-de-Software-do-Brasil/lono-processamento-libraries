package espe.lono.db.models;

import java.util.Date;

/**
 *
 * @author Espe
 */

public class TipoPadrao{
    private Integer idTipoPadrao;    
    private String chaveTipo;    
    private String nmTipo;    
    private String descTipo;    
    private Date datCad;    
    private String sitCad;    
    private int usuCad;

    public TipoPadrao() {
    }

    public TipoPadrao(Integer idTipoPadrao) {
        this.idTipoPadrao = idTipoPadrao;
    }

    public TipoPadrao(Integer idTipoPadrao, String chaveTipo, String nmTipo, Date datCad, String sitCad, int usuCad) {
        this.idTipoPadrao = idTipoPadrao;
        this.chaveTipo = chaveTipo;
        this.nmTipo = nmTipo;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdTipoPadrao() {
        return idTipoPadrao;
    }

    public void setIdTipoPadrao(Integer idTipoPadrao) {
        this.idTipoPadrao = idTipoPadrao;
    }

    public String getChaveTipo() {
        return chaveTipo;
    }

    public void setChaveTipo(String chaveTipo) {
        this.chaveTipo = chaveTipo;
    }

    public String getNmTipo() {
        return nmTipo;
    }

    public void setNmTipo(String nmTipo) {
        this.nmTipo = nmTipo;
    }

    public String getDescTipo() {
        return descTipo;
    }

    public void setDescTipo(String descTipo) {
        this.descTipo = descTipo;
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
        hash += (idTipoPadrao != null ? idTipoPadrao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoPadrao)) {
            return false;
        }
        TipoPadrao other = (TipoPadrao) object;
        if ((this.idTipoPadrao == null && other.idTipoPadrao != null) || (this.idTipoPadrao != null && !this.idTipoPadrao.equals(other.idTipoPadrao))) {
            return false;
        }
        return true;
    }

    
}