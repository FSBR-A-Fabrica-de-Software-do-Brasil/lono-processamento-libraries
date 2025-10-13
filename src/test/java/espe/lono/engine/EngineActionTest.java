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
        request.setAction(EngineActionEnum.PESQUISA_JURIDICA);

        EngineAction engineAction = new EngineAction();
        Assertions.assertTrue(engineAction.notifyClient(request));

    }

    private HashMap<String, Object> createPayload(EngineNotifyClientRequest request){
        String clienteNome = "Ruben";

        // ATENCAO
        // Manualmente -> verificar tabela cliente
        long idCliente = 293L;

        // ATENCAO
        // Manualmente setar em materia_publicaco e materia_cleinte
        Long[] idsMaterias = new Long[] { 118533L };
        long qtdMateria = idsMaterias.length;
        boolean isHistorico = false;

        return request.generateMateriaPayload(clienteNome, idCliente, qtdMateria, isHistorico, idsMaterias);

    }
}