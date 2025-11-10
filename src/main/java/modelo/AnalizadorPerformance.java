package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase para analizar el rendimiento del resolvedor de Sudoku
 */
public class AnalizadorPerformance {
    private GeneradorSudoku generador;
    private ResolvedorSudoku resolvedor;
    private ValidadorSudoku validador;
    
    public AnalizadorPerformance() {
        this.generador = new GeneradorSudoku();
        this.validador = new ValidadorSudoku();
        this.resolvedor = new ResolvedorSudoku(validador);
    }
    
    /**
     * Analiza el rendimiento para diferentes cantidades de valores prefijados
     * @param cantidadesPrefijados Lista de cantidades a analizar
     * @param ejecucionesPorCantidad Número de Sudokus a generar y resolver por cada cantidad
     * @return Mapa con cantidad de prefijados como clave y tiempo promedio en milisegundos como valor
     */
    public Map<Integer, Double> analizarRendimiento(List<Integer> cantidadesPrefijados, int ejecucionesPorCantidad) {
        Map<Integer, Double> resultados = new HashMap<>();
        
        for (Integer cantidad : cantidadesPrefijados) {
            List<Long> tiempos = new ArrayList<>();
            
            for (int i = 0; i < ejecucionesPorCantidad; i++) {
                // Generar un Sudoku
                GrillaSudoku grilla = generador.generarSudoku(cantidad);
                
                // Medir tiempo de resolución
                long tiempoInicio = System.nanoTime();
                resolvedor.resolver(grilla);
                long tiempoFin = System.nanoTime();
                
                // Convertir a milisegundos
                long tiempoMs = (tiempoFin - tiempoInicio) / 1_000_000;
                tiempos.add(tiempoMs);
            }
            
            // Calcular promedio
            double promedio = tiempos.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            
            resultados.put(cantidad, promedio);
        }
        
        return resultados;
    }
    
    /**
     * Analiza el rendimiento con valores por defecto
     * @return Mapa con cantidad de prefijados como clave y tiempo promedio en milisegundos como valor
     */
    public Map<Integer, Double> analizarRendimiento() {
        List<Integer> cantidades = new ArrayList<>();
        for (int i = 17; i <= 40; i += 3) {
            cantidades.add(i);
        }
        return analizarRendimiento(cantidades, 10);
    }
}

