package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    private ArrayList<Text> etiquetasTiempos;

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

        etiquetasTiempos = new ArrayList<>();

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
        vaciar();
        if (etiquetas == null || valores == null || etiquetas.isEmpty()) {
            return;
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Tiempo de ejecución (ms)");
        for (int i = 0; i < etiquetas.size(); i++) {
            serie.getData().add(crearDato(etiquetas.get(i), valores.get(i)));
        }
        grafica.getData().add(serie);
    }


    public void actualizarComparativa(ArrayList<String> etiquetas,
                                      String nombreSerie1, ArrayList<Double> valores1,
                                      String nombreSerie2, ArrayList<Double> valores2) {
        vaciar();
        if (etiquetas == null || valores1 == null || valores2 == null) {
            return;
        }

        XYChart.Series<String, Number> serie1 = new XYChart.Series<>();
        serie1.setName(nombreSerie1);
        XYChart.Series<String, Number> serie2 = new XYChart.Series<>();
        serie2.setName(nombreSerie2);

        for (int i = 0; i < etiquetas.size(); i++) {
            serie1.getData().add(crearDato(etiquetas.get(i), valores1.get(i)));
            serie2.getData().add(crearDato(etiquetas.get(i), valores2.get(i)));
        }
        grafica.getData().addAll(serie1, serie2);
    }

    private XYChart.Data<String, Number> crearDato(String categoria, double valor) {
        XYChart.Data<String, Number> dato = new XYChart.Data<>(categoria, valor);

        Text etiqueta = new Text(formatearTiempo(valor));
        etiqueta.getStyleClass().add("etiqueta-barra");
        etiquetasTiempos.add(etiqueta);

        // La gráfica crea la barra después, y en ese momento se enlaza el texto a ella
        dato.nodeProperty().addListener((obs, anterior, barra) -> {
            if (barra == null) return;

            // El texto se agrega al mismo grupo donde la gráfica dibuja la barra
            barra.parentProperty().addListener((o, padreAnterior, padre) -> {
                if (padre instanceof Group && !((Group) padre).getChildren().contains(etiqueta)) {
                    ((Group) padre).getChildren().add(etiqueta);
                }
            });

            // Cada vez que la barra cambia de posición o tamaño, el texto la sigue
            barra.boundsInParentProperty().addListener((o, limitesAnteriores, limites) -> {
                etiqueta.setLayoutX(limites.getMinX() + limites.getWidth() / 2 - etiqueta.prefWidth(-1) / 2);
                // El máximo evita que el texto de la barra más alta se salga por arriba de la gráfica
                etiqueta.setLayoutY(Math.max(limites.getMinY() - 4, etiqueta.prefHeight(-1)));
            });
        });
        return dato;
    }

    private String formatearTiempo(double milisegundos) {
        if (milisegundos >= 100) {
            return String.format("%.0f ms", milisegundos);
        }
        if (milisegundos >= 1) {
            return String.format("%.2f ms", milisegundos);
        }
        return String.format("%.4f ms", milisegundos);
    }

    private void quitarEtiquetas() {
        for (Text etiqueta : etiquetasTiempos) {
            if (etiqueta.getParent() instanceof Group) {
                ((Group) etiqueta.getParent()).getChildren().remove(etiqueta);
            }
        }
        etiquetasTiempos.clear();
    }

    /**
     * Metodo que limpia todos los datos dibujados actualmente en la gráfica
     */
    public void limpiar() {
        vaciar();
    }

    private void vaciar() {
        quitarEtiquetas();
        grafica.getData().clear();
    }
}