package vista;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.text.DecimalFormat;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Vista para mostrar el análisis de performance con gráficos
 */
public class VistaPerformance extends JDialog {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private JLabel etiquetaProgreso;
    private JProgressBar barraProgreso;
    
    public VistaPerformance(JFrame parent) {
        super(parent, "Análisis de Performance", true);
        inicializarComponentes();
        organizarComponentes();
    }
    
    private void inicializarComponentes() {
        dataset = new DefaultCategoryDataset();
        
        chart = ChartFactory.createBarChart(
            "Tiempo Promedio de Resolución por Cantidad de Valores Prefijados",
            "Cantidad de Valores Prefijados",
            "Tiempo Promedio (ms)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Configurar el renderer para mostrar valores en las barras
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(true);
        
        // Crear un generador de etiquetas personalizado
        CategoryItemLabelGenerator generadorEtiquetas = new StandardCategoryItemLabelGenerator(
            "{2} ms", 
            new DecimalFormat("#.##")
        );
        renderer.setDefaultItemLabelGenerator(generadorEtiquetas);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        
        etiquetaProgreso = new JLabel("Preparando análisis...");
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(true);
        barraProgreso.setValue(0);
    }
    
    private void organizarComponentes() {
        setLayout(new BorderLayout());
        
        JPanel panelProgreso = new JPanel(new BorderLayout());
        panelProgreso.add(etiquetaProgreso, BorderLayout.NORTH);
        panelProgreso.add(barraProgreso, BorderLayout.CENTER);
        panelProgreso.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(panelProgreso, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    public void actualizarProgreso(int valor, int maximo, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            barraProgreso.setMaximum(maximo);
            barraProgreso.setValue(valor);
            etiquetaProgreso.setText(mensaje);
        });
    }
    
    public void mostrarResultados(Map<Integer, Double> resultados) {
        SwingUtilities.invokeLater(() -> {
            dataset.clear();
            
            // Ordenar las cantidades de menor a mayor
            List<Integer> cantidadesOrdenadas = new ArrayList<>(resultados.keySet());
            Collections.sort(cantidadesOrdenadas);
            
            for (Integer cantidad : cantidadesOrdenadas) {
                dataset.addValue(resultados.get(cantidad), "Tiempo Promedio", 
                    String.valueOf(cantidad));
            }
            
            barraProgreso.setValue(barraProgreso.getMaximum());
            etiquetaProgreso.setText("Análisis completado. " + resultados.size() + " puntos analizados.");
            
            chartPanel.repaint();
        });
    }
    
    public void ocultarProgreso() {
        SwingUtilities.invokeLater(() -> {
            remove(barraProgreso.getParent());
            revalidate();
            repaint();
        });
    }
}

