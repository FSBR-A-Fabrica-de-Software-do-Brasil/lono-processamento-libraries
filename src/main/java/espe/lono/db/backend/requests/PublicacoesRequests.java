package espe.lono.db.backend.requests;

import espe.lono.db.LonoDatabaseConfigs;
import espe.lono.db.models.MateriaPublicacao;
import espe.lono.db.models.NomePesquisaCliente;
import espe.lono.db.models.PautaPublicacao;
import espe.lono.db.models.PublicacaoJornal;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.text.SimpleDateFormat;

public class PublicacoesRequests {

    public PublicacaoJornal AdicionarNovaPublicacaoJornal(PublicacaoJornal publicacao) {
        return null;
    }


    public static void AdicionarNovaMateriaCliente(PublicacaoJornal publicacao, NomePesquisaCliente nomePesquisa, MateriaPublicacao materia, PautaPublicacao pauta) throws Exception {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject body;
        try {
            // Montando o JSON body a ser enviado (os parâmetros serão armazenando em JSON)
            JSONObject jsonPostData = new JSONObject();
            jsonPostData.put("id_cliente", materia.getIdCliente());
            jsonPostData.put("id_materia", materia.getIdMateria());
            jsonPostData.put("id_publicacao", materia.getIdPublicacao());
            jsonPostData.put("id_jornal", publicacao.getIdJornal());
            jsonPostData.put("id_nome_pesquisa", nomePesquisa.getIdNomePesquisa());
            jsonPostData.put("status_materia", "I");
            jsonPostData.put("titulo_materia", materia.getTituloMateria());
            jsonPostData.put("subtitulo", materia.getSubtituloMateria());
            jsonPostData.put("processo", materia.getProcesso());
            jsonPostData.put("materia", materia.getMateria());
            jsonPostData.put("dt_divulgacao", simpleDateFormat.format(publicacao.getDtDivulgacao()));
            jsonPostData.put("dt_publicacao", simpleDateFormat.format(publicacao.getDtPublicacao()));
            jsonPostData.put("edicao_publicacao", publicacao.getEdicaoPublicacao());
            jsonPostData.put("nome_pesquisa", nomePesquisa.getNomePesquisa());
            jsonPostData.put("literal", nomePesquisa.isLiteral());
            jsonPostData.put("num_oab", (nomePesquisa.getUfOAB() != null && nomePesquisa.getUfOAB().length() > 0));
            jsonPostData.put("pauta", (pauta != null) ? pauta.getPauta() : "");
            jsonPostData.put("nome_jornal", publicacao.getJornalPublicacao().getNomeJornal());
            jsonPostData.put("sigla_jornal", publicacao.getJornalPublicacao().getSiglaJornal());
            jsonPostData.put("nome_orgao", publicacao.getJornalPublicacao().getOrgaoJornal());
            jsonPostData.put("sigla_orgao", publicacao.getJornalPublicacao().getSiglaJornal());

            // Enviando a requisição
            HttpResponse<JsonNode> response = Unirest.post(LonoDatabaseConfigs.LONOBACKEND_BASEURL + "engine-materia")
                    .header("engine-key", LonoDatabaseConfigs.LONOBACKEND_KEY)
                    .body(jsonPostData)
                    .asJson();

            if ( !response.isSuccess())
                throw new Exception("Erro realizando execução do Engine -> StatusCode: " + response.getStatus());

            if ( response.getBody() == null )
                throw new Exception("Erro realizando execução do Engine -> " + response.getStatusText());

            // Validando a resposta
            body = response.getBody().getObject();
            if ( !body.getString("status").equalsIgnoreCase("success") )
                throw new Exception(body.getString("message"));
        } catch ( Exception exception ) {
            System.out.println(exception.getMessage());
            System.out.println(exception.toString());
            //exception.printStackTrace();
        }
    }
}
