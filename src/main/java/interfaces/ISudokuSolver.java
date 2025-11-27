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
    
    /**
     * Calcula y almacena todas las soluciones posibles para la grilla
     * @param grilla La grilla de Sudoku
     * @return La cantidad de soluciones encontradas
     */
    int prepararSoluciones(GrillaSudoku grilla);
    
    /**
     * Obtiene una solución previamente calculada mediante {@link #prepararSoluciones(GrillaSudoku)}
     * @param indice Índice de la solución (base cero)
     * @return La solución solicitada o null si el índice es inválido
     */
    GrillaSudoku obtenerSolucion(int indice);
}

