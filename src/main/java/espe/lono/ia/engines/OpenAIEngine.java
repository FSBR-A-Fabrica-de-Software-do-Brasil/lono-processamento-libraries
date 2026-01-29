package espe.lono.ia.engines;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import espe.lono.db.models.TipoConteudoWeb;
import espe.lono.ia.IAConfigs;
import espe.lono.ia.IAEngineInterface;
import espe.lono.ia.IAEngines;
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
        final String prompt = getPrompt__classificMateria(listaTiposDesjados, conteudo);
        final ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model("gpt-5-nano")
                .addUserMessage(prompt)
                .build();

        // Realizando a requisição ao ChatGPT
        final ChatCompletion response = client.chat().completions().create(params);
        String json = response.choices().get(0).message().content().orElse(null);
        if ( json == null ) return null;

        // Convertando em JSON e obtendo o retorno
        try {
            JSONObject obj = new JSONObject(json);
            if ( obj == null ) return null;

            // Procurando a categoria na lista
            final String categoriaSujerida = obj.getString("categoria");
            final TipoConteudoWeb tipoConteudoWeb = Arrays.stream(listaTiposDesjados)
                    .filter(item -> item.getDescricao().equalsIgnoreCase(categoriaSujerida))
                    .findFirst().orElse(null);

            return tipoConteudoWeb;
        } catch (Exception ignore) {
            return null;
        }
    }



    //region ---- Schemas and Prompts
    private String getPrompt__classificMateria(TipoConteudoWeb[] listaTiposDesejados, String conteudo) {
        StringJoiner joiner = new StringJoiner(", ", "", "");
        for ( TipoConteudoWeb tipoConteudoWeb: listaTiposDesejados )
            joiner.add(tipoConteudoWeb.getDescricao());

        return """
            Você é um classificador de texto.        
            Regras:
            - Retorne APENAS um JSON válido
            - Não escreva nenhum texto fora do JSON
            - A categoria DEVE ser uma das opções abaixo
            Categorias permitidas:
            %s
                
            Formato EXATO de saída:
            {
                "categoria": "UMA_DAS_CATEGORIAS",
                "confianca": 0.0
            }
        
            Texto da matéria:
            %s
            """.formatted(joiner.toString(), conteudo);
    }
    //endregion
}
