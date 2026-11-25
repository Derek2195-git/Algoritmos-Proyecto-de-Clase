package com.example.proyectofinal_algoritmos.Vista.Ventanas;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
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

/**
 * Clase que modela la ventana secundaria para la edición manual, estructura y visualización gráfica del grafo
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
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

    /**
     * Constructor de la ventana de gestión y visualización del grafo
     * @param owner Ventana principal propietaria de esta ventana secundaria
     */
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

    /**
     * Getter del contenedor principal de la ventana del grafo
     * @return Contenedor donde se ubican los controles y la sección gráfica
     */
    public VBox getContenedor() {
        return contenedor;
    }

    /**
     * Getter de la sección gráfica donde se dibuja el grafo
     * @return Instancia de SeccionGrafo contenida en la ventana
     */
    public SeccionGrafo getSeccionGrafica() {
        return seccionGrafo;
    }

    /**
     * Metodo que muestra la ventana del grafo en pantalla o la trae al frente si ya está abierta
     */
    public void mostrar() {
        if (!ventana.isShowing()) {
            ventana.show();
        } else {
            ventana.toFront();
        }
    }

    /**
     * Metodo que dibuja en la sección gráfica un grafo dentro del límite de 50 vértices
     * @param grafo Grafo a representar visualmente
     */
    public void mostrarGrafoPequeno(Grafo grafo) {
        seccionGrafo.renderizarGrafoPequeno(grafo);
    }

    /**
     * Metodo que muestra un aviso cuando el grafo excede el límite visual de 50 vértices
     * @param totalVertices Cantidad total de vértices del grafo
     */
    public void mostrarAvisoGrafoGrande(int totalVertices) {
        seccionGrafo.mostrarMensajeGrafoGrande(totalVertices);
    }

    /**
     * Metodo que resalta en el grafo visual una secuencia de vértices
     * @param secuenciaVertices Lista ordenada con los identificadores de los vértices a resaltar
     */
    public void resaltarRuta(ArrayList<Integer> secuenciaVertices) {
        seccionGrafo.resaltarRuta(secuenciaVertices);
    }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de agregar vértice
     * @param accion Acción a realizar
     */
    public void alAgregarVertice(Runnable accion) { btnAgregarVertice.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de eliminar vértice
     * @param accion Acción a realizar
     */
    public void alEliminarVertice(Runnable accion) { btnEliminarVertice.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de agregar arista
     * @param accion Acción a realizar
     */
    public void alAgregarArista(Runnable accion) { btnAgregarArista.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de modificar peso
     * @param accion Acción a realizar
     */
    public void alModificarPeso(Runnable accion) { btnModificarPeso.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de eliminar arista
     * @param accion Acción a realizar
     */
    public void alEliminarArista(Runnable accion) { btnEliminarArista.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de ver conexiones
     * @param accion Acción a realizar
     */
    public void alVerConexiones(Runnable accion) { btnVerConexiones.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de ver matriz de adyacencia
     * @param accion Acción a realizar
     */
    public void alVerMatriz(Runnable accion) { btnVerMatriz.setOnAction(e -> accion.run()); }

    /**
     * Getter del texto ingresado en el campo de vértice de origen
     * @return Texto del origen sin espacios en los extremos
     */
    public String getTextoOrigen() { return txtOrigen.getText().trim(); }

    /**
     * Getter del texto ingresado en el campo de vértice de destino
     * @return Texto del destino sin espacios en los extremos
     */
    public String getTextoDestino() { return txtDestino.getText().trim(); }

    /**
     * Getter del texto ingresado en el campo de peso de la arista
     * @return Texto del peso sin espacios en los extremos
     */
    public String getTextoPeso() { return txtPeso.getText().trim(); }

    /**
     * Metodo que limpia los campos de texto de origen, destino y peso
     */
    public void limpiarCampos() {
        txtOrigen.clear();
        txtDestino.clear();
        txtPeso.clear();
    }

    /**
     * Metodo que asigna la acción a realizar cuando se hace clic sobre un vértice en el dibujo
     * @param accion Acción que recibe el identificador entero del vértice seleccionado
     */
    public void alSeleccionarVertice(Consumer<Integer> accion) {
        seccionGrafo.alSeleccionarVertice(accion);
    }

    /**
     * Setter que asigna un texto al campo de vértice de origen
     * @param texto Nuevo texto para el campo de origen
     */
    public void setTextoOrigen(String texto) {
        txtOrigen.setText(texto);
    }

    /**
     * Setter que asigna un texto al campo de vértice de destino
     * @param texto Nuevo texto para el campo de destino
     */
    public void setTextoDestino(String texto) {
        txtDestino.setText(texto);
    }

    /**
     * Setter que asigna un texto al campo de peso
     * @param texto Nuevo texto para el campo de peso
     */
    public void setTextoPeso(String texto) {
        txtPeso.setText(texto);
    }
}