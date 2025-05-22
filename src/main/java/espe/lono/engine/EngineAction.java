package espe.lono.engine;

import espe.lono.engine.models.EngineNotifyClientRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

public class EngineAction {
    static public String LONO_BACKEND_URL = "https://api.lono.com.br";
    static public String X_ENGINE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJsb25vLXByb2Nlc3NhbWVudG8iLCJuYW1lIjoibG9uby1wcm9jIn0.Arl2IltReI0Z37CkbD4YVn3n4AQqIA8Ni0ICIMfKteY";

    public boolean notifyClient(EngineNotifyClientRequest request) {
        final String finalURL = generateFinalUrl("engine/notify-client");
        HttpResponse<String> response = Unirest.post(EngineAction.LONO_BACKEND_URL)
                .body(request)
                .header("X-Engine-Key", EngineAction.X_ENGINE_KEY)
                .asString();
        return response.isSuccess();
    }

    public boolean notifyClient(JSONObject jsonObject) {
        final String finalURL = generateFinalUrl("engine/notify-client");
        HttpResponse<String> response = Unirest.post(EngineAction.LONO_BACKEND_URL)
                .body(jsonObject)
                .header("X-Engine-Key", EngineAction.X_ENGINE_KEY)
                .asString();
        return response.isSuccess();
    }


    // ------
    private String generateFinalUrl(final String uri) {
        final StringBuilder sb = new StringBuilder();
        sb.append(EngineAction.LONO_BACKEND_URL);
        if (!EngineAction.LONO_BACKEND_URL.endsWith("/") ) sb.append("/");
        if ( !uri.startsWith("/") ) sb.append(uri);
        else sb.append(uri.substring(1));

        return sb.toString();
    }
}
