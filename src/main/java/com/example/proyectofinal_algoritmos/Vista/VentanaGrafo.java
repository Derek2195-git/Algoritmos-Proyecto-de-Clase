package com.example.proyectofinal_algoritmos.Vista;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.GraficaBarras;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionGrafo;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class VentanaGrafo {
    private Stage ventana;
    private VBox contenedor;
    private SeccionGrafo seccionGrafica;
    private GraficaBarras graficaBarras;

    public VentanaGrafo() {
        contenedor = new VBox(12);
        contenedor.setPadding(new Insets(8));
        contenedor.getStyleClass().add("ventana-grafo");

        seccionGrafica = new SeccionGrafo();
        VBox.setVgrow(seccionGrafica.getContenedor(), Priority.ALWAYS);

        graficaBarras = new GraficaBarras("Analisis Empirico");

        contenedor.getChildren().addAll(seccionGrafica.getContenedor(), graficaBarras.getContenedor());
        ventana = new Stage();
        ventana.setTitle("Grafos y Graficas de tiempos");
        ventana.setScene(new Scene(contenedor, 800, 600));
    }

    public VBox getContenedor() {
        return contenedor;
    }

    public SeccionGrafo getSeccionGrafica() {
        return seccionGrafica;
    }

    public GraficaBarras getGraficaBarras() {
        return graficaBarras;
    }

    public void mostrar() {
        if (!ventana.isShowing()) {
            ventana.show();
        } else {
            ventana.toFront();
        }
    }

    public void cerrar() {
        if (ventana.isShowing()) {
            ventana.close();
        }
    }

    public void mostrarGrafoPequeno(Grafo grafo) {
        seccionGrafica.renderizarGrafoPequeno(grafo);
    }

    public void mostrarAvisoGrafoGrande(int totalVertices) {
        seccionGrafica.mostrarMensajeGrafoGrande(totalVertices);
    }

    public void resaltarRuta(ArrayList<Integer> secuenciaVertices) {
        seccionGrafica.resaltarRuta(secuenciaVertices);
    }

    public void actualizarGraficaBarras(ArrayList<Medicion> mediciones) {
        graficaBarras.actualizarDatos(mediciones);
    }

    public void actualizarComparativaOrdenamientos(String nombre1, ArrayList<Medicion> serie1,
                                                   String nombre2, ArrayList<Medicion> serie2) {
        graficaBarras.actualizarComparativa(nombre1, serie1, nombre2, serie2);
    }

    public void limpiarTodo() {
        seccionGrafica.limpiar();
        graficaBarras.limpiar();
    }
}
