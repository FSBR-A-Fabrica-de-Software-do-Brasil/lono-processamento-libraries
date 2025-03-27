package espe.lono.db.models;

import java.util.Date;

/**
 *
 * @author Espe
 */
public class AdicionalMarcacaoTipoPadrao {
    
  private int idAddTipoPadraoJornal;
  private int idTipoPadraoJornal;
  private int idTipoPadrao;
  private int idPadrao;
  private char tipoAdicional;
  private String textoAdicional;
  private String regexAdicional;
  private int addLinhaAcima;
  private int addLinhaAbaixo;
  private Date datCad;
  private String sitCad;
  private int usuCad;
  private TipoPadraoJornal tipoPadraoJornal;

    public int getIdAddTipoPadraoJornal() {
        return idAddTipoPadraoJornal;
    }

    public void setIdAddTipoPadraoJornal(int idAddTipoPadraoJornal) {
        this.idAddTipoPadraoJornal = idAddTipoPadraoJornal;
    }

    public int getIdTipoPadraoJornal() {
        return idTipoPadraoJornal;
    }

    public void setIdTipoPadraoJornal(int idTipoPadraoJornal) {
        this.idTipoPadraoJornal = idTipoPadraoJornal;
    }

    public int getIdTipoPadrao() {
        return idTipoPadrao;
    }

    public void setIdTipoPadrao(int idTipoPadrao) {
        this.idTipoPadrao = idTipoPadrao;
    }

    public char getTipoAdicional() {
        return tipoAdicional;
    }

    public void setTipoAdicional(char tipoAdicional) {
        this.tipoAdicional = tipoAdicional;
    }

    public String getTextoAdicional() {
        return textoAdicional;
    }

    public void setTextoAdicional(String textoAdicional) {
        this.textoAdicional = textoAdicional;
    }

    public String getRegexAdicional() {
        return regexAdicional;
    }

    public void setRegexAdicional(String regexAdicional) {
        this.regexAdicional = regexAdicional;
    }

    public int getAddLinhaAcima() {
        return addLinhaAcima;
    }

    public void setAddLinhaAcima(int addLinhaAcima) {
        this.addLinhaAcima = addLinhaAcima;
    }

    public int getAddLinhaAbaixo() {
        return addLinhaAbaixo;
    }

    public void setAddLinhaAbaixo(int addLinhaAbaixo) {
        this.addLinhaAbaixo = addLinhaAbaixo;
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

    public int getIdPadrao() {
        return idPadrao;
    }

    public void setIdPadrao(int idPadrao) {
        this.idPadrao = idPadrao;
    }

    public TipoPadraoJornal getTipoPadraoJornal() {
        return tipoPadraoJornal;
    }

    public void setTipoPadraoJornal(TipoPadraoJornal tipoPadraoJornal) {
        this.tipoPadraoJornal = tipoPadraoJornal;
    }
    
   public boolean equals(Object o){
        if(o == null)                return false;
        if(!(o instanceof AdicionalMarcacaoTipoPadrao)) return false;

        AdicionalMarcacaoTipoPadrao other = (AdicionalMarcacaoTipoPadrao) o;
        if(! this.textoAdicional.equals(other.getTextoAdicional())) return false;
        if(this.idTipoPadrao != other.getIdTipoPadrao()) return false;
        if(this.tipoAdicional != other.getTipoAdicional()) return false;
        if(this.idPadrao != other.getIdPadrao()) return false;

        return true;
  }
   
   public int hashCode(Object o){
        return (int)this.idTipoPadrao *
               (int)this.idPadrao *
                this.textoAdicional.hashCode();
   }
    
}
