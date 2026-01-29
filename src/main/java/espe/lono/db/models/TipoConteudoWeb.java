package espe.lono.db.models;

public class TipoConteudoWeb {
    private long id;
    private String descricao;

    public TipoConteudoWeb() {
    }

    public TipoConteudoWeb(long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // ...
    final public static TipoConteudoWeb Indefindo = new TipoConteudoWeb(100, "Indefinido");
}
