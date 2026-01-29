package espe.lono.ia;

public enum IAEngines {
    OpenAI("OpenAI");

    private final String value;
    IAEngines(String engineName) {
        this.value = engineName;
    }

    public String getValue() {
        return value;
    }

    public Boolean equals(IAEngines engine) {
        return this.value.equals(engine.getValue());
    }
}
