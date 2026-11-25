package com.example.proyectofinal_algoritmos.Vista.Ventanas;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.GraficaBarras;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Clase que modela la ventana secundaria para la visualización de la gráfica de análisis empírico
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class VentanaAnalisis {
    private Stage ventana;
    private VBox contenedor;
    private GraficaBarras graficaBarras;

    /**
     * Constructor de la ventana de análisis empírico
     * @param owner Ventana principal propietaria de esta ventana secundaria
     */
    public VentanaAnalisis(Stage owner) {
        contenedor = new VBox(10);
        contenedor.setPadding(new Insets(12));
        contenedor.getStyleClass().add("ventana-analisis");

        graficaBarras = new GraficaBarras("Análisis Empírico: Comparativa de Tiempos (ms)");
        VBox.setVgrow(graficaBarras.getContenedor(), Priority.ALWAYS);

        contenedor.getChildren().add(graficaBarras.getContenedor());

        ventana = new Stage();
        if (owner != null) {
            ventana.initOwner(owner);
        }
        ventana.setTitle("Gráfica de Análisis Empírico");
        ventana.setScene(new Scene(contenedor, 780, 440));
    }

    /**
     * Metodo que muestra la ventana de análisis en pantalla o la trae al frente si ya está abierta
     */
    public void mostrar() {
        if (!ventana.isShowing()) {
            ventana.show();
        } else {
            ventana.toFront();
        }
    }

    /**
     * Metodo que actualiza la gráfica de barras con una serie individual de mediciones
     * @param mediciones Lista de mediciones registradas a graficar
     */
    public void actualizarDatos(ArrayList<Medicion> mediciones) {
        graficaBarras.actualizarDatos(mediciones);
    }

    /**
     * Metodo que actualiza la gráfica de barras comparando dos series de distintos algoritmos
     * @param nombre1 Nombre identificador de la primera serie
     * @param serie1 Lista de mediciones correspondientes al primer algoritmo
     * @param nombre2 Nombre identificador de la segunda serie
     * @param serie2 Lista de mediciones correspondientes al segundo algoritmo
     */
    public void actualizarComparativa(String nombre1, ArrayList<Medicion> serie1,
                                      String nombre2, ArrayList<Medicion> serie2) {
        graficaBarras.actualizarComparativa(nombre1, serie1, nombre2, serie2);
    }

    /**
     * Metodo que limpia los datos dibujados en la gráfica de barras
     */
    public void limpiar() {
        graficaBarras.limpiar();
    }
}