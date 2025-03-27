package espe.lono.db.exceptions;

/**
 *
 * @author Luiz Diniz - Espe
 * @hidden
 */

public class ChavePadraoNaoEncontradaException extends Exception {
      
    public ChavePadraoNaoEncontradaException(){  
        super("A chave de tipo padrão solicitada, não foi encontrada.");  
    }
    
}
