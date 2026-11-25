package com.example.proyectofinal_algoritmos.Vista.Secciones;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    private Button btnAnalisisEmpirico;

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
        txtFinBusqueda.setPromptText("Fin (Dijkstra)");
        txtFinBusqueda.setPrefWidth(95);

        btnEjecutarBusqueda = new Button("Ejecutar Búsqueda");

        cmbAlgoritmoOrdenamiento = new ComboBox<>();
        cmbAlgoritmoOrdenamiento.getItems().addAll("QuickSort", "Shell Sort");
        cmbAlgoritmoOrdenamiento.getSelectionModel().selectFirst();

        cmbCriterioOrdenamiento = new ComboBox<>();
        cmbCriterioOrdenamiento.getItems().addAll("Por Peso", "Por Nombre de Vértice");
        cmbCriterioOrdenamiento.getSelectionModel().selectFirst();

        btnEjecutarOrdenamiento = new Button("Ordenar");
        btnAnalisisEmpirico = new Button("Análisis Empírico");

        HBox filaAlgoritmos = new HBox(8,
                new Label("Búsqueda:"),
                cmbAlgoritmoBusqueda, txtInicioBusqueda, txtFinBusqueda, btnEjecutarBusqueda,
                new Label("Ordenar:"),
                cmbAlgoritmoOrdenamiento, cmbCriterioOrdenamiento, btnEjecutarOrdenamiento,
                btnAnalisisEmpirico
        );
        filaAlgoritmos.setAlignment(Pos.CENTER_LEFT);

        contenedor.getChildren().addAll(filaArchivos, filaAlgoritmos);
    }

    public VBox getContenedor() {
        return contenedor;
    }

    // Delegadores
    public void alCargarCSV(Runnable accion) { btnCargarCSV.setOnAction(e -> accion.run()); }
    public void alGuardarCSV(Runnable accion) { btnGuardarCSV.setOnAction(e -> accion.run()); }
    public void alGenerarPrueba(Runnable accion) { btnGenerarPrueba.setOnAction(e -> accion.run()); }
    public void alCambiarDirigido(Runnable accion) { chkDirigido.setOnAction(e -> accion.run()); }
    public void alAbrirVentanaGrafo(Runnable accion) { btnAbrirVentanaGrafo.setOnAction(e -> accion.run()); }
    public void alEjecutarBusqueda(Runnable accion) { btnEjecutarBusqueda.setOnAction(e -> accion.run()); }
    public void alEjecutarOrdenamiento(Runnable accion) { btnEjecutarOrdenamiento.setOnAction(e -> accion.run()); }
    public void alEjecutarAnalisisEmpirico(Runnable accion) { btnAnalisisEmpirico.setOnAction(e -> accion.run()); }

    // Getters
    public String getTextoInicioBusqueda() { return txtInicioBusqueda.getText().trim(); }
    public String getTextoFinBusqueda() { return txtFinBusqueda.getText().trim(); }
    public boolean esDirigido() { return chkDirigido.isSelected(); }
    public void setDirigido(boolean dirigido) { chkDirigido.setSelected(dirigido); }
    public String getTextoCantidadVertices() {
        return txtCantidadVertices.getText().trim();
    }
    public String getBusquedaSeleccionada() { return cmbAlgoritmoBusqueda.getValue(); }
    public String getOrdenamientoSeleccionado() { return cmbAlgoritmoOrdenamiento.getValue(); }
    public String getCriterioOrdenamiento() { return cmbCriterioOrdenamiento.getValue(); }
}
