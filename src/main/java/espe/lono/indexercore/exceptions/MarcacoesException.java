package espe.lono.indexercore.exceptions;

/**
 * @author ESPE
 * @date 06/12/2017
 */
public class MarcacoesException extends Exception {
    private int idPadraoJornal;
    private String queryLucene;
    
    public MarcacoesException(String message, String queryLucene, Integer idPadraoJornal) {
        super(message);
        this.queryLucene = queryLucene;
    }

    public MarcacoesException(String message, Integer idPadraoJornal) {
        super(message);
        this.idPadraoJornal = idPadraoJornal;
    }

    public int getIdPadraoJornal() {
        return idPadraoJornal;
    }

    public void setIdPadraoJornal(Integer idPadraoJornal) {
        this.idPadraoJornal = idPadraoJornal;
    }

    public String getQueryLucene() {
        return queryLucene;
    }

    public void setQueryLucene(String queryLucene) {
        this.queryLucene = queryLucene;
    }
    
}
