package espe.lono.db.models;

import espe.lono.db.utils.DBUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class BlogJornal {
    @Column(name = "id") private int id;
    @Column(name = "id_jornal") private int idJornal;
    @Column(name = "url_publicacao") private String urlPublicacao;
    @Column(name = "dt_publicacao") private Date dtPublicacao;
    @Column(name = "dt_cad") private Date dtCad;
    @Column(name = "updated_at") private Date updatedAt;
    @Column(name = "sit_cad") private String sitCad;
    @Column(name = "usu_cad") private int usuCad;
    @Column(name = "blog_ult_hash") private String blogUltHash;
    @Column(name = "inicio_processamento") private Date inicioProcessamento;
    @Column(name = "fim_processamento") private Date fimProcessamento;

    private Jornal blog;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdJornal() {
        return idJornal;
    }

    public void setIdJornal(int idJornal) {
        this.idJornal = idJornal;
    }

    public String getUrlPublicacao() {
        return urlPublicacao;
    }

    public void setUrlPublicacao(String urlPublicacao) {
        this.urlPublicacao = urlPublicacao;
    }

    public Date getDtPublicacao() {
        return dtPublicacao;
    }

    public void setDtPublicacao(Date dtPublicacao) {
        this.dtPublicacao = dtPublicacao;
    }

    public Date getDtCad() {
        return dtCad;
    }

    public void setDtCad(Date dtCad) {
        this.dtCad = dtCad;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSitCad() {
        return sitCad;
    }

    public void setSitCad(String sitCad) {
        this.sitCad = sitCad;
    }

    public int getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(int usuCad) {
        this.usuCad = usuCad;
    }

    public String getBlogUltHash() {
        return blogUltHash;
    }

    public void setBlogUltHash(String blogUltHash) {
        this.blogUltHash = blogUltHash;
    }

    public Date getInicioProcessamento() {
        return inicioProcessamento;
    }

    public void setInicioProcessamento(Date inicioProcessamento) {
        this.inicioProcessamento = inicioProcessamento;
    }

    public Date getFimProcessamento() {
        return fimProcessamento;
    }

    public void setFimProcessamento(Date fimProcessamento) {
        this.fimProcessamento = fimProcessamento;
    }

    public Jornal getBlog() {
        return blog;
    }

    public void setBlog(Jornal blog) {
        this.blog = blog;
    }

    public String getCaminhoDirPublicacao(String diretorioDocs) {
        String[] dadosDtPub = DBUtils.DadosData(this.getDtPublicacao());
        return diretorioDocs + "/" + dadosDtPub[0] + "/" + dadosDtPub[1] + "/" + dadosDtPub[2] + "/" + this.getIdJornal() + "/" + this.getId() + "/";
    }
}
