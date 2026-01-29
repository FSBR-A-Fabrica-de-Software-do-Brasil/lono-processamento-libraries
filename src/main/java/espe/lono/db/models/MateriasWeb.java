package espe.lono.db.models;

import java.util.Date;

public class MateriasWeb {
    private long id;
    private Long veiculoId;
    private Long tipoMateriaId;
    private Long tipoConteudoId;
    private String resumida;
    private String integral;
    private String titulo;
    private Date dataPublicacao;
    private Date datCad;
    private String sitCad;
    private String staCad;
    private long usuCad;
    private String subTitulo;
    private String urlMateria;
    private byte[] imagem;
    private String urlImagem;
    private Boolean novaMateria;
    private Boolean favorita;
    private Boolean contemVideo;
    private String urlVideo;
    private String hash;

    private TipoConteudoWeb tipoConteudo;

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = (urlVideo == null || urlVideo.isEmpty()) ? null : urlVideo;
    }

    public Boolean getContemVideo() {
        return contemVideo;
    }

    public void setContemVideo(Boolean contemVideo) {
        this.contemVideo = contemVideo;
    }

    // Construtor padrão
    public MateriasWeb() {
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(Long veiculoId) {
        this.veiculoId = veiculoId;
    }

    public Long getTipoMateriaId() {
        return tipoMateriaId;
    }

    public void setTipoMateriaId(Long tipoMateriaId) {
        this.tipoMateriaId = tipoMateriaId;
    }

    public Long getTipoConteudoId() {
        return tipoConteudoId;
    }

    public void setTipoConteudoId(Long tipoConteudoId) {
        this.tipoConteudoId = tipoConteudoId;
    }

    public String getResumida() {
        return resumida;
    }

    public void setResumida(String resumida) {
        this.resumida = resumida;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
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

    public String getStaCad() {
        return staCad;
    }

    public void setStaCad(String staCad) {
        this.staCad = staCad;
    }

    public long getUsuCad() {
        return usuCad;
    }

    public void setUsuCad(long usuCad) {
        this.usuCad = usuCad;
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }

    public String getUrlMateria() {
        return urlMateria;
    }

    public void setUrlMateria(String urlMateria) {
        this.urlMateria = (urlMateria == null || urlMateria.isEmpty()) ? null : urlMateria;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = (urlImagem == null || urlImagem.isEmpty()) ? null : urlImagem;
    }

    public Boolean getNovaMateria() {
        return novaMateria;
    }

    public void setNovaMateria(Boolean novaMateria) {
        this.novaMateria = novaMateria;
    }

    public Boolean getFavorita() {
        return favorita;
    }

    public void setFavorita(Boolean favorita) {
        this.favorita = favorita;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public TipoConteudoWeb getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(TipoConteudoWeb tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }
}
