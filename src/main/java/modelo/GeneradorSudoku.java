package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generador de instancias aleatorias de Sudoku
 */
public class GeneradorSudoku {
    private Random aleatorio;
    private ResolvedorSudoku resolvedor;
    private ValidadorSudoku validador;
    
    public GeneradorSudoku() {
        this.aleatorio = new Random();
        this.validador = new ValidadorSudoku();
        this.resolvedor = new ResolvedorSudoku(validador);
    }
    
    /**
     * Genera una grilla de Sudoku resuelta (completamente llena)
     * @return Una grilla de Sudoku resuelta
     */
    public GrillaSudoku generarSudokuResuelto() {
        GrillaSudoku grilla = new GrillaSudoku();
        
        // Llenar la diagonal principal de las 3 cajas principales
        llenarCajasDiagonales(grilla);
        
        // Resolver el resto usando backtracking
        resolvedor.resolver(grilla);
        
        return grilla;
    }
    
    /**
     * Genera una instancia de Sudoku con un número específico de valores prefijados
     * @param cantidadPrefijados El número de valores prefijados (debe estar entre 17 y 81)
     * @return Una grilla de Sudoku con valores prefijados
     */
    public GrillaSudoku generarSudoku(int cantidadPrefijados) {
        if (cantidadPrefijados < 17 || cantidadPrefijados > 81) {
            throw new IllegalArgumentException("El número de valores prefijados debe estar entre 17 y 81");
        }
        
        // Generar un Sudoku resuelto
        GrillaSudoku resuelto = generarSudokuResuelto();
        
        // Crear una lista de todas las posiciones
        List<int[]> posiciones = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                posiciones.add(new int[]{i, j});
            }
        }
        
        // Mezclar aleatoriamente
        Collections.shuffle(posiciones, aleatorio);
        
        // Crear una nueva grilla y quitar valores hasta alcanzar el número deseado
        GrillaSudoku puzzle = new GrillaSudoku(resuelto);
        int celdasAEliminar = 81 - cantidadPrefijados;
        
        for (int i = 0; i < celdasAEliminar && i < posiciones.size(); i++) {
            int[] posicion = posiciones.get(i);
            puzzle.establecerValor(posicion[0], posicion[1], 0);
        }
        
        return puzzle;
    }
    
    /**
     * Llena las tres cajas diagonales principales con valores aleatorios válidos
     */
    private void llenarCajasDiagonales(GrillaSudoku grilla) {
        for (int caja = 0; caja < 3; caja++) {
            llenarCaja(grilla, caja * 3, caja * 3);
        }
    }
    
    /**
     * Llena una caja 3x3 con valores aleatorios válidos
     */
    private void llenarCaja(GrillaSudoku grilla, int filaInicio, int columnaInicio) {
        List<Integer> valores = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            valores.add(i);
        }
        Collections.shuffle(valores, aleatorio);
        
        int indice = 0;
        for (int i = filaInicio; i < filaInicio + 3; i++) {
            for (int j = columnaInicio; j < columnaInicio + 3; j++) {
                grilla.establecerValor(i, j, valores.get(indice++));
            }
        }
    }
}

