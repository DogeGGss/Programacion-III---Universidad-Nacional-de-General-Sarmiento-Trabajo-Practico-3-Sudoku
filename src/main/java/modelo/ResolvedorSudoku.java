package modelo;

import interfaces.ISudokuSolver;
import interfaces.ISudokuValidator;

/**
 * Implementación del algoritmo de backtracking para resolver Sudoku
 */
public class ResolvedorSudoku implements ISudokuSolver {
    private ISudokuValidator validador;
    private int contadorSoluciones;
    private boolean detenerConteo;
    
    public ResolvedorSudoku(ISudokuValidator validador) {
        this.validador = validador;
        this.contadorSoluciones = 0;
        this.detenerConteo = false;
    }
    
    @Override
    public boolean resolver(GrillaSudoku grilla) {
        detenerConteo = true;
        return resolverRecursivo(grilla);
    }
    
    @Override
    public int contarSoluciones(GrillaSudoku grilla) {
        contadorSoluciones = 0;
        detenerConteo = false;
        contarSolucionesRecursivo(new GrillaSudoku(grilla));
        return contadorSoluciones;
    }
    
    /**
     * Método recursivo para resolver el Sudoku usando backtracking
     */
    private boolean resolverRecursivo(GrillaSudoku grilla) {
        int[] siguienteVacio = encontrarSiguienteVacio(grilla);
        
        // Si no hay celdas vacías, el Sudoku está resuelto
        if (siguienteVacio == null) {
            return true;
        }
        
        int fila = siguienteVacio[0];
        int columna = siguienteVacio[1];
        
        // Intentar cada valor del 1 al 9
        for (int valor = 1; valor <= 9; valor++) {
            if (validador.esColocacionValida(grilla, fila, columna, valor)) {
                grilla.establecerValor(fila, columna, valor);
                
                // Recursión
                if (resolverRecursivo(grilla)) {
                    return true;
                }
                
                // Backtrack: deshacer el cambio
                grilla.establecerValor(fila, columna, 0);
            }
        }
        
        return false; // No se encontró solución
    }
    
    /**
     * Método recursivo para contar todas las soluciones
     */
    private void contarSolucionesRecursivo(GrillaSudoku grilla) {
        int[] siguienteVacio = encontrarSiguienteVacio(grilla);
        
        // Si no hay celdas vacías, encontramos una solución
        if (siguienteVacio == null) {
            contadorSoluciones++;
            return;
        }
        
        int fila = siguienteVacio[0];
        int columna = siguienteVacio[1];
        
        // Intentar cada valor del 1 al 9
        for (int valor = 1; valor <= 9; valor++) {
            if (validador.esColocacionValida(grilla, fila, columna, valor)) {
                grilla.establecerValor(fila, columna, valor);
                
                contarSolucionesRecursivo(grilla);
                
                // Backtrack: deshacer el cambio
                grilla.establecerValor(fila, columna, 0);
                
                // Si solo queremos saber si hay más de una solución, podemos optimizar
                if (detenerConteo && contadorSoluciones > 1) {
                    return;
                }
            }
        }
    }
    
    /**
     * Encuentra la siguiente celda vacía en la grilla
     * @param grilla La grilla
     * @return Un array [fila, columna] con las coordenadas, o null si no hay celdas vacías
     */
    private int[] encontrarSiguienteVacio(GrillaSudoku grilla) {
        for (int i = 0; i < grilla.obtenerTamano(); i++) {
            for (int j = 0; j < grilla.obtenerTamano(); j++) {
                if (grilla.estaVacio(i, j)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}

