/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package espe.lono.db.models;

import java.util.ArrayList;
import java.util.Date;
/**
 *
 * @author Espe
 */

public class PadraoJornal  {
    private Integer idPadrao;    
    private Integer idJornal;    
    private Date inicioValidade;    
    private Date fimValidade;    
    private Date datCad;    
    private String sitCad;   
    private int usuCad;
    private TipoPadraoJornal[] tiposPadraoJornal;    
    private ClassificacaoReplaceTipo[] classificacaoReplaceTipo;

    
    public PadraoJornal() {
    }

    public PadraoJornal(Integer idPadrao) {
        this.idPadrao = idPadrao;
    }

    public PadraoJornal(Integer idPadrao, Date datCad, String sitCad, int usuCad) {
        this.idPadrao = idPadrao;
        this.datCad = datCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdPadrao() {
        return idPadrao;
    }

    public void setIdPadrao(Integer idPadrao) {
        this.idPadrao = idPadrao;
    }

    public Integer getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(Integer idJornal) {
        this.idJornal = idJornal;
    }

    public Date getInicioValidade() {
        return inicioValidade;
    }

    public void setInicioValidade(Date inicioValidade) {
        this.inicioValidade = inicioValidade;
    }

    public Date getFimValidade() {
        return fimValidade;
    }

    public void setFimValidade(Date fimValidade) {
        this.fimValidade = fimValidade;
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
    
    public TipoPadraoJornal[] getTiposPadraoJornal() {
        return tiposPadraoJornal;
    }

    public void setTiposPadraoJornal(TipoPadraoJornal[] tiposPadraoJornal) {
        this.tiposPadraoJornal = tiposPadraoJornal;
    }
    
    public void carregarReplacesClassificacoes(){
        ArrayList<ClassificacaoReplaceTipo> listaClassifReplace = new ArrayList();
        String[] temp,temp1,temp2;
        
        for(int x = 0; x < tiposPadraoJornal.length; x++){
            if( tiposPadraoJornal[x].getTrechosReplace() != null &&
              (!tiposPadraoJornal[x].getTrechosReplace().trim().equals("")) )
            {    
                temp = tiposPadraoJornal[x].getTrechosReplace().split("\\,");
                for(int y = 0; y < temp.length; y++)
                {
                        if( temp[y] != null && (!temp[y].trim().equals("")) ){
                           // "#ul><li> #|c:subt"
                           temp1 = temp[y].split("\\|");
                           if(temp1.length == 2){
                               temp2 = temp1[1].split(":");
                               if(temp2.length == 2){
                                   listaClassifReplace.add(new ClassificacaoReplaceTipo(temp1[0], temp2[0].toUpperCase().charAt(0), temp2[1]));
                               }
                           }
                        }
                }
            } // if
        }
        
        this.classificacaoReplaceTipo = listaClassifReplace.toArray(new ClassificacaoReplaceTipo[0]);
    }

    public ClassificacaoReplaceTipo[] getClassificacaoReplaceTipo() {
        return classificacaoReplaceTipo;
    }

    public void setClassificacaoReplaceTipo(ClassificacaoReplaceTipo[] classificacaoReplaceTipo) {
        this.classificacaoReplaceTipo = classificacaoReplaceTipo;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPadrao != null ? idPadrao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PadraoJornal)) {
            return false;
        }
        PadraoJornal other = (PadraoJornal) object;
        if ((this.idPadrao == null && other.idPadrao != null) || (this.idPadrao != null && !this.idPadrao.equals(other.idPadrao))) {
            return false;
        }
        return true;
    }
    
}
