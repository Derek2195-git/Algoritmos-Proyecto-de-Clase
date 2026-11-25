package com.example.proyectofinal_algoritmos.Vista;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.GraficaBarras;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionGrafo;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.function.Consumer;

public class VentanaGrafo {
    private Stage ventana;
    private VBox contenedor;

    // Controles de creación y edición manual
    private TextField txtOrigen;
    private TextField txtDestino;
    private TextField txtPeso;
    private Button btnAgregarVertice;
    private Button btnEliminarVertice;
    private Button btnAgregarArista;
    private Button btnModificarPeso;
    private Button btnEliminarArista;

    // Controles de estructura en tabla principal
    private Button btnVerConexiones;
    private Button btnVerMatriz;

    // Dibujo del grafo
    private SeccionGrafo seccionGrafo;

    public VentanaGrafo(Stage owner) {
        contenedor = new VBox(10);
        contenedor.setPadding(new Insets(12));
        contenedor.getStyleClass().add("ventana-grafo");

        // Fila 1: Edición de vértices y aristas
        txtOrigen = new TextField();
        txtOrigen.setPromptText("Origen");
        txtOrigen.setPrefWidth(90);

        txtDestino = new TextField();
        txtDestino.setPromptText("Destino");
        txtDestino.setPrefWidth(90);

        txtPeso = new TextField();
        txtPeso.setPromptText("Peso");
        txtPeso.setPrefWidth(75);

        btnAgregarVertice = new Button("+ Vértice");
        btnEliminarVertice = new Button("- Vértice");
        btnAgregarArista = new Button("+ Arista");
        btnModificarPeso = new Button("Modificar Peso");
        btnEliminarArista = new Button("- Arista");

        HBox filaEdicion = new HBox(8,
                new Label("Edición:"),
                txtOrigen, txtDestino, txtPeso,
                btnAgregarVertice, btnEliminarVertice,
                btnAgregarArista, btnModificarPeso, btnEliminarArista
        );
        filaEdicion.setAlignment(Pos.CENTER_LEFT);

        // Fila 2: Estructura del grafo
        btnVerConexiones = new Button("Ver Conexiones en Tabla");
        btnVerMatriz = new Button("Ver Matriz de Adyacencia en Tabla");

        HBox filaEstructura = new HBox(10,
                new Label("Estructura del Grafo:"),
                btnVerConexiones,
                new Separator(),
                btnVerMatriz
        );
        filaEstructura.setAlignment(Pos.CENTER_LEFT);

        // Área gráfica del grafo
        seccionGrafo = new SeccionGrafo();
        VBox.setVgrow(seccionGrafo.getContenedor(), Priority.ALWAYS);

        contenedor.getChildren().addAll(
                filaEdicion,
                filaEstructura,
                seccionGrafo.getContenedor()
        );

        ventana = new Stage();
        if (owner != null) {
            ventana.initOwner(owner);
        }
        ventana.setTitle("Gestión y Visualización del Grafo");
        ventana.setScene(new Scene(contenedor, 920, 540));
    }

    public VBox getContenedor() {
        return contenedor;
    }

    public SeccionGrafo getSeccionGrafica() {
        return seccionGrafo;
    }

    public void mostrar() {
        if (!ventana.isShowing()) {
            ventana.show();
        } else {
            ventana.toFront();
        }
    }

    public void mostrarGrafoPequeno(Grafo grafo) {
        seccionGrafo.renderizarGrafoPequeno(grafo);
    }

    public void mostrarAvisoGrafoGrande(int totalVertices) {
        seccionGrafo.mostrarMensajeGrafoGrande(totalVertices);
    }

    public void resaltarRuta(ArrayList<Integer> secuenciaVertices) {
        seccionGrafo.resaltarRuta(secuenciaVertices);
    }

    // Delegadores para el Controlador
    public void alAgregarVertice(Runnable accion) { btnAgregarVertice.setOnAction(e -> accion.run()); }
    public void alEliminarVertice(Runnable accion) { btnEliminarVertice.setOnAction(e -> accion.run()); }
    public void alAgregarArista(Runnable accion) { btnAgregarArista.setOnAction(e -> accion.run()); }
    public void alModificarPeso(Runnable accion) { btnModificarPeso.setOnAction(e -> accion.run()); }
    public void alEliminarArista(Runnable accion) { btnEliminarArista.setOnAction(e -> accion.run()); }
    public void alVerConexiones(Runnable accion) { btnVerConexiones.setOnAction(e -> accion.run()); }
    public void alVerMatriz(Runnable accion) { btnVerMatriz.setOnAction(e -> accion.run()); }

    // Getters de edición
    public String getTextoOrigen() { return txtOrigen.getText().trim(); }
    public String getTextoDestino() { return txtDestino.getText().trim(); }
    public String getTextoPeso() { return txtPeso.getText().trim(); }

    public void limpiarCampos() {
        txtOrigen.clear();
        txtDestino.clear();
        txtPeso.clear();
    }

    public void alSeleccionarVertice(Consumer<Integer> accion) {
        seccionGrafo.alSeleccionarVertice(accion);
    }

    public void setTextoOrigen(String texto) {
        txtOrigen.setText(texto);
    }

    public void setTextoDestino(String texto) {
        txtDestino.setText(texto);
    }

    public void setTextoPeso(String texto) {
        txtPeso.setText(texto);
    }
}
