package espe.lono.db.models;

import espe.lono.db.utils.DBUtils;
import org.joda.time.DateTimeComparator;

import java.util.Date;

/**
 *
 * @author Espe
 */

public class NomePesquisaCliente {
    private Integer idNomePesquisa;    
    private Integer idCliente;    
    private String nomePesquisa;    
    private Date datCad;
    private Date blacklistNotifyDat;
    private String sitCad;
    private int usuCad;
    private String nomePesquisaLimpo;
    private int[] publicacoesReProcessar;    
    private boolean literal;
    private float porcetualColisao;
    boolean processo;
    private String uf_oab;
    private boolean blacklist;


    private String nomePesquisaExt;

    
    public String getUfOAB()
    {
        return uf_oab;
    }

    public void setUfOAB( String uf_oab )
    {
        this.uf_oab = uf_oab;
    }
    
    public NomePesquisaCliente() {
    }

    public NomePesquisaCliente(Integer idNomePesquisa) {
        this.idNomePesquisa = idNomePesquisa;
    }

    public NomePesquisaCliente(Integer idNomePesquisa, Date datCad, String sitCad, int usuCad) {
        this.idNomePesquisa = idNomePesquisa;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    
    public Integer getIdNomePesquisa() {
        return idNomePesquisa;
    }

    public void setIdNomePesquisa(Integer idNomePesquisa) {
        this.idNomePesquisa = idNomePesquisa;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
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

    public String getNomePesquisaLimpo() {
        return nomePesquisaLimpo;
    }

    public boolean isLiteral()
    {
        return literal;
    }

    public void setLiteral( boolean literal )
    {
        this.literal = literal;
    }

    public float getPorcetualColisao()
    {
        return porcetualColisao;
    }

    public void setPorcetualColisao( float porcetualColisao )
    {
        this.porcetualColisao = porcetualColisao;
    }
    
    public void setNomePesquisaLimpo(String nomePesquisaLimpo) {
        this.nomePesquisaLimpo = DBUtils.RemoveAccents(nomePesquisaLimpo);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idNomePesquisa != null ? idNomePesquisa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NomePesquisaCliente)) {
            return false;
        }
        NomePesquisaCliente other = (NomePesquisaCliente) object;
        if ((this.idNomePesquisa == null && other.idNomePesquisa != null) || (this.idNomePesquisa != null && !this.idNomePesquisa.equals(other.idNomePesquisa))) {
            return false;
        }
        return true;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void setBlacklist(boolean blacklist) {
        this.blacklist = blacklist;
    }

    public void setBlacklistNotifyDat(Date blacklistNotifyDat) {
        this.blacklistNotifyDat = blacklistNotifyDat;
    }

    public boolean needNotifyBlacklist() {
        if ( this.blacklistNotifyDat == null )
            return true;
        else {
            DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
            int compReturn = dateTimeComparator.compare(this.blacklistNotifyDat, new Date());
            if ( compReturn < 0 ) return true;
            else return false;
        }
    }

    public void setNumProcesso(boolean vl) {
        this.processo = vl;
    }

    public boolean isNumProcesso() {
        return this.processo;
    }

    public String getNomePesquisaExt() {
        return (nomePesquisaExt != null) ? nomePesquisaExt.trim() : null;
    }

    public void setNomePesquisaExt(String nomePesquisaExt) {
        this.nomePesquisaExt = nomePesquisaExt;
    }
}
