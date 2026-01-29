package espe.lono.ia;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.TipoConteudoWeb;
import espe.lono.ia.engines.OpenAIEngine;

import java.util.ArrayList;
import java.util.List;

public class IARequests {
    final private static List<IAEngineInterface> SUPPORTED_IA_ENGINES = new ArrayList<>(){{
        add(new OpenAIEngine());
    }};

    public TipoConteudoWeb localizarTipoConteudoWebPorConteudo(IAEngines engine, String conteudo, TipoConteudoWeb[] listaConteudoDesejados, DbConnection dbConnection) {
        // Localizando o motor de IA solicitado
        IAEngineInterface iaEngine = SUPPORTED_IA_ENGINES.stream()
                .filter(item -> item.getEngine().equals(engine))
                .findFirst()
                .orElse(null);
        if ( iaEngine == null )
            throw new IllegalArgumentException("Motor de IA não suportado: " + engine.getValue());

        // Executando a consulta
        TipoConteudoWeb tipoConteudoWeb = iaEngine.localizarTipoConteudoWebPorConteudo(conteudo, listaConteudoDesejados);

        // Retornando o tipo de conteúdo identificado ou Indefinido se nenhum for encontrado
        return (tipoConteudoWeb == null ? TipoConteudoWeb.Indefindo : tipoConteudoWeb);
    }
}
