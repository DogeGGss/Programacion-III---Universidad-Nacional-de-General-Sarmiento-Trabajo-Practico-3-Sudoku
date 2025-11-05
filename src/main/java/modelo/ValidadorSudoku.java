package modelo;

import interfaces.ISudokuValidator;

/**
 * Implementación de validación de reglas de Sudoku
 */
public class ValidadorSudoku implements ISudokuValidator {
    
    @Override
    public boolean esColocacionValida(GrillaSudoku grilla, int fila, int columna, int valor) {
        if (valor < 1 || valor > 9) {
            return false;
        }
        
        // Verificar fila
        for (int j = 0; j < grilla.obtenerTamano(); j++) {
            if (j != columna && grilla.obtenerValor(fila, j) == valor) {
                return false;
            }
        }
        
        // Verificar columna
        for (int i = 0; i < grilla.obtenerTamano(); i++) {
            if (i != fila && grilla.obtenerValor(i, columna) == valor) {
                return false;
            }
        }
        
        // Verificar caja 3x3
        int filaCaja = (fila / grilla.obtenerTamanoCaja()) * grilla.obtenerTamanoCaja();
        int columnaCaja = (columna / grilla.obtenerTamanoCaja()) * grilla.obtenerTamanoCaja();
        
        for (int i = filaCaja; i < filaCaja + grilla.obtenerTamanoCaja(); i++) {
            for (int j = columnaCaja; j < columnaCaja + grilla.obtenerTamanoCaja(); j++) {
                if (i != fila && j != columna && grilla.obtenerValor(i, j) == valor) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    public boolean esGrillaValida(GrillaSudoku grilla) {
        // Verificar que no haya conflictos
        for (int i = 0; i < grilla.obtenerTamano(); i++) {
            for (int j = 0; j < grilla.obtenerTamano(); j++) {
                int valor = grilla.obtenerValor(i, j);
                if (valor != 0 && !esColocacionValida(grilla, i, j, valor)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean tieneConflictos(GrillaSudoku grilla) {
        return !esGrillaValida(grilla);
    }
}

