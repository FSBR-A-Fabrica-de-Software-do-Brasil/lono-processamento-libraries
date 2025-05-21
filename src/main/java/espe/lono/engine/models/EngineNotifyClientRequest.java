package espe.lono.engine.models;

import espe.lono.engine.enums.EngineActionEnum;

import java.util.HashMap;

public class EngineNotifyClientRequest {
    private String action;
    private HashMap<String, Object> payyload;

    public String getAction() {
        return action;
    }

    public void setAction(EngineActionEnum action) {
        this.action = action.getValue();
    }

    public HashMap<String, Object> getPayyload() {
        return payyload;
    }

    public void setPayyload(HashMap<String, Object> payyload) {
        this.payyload = payyload;
    }
}
