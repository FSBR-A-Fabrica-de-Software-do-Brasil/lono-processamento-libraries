package espe.lono.db.models;

public class PJEPesquisaClienteJornal {
    private int idPesquisa;
    private Jornal jornal;
    private NomePesquisaCliente nomePesquisaCliente;
    private String sitCad;

    public int getIdPesquisa() {
        return idPesquisa;
    }

    public void setIdPesquisa(int idPesquisa) {
        this.idPesquisa = idPesquisa;
    }

    public Jornal getJornal() {
        return jornal;
    }

    public void setJornal(Jornal jornal) {
        this.jornal = jornal;
    }

    public NomePesquisaCliente getNomePesquisaCliente() {
        return nomePesquisaCliente;
    }

    public void setNomePesquisaCliente(NomePesquisaCliente nomePesquisaCliente) {
        this.nomePesquisaCliente = nomePesquisaCliente;
    }

    public String getSitCad() {
        return sitCad;
    }

    public void setSitCad(String sitCad) {
        this.sitCad = sitCad;
    }
}
