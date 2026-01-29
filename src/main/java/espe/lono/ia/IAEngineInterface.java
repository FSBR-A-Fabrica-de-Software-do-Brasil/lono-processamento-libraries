package espe.lono.ia;

import espe.lono.db.models.TipoConteudoWeb;

public interface IAEngineInterface {
    IAEngines getEngine();
    TipoConteudoWeb localizarTipoConteudoWebPorConteudo(final String conteudo, final TipoConteudoWeb[] listaTiposDesjados);
}
