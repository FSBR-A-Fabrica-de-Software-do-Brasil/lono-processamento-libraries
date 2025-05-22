package espe.lono.engine.models;

import espe.lono.engine.enums.EngineActionEnum;

import java.util.HashMap;

public class EngineNotifyClientRequest {
    private String action;
    private HashMap<String, Object> payyload;

    public EngineNotifyClientRequest(String action, HashMap<String, Object> payyload) {
        this.action = action;
        this.payyload = payyload;
    }

    public EngineNotifyClientRequest(EngineActionEnum action, HashMap<String, Object> payyload) {
        this.action = action.getValue();
        this.payyload = payyload;
    }

    public EngineNotifyClientRequest() {
    }

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


    // Metodo publicos statis para adiantar a criação de payload
    public static HashMap<String, Object> generateMateriaPayload(String clienteNome, long idCliente, long qtdMateria)
    {
        return EngineNotifyClientRequest.generateMateriaPayload(clienteNome, idCliente, qtdMateria, false, null);
    }
    public static HashMap<String, Object> generateMateriaPayload(String clienteNome, long idCliente, long qtdMateria, boolean isHistorico, Long[] idsMaterias)
    {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("cliente", idCliente);
        payload.put("qtd-materias", qtdMateria);
        payload.put("historico", isHistorico);
        if ( idsMaterias != null && idsMaterias.length > 0 ) {
            StringBuilder sb = new StringBuilder();
            for (Long id : idsMaterias) {
                sb.append(id).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove o último caractere ","
            payload.put("ids", sb.toString());
        }
        return payload;
    }
}
