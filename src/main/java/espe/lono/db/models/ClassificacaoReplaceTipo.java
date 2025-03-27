package espe.lono.db.models;

/**
 *
 * @author Espe
 */
public class ClassificacaoReplaceTipo {
    
    private String pesquisa;
    private char tipoAcao;
    private String valor;
    
    public ClassificacaoReplaceTipo(String pesquisa, char tipoAcao, String valor){
        this.pesquisa = pesquisa;
        this.tipoAcao = tipoAcao;
        this.valor = valor;
    }

    public String getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(String pesquisa) {
        this.pesquisa = pesquisa;
    }

    public char getTipoAcao() {
        return tipoAcao;
    }

    public void setTipoAcao(char tipoAcao) {
        this.tipoAcao = tipoAcao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
}
