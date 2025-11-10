package vista;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Vista de la interfaz gráfica del Sudoku
 */
public class VistaSudoku extends JFrame {
    private static final int TAMANO = 9;
    private static final int TAMANO_CAJA = 3;
    
    private JTextField[][] celdas;
    private JButton botonResolver;
    private JButton botonLimpiar;
    private JButton botonGenerar;
    private JButton botonValidar;
    private JButton botonContarSoluciones;
    private JButton botonAnalisisPerformance;
    private JButton botonSolucionAnterior;
    private JButton botonSolucionSiguiente;
    private JSpinner spinnerPrefijados;
    private JLabel etiquetaEstado;
    private JLabel etiquetaInfoSolucion;
    
    private int indiceSolucionActual;
    private java.util.List<int[][]> soluciones;
    
    public VistaSudoku() {
        soluciones = new java.util.ArrayList<>();
        indiceSolucionActual = -1;
        inicializarComponentes();
        organizarComponentes();
    }
    
    private void inicializarComponentes() {
        celdas = new JTextField[TAMANO][TAMANO];
        
        // Crear los campos de texto con bordes especiales para las cajas
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                JTextField celda = new JTextField();
                celda.setHorizontalAlignment(JTextField.CENTER);
                celda.setFont(new Font("Arial", Font.BOLD, 20));
                
                // Determinar qué bordes deben ser gruesos (bordes de caja)
                boolean bordeSuperiorGrueso = (i % TAMANO_CAJA == 0);
                boolean bordeIzquierdoGrueso = (j % TAMANO_CAJA == 0);
                boolean bordeInferiorGrueso = (i == TAMANO - 1 || (i + 1) % TAMANO_CAJA == 0);
                boolean bordeDerechoGrueso = (j == TAMANO - 1 || (j + 1) % TAMANO_CAJA == 0);
                
                Border borde = BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                        bordeSuperiorGrueso ? 2 : 1, bordeIzquierdoGrueso ? 2 : 1,
                        bordeInferiorGrueso ? 2 : 1, bordeDerechoGrueso ? 2 : 1,
                        bordeSuperiorGrueso || bordeIzquierdoGrueso || bordeInferiorGrueso || bordeDerechoGrueso ? Color.BLACK : Color.LIGHT_GRAY
                    ),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                );
                celda.setBorder(borde);
                
                // Permitir solo números del 1 al 9
                celda.setDocument(new javax.swing.text.PlainDocument() {
                    @Override
                    public void insertString(int offset, String texto, javax.swing.text.AttributeSet atributos)
                            throws javax.swing.text.BadLocationException {
                        if (texto != null && (texto.isEmpty() || (texto.length() == 1 && texto.charAt(0) >= '1' && texto.charAt(0) <= '9'))) {
                            super.insertString(offset, texto, atributos);
                        }
                    }
                });
                
                // Confirmar valor al presionar Enter
                celda.addActionListener((ActionEvent e) -> {
                    confirmarValorCelda(celda);
                });
                
                // Confirmar valor al perder el foco
                celda.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        // No hacer nada al ganar foco
                    }
                    
                    @Override
                    public void focusLost(FocusEvent e) {
                        confirmarValorCelda(celda);
                    }
                });
                
                celdas[i][j] = celda;
            }
        }
        
        botonResolver = new JButton("Verificar Solución");
        botonLimpiar = new JButton("Limpiar");
        botonGenerar = new JButton("Generar Aleatorio");
        botonValidar = new JButton("Validar");
        botonContarSoluciones = new JButton("Contar Soluciones");
        botonAnalisisPerformance = new JButton("Análisis de Performance");
        botonSolucionAnterior = new JButton("← Anterior");
        botonSolucionSiguiente = new JButton("Siguiente →");
        
        spinnerPrefijados = new JSpinner(new SpinnerNumberModel(30, 17, 81, 1));
        spinnerPrefijados.setPreferredSize(new Dimension(80, 25));
        
        // Obtener el editor del spinner (que es un JTextField)
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinnerPrefijados.getEditor();
        JTextField campoTexto = editor.getTextField();
        
        // Confirmar valor al presionar Enter
        campoTexto.addActionListener((ActionEvent e) -> {
            confirmarValorSpinner();
        });
        
        // Confirmar valor al perder el foco
        campoTexto.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // No hacer nada al ganar foco
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                confirmarValorSpinner();
            }
        });
        
        etiquetaEstado = new JLabel(" ");
        etiquetaEstado.setForeground(Color.BLUE);
        
        etiquetaInfoSolucion = new JLabel(" ");
        etiquetaInfoSolucion.setForeground(Color.DARK_GRAY);
        
        botonSolucionAnterior.setEnabled(false);
        botonSolucionSiguiente.setEnabled(false);
    }
    
    private void organizarComponentes() {
        setTitle("Resolvedor de Sudoku");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel principal de la grilla
        JPanel panelGrilla = new JPanel(new GridLayout(TAMANO, TAMANO, 0, 0));
        panelGrilla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                panelGrilla.add(celdas[i][j]);
            }
        }
        
        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout());
        panelControles.add(botonResolver);
        panelControles.add(botonLimpiar);
        panelControles.add(botonValidar);
        panelControles.add(new JLabel("Valores prefijados:"));
        panelControles.add(spinnerPrefijados);
        panelControles.add(botonGenerar);
        panelControles.add(botonContarSoluciones);
        panelControles.add(botonAnalisisPerformance);
        
        // Panel de navegación de soluciones
        JPanel panelSoluciones = new JPanel(new FlowLayout());
        panelSoluciones.add(botonSolucionAnterior);
        panelSoluciones.add(etiquetaInfoSolucion);
        panelSoluciones.add(botonSolucionSiguiente);
        
        // Panel de estado
        JPanel panelEstado = new JPanel(new FlowLayout());
        panelEstado.add(etiquetaEstado);
        
        // Panel sur con todos los controles
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelControles, BorderLayout.NORTH);
        panelSur.add(panelSoluciones, BorderLayout.CENTER);
        panelSur.add(panelEstado, BorderLayout.SOUTH);
        
        add(panelGrilla, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(500, 600));
    }
    
    // Métodos para obtener/establecer valores
    
    public int obtenerValor(int fila, int columna) {
        String texto = celdas[fila][columna].getText().trim();
        if (texto.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void establecerValor(int fila, int columna, int valor) {
        if (valor == 0) {
            celdas[fila][columna].setText("");
        } else {
            celdas[fila][columna].setText(String.valueOf(valor));
        }
    }
    
    public void establecerGrilla(int[][] grilla) {
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                establecerValor(i, j, grilla[i][j]);
            }
        }
    }
    
    public int[][] obtenerGrilla() {
        int[][] grilla = new int[TAMANO][TAMANO];
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                grilla[i][j] = obtenerValor(i, j);
            }
        }
        return grilla;
    }
    
    public void limpiarGrilla() {
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                establecerValor(i, j, 0);
            }
        }
    }
    
    public int obtenerCantidadPrefijados() {
        return ((SpinnerNumberModel) spinnerPrefijados.getModel()).getNumber().intValue();
    }
    
    public void establecerEstado(String mensaje) {
        etiquetaEstado.setText(mensaje);
    }
    
    public void establecerInfoSolucion(String informacion) {
        etiquetaInfoSolucion.setText(informacion);
    }
    
    public void establecerSoluciones(java.util.List<int[][]> soluciones) {
        this.soluciones = soluciones;
        indiceSolucionActual = -1;
        actualizarNavegacionSoluciones();
    }
    
    public void mostrarSolucion(int indice) {
        if (indice >= 0 && indice < soluciones.size()) {
            indiceSolucionActual = indice;
            establecerGrilla(soluciones.get(indice));
            actualizarNavegacionSoluciones();
        }
    }
    
    private void actualizarNavegacionSoluciones() {
        botonSolucionAnterior.setEnabled(soluciones.size() > 0 && indiceSolucionActual > 0);
        botonSolucionSiguiente.setEnabled(soluciones.size() > 0 && indiceSolucionActual < soluciones.size() - 1);
        
        if (soluciones.size() > 0 && indiceSolucionActual >= 0) {
            establecerInfoSolucion("Solución " + (indiceSolucionActual + 1) + " de " + soluciones.size());
        } else {
            establecerInfoSolucion(" ");
        }
    }
    
    public void mostrarSolucionAnterior() {
        if (indiceSolucionActual > 0) {
            mostrarSolucion(indiceSolucionActual - 1);
        }
    }
    
    public void mostrarSolucionSiguiente() {
        if (indiceSolucionActual < soluciones.size() - 1) {
            mostrarSolucion(indiceSolucionActual + 1);
        }
    }
    
    public void resaltarConflictos(java.util.List<int[]> conflictos) {
        // Resetear todos los colores
        resetearColoresCeldas();
        
        // Resaltar conflictos en rojo
        for (int[] conflicto : conflictos) {
            if (conflicto[0] >= 0 && conflicto[0] < TAMANO && conflicto[1] >= 0 && conflicto[1] < TAMANO) {
                celdas[conflicto[0]][conflicto[1]].setBackground(new Color(255, 200, 200)); // Rojo claro
            }
        }
    }
    
    public void resetearColoresCeldas() {
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                celdas[i][j].setBackground(Color.WHITE);
            }
        }
    }
    
    // Métodos para agregar listeners
    
    public void agregarListenerResolver(ActionListener listener) {
        botonResolver.addActionListener(listener);
    }
    
    public void agregarListenerLimpiar(ActionListener listener) {
        botonLimpiar.addActionListener(listener);
    }
    
    public void agregarListenerGenerar(ActionListener listener) {
        botonGenerar.addActionListener(listener);
    }
    
    public void agregarListenerValidar(ActionListener listener) {
        botonValidar.addActionListener(listener);
    }
    
    public void agregarListenerContarSoluciones(ActionListener listener) {
        botonContarSoluciones.addActionListener(listener);
    }
    
    public void agregarListenerAnalisisPerformance(ActionListener listener) {
        botonAnalisisPerformance.addActionListener(listener);
    }
    
    public void agregarListenerSolucionAnterior(ActionListener listener) {
        botonSolucionAnterior.addActionListener(listener);
    }
    
    public void agregarListenerSolucionSiguiente(ActionListener listener) {
        botonSolucionSiguiente.addActionListener(listener);
    }
    
    /**
     * Confirma el valor de una celda, validando y normalizando el texto
     */
    private void confirmarValorCelda(JTextField celda) {
        String texto = celda.getText().trim();
        
        // Si está vacío, dejarlo vacío
        if (texto.isEmpty()) {
            celda.setText("");
            return;
        }
        
        // Intentar parsear el número
        try {
            int valor = Integer.parseInt(texto);
            
            // Validar que esté entre 1 y 9
            if (valor >= 1 && valor <= 9) {
                celda.setText(String.valueOf(valor));
            } else {
                // Si está fuera de rango, limpiar
                celda.setText("");
            }
        } catch (NumberFormatException e) {
            // Si no es un número válido, limpiar
            celda.setText("");
        }
    }
    
    /**
     * Confirma el valor del spinner de valores prefijados
     */
    private void confirmarValorSpinner() {
        try {
            // Intentar obtener el valor del spinner
            Object valor = spinnerPrefijados.getValue();
            
            // Si el valor es un número, validar que esté en el rango
            if (valor instanceof Number) {
                int valorInt = ((Number) valor).intValue();
                SpinnerNumberModel modelo = (SpinnerNumberModel) spinnerPrefijados.getModel();
                
                // Validar que esté en el rango permitido
                Comparable<?> minComparable = modelo.getMinimum();
                Comparable<?> maxComparable = modelo.getMaximum();
                
                int min = (minComparable instanceof Number) ? ((Number) minComparable).intValue() : 17;
                int max = (maxComparable instanceof Number) ? ((Number) maxComparable).intValue() : 81;
                
                if (valorInt < min) {
                    spinnerPrefijados.setValue(min);
                } else if (valorInt > max) {
                    spinnerPrefijados.setValue(max);
                } else {
                    spinnerPrefijados.setValue(valorInt);
                }
            }
        } catch (Exception e) {
            // Si hay error, restaurar al valor por defecto
            spinnerPrefijados.setValue(30);
        }
    }
}

