package espe.lono.indexercore.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CNJJson_Processo {
    public long id;
    public String data_disponibilizacao;
    public String siglaTribunal;
    public String tipoComunicacao;
    public String nomeOrgao;
    public long idOrgao;
    public String texto;
    public String numero_processo;
    public String meio;
    public String link;
    public String tipoDocumento;
    public String nomeClasse;
    public String codigoClasse;
    public int numeroComunicacao;
    public boolean ativo;
    public String hash;
    public String status;
    public String motivo_cancelamento;
    public String data_cancelamento;
    public String datadisponibilizacao;
    public String dataenvio;
    public String meiocompleto;
    public String numeroprocessocommascara;
    public CNJJson_ProcessoDestinatario[] destinatarios;
    public CNJJson_DestinatarioAdvogado[] destinatarioadvogados;

    public String getNomeAdvogados() {
        List<String> listAdvogados = new ArrayList<>();
        for ( CNJJson_DestinatarioAdvogado item : destinatarioadvogados ) {
            listAdvogados.add(item.advogado.nome + " " + item.advogado.numero_oab + " " + item.advogado.uf_oab);
        }

        return String.join("  ", listAdvogados);
    }

    public String getNomePartes() {
        List<String> listPartes = new ArrayList<>();
        for ( CNJJson_ProcessoDestinatario item : destinatarios ) {
            listPartes.add(item.nome);
        }

        return String.join("  ", listPartes);
    }
}
