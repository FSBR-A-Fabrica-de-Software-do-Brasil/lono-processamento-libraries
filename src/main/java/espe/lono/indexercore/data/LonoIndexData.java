package espe.lono.indexercore.data;

import espe.lono.db.connections.DbConnectionMarcacao;
import espe.lono.db.models.PublicacaoJornal;


/**
 * @author ESPE
 * @date 07/12/2017
 */
public class LonoIndexData {
    public boolean DocumentoIndexado = false;
    public boolean MarcacoesProcessadas = false;
    public boolean ProcessarArquivosCorte = true;
    
    private String luceneDirMarcacao;
    private String luceneDirPesquisa;
    private DbConnectionMarcacao dbConnMarcacao;
    private PublicacaoJornal publicacaoJornal;
    
    public LonoIndexData(PublicacaoJornal publicacaoJornal) {
        this.publicacaoJornal = publicacaoJornal;
    }
    
    public void setLuceneDirs(String dirMarc, String dirPesquisa) {
        this.luceneDirMarcacao = dirMarc;
        this.luceneDirPesquisa = dirPesquisa;
    }

    public String getLuceneDirMarcacao() {
        return luceneDirMarcacao;
    }

    public String getLuceneDirPesquisa() {
        return luceneDirPesquisa;
    }
    
    public void setMarcacaoDbConnection(DbConnectionMarcacao dbconn) {
        this.dbConnMarcacao = dbconn;
    }

    public DbConnectionMarcacao getDbConnMarcacao() {
        return dbConnMarcacao;
    }

    public PublicacaoJornal getPublicacaoJornal() {
        return publicacaoJornal;
    }

    public void setPublicacaoJornal(PublicacaoJornal publicacaoJornal) {
        this.publicacaoJornal = publicacaoJornal;
    }

    public int getIdJornal() {
        return this.publicacaoJornal.getIdJornal();
    }

    public int getIdPublicacao() {
        return this.publicacaoJornal.getIdPublicacao();
    }
    
}
