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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controlador que coordina la vista y el modelo
 */
public class ControladorSudoku {
    private VistaSudoku vista;
    private ISudokuValidator validador;
    private ISudokuSolver resolvedor;
    private GeneradorSudoku generador;
    private AnalizadorPerformance analizadorPerformance;
    private int indiceSolucionActual = -1;
    private int totalSoluciones = 0;
    
    public ControladorSudoku(VistaSudoku vista) {
        this.vista = vista;
        this.validador = new ValidadorSudoku();
        this.resolvedor = new ResolvedorSudoku(validador);
        this.generador = new GeneradorSudoku();
        this.analizadorPerformance = new AnalizadorPerformance();
        
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
            limpiarSoluciones();
            
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
            limpiarSoluciones();
        }
    }
    
    private class ListenerGenerar implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int cantidadPrefijados = vista.obtenerCantidadPrefijados();
            
            try {
                GrillaSudoku generado = generador.generarSudoku(cantidadPrefijados);
                actualizarVistaDesdeGrilla(generado);
                limpiarSoluciones();
                vista.establecerEstado("Sudoku generado con " + cantidadPrefijados + " valores prefijados.");
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
                limpiarSoluciones();
                return;
            }
            
            // Mostrar diálogo de progreso
            JDialog dialogoProgreso = new JDialog((JFrame) SwingUtilities.getWindowAncestor(vista), "Contando soluciones...", true);
            dialogoProgreso.setSize(300, 100);
            dialogoProgreso.setLocationRelativeTo(vista);
            JLabel etiqueta = new JLabel("Calculando, por favor espere...");
            dialogoProgreso.add(etiqueta);
            
            new Thread(() -> {
                SwingUtilities.invokeLater(() -> dialogoProgreso.setVisible(true));
                
                int total = resolvedor.contarSoluciones(grilla.clonar());
                
                if (total > 0) {
                    resolvedor.prepararSoluciones(grilla.clonar());
                }
                
                SwingUtilities.invokeLater(() -> {
                    dialogoProgreso.setVisible(false);
                    
                    if (total == 0) {
                        vista.establecerEstado("No se encontraron soluciones.");
                        limpiarSoluciones();
                    } else {
                        totalSoluciones = total;
                        vista.establecerEstado(total == 1
                                ? "Se encontró 1 solución única."
                                : "Se encontraron " + total + " soluciones.");
                        indiceSolucionActual = 0;
                        mostrarSolucionActual();
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
            
            new Thread(() -> {
                try {
                    Map<Integer, Double> resultados = new HashMap<>();
                    AtomicInteger ejecucionesRealizadas = new AtomicInteger(0);
                    
                    for (Integer cantidad : cantidades) {
                        final int cantActual = cantidad;
                        double promedio = analizadorPerformance.medirTiempoPromedio(
                                cantActual,
                                ejecucionesPorCantidad,
                                incremento -> {
                                    int actual = ejecucionesRealizadas.incrementAndGet();
                                    SwingUtilities.invokeLater(() -> vistaPerformance.actualizarProgreso(
                                            actual,
                                            totalEjecuciones,
                                            "Analizando " + cantActual + " valores prefijados (" +
                                                    actual + "/" + totalEjecuciones + ")"
                                    ));
                                }
                        );
                        resultados.put(cantActual, promedio);
                    }
                    
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
            if (totalSoluciones <= 0) {
                return;
            }
            if (indiceSolucionActual > 0) {
                indiceSolucionActual--;
                mostrarSolucionActual();
            }
        }
    }
    
    private class ListenerSolucionSiguiente implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (totalSoluciones <= 0) {
                return;
            }
            if (indiceSolucionActual < totalSoluciones - 1) {
                indiceSolucionActual++;
                mostrarSolucionActual();
            }
        }
    }
    
    private GrillaSudoku obtenerGrillaDesdeVista() {
        return vista.construirGrilla();
    }
    
    private void actualizarVistaDesdeGrilla(GrillaSudoku grilla) {
        vista.mostrarGrilla(grilla);
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
    
    private void limpiarSoluciones() {
        indiceSolucionActual = -1;
        totalSoluciones = 0;
        vista.actualizarNavegacionSoluciones(0, 0);
        vista.establecerInfoSolucion(" ");
    }
    
    private void mostrarSolucionActual() {
        if (indiceSolucionActual < 0 || indiceSolucionActual >= totalSoluciones) {
            vista.actualizarNavegacionSoluciones(0, 0);
            return;
        }
        GrillaSudoku solucion = resolvedor.obtenerSolucion(indiceSolucionActual);
        if (solucion == null) {
            vista.actualizarNavegacionSoluciones(0, 0);
            return;
        }
        vista.mostrarGrilla(solucion);
        vista.actualizarNavegacionSoluciones(indiceSolucionActual, totalSoluciones);
    }
}
