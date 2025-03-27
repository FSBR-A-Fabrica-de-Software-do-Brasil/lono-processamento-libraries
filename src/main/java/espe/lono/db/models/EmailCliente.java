package espe.lono.db.models;

import java.util.Date;


/**
 *
 * @author Espe
 */
public class EmailCliente { 
    private Integer idEmail;    
    private Integer idCliente;    
    private String email;   
    private Date datCad;    
    private String sitCad;    
    private int usuCad;
    
    public EmailCliente() {
    }

    public EmailCliente(Integer idEmail) {
        this.idEmail = idEmail;
    }

    public EmailCliente(Integer idEmail, Date datCad, String sitCad, int usuCad) {
        this.idEmail = idEmail;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdEmail() {
        return idEmail;
    }

    public void setIdEmail(Integer idEmail) {
        this.idEmail = idEmail;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        hash += (idEmail != null ? idEmail.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmailCliente)) {
            return false;
        }
        EmailCliente other = (EmailCliente) object;
        if ((this.idEmail == null && other.idEmail != null) || (this.idEmail != null && !this.idEmail.equals(other.idEmail))) {
            return false;
        }
        return true;
    }

    
}
