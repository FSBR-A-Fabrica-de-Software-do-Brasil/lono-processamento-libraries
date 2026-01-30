package espe.lono.ia;

import espe.lono.db.connections.DbConnection;
import espe.lono.db.models.TipoConteudoWeb;
import espe.lono.ia.engines.OpenAIEngine;

import java.util.ArrayList;
import java.util.List;

public class IARequests {
    private static List<IAEngineInterface> SUPPORTED_IA_ENGINES = null;

    public void inicializar() {
        SUPPORTED_IA_ENGINES.add( new OpenAIEngine() );
    }

    public TipoConteudoWeb localizarTipoConteudoWebPorConteudo(IAEngines engine, String conteudo, TipoConteudoWeb[] listaConteudoDesejados, DbConnection dbConnection) {
        if ( SUPPORTED_IA_ENGINES == null )
            throw new RuntimeException("Lista de motores de IA não inicializada. Chame o método inicializar() antes de usar os serviços de IA.");
        
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
