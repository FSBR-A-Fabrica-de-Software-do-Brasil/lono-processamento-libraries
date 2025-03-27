/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espe.lono.db.models;

import java.util.Date;

/**
 *
 * @author ESPE
 */
public class ClienteNotificacao {
    private Integer idNotificacao;
    private Integer idCliente;
    private Integer idUsuario;
    private Integer usuCad;
    private boolean lida;
    private boolean pushSent;
    private String assunto;
    private String mensagem;
    private String actionUrl;
    private Date datCad;
    private boolean showMobile;

    public Integer getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(Integer idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(Integer usuCad) {
        this.usuCad = usuCad;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }

    public boolean isPushSent() {
        return pushSent;
    }

    public void setPushSent(boolean pushSent) {
        this.pushSent = pushSent;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public Date getDatCad() {
        return datCad;
    }

    public void setDatCad(Date datCad) {
        this.datCad = datCad;
    }

    public boolean isShowMobile() {
        return showMobile;
    }

    public void setShowMobile(boolean showMobile) {
        this.showMobile = showMobile;
    }
}
