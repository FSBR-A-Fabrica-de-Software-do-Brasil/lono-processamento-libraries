package espe.lono.db.models;
import java.util.Date;
/**
 *
 * @author Petrus Augusto
 * @corp ESPE
 */


public class Usuario {
    private Integer id;
    private Integer idCliente;
    private String nome;
    private String notificationToken;

    public Usuario() {
    }

    public Usuario(Integer id, String nome, String notificationToken) {
        this.id = id;
        this.nome = nome;
        this.notificationToken = notificationToken;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
    
    
}
