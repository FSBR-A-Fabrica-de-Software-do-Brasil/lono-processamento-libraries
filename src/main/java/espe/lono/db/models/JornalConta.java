package espe.lono.db.models;

import java.util.Date;

/**
 *
 * @author Espe
 */

public class JornalConta {
    private Integer idJornalConta;   
    private Integer idJornal;    
    private Integer idConta;   
    private Date datCad;    
    private String sitCad;   
    private int usuCad;

    public JornalConta() {
    }

    public JornalConta(Integer idJornalConta) {
        this.idJornalConta = idJornalConta;
    }

    public JornalConta(Integer idJornalConta, Date datCad, String sitCad, int usuCad) {
        this.idJornalConta = idJornalConta;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdJornalConta() {
        return idJornalConta;
    }

    public void setIdJornalConta(Integer idJornalConta) {
        this.idJornalConta = idJornalConta;
    }

    public Integer getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(Integer idJornal) {
        this.idJornal = idJornal;
    }

    public Integer getIdConta() {
        return idConta;
    }

    public void setIdConta(Integer idConta) {
        this.idConta = idConta;
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
        hash += (idJornalConta != null ? idJornalConta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JornalConta)) {
            return false;
        }
        JornalConta other = (JornalConta) object;
        if ((this.idJornalConta == null && other.idJornalConta != null) || (this.idJornalConta != null && !this.idJornalConta.equals(other.idJornalConta))) {
            return false;
        }
        return true;
    }
    
}