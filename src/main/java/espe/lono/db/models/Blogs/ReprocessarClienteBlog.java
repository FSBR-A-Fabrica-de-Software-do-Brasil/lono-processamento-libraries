package espe.lono.db.models.Blogs;

import espe.lono.db.models.BlogJornal;
import espe.lono.db.models.Jornal;
import espe.lono.db.models.NomePesquisaCliente;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class ReprocessarClienteBlog {
    @Column(name = "id") private int id;
    @Column(name = "id_cliente") private int idCliente;
    @Column(name = "id_blog") private int idBlogJornal;
    @Column(name = "id_termo") private int idNomePesquisa;
    @Column(name = "dat_cad") private Date datCad;
    @Column(name = "sit_cad") private String sitCad;
    @Column(name = "usu_cad") private int usuCad;
    @Column(name = "proc_dat") private Date procDat;

    private BlogJornal blogJornal;
    private NomePesquisaCliente nomePesquisaCliente;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdNomePesquisa() {
        return idNomePesquisa;
    }

    public void setIdNomePesquisa(int idNomePesquisa) {
        this.idNomePesquisa = idNomePesquisa;
    }

    public Date getDatCad() {
        return datCad;
    }

    public void setDatCad(Date datCad) {
        this.datCad = datCad;
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

    public Date getProcDat() {
        return procDat;
    }

    public void setProcDat(Date procDat) {
        this.procDat = procDat;
    }

    public NomePesquisaCliente getNomePesquisaCliente() {
        return nomePesquisaCliente;
    }

    public void setNomePesquisaCliente(NomePesquisaCliente nomePesquisaCliente) {
        this.nomePesquisaCliente = nomePesquisaCliente;
    }

    public BlogJornal getBlogJornal() {
        return blogJornal;
    }

    public void setBlogJornal(BlogJornal blogJornal) {
        this.blogJornal = blogJornal;
    }

    public int getIdBlogJornal() {
        return idBlogJornal;
    }

    public void setIdBlogJornal(int idBlogJornal) {
        this.idBlogJornal = idBlogJornal;
    }
}
