package espe.lono.engine.models;

import espe.lono.engine.enums.EngineActionEnum;
import kong.unirest.json.JSONObject;

import java.util.HashMap;

public class EngineNotifyClientRequest {
    private String action;
    private HashMap<String, Object> payyload;
    private int dalayInSeconds = 0; // Tempo de espera para executar a acao, em segundos
    private String jobName = null; // Nome do job, se for o caso de ser um job agendado

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

    public int getDalayInSeconds() {
        return dalayInSeconds;
    }

    public void setDalayInSeconds(int dalayInSeconds) {
        this.dalayInSeconds = dalayInSeconds;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    // Metodo publicos statis para adiantar a criacao de payload
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
        payload.put("title", "title");
        payload.put("message", "message");
        if ( idsMaterias != null && idsMaterias.length > 0 ) {
            StringBuilder sb = new StringBuilder();
            for (Long id : idsMaterias) {
                sb.append(id).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove o ultimo caractere ","
            payload.put("ids", sb.toString());
        }
        return payload;
    }

    public JSONObject toJson() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("action", this.action);
        response.put("payload", this.payyload);
        response.put("dalayInSeconds", this.dalayInSeconds);
        response.put("jobName", this.jobName);
        return new JSONObject(response);
    }

    public String toStringData() {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: ").append(this.action).append("\n");
        sb.append("Payload: ").append(this.payyload != null ? this.payyload.toString() : "null").append("\n");
        sb.append("DelayInSeconds: ").append(this.dalayInSeconds).append("\n");
        sb.append("JobName: ").append(this.jobName != null ? this.jobName : "null").append("\n");
        return sb.toString();
    }
}
