package espe.lono.db.models;

/**
 * @corp ESPE
 * @author Petrus Augusto (R@g3)
 * @date 11/04/2018
 */
public class ReprocessarClientePublicacao {
    /**
     * Lista(enum) de status do 'Reprocessamento'
     * Nota: usada mais como referencias ao consultar no banco de dados
     */
    public enum Status
    {
        SIT_AGUARDANDO("A"), // Arquivo publicado... Ainda não foi 'tocado'
        SIT_PROCESSANDO("I"), // Reprocessando no momento
        SIT_ERRO("X"), // Reprocessamento Finalizado com ERRO
        SIT_PROCESSADO("F"), // Reprocessamento Finalizado com Sucesso
        SIT_NAOSUPORTADO("Z"); // Reprocessamento da Pub. Nao mais suportado
        
        private final String value;
        private Status(String value)
        {
            this.value = value;
        }
        @Override
        public String toString() { return this.value; }
    }
    
    private Integer idReprocessamento;
    private Integer idPublicacao;
    private Integer idCliente;
    private Integer idNomePesquisa;
    private String sitCad;
    private Integer usuCad;
    
    public Integer getIdReprocessamento() {
        return idReprocessamento;
    }

    public void setIdReprocessamento(Integer idReprocessamento) {
        this.idReprocessamento = idReprocessamento;
    }
    
    public Integer getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }


    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdNomePesquisa() {
        return idNomePesquisa;
    }

    public void setIdNomePesquisa(Integer idNomePesquisa) {
        this.idNomePesquisa = idNomePesquisa;
    }

    public String getSitCad() {
        return sitCad;
    }

    public void setSitCad(String sitCad) {
        this.sitCad = sitCad;
    }

    public Integer getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(Integer usuCad) {
        this.usuCad = usuCad;
    }
    
    
}
