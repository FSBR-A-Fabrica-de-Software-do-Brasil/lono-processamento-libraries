package espe.lono.db.models;

import java.util.Date;


/**
 *
 * @author Espe
 */
public class Conta {
    private Integer idConta;
    private String tipoConta;    
    private String cpfConta;    
    private String cnpjConta;    
    private String nomeConta;    
    private Integer qtdClientePesquisa;    
    private String nomeContato;    
    private String endereco;    
    private String numero;   
    private String complemento;    
    private String bairro;    
    private String cidade;   
    private String uf;   
    private String cep;   
    private String fone1;
    private String fone2;
    private String email;    
    private Date dtCad;    
    private String sitCad;    
    private int usuCad;

    public Conta() {
    }

    public Conta(Integer idConta) {
        this.idConta = idConta;
    }

    public Conta(Integer idConta, Date dtCad, String sitCad, int usuCad) {
        this.idConta = idConta;
        this.dtCad = dtCad;
        this.sitCad = sitCad;
        this.usuCad = usuCad;
    }

    public Integer getIdConta() {
        return idConta;
    }

    public void setIdConta(Integer idConta) {
        this.idConta = idConta;
    }

    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    public String getCpfConta() {
        return cpfConta;
    }

    public void setCpfConta(String cpfConta) {
        this.cpfConta = cpfConta;
    }

    public String getCnpjConta() {
        return cnpjConta;
    }

    public void setCnpjConta(String cnpjConta) {
        this.cnpjConta = cnpjConta;
    }

    public String getNomeConta() {
        return nomeConta;
    }

    public void setNomeConta(String nomeConta) {
        this.nomeConta = nomeConta;
    }

    public Integer getQtdClientePesquisa() {
        return qtdClientePesquisa;
    }

    public void setQtdClientePesquisa(Integer qtdClientePesquisa) {
        this.qtdClientePesquisa = qtdClientePesquisa;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getFone1() {
        return fone1;
    }

    public void setFone1(String fone1) {
        this.fone1 = fone1;
    }

    public String getFone2() {
        return fone2;
    }

    public void setFone2(String fone2) {
        this.fone2 = fone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDtCad() {
        return dtCad;
    }

    public void setDtCad(Date dtCad) {
        this.dtCad = dtCad;
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
        hash += (idConta != null ? idConta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Conta)) {
            return false;
        }
        Conta other = (Conta) object;
        if ((this.idConta == null && other.idConta != null) || (this.idConta != null && !this.idConta.equals(other.idConta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "testelucene.classes.Conta[ idConta=" + idConta + " ]";
    }
    
}
