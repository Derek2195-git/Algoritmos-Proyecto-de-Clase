package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * Clase que modela la gráfica de barras para el análisis empírico en la vista
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class GraficaBarras {
    private BarChart<String, Number> grafica;
    private CategoryAxis ejeX;
    private NumberAxis ejeY;
    private VBox contenedor;

    /**
     * Constructor de la gráfica de barras
     * @param titulo Título que se mostrará en la parte superior de la gráfica
     */
    public GraficaBarras(String titulo) {
        ejeX = new CategoryAxis();
        ejeY = new NumberAxis();
        ejeX.setLabel("Algoritmo / Tamaño");
        ejeY.setLabel("Tiempo (ms)");

        grafica = new BarChart<>(ejeX, ejeY);
        grafica.setTitle(titulo);
        grafica.setAnimated(false);
        grafica.setLegendVisible(true);
        grafica.setMinHeight(260);
        grafica.getStyleClass().add("Grafica-barras");

        contenedor = new VBox(grafica);
    }

    /**
     * Getter del contenedor donde se ubica la gráfica de barras
     * @return Contenedor donde se está almacenando la gráfica
     */
    public VBox getContenedor() {
        return contenedor;
    }


    public void actualizarDatos(ArrayList<String> etiquetas, ArrayList<Double> valores) {
        grafica.getData().clear();
        if (etiquetas == null || valores == null || etiquetas.isEmpty()) {
            return;
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Tiempo de ejecución (ms)");
        for (int i = 0; i < etiquetas.size(); i++) {
            serie.getData().add(new XYChart.Data<>(etiquetas.get(i), valores.get(i)));
        }
        grafica.getData().add(serie);
    }

    public void actualizarComparativa(ArrayList<String> etiquetas,
                                      String nombreSerie1, ArrayList<Double> valores1,
                                      String nombreSerie2, ArrayList<Double> valores2) {
        grafica.getData().clear();
        if (etiquetas == null || valores1 == null || valores2 == null) {
            return;
        }

        XYChart.Series<String, Number> serie1 = new XYChart.Series<>();
        serie1.setName(nombreSerie1);
        XYChart.Series<String, Number> serie2 = new XYChart.Series<>();
        serie2.setName(nombreSerie2);

        for (int i = 0; i < etiquetas.size(); i++) {
            serie1.getData().add(new XYChart.Data<>(etiquetas.get(i), valores1.get(i)));
            serie2.getData().add(new XYChart.Data<>(etiquetas.get(i), valores2.get(i)));
        }
        grafica.getData().addAll(serie1, serie2);
    }

    /**
     * Metodo que limpia todos los datos dibujados actualmente en la gráfica
     */
    public void limpiar() {
        grafica.getData().clear();
    }
}