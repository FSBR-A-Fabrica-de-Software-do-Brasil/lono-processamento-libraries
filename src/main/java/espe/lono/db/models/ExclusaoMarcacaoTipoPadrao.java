package espe.lono.db.models;

import java.util.Date;

/**
 *
 * @author Espe
 * #7335
 */
public class ExclusaoMarcacaoTipoPadrao {
    private int idExcTipoPadraoJornal;
    private int idTipoPadraoJornal;
    private int idTipoPadrao;
    private int idPadrao;
    private char tipoExclusao;
    private String textoExclusao;
    private String regexExclusao;
    private Date datCad;
    private String sitCad;
    private int usuCad;

    public int getIdExcTipoPadraoJornal() {
        return idExcTipoPadraoJornal;
    }

    public void setIdExcTipoPadraoJornal(int idExcTipoPadraoJornal) {
        this.idExcTipoPadraoJornal = idExcTipoPadraoJornal;
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

    public int getIdPadrao() {
        return idPadrao;
    }

    public void setIdPadrao(int idPadrao) {
        this.idPadrao = idPadrao;
    }

    public char getTipoExclusao() {
        return tipoExclusao;
    }

    public void setTipoExclusao(char tipoExclusao) {
        this.tipoExclusao = tipoExclusao;
    }

    public String getTextoExclusao() {
        return textoExclusao;
    }

    public void setTextoExclusao(String textoExclusao) {
        this.textoExclusao = textoExclusao;
    }

    public String getRegexExclusao() {
        return regexExclusao;
    }

    public void setRegexExclusao(String regexExclusao) {
        this.regexExclusao = regexExclusao;
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
    
    public boolean equals(Object o){
        if(o == null)                return false;
        if(!(o instanceof ExclusaoMarcacaoTipoPadrao)) return false;

        ExclusaoMarcacaoTipoPadrao other = (ExclusaoMarcacaoTipoPadrao) o;
        if(! this.textoExclusao.equals(other.getTextoExclusao())) return false;
        if(this.idTipoPadrao != other.getIdTipoPadrao()) return false;
        if(this.idPadrao != other.getIdPadrao()) return false;

        return true;
  }
   
   public int hashCode(Object o){
        return (int)this.idTipoPadrao *
               (int)this.idPadrao *
                this.textoExclusao.hashCode();
   }

}
