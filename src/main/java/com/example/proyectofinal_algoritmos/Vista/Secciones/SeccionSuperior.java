package com.example.proyectofinal_algoritmos.Vista.Secciones;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Clase que modela la barra superior de controles de archivos, algoritmos y ventanas en la vista
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class SeccionSuperior {
    private VBox contenedor;

    // Fila 1: Archivos CSV, generación y ventana del grafo
    private Button btnCargarCSV;
    private Button btnGuardarCSV;
    private TextField txtCantidadVertices;
    private Button btnGenerarPrueba;
    private CheckBox chkDirigido;
    private Button btnAbrirVentanaGrafo;

    // Fila 2: Búsquedas, ordenamientos y análisis empírico
    private ComboBox<String> cmbAlgoritmoBusqueda;
    private TextField txtInicioBusqueda;
    private TextField txtFinBusqueda;
    private Button btnEjecutarBusqueda;
    private ComboBox<String> cmbAlgoritmoOrdenamiento;
    private ComboBox<String> cmbCriterioOrdenamiento;
    private Button btnEjecutarOrdenamiento;


    /**
     * Constructor de la sección superior de controles
     */
    public SeccionSuperior() {
        contenedor = new VBox(10);
        contenedor.setPadding(new Insets(12));
        contenedor.getStyleClass().add("seccion-superior");

        // Fila 1
        btnCargarCSV = new Button("Cargar CSV");
        btnGuardarCSV = new Button("Guardar CSV");

        txtCantidadVertices = new TextField();
        txtCantidadVertices.setPromptText("Núm. vértices");
        txtCantidadVertices.setPrefWidth(105);
        txtCantidadVertices.setTextFormatter(new TextFormatter<>(cambio -> {
            if (cambio.getControlNewText().matches("\\d*")) {
                return cambio;
            }
            return null;
        }));

        btnGenerarPrueba = new Button("Generar Grafo");
        chkDirigido = new CheckBox("Grafo Dirigido");
        btnAbrirVentanaGrafo = new Button("Ver Grafo");

        HBox filaArchivos = new HBox(10,
                btnCargarCSV, btnGuardarCSV,
                txtCantidadVertices, btnGenerarPrueba, chkDirigido,
                btnAbrirVentanaGrafo
        );
        filaArchivos.setAlignment(Pos.CENTER_LEFT);

        // Fila 2
        cmbAlgoritmoBusqueda = new ComboBox<>();
        cmbAlgoritmoBusqueda.getItems().addAll(
                "BFS (Anchura)",
                "DFS (Profundidad)",
                "Dijkstra (Ruta Óptima)"
        );
        cmbAlgoritmoBusqueda.getSelectionModel().selectFirst();

        txtInicioBusqueda = new TextField();
        txtInicioBusqueda.setPromptText("Inicio");
        txtInicioBusqueda.setPrefWidth(75);

        txtFinBusqueda = new TextField();
        txtFinBusqueda.setPromptText("Fin");
        txtFinBusqueda.setPrefWidth(95);

        btnEjecutarBusqueda = new Button("Ejecutar Búsqueda");
        cmbAlgoritmoBusqueda.setOnAction(e -> actualizarCampoFin());
        actualizarCampoFin();

        cmbAlgoritmoOrdenamiento = new ComboBox<>();
        cmbAlgoritmoOrdenamiento.getItems().addAll("QuickSort", "Shell Sort");
        cmbAlgoritmoOrdenamiento.getSelectionModel().selectFirst();

        cmbCriterioOrdenamiento = new ComboBox<>();
        cmbCriterioOrdenamiento.getItems().addAll("Por Peso", "Por Nombre de Vértice");
        cmbCriterioOrdenamiento.getSelectionModel().selectFirst();

        btnEjecutarOrdenamiento = new Button("Ordenar");


        HBox filaAlgoritmos = new HBox(8,
                new Label("Búsqueda:"),
                cmbAlgoritmoBusqueda, txtInicioBusqueda, txtFinBusqueda, btnEjecutarBusqueda,
                new Label("Ordenar:"),
                cmbAlgoritmoOrdenamiento, cmbCriterioOrdenamiento, btnEjecutarOrdenamiento
        );
        filaAlgoritmos.setAlignment(Pos.CENTER_LEFT);

        contenedor.getChildren().addAll(filaArchivos, filaAlgoritmos);
    }

    // Solo permite escribir el vértice destino cuando el algoritmo elegido es Dijkstra
    private void actualizarCampoFin() {
        String algoritmo = cmbAlgoritmoBusqueda.getValue();
        boolean esDijkstra = algoritmo != null && algoritmo.startsWith("Dijkstra");
        txtFinBusqueda.setDisable(!esDijkstra);
        if (!esDijkstra) {
            txtFinBusqueda.clear();
        }
    }

    /**
     * Getter del contenedor donde se ubica la sección superior
     * @return Contenedor donde se está almacenando la sección
     */
    public VBox getContenedor() {
        return contenedor;
    }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de cargar CSV
     * @param accion Acción a realizar
     */
    public void alCargarCSV(Runnable accion) { btnCargarCSV.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de guardar CSV
     * @param accion Acción a realizar
     */
    public void alGuardarCSV(Runnable accion) { btnGuardarCSV.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de generar grafo
     * @param accion Acción a realizar
     */
    public void alGenerarPrueba(Runnable accion) { btnGenerarPrueba.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al cambiar la selección de grafo dirigido
     * @param accion Acción a realizar
     */
    public void alCambiarDirigido(Runnable accion) { chkDirigido.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de ver grafo
     * @param accion Acción a realizar
     */
    public void alAbrirVentanaGrafo(Runnable accion) { btnAbrirVentanaGrafo.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de ejecutar búsqueda
     * @param accion Acción a realizar
     */
    public void alEjecutarBusqueda(Runnable accion) { btnEjecutarBusqueda.setOnAction(e -> accion.run()); }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de ordenar
     * @param accion Acción a realizar
     */
    public void alEjecutarOrdenamiento(Runnable accion) { btnEjecutarOrdenamiento.setOnAction(e -> accion.run()); }

    /**
     * Getter del texto ingresado en el campo de vértice inicial de búsqueda
     * @return Texto del vértice inicial sin espacios en los extremos
     */
    public String getTextoInicioBusqueda() { return txtInicioBusqueda.getText().trim(); }

    /**
     * Getter del texto ingresado en el campo de vértice destino para Dijkstra
     * @return Texto del vértice destino sin espacios en los extremos
     */
    public String getTextoFinBusqueda() { return txtFinBusqueda.getText().trim(); }

    /**
     * Getter del estado de la casilla de grafo dirigido
     * @return true si está marcada la opción de grafo dirigido, false en caso contrario
     */
    public boolean esDirigido() { return chkDirigido.isSelected(); }

    /**
     * Setter que cambia el estado de la casilla de grafo dirigido
     * @param dirigido Boolean que indica si el grafo es dirigido
     */
    public void setDirigido(boolean dirigido) { chkDirigido.setSelected(dirigido); }

    /**
     * Getter del texto ingresado con la cantidad de vértices a generar
     * @return Texto numérico de la cantidad de vértices sin espacios en los extremos
     */
    public String getTextoCantidadVertices() {
        return txtCantidadVertices.getText().trim();
    }

    /**
     * Getter del algoritmo de búsqueda seleccionado en el combo
     * @return Nombre del algoritmo de búsqueda seleccionado
     */
    public String getBusquedaSeleccionada() { return cmbAlgoritmoBusqueda.getValue(); }

    /**
     * Getter del algoritmo de ordenamiento seleccionado en el combo
     * @return Nombre del algoritmo de ordenamiento seleccionado
     */
    public String getOrdenamientoSeleccionado() { return cmbAlgoritmoOrdenamiento.getValue(); }

    /**
     * Getter del criterio de ordenamiento seleccionado en el combo
     * @return Criterio de ordenamiento seleccionado
     */
    public String getCriterioOrdenamiento() { return cmbCriterioOrdenamiento.getValue(); }
}