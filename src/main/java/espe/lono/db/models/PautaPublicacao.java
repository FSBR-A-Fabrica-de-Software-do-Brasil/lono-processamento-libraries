package espe.lono.db.models;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Espe
 */

public class PautaPublicacao implements Serializable
{
    private int idPauta;
    private int idPublicacao;
    private long numDocLucene;
    private int pagina;
    private String pautaHash;
    private String pauta;
    private Date datCad;
    private String sitCad;
    private int usuCad;

    public String getPautaHash()
    {
        return pautaHash;
    }

    public void setPautaHash( final String hash )
    {
        this.pautaHash = hash;
    }

    public int getPagina()
    {
        return pagina;
    }

    public void setPagina( int pagina )
    {
        this.pagina = pagina;
    }

    public int getIdPauta()
    {
        return idPauta;
    }

    public void setIdPauta( int idPauta )
    {
        this.idPauta = idPauta;
    }

    public int getIdPublicacao()
    {
        return idPublicacao;
    }

    public void setIdPublicacao( int idPublicacao )
    {
        this.idPublicacao = idPublicacao;
    }

    public long getNumDocLucene()
    {
        return numDocLucene;
    }

    public void setNumDocLucene( long numDocLucene )
    {
        this.numDocLucene = numDocLucene;
    }

    public String getPauta()
    {
        return pauta;
    }

    public void setPauta( String pauta )
    {
        this.pauta = pauta;
    }

    public Date getDatCad()
    {
        return datCad;
    }

    public void setDatCad( Date datCad )
    {
        this.datCad = datCad;
    }

    public String getSitCad()
    {
        return sitCad;
    }

    public void setSitCad( String sitCad )
    {
        this.sitCad = sitCad;
    }

    public int getUsuCad()
    {
        return usuCad;
    }

    public void setUsuCad( int usuCad )
    {
        this.usuCad = usuCad;
    }
    
}
