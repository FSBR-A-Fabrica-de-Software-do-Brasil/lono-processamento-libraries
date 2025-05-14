package espe.lono.db.models;

public class Configuracao {
    private String valorPrimario;
    private String valorSecundario;
    private String valorTerciario;

    public String getValorPrimario() {
        return valorPrimario;
    }

    public void setValorPrimario(String valorPrimario) {
        this.valorPrimario = valorPrimario;
    }

    public String getValorSecundario() {
        return valorSecundario;
    }

    public void setValorSecundario(String valorSecundario) {
        this.valorSecundario = valorSecundario;
    }

    public String getValorTerciario() {
        return valorTerciario;
    }

    public void setValorTerciario(String valorTerciario) {
        this.valorTerciario = valorTerciario;
    }

    public Configuracao() {
    }

    public Configuracao(String valorPrimario, String valorSecundario, String valorTerciario) {
        this.valorPrimario = valorPrimario;
        this.valorSecundario = valorSecundario;
        this.valorTerciario = valorTerciario;
    }
}
