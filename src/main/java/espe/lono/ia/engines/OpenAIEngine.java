package espe.lono.ia.engines;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import espe.lono.db.models.MateriaPublicacao;
import espe.lono.db.models.TipoConteudoWeb;
import espe.lono.ia.IAConfigs;
import espe.lono.ia.IAEngineInterface;
import espe.lono.ia.IAEngines;
import espe.lono.ia.data.CorteJuridico;
import kong.unirest.json.JSONObject;

import java.util.Arrays;
import java.util.StringJoiner;

public class OpenAIEngine implements IAEngineInterface {
    final OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey(IAConfigs.OPENAI_TOKEN)
            .build();

    @Override
    public IAEngines getEngine() {
        return IAEngines.OpenAI;
    }

    @Override
    public TipoConteudoWeb localizarTipoConteudoWebPorConteudo(String conteudo, TipoConteudoWeb[] listaTiposDesjados) {
        final String prompt = getPrompt__classificMateria(listaTiposDesjados, this.reduzirTexto(conteudo));
        final String basePrompt = """
               Você classifica textos jornalísticos.
               Responda apenas com o nome exato de UMA das categorias listadas.
               Não explique.
               Não escreva nada além da categoria.""".replaceAll("\\s+", " ").trim();;

        final ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model("gpt-5-nano")
                .addSystemMessage(basePrompt)
                .addUserMessage(prompt)
                .build();

        // Realizando a requisição ao ChatGPT
        final ChatCompletion response = client.chat().completions().create(params);
        String categoria = response.choices().get(0).message().content().orElse("").trim();
        if ( categoria == null ) return null;
        // Convertando em JSON e obtendo o retorno
        try {
            // Procurando a categoria na lista
            final TipoConteudoWeb tipoConteudoWeb = Arrays.stream(listaTiposDesjados)
                    .filter(item -> item.getDescricao().equalsIgnoreCase(categoria))
                    .findFirst().orElse(null);

            return tipoConteudoWeb;
        } catch (Exception ignore) {
            return null;
        }
    }

    @Override
    public CorteJuridico realizarCorteJuridico(String materiaTextoPuro, String termoReferenci) {
        final String prompt = getPrompt__corteJuridico(termoReferenci, materiaTextoPuro);
        final String basePrompt = """
             Você é um especialista em direito. 
             Analise o texto e extraia apenas as informações relevantes para um corte jurídico (máteria), levando em conta que a materia contem o 'Nome' informado.
             
             Responda apenas com o texto do corte jurídico, sem explicações ou informações adicionais.
              """;


        final ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model("gpt-5-nano")
                .addSystemMessage(basePrompt)
                .addUserMessage(prompt)
                .build();

        // Realizando a requisição ao ChatGPT
        final ChatCompletion response = client.chat().completions().create(params);
        String texto = response.choices().get(0).message().content().orElse("").trim();

        String[] textoLines = texto.split("\n");
        CorteJuridico corteJuridico = new CorteJuridico();
        corteJuridico.setTitulo(textoLines[0].trim());
        corteJuridico.setMateria(texto);
        corteJuridico.setTermo(termoReferenci);
        return corteJuridico;
    }


    private String reduzirTexto(String texto) {
        int limite = 1200; // ajuste conforme necessário
        return texto.length() > limite ? texto.substring(0, limite) : texto;
    }


    //region ---- Schemas and Prompts
    private String getPrompt__classificMateria(TipoConteudoWeb[] listaTiposDesejados, String conteudo) {
        StringJoiner joiner = new StringJoiner("|");
        for (TipoConteudoWeb tipo : listaTiposDesejados) {
            joiner.add(tipo.getDescricao());
        }

        return """
            Categorias: %s
            Texto: %s
            """.formatted(joiner.toString(), conteudo);
    }
    private String getPrompt__corteJuridico(String nome, String materiaTextoPuro) {
        return """
            Nome: %s
            Texto: %s
            """.formatted(nome, materiaTextoPuro);
    }
    //endregion
}
