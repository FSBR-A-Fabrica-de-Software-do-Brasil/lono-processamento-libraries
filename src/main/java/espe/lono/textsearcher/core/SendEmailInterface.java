package espe.lono.textsearcher.core;

public interface SendEmailInterface {
    void sendEmail(Integer idCliente, Integer[] idsMaterias, String origem);
    void sendTesteEmail(String emailDestino);
}
