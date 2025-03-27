package espe.lono.db.exceptions;

/**
 *
 * @author Luiz Diniz - Espe
 * @hidden
 */

public class QtdDifMateriasGravadaException extends Exception{
    
    public QtdDifMateriasGravadaException(){  
        super("A quantidade de matérias encontrada está diferente da quantidade gravada.");  
    }
    
    public QtdDifMateriasGravadaException(int qtdEncontrada, int qtdGravada){  
        super("A quantidade(" + qtdEncontrada + ") de matérias encontrada está diferente da quantidade(" + qtdGravada + ") gravada.");  
    }
    
}
