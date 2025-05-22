package espe.lono.engine.enums;

public enum EngineActionEnum {
    NOTIFICACAO_CLIENTE("notificacao-cliente"),
    PESQUISA_WEB("pesquisa-web"),
    PESQUISA_JURIDICA("pesquisa-juridica"),
    WEB_RELEVANTES("relevantes-web");

    private String value;
    public String getValue() {
        return value;
    }
    EngineActionEnum(String value) {
        this.value = value;
    }
}
