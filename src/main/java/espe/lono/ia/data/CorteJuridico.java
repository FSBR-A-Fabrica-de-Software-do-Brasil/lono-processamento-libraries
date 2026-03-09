package espe.lono.ia.data;

public class CorteJuridico {
    private String titulo;
    private String materia;
    private String termo;

    public CorteJuridico() {
    }

    public CorteJuridico(String titulo, String materia, String termo) {
        this.titulo = titulo;
        this.materia = materia;
        this.termo = termo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(String termo) {
        this.termo = termo;
    }
}
