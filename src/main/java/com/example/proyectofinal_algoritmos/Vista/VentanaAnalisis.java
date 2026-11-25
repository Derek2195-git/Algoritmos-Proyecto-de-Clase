package com.example.proyectofinal_algoritmos.Vista;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.GraficaBarras;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class VentanaAnalisis {
    private Stage ventana;
    private VBox contenedor;
    private GraficaBarras graficaBarras;

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

    public void mostrar() {
        if (!ventana.isShowing()) {
            ventana.show();
        } else {
            ventana.toFront();
        }
    }

    public void actualizarDatos(ArrayList<Medicion> mediciones) {
        graficaBarras.actualizarDatos(mediciones);
    }

    public void actualizarComparativa(String nombre1, ArrayList<Medicion> serie1,
                                      String nombre2, ArrayList<Medicion> serie2) {
        graficaBarras.actualizarComparativa(nombre1, serie1, nombre2, serie2);
    }

    public void limpiar() {
        graficaBarras.limpiar();
    }
}