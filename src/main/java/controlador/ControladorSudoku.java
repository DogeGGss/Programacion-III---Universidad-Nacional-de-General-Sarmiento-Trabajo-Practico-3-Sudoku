package controlador;

import modelo.*;
import vista.VistaSudoku;
import interfaces.ISudokuSolver;
import interfaces.ISudokuValidator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
        vista.agregarListenerSolucionAnterior(new ListenerSolucionAnterior());
        vista.agregarListenerSolucionSiguiente(new ListenerSolucionSiguiente());
    }
    
    private class ListenerResolver implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GrillaSudoku grilla = obtenerGrillaDesdeVista();
            
            if (validador.tieneConflictos(grilla)) {
                vista.establecerEstado("Error: La grilla contiene conflictos. Use 'Validar' para ver detalles.");
                resaltarConflictos(grilla);
                return;
            }
            
            GrillaSudoku grillaTrabajo = new GrillaSudoku(grilla);
            
            if (resolvedor.resolver(grillaTrabajo)) {
                actualizarVistaDesdeGrilla(grillaTrabajo);
                vista.establecerEstado("Sudoku resuelto exitosamente!");
            } else {
                vista.establecerEstado("No se encontró solución para este Sudoku.");
            }
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
