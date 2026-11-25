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

public class SeccionGrafo {
    private VBox contenedor;
    private VBox contenedorGrafo;
    private ScrollPane scrollGrafo;
    private Label lblEstadoGrafo;
    private GrafoGUI grafoGUI;

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

        contenedor.getChildren().addAll(lblEstadoGrafo, scrollGrafo);
    }

    public VBox getContenedor() {
        return contenedor;
    }

    public void renderizarGrafoPequeno(Grafo grafo) {
        contenedorGrafo.getChildren().clear();
        lblEstadoGrafo.setText("Grafo: " + grafo.getNombre()
                + " (" + grafo.cantidadVertices() + " vértices, "
                + grafo.cantidadAristas() + " aristas)");

        ArrayList<Integer> ids = grafo.obtenerIds();
        ArrayList<Arista> listaAristas = grafo.obtenerAristas();
        int[][] conexiones = new int[listaAristas.size()][3];

        for (int i = 0; i < listaAristas.size(); i++) {
            Arista a = listaAristas.get(i);
            conexiones[i][0] = a.getOrigen();
            conexiones[i][1] = a.getDestino();
            conexiones[i][2] = a.getPeso();
        }

        grafoGUI.dibujar(ids, conexiones, grafo.isDirigido());
        contenedorGrafo.getChildren().add(grafoGUI.getContenedor());
    }

    public void mostrarMensajeGrafoGrande(int totalVertices) {
        contenedorGrafo.getChildren().clear();
        grafoGUI.limpiar();
        lblEstadoGrafo.setText("Grafo de " + totalVertices + " vértices (límite visual: 50)");

        Label aviso = new Label("El grafo supera los 50 vértices permitidos para dibujarse en pantalla.\n"
                + "Consulta sus conexiones o matriz de adyacencia en la ventana principal.");
        aviso.getStyleClass().add("mensaje-aviso");
        contenedorGrafo.getChildren().add(aviso);
    }

    public void resaltarRuta(ArrayList<Integer> secuenciaVertices) {
        grafoGUI.resaltarRuta(secuenciaVertices);
    }

    public void limpiar() {
        contenedorGrafo.getChildren().clear();
        grafoGUI.limpiar();
        lblEstadoGrafo.setText("Visualización del Grafo (máximo 50 vértices)");
    }
}
