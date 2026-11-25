package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class GraficaBarras {
    private BarChart<String, Number> grafica;
    private CategoryAxis ejeX;
    private NumberAxis ejeY;
    private VBox contenedor;

    /**
     * Constructor de la gráfica de barras
     * @param titulo
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

    public VBox getContenedor() {
        return contenedor;
    }

    public void actualizarDatos(ArrayList<Medicion> mediciones) {
        grafica.getData().clear();
        if (mediciones == null || mediciones.isEmpty()) {
            return;
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Tiempo de ejecución (ms)");

        for (Medicion m : mediciones) {
            String etiqueta = m.getAlgoritmo() + " (" + m.getTamanio() + "v)";
            serie.getData().add(new XYChart.Data<>(etiqueta, m.getTiempoMilisegundos()));
        }
        grafica.getData().add(serie);
    }

    public void actualizarComparativa(String nombreSerie1, ArrayList<Medicion> s1,
                                      String nombreSerie2, ArrayList<Medicion> s2) {
        grafica.getData().clear();
        if (s1 == null || s2 == null) {
            return;
        }

        XYChart.Series<String, Number> serie1 = new XYChart.Series<>();
        serie1.setName(nombreSerie1);
        for (Medicion m : s1) {
            serie1.getData().add(new XYChart.Data<>(m.getTamanio() + " vértices", m.getTiempoMilisegundos()));
        }

        XYChart.Series<String, Number> serie2 = new XYChart.Series<>();
        serie2.setName(nombreSerie2);
        for (Medicion m : s2) {
            serie2.getData().add(new XYChart.Data<>(m.getTamanio() + " vértices", m.getTiempoMilisegundos()));
        }

        grafica.getData().addAll(serie1, serie2);
    }

    public void limpiar() {
        grafica.getData().clear();
    }
}