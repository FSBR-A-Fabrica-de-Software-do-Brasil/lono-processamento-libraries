package espe.lono.db.models;

import java.util.Date;

/**
 *
 * @author Espe
 */

public class ClienteJornal {
    private Integer idClienteJornal;
    private Integer idCliente;
    private Integer idJornal;
    private Date datCad;
    private String sitCad;
    private int usuCad;

    public ClienteJornal() {
    }

    public ClienteJornal(Integer idClienteJornal) {
        this.idClienteJornal = idClienteJornal;
    }

    public ClienteJornal(Integer idClienteJornal, Date datCad, String sitCad, int usuCad) {
        this.idClienteJornal = idClienteJornal;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdClienteJornal() {
        return idClienteJornal;
    }

    public void setIdClienteJornal(Integer idClienteJornal) {
        this.idClienteJornal = idClienteJornal;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(Integer idJornal) {
        this.idJornal = idJornal;
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
        hash += (idClienteJornal != null ? idClienteJornal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClienteJornal)) {
            return false;
        }
        ClienteJornal other = (ClienteJornal) object;
        if ((this.idClienteJornal == null && other.idClienteJornal != null) || (this.idClienteJornal != null && !this.idClienteJornal.equals(other.idClienteJornal))) {
            return false;
        }
        return true;
    }

}
