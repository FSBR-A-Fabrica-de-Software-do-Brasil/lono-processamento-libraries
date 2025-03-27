package espe.lono.db.enums;

public enum LonoConfigDB_Codes {
    INTERVALO_DOWNLOAD_PUBLICACAO("intervalo_download_publicacao"),
    LIMITE_PUBLICACACOES_MOBILE("limite_publicacacoes_mobile"),
    LONO_CONTATO_EMAIL("lono_contato_email"),
    MIN_LENGTH_TERMO("min_length_termo"),
    TOLERANCIA_TERMO_BLOCK("tolerancia_termo_block"),
    LONO_COMERCIAL_EMAIL("lono_comercial_email"),
    TEMPLATE_ALERT_1("template_alert_1"),
    TEMPLATE_ALERT_2("template_alert_2"),
    TEMPLATE_ALERT_3("template_alert_3");

    private String value;

    LonoConfigDB_Codes(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
