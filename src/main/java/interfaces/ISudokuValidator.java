package interfaces;

import modelo.GrillaSudoku;

/**
 * Interfaz para validación de reglas de Sudoku
 */
public interface ISudokuValidator {
    /**
     * Verifica si un valor puede ser colocado en una posición específica
     * @param grilla La grilla de Sudoku
     * @param fila La fila
     * @param columna La columna
     * @param valor El valor a verificar
     * @return true si el valor puede ser colocado, false en caso contrario
     */
    boolean esColocacionValida(GrillaSudoku grilla, int fila, int columna, int valor);
    
    /**
     * Verifica si la grilla completa es válida
     * @param grilla La grilla de Sudoku
     * @return true si la grilla es válida, false en caso contrario
     */
    boolean esGrillaValida(GrillaSudoku grilla);
    
    /**
     * Verifica si hay conflictos en la grilla actual
     * @param grilla La grilla de Sudoku
     * @return true si hay conflictos (valores duplicados), false si es válida
     */
    boolean tieneConflictos(GrillaSudoku grilla);
}

