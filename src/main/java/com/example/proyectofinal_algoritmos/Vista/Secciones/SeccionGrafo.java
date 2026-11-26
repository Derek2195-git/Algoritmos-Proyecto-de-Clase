package com.example.proyectofinal_algoritmos.Vista.Secciones;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos.GrafoGUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Clase que modela la sección de visualización gráfica del grafo en la vista
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class SeccionGrafo {
    private VBox contenedor;
    private VBox contenedorGrafo;
    private ScrollPane scrollGrafo;
    private Label lblEstadoGrafo;
    private GrafoGUI grafoGUI;

    /**
     * Constructor de la sección gráfica del grafo
     */
    public SeccionGrafo() {
        contenedor = new VBox(8);
        contenedor.setPadding(new Insets(10));
        contenedor.getStyleClass().add("seccion-grafica");

        lblEstadoGrafo = new Label("Visualización del Grafo (máximo 50 vértices)");
        lblEstadoGrafo.getStyleClass().add("titulo-seccion");

        contenedorGrafo = new VBox();
        contenedorGrafo.setAlignment(Pos.CENTER);
        contenedorGrafo.setMinHeight(340);
        contenedorGrafo.getStyleClass().add("contenedor-grafo");

        scrollGrafo = new ScrollPane(contenedorGrafo);
        scrollGrafo.setFitToWidth(true);
        scrollGrafo.setFitToHeight(true);
        VBox.setVgrow(scrollGrafo, Priority.ALWAYS);

        grafoGUI = new GrafoGUI(860, 320);
        VBox.setVgrow(grafoGUI.getContenedor(), Priority.ALWAYS);

        contenedor.getChildren().addAll(lblEstadoGrafo, scrollGrafo);
    }

    /**
     * Getter del contenedor donde se ubica la sección gráfica del grafo
     * @return Contenedor donde se está almacenando la sección
     */
    public VBox getContenedor() {
        return contenedor;
    }


    public void renderizarGrafoPequeno(String nombre, ArrayList<Integer> ids,
                                       int[][] conexiones, boolean dirigido) {
        contenedorGrafo.getChildren().clear();
        lblEstadoGrafo.setText("Grafo: " + nombre
                + " (" + ids.size() + " vértices, "
                + conexiones.length + " aristas)");

        grafoGUI.dibujar(ids, conexiones, dirigido);
        contenedorGrafo.getChildren().add(grafoGUI.getContenedor());
    }

    /**
     * Metodo que muestra un mensaje de aviso cuando el grafo supera los 50 vértices permitidos
     * @param totalVertices Cantidad total de vértices que posee el grafo actual
     */
    public void mostrarMensajeGrafoGrande(int totalVertices) {
        contenedorGrafo.getChildren().clear();
        grafoGUI.limpiar();
        lblEstadoGrafo.setText("Grafo de " + totalVertices + " vértices (límite visual: 50)");

        Label aviso = new Label("El grafo supera los 50 vértices permitidos para dibujarse en pantalla.\n"
                + "Consulta sus conexiones o matriz de adyacencia en la ventana principal. \n" +
                "Tambien puede crear otro grafo con menos de 50 vertices.");
        aviso.getStyleClass().add("mensaje-aviso");
        contenedorGrafo.getChildren().add(aviso);
    }

    /**
     * Metodo que resalta en el grafo dibujado una secuencia de vértices
     * @param secuenciaVertices Lista ordenada con los identificadores de los vértices de la ruta
     */
    public void resaltarRuta(ArrayList<Integer> secuenciaVertices) {
        grafoGUI.resaltarRuta(secuenciaVertices);
    }

    /**
     * Metodo que asigna la acción a realizar cuando se hace clic sobre un vértice dibujado
     * @param accion Acción que recibe el identificador entero del vértice seleccionado
     */
    public void alSeleccionarVertice(Consumer<Integer> accion) {
        grafoGUI.alHacerClicVertice(accion);
    }

    /**
     * Metodo que limpia el área de dibujo y restablece el texto de estado
     */
    public void limpiar() {
        contenedorGrafo.getChildren().clear();
        grafoGUI.limpiar();
        lblEstadoGrafo.setText("Visualización del Grafo (máximo 50 vértices)");
    }
}