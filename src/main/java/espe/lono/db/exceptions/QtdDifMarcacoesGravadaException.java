package espe.lono.db.exceptions;

/**
 *
 * @author Luiz Diniz - Espe
 * @hidden
 */

public class QtdDifMarcacoesGravadaException extends Exception {
    
    public QtdDifMarcacoesGravadaException(){  
        super("A quantidade de marcacoes encontrada está diferente da quantidade gravada.");  
    }
    
    public QtdDifMarcacoesGravadaException(int qtdEncontrada, int qtdGravada){  
        super("A quantidade(" + qtdEncontrada + ") de marcacoes encontrada está diferente da quantidade(" + qtdGravada + ") gravada.");  
    }
    
}
