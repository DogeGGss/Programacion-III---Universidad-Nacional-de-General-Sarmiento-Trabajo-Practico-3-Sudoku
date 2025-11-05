package interfaces;

import modelo.GrillaSudoku;

/**
 * Interfaz para estrategias de resolución de Sudoku
 */
public interface ISudokuSolver {
    /**
     * Resuelve un Sudoku
     * @param grilla La grilla de Sudoku a resolver
     * @return true si se encontró una solución, false en caso contrario
     */
    boolean resolver(GrillaSudoku grilla);
    
    /**
     * Cuenta todas las soluciones posibles del Sudoku
     * @param grilla La grilla de Sudoku
     * @return El número de soluciones encontradas
     */
    int contarSoluciones(GrillaSudoku grilla);
}

