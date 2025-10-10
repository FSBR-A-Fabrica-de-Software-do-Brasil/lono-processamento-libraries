package espe.lono.engine;

import espe.lono.engine.enums.EngineActionEnum;
import espe.lono.engine.models.EngineNotifyClientRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EngineActionTest {

    //@Test
    void notifyClient() {

        EngineNotifyClientRequest request = new EngineNotifyClientRequest();
        HashMap<String, Object> payload = createPayload(request);
        request.setPayyload(payload);
        request.setAction(EngineActionEnum.NOTIFICACAO_CLIENTE);

        EngineAction engineAction = new EngineAction();
        Assertions.assertTrue(engineAction.notifyClient(request));

    }

    private HashMap<String, Object> createPayload(EngineNotifyClientRequest request){
        String clienteNome = "Ruben";

        // Manualmente -> verificar tabela cliente
        long idCliente = 15L;

        long qtdMateria = 2L;
        // ATENCAO - Manualmente setar em materia_publicaco
        Long[] idsMaterias = new Long[] { 1L, 142L, 1003L };
        boolean isHistorico = false;

        return request.generateMateriaPayload(clienteNome, idCliente, qtdMateria, isHistorico, idsMaterias);

    }
}