package espe.lono.db.models.Blogs;

import javax.persistence.Entity;
import javax.persistence.Column;
import java.util.Date;

@Entity
public class MonitorConteudo {
    @Column(name = "id") private int id;
    @Column(name = "id_jornal") private int idJornal;
    @Column(name = "ult_url_publicada") private String ultUrl;
    @Column(name = "ult_hash") private String ultHash;
    @Column(name = "sit_cad") private String sitCad;
    @Column(name = "created_at") private Date createdAt;
    @Column(name = "updated_at") private Date updatedAt;

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

    public String getUltUrl() {
        return ultUrl;
    }

    public void setUltUrl(String ultUrl) {
        this.ultUrl = ultUrl;
    }

    public String getUltHash() {
        return ultHash;
    }

    public void setUltHash(String ultHash) {
        this.ultHash = ultHash;
    }

    public String getSitCad() {
        return sitCad;
    }

    public void setSitCad(String sitCad) {
        this.sitCad = sitCad;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
