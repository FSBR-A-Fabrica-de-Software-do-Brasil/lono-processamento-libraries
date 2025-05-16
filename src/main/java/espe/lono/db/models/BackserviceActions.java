package espe.lono.db.models;

import espe.lono.db.Fachada;
import espe.lono.db.connections.DbConnection;
import espe.lono.db.dao.VeiculosDAO;

import java.sql.SQLException;
import java.util.Date;

public class BackserviceActions {
    private int idBackserviceReq;
    private String acao;
    private String termo;
    private int idNomePesquisa;
    private int idCliente;
    private int idJornal;
    private long idVeiculo;
    private String sitCad;
    private Date datCad;
    private int usuCad;
    private Fachada fachada = new Fachada();

    private NomePesquisaCliente nomePesquisa = null;
    private Jornal jornal = null;
    private Veiculo veiculo = null;

    public int getIdBackserviceReq() {
        return idBackserviceReq;
    }

    public void setIdBackserviceReq(int idBackserviceReq) {
        this.idBackserviceReq = idBackserviceReq;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public int getIdNomePesquisa() {
        return idNomePesquisa;
    }

    public void setIdNomePesquisa(int idNomePesquisa) {
        this.idNomePesquisa = idNomePesquisa;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(int idJornal) {
        this.idJornal = idJornal;
    }

    public String getSitCad() {
        return sitCad;
    }

    public void setSitCad(String sitCad) {
        this.sitCad = sitCad;
    }

    public Date getDatCad() {
        return datCad;
    }

    public void setDatCad(Date datCad) {
        this.datCad = datCad;
    }

    public int getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(int usuCad) {
        this.usuCad = usuCad;
    }

    public Cliente getCliente(DbConnection dbConnection) throws SQLException {
        if ( this.idCliente != 0 )
            return fachada.listarClientePorID(this.idCliente, dbConnection);
        else
            return null;
    }

    public NomePesquisaCliente getNomePesquisa(DbConnection dbConnection) throws SQLException {
        if ( this.idNomePesquisa != 0) {
            if ( this.nomePesquisa == null )
                nomePesquisa = new Fachada().listarNomePesquisaPorID(this.idNomePesquisa, dbConnection);

            return nomePesquisa;
        }
        else
            return null;
    }

    public Jornal getJornal(DbConnection dbConnection) throws SQLException {
        if ( this.idJornal!= 0) {
            if (jornal == null) jornal = fachada.localizarJornalID(idJornal, dbConnection);
            return jornal;
        }
        else
            return null;
    }

    public Veiculo getVeiculo(DbConnection dbConnection) throws SQLException {
        if ( this.idVeiculo != 0 ) {
            if ( veiculo == null ) veiculo = new VeiculosDAO().buscarVeiculoById(this.idVeiculo, dbConnection);
            return veiculo;
        } else {
            return null;
        }
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(String termo) {
        this.termo = termo;
    }

    public long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(long idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Fachada getFachada() {
        return fachada;
    }

    public void setFachada(Fachada fachada) {
        this.fachada = fachada;
    }
}
