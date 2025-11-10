package controlador;

import modelo.*;
import vista.VistaSudoku;
import vista.VistaPerformance;
import interfaces.ISudokuSolver;
import interfaces.ISudokuValidator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador que coordina la vista y el modelo
 */
public class ControladorSudoku {
    private VistaSudoku vista;
    private ISudokuValidator validador;
    private ISudokuSolver resolvedor;
    private GeneradorSudoku generador;
    
    public ControladorSudoku(VistaSudoku vista) {
        this.vista = vista;
        this.validador = new ValidadorSudoku();
        this.resolvedor = new ResolvedorSudoku(validador);
        this.generador = new GeneradorSudoku();
        
        configurarListeners();
    }
    
    private void configurarListeners() {
        vista.agregarListenerResolver(new ListenerResolver());
        vista.agregarListenerLimpiar(new ListenerLimpiar());
        vista.agregarListenerGenerar(new ListenerGenerar());
        vista.agregarListenerValidar(new ListenerValidar());
        vista.agregarListenerContarSoluciones(new ListenerContarSoluciones());
        vista.agregarListenerAnalisisPerformance(new ListenerAnalisisPerformance());
        vista.agregarListenerSolucionAnterior(new ListenerSolucionAnterior());
        vista.agregarListenerSolucionSiguiente(new ListenerSolucionSiguiente());
    }
    
    private class ListenerResolver implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GrillaSudoku grilla = obtenerGrillaDesdeVista();
            
            // Verificar si la grilla está completa (sin celdas vacías)
            boolean estaCompleta = true;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (grilla.estaVacio(i, j)) {
                        estaCompleta = false;
                        break;
                    }
                }
                if (!estaCompleta) break;
            }
            
            if (!estaCompleta) {
                // Mostrar mensaje de "Perdiste" porque no está completo
                JOptionPane.showMessageDialog(
                    vista,
                    "¡Perdiste! El Sudoku no está completo.\nFaltan celdas por llenar.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE
                );
                vista.establecerEstado("El Sudoku no está completo. Sigue intentando!");
                return;
            }
            
            // Verificar si es válida (sin conflictos)
            if (validador.tieneConflictos(grilla)) {
                // Mostrar mensaje de "Perdiste" porque tiene errores
                resaltarConflictos(grilla);
                JOptionPane.showMessageDialog(
                    vista,
                    "¡Perdiste! El Sudoku tiene errores.\nHay valores duplicados en filas, columnas o cajas.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE
                );
                vista.establecerEstado("El Sudoku tiene errores. Corrige los conflictos.");
                return;
            }
            
            // Si llegamos aquí, está completo y sin conflictos = Ganaste!
            JOptionPane.showMessageDialog(
                vista,
                "¡Ganaste! 🎉\nEl Sudoku está completo y correcto.",
                "Resultado",
                JOptionPane.INFORMATION_MESSAGE
            );
            vista.establecerEstado("¡Felicitaciones! Sudoku resuelto correctamente.");
            vista.resetearColoresCeldas();
        }
    }
    
    private class ListenerLimpiar implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            vista.limpiarGrilla();
            vista.establecerEstado("Grilla limpiada.");
            vista.establecerInfoSolucion(" ");
        }
    }
    
    private class ListenerGenerar implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int cantidadPrefijados = vista.obtenerCantidadPrefijados();
            
            try {
                GrillaSudoku generado = generador.generarSudoku(cantidadPrefijados);
                actualizarVistaDesdeGrilla(generado);
                vista.establecerEstado("Sudoku generado con " + cantidadPrefijados + " valores prefijados.");
                vista.establecerInfoSolucion(" ");
            } catch (IllegalArgumentException ex) {
                vista.establecerEstado("Error: " + ex.getMessage());
            }
        }
    }
    
    private class ListenerValidar implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GrillaSudoku grilla = obtenerGrillaDesdeVista();
            
            if (validador.tieneConflictos(grilla)) {
                vista.establecerEstado("La grilla contiene conflictos (valores duplicados).");
                resaltarConflictos(grilla);
            } else {
                vista.establecerEstado("La grilla es válida (sin conflictos).");
                vista.resetearColoresCeldas();
            }
        }
    }
    
    private class ListenerContarSoluciones implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GrillaSudoku grilla = obtenerGrillaDesdeVista();
            
            if (validador.tieneConflictos(grilla)) {
                vista.establecerEstado("Error: La grilla contiene conflictos. No se pueden contar soluciones.");
                resaltarConflictos(grilla);
                return;
            }
            
            // Mostrar diálogo de progreso
            JDialog dialogoProgreso = new JDialog((JFrame) SwingUtilities.getWindowAncestor(vista), "Contando soluciones...", true);
            dialogoProgreso.setSize(300, 100);
            dialogoProgreso.setLocationRelativeTo(vista);
            JLabel etiqueta = new JLabel("Calculando, por favor espere...");
            dialogoProgreso.add(etiqueta);
            
            // Ejecutar en un hilo separado para no bloquear la UI
            new Thread(() -> {
                SwingUtilities.invokeLater(() -> dialogoProgreso.setVisible(true));
                
                int contador = resolvedor.contarSoluciones(grilla);
                
                SwingUtilities.invokeLater(() -> {
                    dialogoProgreso.setVisible(false);
                    
                    if (contador == 0) {
                        vista.establecerEstado("No se encontraron soluciones.");
                        vista.establecerInfoSolucion(" ");
                        vista.establecerSoluciones(new ArrayList<>());
                    } else if (contador == 1) {
                        vista.establecerEstado("Se encontró 1 solución única.");
                        vista.establecerInfoSolucion(" ");
                        // Resolver y mostrar la solución
                        GrillaSudoku solucion = new GrillaSudoku(grilla);
                        resolvedor.resolver(solucion);
                        vista.establecerGrilla(solucion.obtenerGrilla());
                        vista.establecerSoluciones(new ArrayList<>());
                    } else {
                        vista.establecerEstado("Se encontraron " + contador + " soluciones.");
                        // Generar todas las soluciones para navegación
                        generarTodasLasSoluciones(grilla, contador);
                    }
                });
            }).start();
        }
    }
    
    private class ListenerAnalisisPerformance implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            VistaPerformance vistaPerformance = new VistaPerformance((JFrame) SwingUtilities.getWindowAncestor(vista));
            
            // Configurar cantidades a analizar
            List<Integer> cantidades = new ArrayList<>();
            for (int i = 17; i <= 40; i += 3) {
                cantidades.add(i);
            }
            
            int ejecucionesPorCantidad = 10;
            int totalEjecuciones = cantidades.size() * ejecucionesPorCantidad;
            
            // Inicializar la barra de progreso
            SwingUtilities.invokeLater(() -> {
                vistaPerformance.actualizarProgreso(0, totalEjecuciones, "Iniciando análisis...");
                vistaPerformance.setVisible(true);
            });
            
            // Pequeño delay para que la ventana se muestre
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            // Ejecutar análisis en un hilo separado
            new Thread(() -> {
                try {
                    int ejecucionActual = 0;
                    Map<Integer, Double> resultados = new HashMap<>();
                    
                    for (Integer cantidad : cantidades) {
                        List<Long> tiempos = new ArrayList<>();
                        
                        for (int i = 0; i < ejecucionesPorCantidad; i++) {
                            ejecucionActual++;
                            final int ejecActual = ejecucionActual;
                            final int cantActual = cantidad;
                            
                            // Actualizar progreso
                            SwingUtilities.invokeLater(() -> {
                                vistaPerformance.actualizarProgreso(
                                    ejecActual, 
                                    totalEjecuciones,
                                    "Analizando " + cantActual + " valores prefijados (" + 
                                    ejecActual + "/" + totalEjecuciones + ")"
                                );
                            });
                            
                            // Pequeño delay para permitir actualización de UI
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                            
                            // Generar un Sudoku (ya viene con valores prefijados, no resuelto)
                            GrillaSudoku grilla = generador.generarSudoku(cantidad);
                            
                            // Crear una copia para resolver (no modificar la original)
                            GrillaSudoku grillaParaResolver = new GrillaSudoku(grilla);
                            
                            // Medir tiempo de resolución
                            long tiempoInicio = System.nanoTime();
                            boolean resuelto = resolvedor.resolver(grillaParaResolver);
                            long tiempoFin = System.nanoTime();
                            
                            // Solo contar si realmente se resolvió
                            if (resuelto) {
                                long tiempoMs = (tiempoFin - tiempoInicio) / 1_000_000;
                                tiempos.add(tiempoMs);
                            } else {
                                // Si no se pudo resolver, agregar un tiempo alto para indicar dificultad
                                tiempos.add(1000L); // 1 segundo como penalización
                            }
                        }
                        
                        // Calcular promedio
                        double promedio = tiempos.stream()
                                .mapToLong(Long::longValue)
                                .average()
                                .orElse(0.0);
                        
                        resultados.put(cantidad, promedio);
                    }
                    
                    // Mostrar resultados
                    SwingUtilities.invokeLater(() -> {
                        vistaPerformance.mostrarResultados(resultados);
                        vistaPerformance.ocultarProgreso();
                    });
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            vistaPerformance,
                            "Error durante el análisis: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        vistaPerformance.dispose();
                    });
                }
            }).start();
        }
    }
    
    private class ListenerSolucionAnterior implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            vista.mostrarSolucionAnterior();
        }
    }
    
    private class ListenerSolucionSiguiente implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            vista.mostrarSolucionSiguiente();
        }
    }
    
    private GrillaSudoku obtenerGrillaDesdeVista() {
        int[][] datosGrilla = vista.obtenerGrilla();
        return new GrillaSudoku(datosGrilla);
    }
    
    private void actualizarVistaDesdeGrilla(GrillaSudoku grilla) {
        vista.establecerGrilla(grilla.obtenerGrilla());
    }
    
    private void resaltarConflictos(GrillaSudoku grilla) {
        List<int[]> conflictos = encontrarConflictos(grilla);
        vista.resaltarConflictos(conflictos);
    }
    
    private List<int[]> encontrarConflictos(GrillaSudoku grilla) {
        List<int[]> conflictos = new ArrayList<>();
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int valor = grilla.obtenerValor(i, j);
                if (valor != 0 && !validador.esColocacionValida(grilla, i, j, valor)) {
                    conflictos.add(new int[]{i, j});
                }
            }
        }
        
        return conflictos;
    }
    
    private void generarTodasLasSoluciones(GrillaSudoku grilla, int maxSoluciones) {
        // Para no sobrecargar, limitamos a las primeras 10 soluciones
        int limite = Math.min(maxSoluciones, 10);
        List<int[][]> soluciones = new ArrayList<>();
        
        // Generar soluciones usando backtracking modificado
        generarSolucionesRecursivo(new GrillaSudoku(grilla), soluciones, limite);
        
        vista.establecerSoluciones(soluciones);
        if (soluciones.size() > 0) {
            vista.mostrarSolucion(0);
            if (maxSoluciones > limite) {
                vista.establecerEstado("Se encontraron " + maxSoluciones + " soluciones. Mostrando las primeras " + limite + ".");
            }
        }
    }
    
    private void generarSolucionesRecursivo(GrillaSudoku grilla, List<int[][]> soluciones, int limite) {
        if (soluciones.size() >= limite) {
            return;
        }
        
        int[] siguienteVacio = encontrarSiguienteVacio(grilla);
        
        if (siguienteVacio == null) {
            // Solución encontrada
            soluciones.add(grilla.obtenerGrilla());
            return;
        }
        
        int fila = siguienteVacio[0];
        int columna = siguienteVacio[1];
        
        for (int valor = 1; valor <= 9 && soluciones.size() < limite; valor++) {
            if (validador.esColocacionValida(grilla, fila, columna, valor)) {
                grilla.establecerValor(fila, columna, valor);
                generarSolucionesRecursivo(grilla, soluciones, limite);
                grilla.establecerValor(fila, columna, 0);
            }
        }
    }
    
    private int[] encontrarSiguienteVacio(GrillaSudoku grilla) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grilla.estaVacio(i, j)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
