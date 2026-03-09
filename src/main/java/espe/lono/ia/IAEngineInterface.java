package espe.lono.ia;

import espe.lono.db.models.MateriaPublicacao;
import espe.lono.db.models.MateriasWeb;
import espe.lono.db.models.TipoConteudoWeb;
import espe.lono.ia.data.CorteJuridico;

public interface IAEngineInterface {
    IAEngines getEngine();

    //region: Métodos ligados ao Web
    TipoConteudoWeb localizarTipoConteudoWebPorConteudo(final String conteudo, final TipoConteudoWeb[] listaTiposDesjados);
    //endregion

    //region Métodos ligados ao Juridico
    CorteJuridico realizarCorteJuridico(final String materiaTextoPuro, String termoReferencia);
    //endregion
}
