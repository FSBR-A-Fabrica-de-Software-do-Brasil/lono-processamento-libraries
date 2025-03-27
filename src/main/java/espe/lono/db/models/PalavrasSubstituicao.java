package espe.lono.db.models;

/**
 * @author Espe
 */

public class PalavrasSubstituicao 
{
    protected int idPalavra;
    protected String palavraOriginal;
    protected String palavraNova;

    public int getIdPalavra()
    {
        return idPalavra;
    }

    public void setIdPalavra( int idPalavra )
    {
        this.idPalavra = idPalavra;
    }

    
    public String getPalavraOriginal()
    {
        return palavraOriginal;
    }

    public void setPalavraOriginal( String palavraOriginal )
    {
        this.palavraOriginal = palavraOriginal;
    }

    public String getPalavraNova()
    {
        return palavraNova;
    }

    public void setPalavraNova( String palavraNova )
    {
        this.palavraNova = palavraNova;
    }
    
    
}
